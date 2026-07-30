const fs = require("fs");
const path = require("path");
const vm = require("vm");

const root = path.resolve(__dirname, "..");
const sourceFiles = [
  "assets/data/ingredients.js",
  "assets/data/personal-recipes.js",
  "assets/data/classic-recipes.js"
];
const outputPath = path.join(root, "android", "app", "src", "main", "assets", "catalog.json");

function loadCatalog() {
  const context = { window: { DD_DATA: {} } };
  vm.createContext(context);
  sourceFiles.forEach(relativePath => {
    const source = fs.readFileSync(path.join(root, relativePath), "utf8").replace(/^\uFEFF/, "");
    vm.runInContext(source, context, { filename: relativePath });
  });
  const data = context.window.DD_DATA;
  return {
    schemaVersion: "0.3-native-alpha01",
    generatedFrom: sourceFiles,
    ingredients: data.ingredients || [],
    recipes: [].concat(data.personalRecipes || [], data.classicRecipes || [])
  };
}

function validateCatalog(catalog) {
  const issues = [];
  const ingredientIds = new Set();
  const recipeIds = new Set();
  catalog.ingredients.forEach(item => {
    if (!item.id || ingredientIds.has(item.id)) issues.push(`Invalid or duplicate ingredient id: ${item.id}`);
    ingredientIds.add(item.id);
    if (!item.zh || !item.en) issues.push(`Ingredient ${item.id} is missing a bilingual name`);
  });
  catalog.recipes.forEach(recipe => {
    if (!recipe.id || recipeIds.has(recipe.id)) issues.push(`Invalid or duplicate recipe id: ${recipe.id}`);
    recipeIds.add(recipe.id);
    if (!recipe.name || !recipe.name.zh || !recipe.name.en) issues.push(`Recipe ${recipe.id} is missing a bilingual name`);
    if (!recipe.steps || !Array.isArray(recipe.steps.zh) || !recipe.steps.zh.length || !Array.isArray(recipe.steps.en) || !recipe.steps.en.length) {
      issues.push(`Recipe ${recipe.id} is missing bilingual steps`);
    }
    (recipe.base || []).forEach(id => { if (!ingredientIds.has(id)) issues.push(`Recipe ${recipe.id} references unknown base ${id}`); });
    (recipe.ingredients || []).forEach(item => {
      if (!ingredientIds.has(item.id)) issues.push(`Recipe ${recipe.id} references unknown ingredient ${item.id}`);
      const modes = [Number.isFinite(item.amount), Number.isFinite(item.fillTo), item.topUp === true].filter(Boolean).length;
      if (modes !== 1) issues.push(`Recipe ${recipe.id}/${item.id} has an invalid amount mode`);
    });
  });
  if (catalog.recipes.length !== 45) issues.push(`Expected 45 recipes, found ${catalog.recipes.length}`);
  return issues;
}

function serializeCatalog(catalog) {
  return JSON.stringify(catalog, null, 2) + "\n";
}

function main() {
  const checkOnly = process.argv.includes("--check");
  const catalog = loadCatalog();
  const issues = validateCatalog(catalog);
  if (issues.length) throw new Error(issues.join("\n"));
  const content = serializeCatalog(catalog);
  if (checkOnly) {
    const current = fs.existsSync(outputPath) ? fs.readFileSync(outputPath, "utf8") : "";
    if (current !== content) throw new Error("Native catalog is out of date. Run npm run sync:native-data.");
    console.log(`Native catalog is current: ${catalog.recipes.length} recipes, ${catalog.ingredients.length} ingredients.`);
    return;
  }
  fs.mkdirSync(path.dirname(outputPath), { recursive: true });
  fs.writeFileSync(outputPath, content, "utf8");
  console.log(`Wrote ${path.relative(root, outputPath)} with ${catalog.recipes.length} recipes and ${catalog.ingredients.length} ingredients.`);
}

if (require.main === module) main();
module.exports = { loadCatalog, validateCatalog, serializeCatalog, outputPath, sourceFiles };