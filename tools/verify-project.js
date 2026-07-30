const fs = require("fs");
const path = require("path");
const vm = require("vm");
const { loadCatalog, validateCatalog, serializeCatalog, outputPath } = require("./export-native-catalog");

const root = path.resolve(__dirname, "..");
const failures = [];
const jsFiles = [
  "sw.js",
  ...fs.readdirSync(path.join(root, "assets", "data")).filter(name => name.endsWith(".js")).map(name => `assets/data/${name}`),
  ...fs.readdirSync(path.join(root, "assets", "js")).filter(name => name.endsWith(".js")).map(name => `assets/js/${name}`),
  ...fs.readdirSync(path.join(root, "tools")).filter(name => name.endsWith(".js")).map(name => `tools/${name}`)
];
for (const relativePath of jsFiles) {
  try { new vm.Script(fs.readFileSync(path.join(root, relativePath), "utf8"), { filename: relativePath }); }
  catch (error) { failures.push(error.message); }
}

const catalog = loadCatalog();
failures.push(...validateCatalog(catalog));
const generated = fs.existsSync(outputPath) ? fs.readFileSync(outputPath, "utf8") : "";
if (generated !== serializeCatalog(catalog)) failures.push("android/app/src/main/assets/catalog.json is not synchronized");

const indexPath = path.join(root, "index.html");
const html = fs.readFileSync(indexPath, "utf8");
const requiredUiIds = [
  "webFilterToggle", "recipeFilters", "settingTheme", "settingFontScale", "fontScaleOutput"
];
for (const id of requiredUiIds) {
  const occurrences = [...html.matchAll(new RegExp(`id=["']${id}["']`, "g"))].length;
  if (occurrences !== 1) failures.push(`Expected exactly one #${id}, found ${occurrences}`);
}
const localRefs = [...html.matchAll(/(?:src|href)=["']([^"'#?]+)["']/g)]
  .map(match => match[1])
  .filter(value => !/^(?:https?:|data:|mailto:|tel:)/.test(value));
for (const relativePath of localRefs) {
  if (!fs.existsSync(path.join(root, relativePath))) failures.push(`Missing index asset: ${relativePath}`);
}
JSON.parse(fs.readFileSync(path.join(root, "manifest.webmanifest"), "utf8"));

const css = fs.readFileSync(path.join(root, "assets", "css", "styles.css"), "utf8");
if ((css.match(/{/g) || []).length !== (css.match(/}/g) || []).length) failures.push("Unbalanced CSS braces");
for (const token of ['data-theme="dark"', 'data-accent="coral"', "--font-scale"]) {
  if (!css.includes(token)) failures.push(`Missing appearance token: ${token}`);
}

const appSource = fs.readFileSync(path.join(root, "assets", "js", "app.js"), "utf8");
const brandedAlcoholIds = [
  "aperol", "baileys", "triple_sec", "dry_vermouth", "campari", "coffee_liqueur",
  "blue_curacao", "sweet_vermouth", "jagermeister", "angostura", "brandy", "white_rum",
  "bourbon", "vodka", "gin", "tequila", "prosecco", "whiskey"
];
for (const id of brandedAlcoholIds) {
  if (!new RegExp(`\\b${id}\\s*:`).test(appSource)) failures.push(`Missing web brand profile: ${id}`);
}

if (failures.length) {
  console.error(failures.map(value => `- ${value}`).join("\n"));
  process.exit(1);
}
console.log(JSON.stringify({
  syntaxFiles: jsFiles.length,
  ingredients: catalog.ingredients.length,
  recipes: catalog.recipes.length,
  localAssets: localRefs.length,
  nativeCatalog: path.relative(root, outputPath)
}, null, 2));
