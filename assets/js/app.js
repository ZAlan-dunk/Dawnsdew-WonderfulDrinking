(function () {
  "use strict";

  const data = window.DD_DATA;
  const i18n = window.DD_I18N;
  const storage = window.DD_STORAGE;
  const calc = window.DD_CALC;
  const ingredientProfiles = window.DD_INGREDIENT_PROFILES;
  const native = window.DD_NATIVE || { isNative: () => false, shareJson: async () => false, onBackButton: () => {}, exitApp: () => {} };
  const $ = selector => document.querySelector(selector);
  const $$ = selector => Array.from(document.querySelectorAll(selector));
  const map = data.ingredientMap;
  const tasteKeys = ["sweet", "creamy", "fresh", "citrus", "fruity", "sparkling", "strong", "coconut", "bitter", "herbal", "dry", "spicy", "coffee"];
  const commonIngredients = ["white_rum", "vodka", "brandy", "baileys", "blue_curacao", "cola", "sprite", "soda_water", "orange_juice", "grape_juice", "lemon_juice", "lime_juice"];
  const pantryCategories = [
    ["spirit", "基酒", "Spirits"], ["liqueur", "利口酒", "Liqueurs"], ["sparkling", "起泡酒", "Sparkling"],
    ["soda", "气泡饮料", "Sodas"], ["juice", "果汁", "Juices"], ["dairy", "乳品", "Dairy"],
    ["tea", "茶饮", "Tea"], ["fruit", "鲜果与装饰", "Fruit & Garnish"], ["other", "其他", "Other"]
  ];
  const brandProfiles = {
    vodka: [["Absolut", "原味伏特加", "干净、中性", "Clean and neutral"], ["Smirnoff", "No.21", "轻盈直接", "Light and direct"], ["Finlandia", "Classic", "干爽利落", "Dry and crisp"]],
    white_rum: [["Bacardi", "Carta Blanca", "清爽甘蔗香", "Light sugarcane"], ["Havana Club", "3 Años", "甘蔗与轻橡木", "Sugarcane and light oak"], ["Planteray", "3 Stars", "热带果香", "Tropical fruit"]],
    tequila: [["Olmeca", "Blanco", "龙舌兰与青柑橘", "Agave and green citrus"], ["Espolòn", "Blanco", "植物感清爽", "Botanical and fresh"], ["José Cuervo", "Especial Silver", "直接易搭配", "Approachable and mixable"]],
    bourbon: [["Jim Beam", "White Label", "焦糖、香草和橡木", "Caramel, vanilla and oak"]],
    whiskey: [["Jim Beam", "White Label", "焦糖波本风格", "Caramel-led bourbon"], ["Jameson", "Irish Whiskey", "柔和谷物感", "Soft and grain-forward"], ["Johnnie Walker", "Black Label", "烟熏与果干", "Smoke and dried fruit"]],
    gin: [["Beefeater", "London Dry", "经典杜松子", "Classic juniper"], ["Tanqueray", "London Dry", "强植物感", "Bold botanicals"], ["Bombay Sapphire", "London Dry", "柑橘与香料", "Citrus and spice"]],
    brandy: [["St-Rémy", "VSOP", "成熟果香与橡木", "Ripe fruit and oak"], ["Martell", "VS", "明亮果香", "Bright fruit"]],
    triple_sec: [["Cointreau", "L'Unique", "干净橙皮香", "Clean orange peel"]], coffee_liqueur: [["Kahlúa", "Coffee Liqueur", "咖啡、焦糖和可可", "Coffee, caramel and cocoa"]],
    dry_vermouth: [["Martini", "Extra Dry", "草本干爽", "Herbal and dry"]], sweet_vermouth: [["Cinzano", "Rosso", "甜香料与红果", "Sweet spice and red fruit"]], prosecco: [["Mionetto", "Prosecco Brut", "青苹果与白花", "Green apple and white flowers"]],
    blue_curacao: [["Bols", "Blue Curaçao", "橙皮香", "Orange peel"]], baileys: [["Baileys", "Original", "奶油与可可", "Cream and cocoa"]],
    aperol: [["Aperol", "Aperitivo", "轻苦橙皮", "Light bitter orange"]], campari: [["Campari", "Bitter", "苦橙与草本", "Bitter orange and herbs"]],
    jagermeister: [["Jägermeister", "Herbal Liqueur", "深色草本与香料", "Dark herbs and spice"]], angostura: [["Angostura", "Aromatic Bitters", "香料与烘烤感", "Spice and toast"]]
  };
  let state = storage.load();
  let featuredId = null;
  let activeView = "home";
  let modalReturnFocus = null;
  let modalCloseTimer = null;
  let ingredientReturnFocus = null;
  let searchReturnFocus = null;
  let activePantryCategory = "spirit";
  let activeSearchTarget = "recipe";
  const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)");
  const systemDark = window.matchMedia("(prefers-color-scheme: dark)");
  const lowMotionDevice = Boolean((navigator.deviceMemory && navigator.deviceMemory <= 2) || (navigator.hardwareConcurrency && navigator.hardwareConcurrency <= 2) || (navigator.connection && navigator.connection.saveData));

  function t(key, values) { return i18n.t(key, state.settings.lang, values); }
  function copy(zh, en) { return state.settings.lang === "en" ? en : zh; }
  function langValue(value) {
    if (!value) return "";
    if (typeof value === "string") return value;
    return value[state.settings.lang] || value.zh || value.en || "";
  }
  function esc(value) {
    return String(value === undefined || value === null ? "" : value)
      .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;").replace(/'/g, "&#039;");
  }
  function save() { state = storage.save(state); }
  function allRecipes() { return [].concat(data.personalRecipes || [], data.classicRecipes || [], state.customRecipes || []); }
  function recipeById(id) { return allRecipes().find(recipe => recipe.id === id); }
  function ingredientName(id) { return map[id] ? map[id][state.settings.lang] : t("unknownIngredient"); }
  function originName(origin) {
    return t({ personal: "originPersonal", classic: "originClassic", convenience: "originConvenience", custom: "originCustom" }[origin] || "custom");
  }
  function difficultyName(value) { return t(value || "easy"); }
  function tasteName(value) {
    const key = `taste${String(value || "").charAt(0).toUpperCase()}${String(value || "").slice(1)}`;
    return t(key) === key ? value : t(key);
  }
  function methodName(value) { return t({ build: "methodBuild", shake: "methodShake", stir: "methodStir" }[value] || "methodBuild"); }
  function formatNumber(value, digits) {
    return Number(value || 0).toLocaleString(state.settings.lang === "en" ? "en-US" : "zh-CN", { maximumFractionDigits: digits === undefined ? 1 : digits });
  }
  function isFavorite(id) { return state.favorites.includes(id); }
  function isInTonightMenu(id) { return state.tonightMenu.includes(id); }
  function brandSuggestions(recipe) {
    return (recipe.base || []).flatMap(id => brandProfiles[id] || []).slice(0, 3);
  }

  function toast(message) {
    const node = document.createElement("div");
    node.className = "toast";
    node.textContent = message;
    $("#toastRegion").appendChild(node);
    window.setTimeout(() => node.remove(), 2600);
  }

  function stageRecipeCards(container) {
    if (reduceMotion.matches) return;
    const cards = Array.from(container.querySelectorAll(".recipe-card"));
    cards.forEach((card, index) => {
      card.classList.add("motion-pending");
      card.style.setProperty("--motion-delay", `${Math.min(index, 8) * 38}ms`);
    });
    window.requestAnimationFrame(() => window.requestAnimationFrame(() => {
      cards.forEach(card => card.classList.add("motion-ready"));
      window.setTimeout(() => cards.forEach(card => {
        card.classList.remove("motion-pending", "motion-ready");
        card.style.removeProperty("--motion-delay");
      }), 720);
    }));
  }

  function renderRecipeCollection(selector, recipes, compact, emptyHtml) {
    const container = $(selector);
    const signature = recipes.map(recipe => recipe.id).join("|");
    const shouldAnimate = container.dataset.recipeSignature !== signature;
    container.innerHTML = recipes.length ? recipes.map(recipe => recipeCard(recipe, compact)).join("") : (emptyHtml || "");
    container.dataset.recipeSignature = signature;
    if (shouldAnimate && recipes.length) stageRecipeCards(container);
  }

  function renderArt(colors, className) {
    const palette = colors && colors.length > 1 ? colors : ["#5f2736", "#d98a54"];
    return `<div class="${className}" style="--c1:${esc(palette[0])};--c2:${esc(palette[1])}"><div class="art-glass"></div></div>`;
  }

  function matchBadge(recipe) {
    const match = calc.matchRecipe(recipe, state.pantry);
    const label = match.status === "makeable" ? t("makeable") : (match.status === "almost" ? t("almost") : t("missingCount", { count: match.missing.length }));
    return `<span class="match-badge ${match.status}">${esc(label)}</span>`;
  }

  function recipeCard(recipe, compact) {
    const abv = calc.estimateAbv(recipe, state.settings, state.pantry);
    const favoriteLabel = isFavorite(recipe.id) ? t("unfavorite") : t("favorite");
    const tags = (recipe.taste || []).slice(0, compact ? 1 : 2).map(tag => `<span class="tag">${esc(tasteName(tag))}</span>`).join("");
    const rating = recipe.rating ? `<span class="rating">${esc(recipe.rating)}</span>` : "";
    const brand = brandSuggestions(recipe)[0];
    return `<article class="recipe-card" data-recipe-card="${esc(recipe.id)}">
      ${renderArt(recipe.colors, "recipe-art")}
      <div class="card-body">
        <div class="card-topline"><span>${esc(originName(recipe.origin))}</span>${rating}</div>
        <button class="recipe-card-button" type="button" data-open-recipe="${esc(recipe.id)}">
          <h3>${esc(langValue(recipe.name))}</h3>${brand ? `<p class="brand-line">${esc(brand[0])} · ${esc(brand[1])}</p>` : ""}<p class="english-name">${esc(state.settings.lang === "zh" ? recipe.name.en : recipe.name.zh)}</p>
        </button>
        <div class="meta-row">${tags}${matchBadge(recipe)}</div>
        <div class="card-metrics"><span>${esc(difficultyName(recipe.difficulty))}</span><span>${formatNumber(abv.abv, 1)}% ABV</span></div>
        <button class="favorite-button ${isFavorite(recipe.id) ? "active" : ""}" type="button" data-favorite="${esc(recipe.id)}" aria-label="${esc(favoriteLabel)}">${isFavorite(recipe.id) ? "♥" : "♡"}</button>
        <button class="tonight-card-button ${isInTonightMenu(recipe.id) ? "active" : ""}" type="button" data-tonight-toggle="${esc(recipe.id)}" aria-label="${esc(isInTonightMenu(recipe.id) ? t("removeFromTonight") : t("addToTonight"))}">${isInTonightMenu(recipe.id) ? "✓ " + esc(t("navTonightShort")) : "+ " + esc(t("navTonightShort"))}</button>
      </div>
    </article>`;
  }

  function renderHome() {
    const recipes = allRecipes();
    const makeable = recipes.filter(recipe => calc.matchRecipe(recipe, state.pantry).status === "makeable").length;
    const pantryCount = Object.values(state.pantry).filter(entry => entry && entry.owned).length;
    $("#homeStats").innerHTML = [
      [recipes.length, t("statRecipes")], [makeable, t("statMakeable")], [state.favorites.length, t("statFavorites")], [pantryCount, t("statPantry")]
    ].map(item => `<article class="stat-card"><strong>${item[0]}</strong><span>${esc(item[1])}</span></article>`).join("");

    if (!featuredId || !recipeById(featuredId)) featuredId = recipes[Math.floor(Math.random() * recipes.length)].id;
    const featured = recipeById(featuredId);
    const abv = calc.estimateAbv(featured, state.settings, state.pantry);
    $("#featuredRecipe").innerHTML = `<article class="featured">
      ${renderArt(featured.colors, "featured-art")}
      <div class="featured-body"><div class="meta-row"><span class="tag">${esc(originName(featured.origin))}</span>${matchBadge(featured)}</div>
      <h2>${esc(langValue(featured.name))}</h2><p class="english-name">${esc(state.settings.lang === "zh" ? featured.name.en : featured.name.zh)}</p>
      <p class="featured-description">${esc(langValue(featured.description))}</p>
      <div class="card-metrics"><span>${esc(difficultyName(featured.difficulty))}</span><span>${formatNumber(abv.abv, 1)}% ABV</span></div>
      <div class="featured-actions"><button class="button primary" type="button" data-open-recipe="${esc(featured.id)}">${esc(t("viewRecipe"))}</button><button class="button ghost" type="button" data-favorite="${esc(featured.id)}">${isFavorite(featured.id) ? "♥ " + esc(t("unfavorite")) : "♡ " + esc(t("favorite"))}</button></div></div>
    </article>`;

    const tonight = state.tonightMenu.map(recipeById).filter(Boolean).slice(0, 4);
    renderRecipeCollection("#homeTonightMenu", tonight, true, `<div class="empty-state tonight-empty"><span>☾</span><p>${esc(t("tonightMenuEmptyHint"))}</p><button class="button ghost small" type="button" data-nav="recipes">${esc(t("browseRecipes"))}</button></div>`);
    const recent = state.recent.map(recipeById).filter(Boolean).slice(0, 4);
    renderRecipeCollection("#recentRecipes", recent, true, `<div class="empty-state"><span>◇</span><p>${esc(t("noRecent"))}</p></div>`);
  }

  function renderTonightMenu() {
    state.tonightMenu = state.tonightMenu.filter(id => Boolean(recipeById(id)));
    const recipes = state.tonightMenu.map(recipeById).filter(Boolean);
    $("#tonightMenuCount").textContent = t("tonightMenuCount", { count: recipes.length });
    renderRecipeCollection("#tonightMenuGrid", recipes, false, "");
    $("#tonightMenuEmpty").classList.toggle("hidden", recipes.length > 0);
    $("#clearTonightMenu").disabled = recipes.length === 0;
  }

  function option(value, label, selected) { return `<option value="${esc(value)}"${value === selected ? " selected" : ""}>${esc(label)}</option>`; }

  function renderFilterOptions() {
    const recipes = allRecipes();
    const origin = $("#originFilter").value;
    const base = $("#baseFilter").value;
    const taste = $("#tasteFilter").value;
    const difficulty = $("#difficultyFilter").value;
    $("#originFilter").innerHTML = option("", t("allOrigins"), origin) + ["personal", "classic", "convenience", "custom"].map(value => option(value, originName(value), origin)).join("");
    const bases = [...new Set(recipes.flatMap(recipe => recipe.base || []))].filter(id => map[id]).sort((a, b) => ingredientName(a).localeCompare(ingredientName(b)));
    $("#baseFilter").innerHTML = option("", t("allBases"), base) + bases.map(value => option(value, ingredientName(value), base)).join("");
    const tastes = [...new Set(recipes.flatMap(recipe => recipe.taste || []))];
    $("#tasteFilter").innerHTML = option("", t("allTastes"), taste) + tastes.map(value => option(value, tasteName(value), taste)).join("");
    $("#difficultyFilter").innerHTML = option("", t("allDifficulties"), difficulty) + ["easy", "beginner", "advanced"].map(value => option(value, difficultyName(value), difficulty)).join("");
  }

  function filteredRecipes() {
    const search = calc.normalizeSearch($("#recipeSearch").value);
    return allRecipes().filter(recipe => {
      if (search && !calc.searchableText(recipe, state.settings.lang).includes(search)) return false;
      if ($("#originFilter").value && recipe.origin !== $("#originFilter").value) return false;
      if ($("#baseFilter").value && !(recipe.base || []).includes($("#baseFilter").value)) return false;
      if ($("#tasteFilter").value && !(recipe.taste || []).includes($("#tasteFilter").value)) return false;
      if ($("#difficultyFilter").value && recipe.difficulty !== $("#difficultyFilter").value) return false;
      if ($("#makeableFilter").checked && calc.matchRecipe(recipe, state.pantry).status !== "makeable") return false;
      if ($("#favoriteFilter").checked && !isFavorite(recipe.id)) return false;
      return true;
    });
  }

  function renderRecipes() {
    renderFilterOptions();
    const recipes = filteredRecipes();
    $("#recipeSearchButton").classList.toggle("active", $("#recipeSearch").value.trim().length > 0);
    $("#recipeResultCount").textContent = ` · ${recipes.length}`;
    const detailedCount = ["originFilter", "baseFilter", "tasteFilter", "difficultyFilter"].filter(id => $(`#${id}`).value).length;
    $("#activeFilterCount").textContent = detailedCount ? ` ${detailedCount}` : "";
    renderRecipeCollection("#recipeGrid", recipes, false, "");
    $("#recipeEmpty").classList.toggle("hidden", recipes.length > 0);
  }

  function pantryEntry(id) {
    const ingredient = map[id];
    if (!state.pantry[id]) state.pantry[id] = { owned: false, stock: "", price: "", packSize: ingredient.pack || "", abv: ingredient.abv || 0 };
    return state.pantry[id];
  }

  function pantryCategory(item) {
    return item.id === "prosecco" ? "sparkling" : item.category;
  }

  function pantryCategoryLabel(category) {
    const item = pantryCategories.find(entry => entry[0] === category);
    return item ? copy(item[1], item[2]) : category;
  }

  function relatedRecipes(ingredientId) {
    return allRecipes().filter(recipe => (recipe.ingredients || []).some(item => item.id === ingredientId));
  }

  function bottleArt(item, profile, large) {
    return `<span class="bottle-art ${large ? "large" : ""}" aria-hidden="true"><span class="bottle-shape bottle-${esc(profile.shape)}" style="--bottle-accent:${esc(profile.accent)}"><i></i><b>${esc(profile.monogram)}</b></span></span>`;
  }

  function renderPantry() {
    const query = calc.normalizeSearch($("#pantrySearch").value);
    document.querySelector('[data-search-target="pantry"]').classList.toggle("active", query.length > 0);
    const matching = data.ingredients.filter(item => !query || calc.normalizeSearch(`${item.zh} ${item.en}`).includes(query));
    if (!matching.some(item => pantryCategory(item) === activePantryCategory) && matching.length) activePantryCategory = pantryCategory(matching[0]);
    const counts = Object.fromEntries(pantryCategories.map(category => [category[0], data.ingredients.filter(item => pantryCategory(item) === category[0]).length]));
    $("#pantryCategoryTabs").innerHTML = pantryCategories.filter(category => counts[category[0]]).map(category => `<button type="button" role="tab" aria-selected="${category[0] === activePantryCategory}" class="${category[0] === activePantryCategory ? "active" : ""}" data-pantry-category="${esc(category[0])}">${esc(copy(category[1], category[2]))}<small>${counts[category[0]]}</small></button>`).join("");
    const ingredients = matching.filter(item => pantryCategory(item) === activePantryCategory);
    $("#pantryGroups").innerHTML = ingredients.length ? `<div class="pantry-list" aria-label="${esc(pantryCategoryLabel(activePantryCategory))}">${ingredients.map(item => {
      const entry = pantryEntry(item.id);
      const profile = ingredientProfiles.get(item);
      const recipeCount = relatedRecipes(item.id).length;
      return `<article class="pantry-item" data-pantry-row="${esc(item.id)}">
        <button class="pantry-item-main" type="button" data-open-ingredient="${esc(item.id)}">
          ${bottleArt(item, profile, false)}
          <span class="pantry-item-copy"><b>${esc(item[state.settings.lang])}</b><small>${esc(state.settings.lang === "zh" ? item.en : item.zh)}</small><span>${esc(copy(`${recipeCount} 杯相关配方`, `${recipeCount} recipes`))}${Number(entry.abv || item.abv) ? ` · ${formatNumber(entry.abv || item.abv, 1)}% ABV` : ""}</span></span>
          <span class="pantry-item-arrow" aria-hidden="true">›</span>
        </button>
        <label class="pantry-switch"><input type="checkbox" data-pantry-field="owned" data-id="${esc(item.id)}" ${entry.owned ? "checked" : ""}><span></span><em>${esc(entry.owned ? copy("已有", "Owned") : copy("未有", "Missing"))}</em></label>
      </article>`;
    }).join("")}</div>` : `<div class="empty-state pantry-empty"><span>⌕</span><p>${esc(copy("当前品类没有匹配材料", "No matching ingredients in this category"))}</p></div>`;
    renderPantrySummary();
  }

  function renderPantrySummary() {
    const owned = Object.values(state.pantry).filter(entry => entry && entry.owned).length;
    const makeable = allRecipes().filter(recipe => calc.matchRecipe(recipe, state.pantry).status === "makeable").length;
    $("#pantrySummary").innerHTML = `<strong>${esc(copy(`已有 ${owned} 种`, `${owned} owned`))}</strong><span aria-hidden="true">·</span><strong>${esc(copy(`可调 ${makeable} 杯`, `${makeable} makeable`))}</strong>`;
  }

  function ingredientOptions(selected) {
    return option("", t("selectIngredient"), selected) + data.ingredients.map(item => option(item.id, item[state.settings.lang], selected)).join("");
  }

  function addCustomIngredientRow(value) {
    const item = value || { id: "", unit: "ml", amount: 30 };
    const row = document.createElement("div");
    row.className = "custom-ingredient-row";
    row.innerHTML = `<select data-custom="id">${ingredientOptions(item.id)}</select>
      <select data-custom="unit">
        ${option("ml", t("fixedMl"), item.unit)}${option("piece", t("fixedPiece"), item.unit)}${option("fill", t("fillTo"), item.unit)}${option("top", t("topToFull"), item.unit)}
      </select>
      <input data-custom="amount" type="number" min="0" step="0.1" value="${esc(item.unit === "fill" ? (item.fillTo || item.amount || 0.8) : (item.amount || 0))}" aria-label="${esc(t("amount"))}">
      <button class="remove-row" type="button" data-remove-row aria-label="${esc(t("remove"))}">×</button>`;
    $("#customIngredientRows").appendChild(row);
    updateCustomAmountState(row);
  }

  function updateCustomAmountState(row) {
    const unit = row.querySelector('[data-custom="unit"]').value;
    const input = row.querySelector('[data-custom="amount"]');
    input.disabled = unit === "top";
    if (unit === "fill") { input.min = "0.1"; input.max = "1"; input.step = "0.1"; if (Number(input.value) > 1 || !Number(input.value)) input.value = "0.8"; }
    else { input.min = "0"; input.removeAttribute("max"); input.step = unit === "piece" ? "1" : "0.1"; }
  }

  function renderCustomControls() {
    const currentBase = $("#customBaseSelect").value;
    const bases = data.ingredients.filter(item => item.category === "spirit" || item.category === "liqueur");
    $("#customBaseSelect").innerHTML = option("", t("selectBase"), currentBase) + bases.map(item => option(item.id, item[state.settings.lang], currentBase)).join("");
    $$('#customRecipeForm select[name="taste"] option').forEach(node => { node.textContent = tasteName(node.value); });
    $$('#customRecipeForm select[name="difficulty"] option').forEach(node => { node.textContent = difficultyName(node.value); });
    $$("#customIngredientRows .custom-ingredient-row").forEach(row => {
      const select = row.querySelector('[data-custom="id"]');
      const selected = select.value;
      select.innerHTML = ingredientOptions(selected);
      const unit = row.querySelector('[data-custom="unit"]');
      const currentUnit = unit.value;
      unit.innerHTML = option("ml", t("fixedMl"), currentUnit) + option("piece", t("fixedPiece"), currentUnit) + option("fill", t("fillTo"), currentUnit) + option("top", t("topToFull"), currentUnit);
    });
  }

  function renderCustomList() {
    $("#customRecipeList").innerHTML = state.customRecipes.length ? state.customRecipes.map(recipe => `<div class="custom-recipe-entry">${recipeCard(recipe, false)}<button class="custom-delete" type="button" data-delete-custom="${esc(recipe.id)}">${esc(t("delete"))}</button></div>`).join("") : `<div class="empty-state"><span>＋</span><p>${esc(t("noCustom"))}</p></div>`;
  }

  function amountLabel(item, estimate) {
    if (item.unit === "ml") return t("amountMl", { amount: formatNumber(item.amount, 1) });
    if (item.unit === "piece") return t("amountPiece", { amount: formatNumber(item.amount, 0) });
    if (item.unit === "fill") return t("fillRatio", { ratio: Math.round(Number(item.fillTo) * 10) });
    if (item.unit === "top") return t("topUp");
    return estimate ? `${formatNumber(estimate, 1)} ml` : "";
  }

  function openIngredient(id) {
    const item = map[id];
    if (!item) return;
    const profile = ingredientProfiles.get(item);
    const entry = pantryEntry(id);
    const recipes = relatedRecipes(id).slice(0, 5);
    const brands = (brandProfiles[id] || []).slice(0, 3);
    const timeline = profile.milestones.map(milestone => `<li><b>${esc(milestone.year)}</b><span>${esc(state.settings.lang === "en" ? milestone.en : milestone.zh)}</span></li>`).join("");
    const recipeLinks = recipes.length ? recipes.map(recipe => `<button type="button" data-open-related-recipe="${esc(recipe.id)}"><span>${esc(langValue(recipe.name))}</span><small>${esc(difficultyName(recipe.difficulty))} · ${formatNumber(calc.estimateAbv(recipe, state.settings, state.pantry).abv, 1)}% ABV</small><i aria-hidden="true">›</i></button>`).join("") : `<p class="ingredient-empty-copy">${esc(copy("暂无关联配方", "No related recipes yet"))}</p>`;
    const brandHtml = brands.length ? `<div class="ingredient-brand-row">${brands.map(brand => `<span><b>${esc(brand[0])}</b><small>${esc(brand[1])}</small></span>`).join("")}</div>` : "";
    $("#ingredientModalContent").innerHTML = `<header class="ingredient-hero" style="--ingredient-accent:${esc(profile.accent)}">
      ${bottleArt(item, profile, true)}
      <div><p>${esc(pantryCategoryLabel(pantryCategory(item)))}</p><h2 id="ingredientModalTitle">${esc(item[state.settings.lang])}</h2><span>${esc(state.settings.lang === "zh" ? item.en : item.zh)}</span></div>
    </header>
    <div class="ingredient-detail-body">
      <p class="ingredient-lead">${esc(langValue(profile.intro))}</p>
      ${brandHtml}
      <section class="ingredient-inventory"><div><h3>${esc(copy("我的库存", "My inventory"))}</h3><label class="detail-owned"><input type="checkbox" data-ingredient-field="owned" data-id="${esc(id)}" ${entry.owned ? "checked" : ""}><span></span>${esc(copy("酒柜里有这款材料", "This ingredient is in my pantry"))}</label></div><div class="ingredient-fields">
        <label><span>${esc(t("pantryStock"))}</span><input type="number" min="0" step="1" data-ingredient-field="stock" data-id="${esc(id)}" value="${esc(entry.stock)}"></label>
        <label><span>${esc(t("pantryPrice"))}</span><input type="number" min="0" step="0.01" data-ingredient-field="price" data-id="${esc(id)}" value="${esc(entry.price)}"></label>
        <label><span>${esc(t("pantryPack"))}</span><input type="number" min="0.01" step="1" data-ingredient-field="packSize" data-id="${esc(id)}" value="${esc(entry.packSize)}"></label>
        <label><span>${esc(t("pantryAbv"))}</span><input type="number" min="0" max="100" step="0.1" data-ingredient-field="abv" data-id="${esc(id)}" value="${esc(entry.abv)}"></label>
      </div></section>
      <section class="ingredient-story"><p class="eyebrow">ORIGIN</p><h3>${esc(copy("起源与脉络", "Origin & context"))}</h3><p>${esc(langValue(profile.origin))}</p><ol>${timeline}</ol></section>
      <section class="ingredient-recipes"><p class="eyebrow">CLASSIC SERVES</p><h3>${esc(copy("常见配方", "Familiar recipes"))}</h3><div>${recipeLinks}</div></section>
    </div>`;
    const modal = $("#ingredientModal");
    ingredientReturnFocus = document.activeElement instanceof HTMLElement ? document.activeElement : null;
    modal.classList.remove("hidden");
    modal.setAttribute("aria-hidden", "false");
    document.body.classList.add("modal-open");
    window.requestAnimationFrame(() => $("#ingredientModalClose").focus({ preventScroll: true }));
  }

  function closeIngredientModal(restoreFocus) {
    const modal = $("#ingredientModal");
    if (modal.classList.contains("hidden")) return;
    modal.classList.add("hidden");
    modal.setAttribute("aria-hidden", "true");
    document.body.classList.remove("modal-open");
    if (restoreFocus !== false && ingredientReturnFocus && ingredientReturnFocus.isConnected) ingredientReturnFocus.focus({ preventScroll: true });
    ingredientReturnFocus = null;
  }

  function openFloatingSearch(target, trigger) {
    activeSearchTarget = target;
    searchReturnFocus = trigger || document.activeElement;
    const source = target === "pantry" ? $("#pantrySearch") : $("#recipeSearch");
    const input = $("#floatingSearchInput");
    $("#floatingSearchLabel").textContent = target === "pantry" ? copy("搜索酒柜材料", "Search ingredients") : copy("搜索配方", "Search recipes");
    input.placeholder = target === "pantry" ? t("searchIngredient") : t("searchPlaceholder");
    input.value = source.value;
    $("#floatingSearch").classList.remove("hidden");
    $("#floatingSearch").setAttribute("aria-hidden", "false");
    document.body.classList.add("modal-open");
    window.requestAnimationFrame(() => { input.focus({ preventScroll: true }); input.select(); });
  }

  function closeFloatingSearch() {
    const overlay = $("#floatingSearch");
    if (overlay.classList.contains("hidden")) return;
    overlay.classList.add("hidden");
    overlay.setAttribute("aria-hidden", "true");
    document.body.classList.remove("modal-open");
    if (searchReturnFocus && searchReturnFocus.isConnected) searchReturnFocus.focus({ preventScroll: true });
    searchReturnFocus = null;
  }

  function applyFloatingSearch(value) {
    const source = activeSearchTarget === "pantry" ? $("#pantrySearch") : $("#recipeSearch");
    source.value = value;
    if (activeSearchTarget === "pantry") renderPantry(); else renderRecipes();
  }

  function initializeParticles() {
    if (reduceMotion.matches || lowMotionDevice) { document.body.classList.add("motion-lite"); return; }
    const field = $("#heroParticles");
    const fragments = Array.from({ length: 16 }, (_, index) => `<i style="--x:${7 + (index * 37) % 88}%;--y:${8 + (index * 53) % 78}%;--size:${index % 4 === 0 ? 4 : 2}px;--delay:-${(index * 0.73).toFixed(2)}s;--duration:${7 + index % 6}s"></i>`);
    field.innerHTML = fragments.join("");
  }

  function burstParticles(event) {
    if (reduceMotion.matches || lowMotionDevice || !event.clientX || !event.clientY) return;
    const layer = $("#clickParticleLayer");
    const colors = ["var(--gold)", "var(--orange)", "var(--green)"];
    Array.from({ length: 6 }, (_, index) => {
      const particle = document.createElement("i");
      const angle = (Math.PI * 2 * index) / 6 + 0.25;
      const distance = 14 + (index % 3) * 5;
      particle.style.cssText = `left:${event.clientX}px;top:${event.clientY}px;--dx:${Math.cos(angle) * distance}px;--dy:${Math.sin(angle) * distance}px;--particle-color:${colors[index % colors.length]}`;
      layer.appendChild(particle);
      window.setTimeout(() => particle.remove(), 520);
    });
  }

  function openRecipe(id) {
    const recipe = recipeById(id);
    if (!recipe) return;
    state.recent = [id].concat(state.recent.filter(value => value !== id)).slice(0, 8);
    save();
    const volume = calc.estimateAmounts(recipe, state.settings);
    const abv = calc.estimateAbv(recipe, state.settings, state.pantry);
    const cost = calc.estimateCost(recipe, state.settings, state.pantry);
    const match = calc.matchRecipe(recipe, state.pantry);
    const missing = match.missing.map(ingredientName);
    const colors = recipe.colors && recipe.colors.length > 1 ? recipe.colors : ["#5f2736", "#d98a54"];
    const ingredients = volume.amounts.map(item => `<li><span>${esc(ingredientName(item.id))}${item.optional ? ` <small>(${esc(t("optional"))})</small>` : ""}</span><b>${esc(amountLabel(item, item.estimatedMl))}</b></li>`).join("");
    const steps = (recipe.steps && (recipe.steps[state.settings.lang] || recipe.steps.zh) || []).map(step => `<li>${esc(step)}</li>`).join("");
    const costLabel = cost.hasCost ? `${esc(state.settings.currency)}${formatNumber(cost.cost, 2)}${cost.coverage < 0.99 ? "*" : ""}` : t("unknownCost");
    const brandHtml = brandSuggestions(recipe).map(profile => `<article class="brand-suggestion"><b>${esc(profile[0])}</b><span>${esc(profile[1])}</span><small>${esc(state.settings.lang === "en" ? profile[3] : profile[2])}</small></article>`).join("");
    $("#modalContent").innerHTML = `<div class="modal-hero" style="--c1:${esc(colors[0])};--c2:${esc(colors[1])}"><div><div class="meta-row"><span class="tag">${esc(originName(recipe.origin))}</span>${recipe.rating ? `<span class="rating">${esc(recipe.rating)}</span>` : ""}${matchBadge(recipe)}</div><h2 id="modalTitle">${esc(langValue(recipe.name))}</h2><p class="english-name">${esc(state.settings.lang === "zh" ? recipe.name.en : recipe.name.zh)}</p></div><div class="art-glass"></div></div>
      <div class="modal-body"><p class="modal-summary">${esc(langValue(recipe.description))}</p>${brandHtml ? `<div class="brand-suggestions"><h3>${esc(t("baseSuggestions"))}</h3><div>${brandHtml}</div></div>` : ""}
      <div class="detail-grid"><div class="metric"><span>${esc(t("estimatedAbv"))}</span><strong>${formatNumber(abv.abv, 1)}%</strong></div><div class="metric"><span>${esc(t("estimatedCost"))}</span><strong>${costLabel}</strong></div><div class="metric"><span>${esc(t("totalVolume"))}</span><strong>${formatNumber(volume.total, 1)} ml</strong></div><div class="metric"><span>${esc(t("method"))}</span><strong>${esc(methodName(recipe.method))}</strong></div></div>
      <div class="detail-section"><h3>${esc(t("recipeIngredients"))}</h3><ul class="ingredient-list">${ingredients}</ul></div>
      <div class="detail-section"><h3>${esc(t("recipeSteps"))}</h3><ol class="step-list">${steps}</ol></div>
      ${missing.length ? `<div class="detail-section"><h3>${esc(t("missingIngredients"))}</h3><p>${esc(missing.join(state.settings.lang === "zh" ? "、" : ", "))}</p></div>` : ""}
      ${recipe.sourceText ? `<div class="original-note"><b>${esc(t("originalFormula"))}</b><br>${esc(recipe.sourceText)}</div>` : ""}
      <p class="warning-note">${esc(t("estimateOnly"))}</p>${volume.exceedsCapacity ? `<p class="warning-note">${esc(t("capacityWarning"))}</p>` : ""}
      <div class="modal-actions"><button class="button primary" type="button" data-favorite="${esc(recipe.id)}">${isFavorite(recipe.id) ? "♥ " + esc(t("unfavorite")) : "♡ " + esc(t("favorite"))}</button><button class="button ghost ${isInTonightMenu(recipe.id) ? "tonight-active" : ""}" type="button" data-tonight-toggle="${esc(recipe.id)}">${isInTonightMenu(recipe.id) ? "✓ " + esc(t("removeFromTonight")) : "+ " + esc(t("addToTonight"))}</button>${missing.length ? `<button class="button ghost" type="button" data-add-missing="${esc(recipe.id)}">${esc(t("addToPantry"))}</button>` : ""}</div></div>`;
    const modal = $("#recipeModal");
    const opening = modal.classList.contains("hidden");
    if (opening) modalReturnFocus = document.activeElement instanceof HTMLElement ? document.activeElement : null;
    window.clearTimeout(modalCloseTimer);
    modal.classList.remove("hidden", "is-closing");
    modal.setAttribute("aria-hidden", "false");
    document.body.classList.add("modal-open");
    document.body.style.overflow = "hidden";
    if (opening) window.requestAnimationFrame(() => $("#modalClose").focus({ preventScroll: true }));
    renderHome();
  }

  function closeModal() {
    const modal = $("#recipeModal");
    if (modal.classList.contains("hidden")) return;
    const finish = () => {
      modal.classList.add("hidden");
      modal.classList.remove("is-closing");
      modal.setAttribute("aria-hidden", "true");
      document.body.classList.remove("modal-open");
      document.body.style.overflow = "";
      const returnTarget = modalReturnFocus && modalReturnFocus.isConnected ? modalReturnFocus : $("#mainContent");
      modalReturnFocus = null;
      if (returnTarget) returnTarget.focus({ preventScroll: true });
    };
    if (reduceMotion.matches) { finish(); return; }
    modal.classList.add("is-closing");
    window.clearTimeout(modalCloseTimer);
    modalCloseTimer = window.setTimeout(finish, 190);
  }

  function toggleFavorite(id) {
    state.favorites = isFavorite(id) ? state.favorites.filter(value => value !== id) : state.favorites.concat(id);
    save();
    renderAll();
    if (!$("#recipeModal").classList.contains("hidden")) openRecipe(id);
  }

  function toggleTonightMenu(id) {
    const removing = isInTonightMenu(id);
    state.tonightMenu = removing ? state.tonightMenu.filter(value => value !== id) : state.tonightMenu.concat(id).slice(-24);
    save();
    renderAll();
    toast(t(removing ? "removedFromTonight" : "addedToTonight"));
    if (!$("#recipeModal").classList.contains("hidden")) openRecipe(id);
  }

  function navigate(view, filterMode) {
    activeView = view;
    $$(".view").forEach(node => node.classList.toggle("active", node.id === `view-${view}`));
    $$('[data-nav]').forEach(node => node.classList.toggle("active", node.dataset.nav === view));
    if (filterMode === "makeable") $("#makeableFilter").checked = true;
    if (view === "recipes") renderRecipes();
    if (view === "tonight") renderTonightMenu();
    if (view === "pantry") renderPantry();
    $("#mainContent").focus({ preventScroll: true });
    window.scrollTo({ top: 0, behavior: reduceMotion.matches ? "auto" : "smooth" });
  }

  function randomRecipe(party) {
    const recipes = allRecipes();
    let candidates = party ? recipes.filter(recipe => calc.matchRecipe(recipe, state.pantry).status === "makeable" && !state.partyHistory.includes(recipe.id)) : recipes;
    if (!candidates.length && party) candidates = recipes.filter(recipe => calc.matchRecipe(recipe, state.pantry).status === "makeable");
    if (!candidates.length) candidates = recipes.filter(recipe => !party || !state.partyHistory.includes(recipe.id));
    if (!candidates.length) { state.partyHistory = []; candidates = recipes; }
    const selected = candidates[Math.floor(Math.random() * candidates.length)];
    if (party) { state.partyHistory = [selected.id].concat(state.partyHistory.filter(id => id !== selected.id)).slice(0, 12); save(); }
    toast(t(party && calc.matchRecipe(selected, state.pantry).status === "makeable" ? "partyFromPantry" : "randomFromAll"));
    openRecipe(selected.id);
  }

  function renderStorageStatus() {
    const serialized = JSON.stringify(state);
    const size = `${formatNumber(new Blob([serialized]).size / 1024, 1)} KB`;
    $("#storageStatus").innerHTML = `<strong>${esc(storage.canUseStorage() ? t("storageAvailable") : t("storageUnavailable"))}</strong><span>${esc(t("storageVersion"))}: ${esc(storage.VERSION)}</span><span>${esc(t("storageSize", { size }))}</span><span>${esc(state.savedAt ? t("lastSaved", { time: new Date(state.savedAt).toLocaleString(state.settings.lang === "en" ? "en-US" : "zh-CN") }) : t("neverSaved"))}</span>`;
    $("#settingGlassCapacity").value = state.settings.glassCapacity;
    $("#settingIcedCapacity").value = state.settings.icedLiquidCapacity;
    $("#settingCurrency").value = state.settings.currency;
    $("#settingTheme").value = state.settings.themeMode;
    $("#settingFontScale").value = state.settings.fontScale;
    $("#fontScaleOutput").textContent = `${Math.round(state.settings.fontScale * 100)}%`;
    $$("[data-accent-choice]").forEach(button => button.classList.toggle("active", button.dataset.accentChoice === state.settings.accent));
  }

  function applyAppearance() {
    const requested = state.settings.themeMode;
    const resolved = requested === "system" ? (systemDark.matches ? "dark" : "light") : requested;
    document.documentElement.dataset.theme = resolved;
    document.documentElement.dataset.accent = state.settings.accent;
    document.documentElement.style.setProperty("--font-scale", state.settings.fontScale);
    const meta = document.querySelector('meta[name="theme-color"]');
    if (meta) meta.content = resolved === "dark" ? "#171417" : "#f7f6f2";
  }

  function applyLanguage() {
    i18n.apply(state.settings.lang);
    const spans = $$("#languageToggle span");
    spans[0].classList.toggle("active", state.settings.lang === "zh");
    spans[1].classList.toggle("active", state.settings.lang === "en");
    document.title = state.settings.lang === "zh" ? "朝露酒笺 · Dawn's Dew" : "Dawn's Dew · 朝露酒笺";
  }

  function renderAll() {
    applyAppearance();
    applyLanguage();
    renderHome();
    renderTonightMenu();
    renderRecipes();
    renderPantrySummary();
    renderCustomControls();
    renderCustomList();
    renderStorageStatus();
  }

  function clearFilters() {
    $("#recipeSearch").value = "";
    ["originFilter", "baseFilter", "tasteFilter", "difficultyFilter"].forEach(id => { $(`#${id}`).value = ""; });
    $("#makeableFilter").checked = false;
    $("#favoriteFilter").checked = false;
    renderRecipes();
  }

  document.addEventListener("click", event => {
    if (event.target.closest("button:not([disabled])")) burstParticles(event);
  }, { capture: true });

  document.addEventListener("click", event => {
    const accentChoice = event.target.closest("[data-accent-choice]");
    if (accentChoice) {
      state.settings.accent = accentChoice.dataset.accentChoice;
      save(); applyAppearance(); renderStorageStatus();
      return;
    }
    const searchTrigger = event.target.closest("[data-search-target]");
    if (searchTrigger) { openFloatingSearch(searchTrigger.dataset.searchTarget, searchTrigger); return; }
    const category = event.target.closest("[data-pantry-category]");
    if (category) { activePantryCategory = category.dataset.pantryCategory; renderPantry(); return; }
    const ingredient = event.target.closest("[data-open-ingredient]");
    if (ingredient) { openIngredient(ingredient.dataset.openIngredient); return; }
    const relatedRecipe = event.target.closest("[data-open-related-recipe]");
    if (relatedRecipe) { const id = relatedRecipe.dataset.openRelatedRecipe; closeIngredientModal(false); openRecipe(id); return; }
    const nav = event.target.closest("[data-nav]");
    if (nav) { navigate(nav.dataset.nav, nav.dataset.filterMode); return; }
    const open = event.target.closest("[data-open-recipe]");
    if (open) { openRecipe(open.dataset.openRecipe); return; }
    const favorite = event.target.closest("[data-favorite]");
    if (favorite) { toggleFavorite(favorite.dataset.favorite); return; }
    const tonightToggle = event.target.closest("[data-tonight-toggle]");
    if (tonightToggle) { toggleTonightMenu(tonightToggle.dataset.tonightToggle); return; }
    const removeRow = event.target.closest("[data-remove-row]");
    if (removeRow) { removeRow.closest(".custom-ingredient-row").remove(); return; }
    const deletion = event.target.closest("[data-delete-custom]");
    if (deletion && window.confirm(t("deleteConfirm"))) {
      const id = deletion.dataset.deleteCustom;
      state.customRecipes = state.customRecipes.filter(recipe => recipe.id !== id);
      state.favorites = state.favorites.filter(value => value !== id);
      state.tonightMenu = state.tonightMenu.filter(value => value !== id);
      save(); renderAll(); toast(t("customDeleted")); return;
    }
    const addMissing = event.target.closest("[data-add-missing]");
    if (addMissing) {
      const recipe = recipeById(addMissing.dataset.addMissing);
      calc.matchRecipe(recipe, state.pantry).missing.forEach(id => { pantryEntry(id).owned = true; });
      save(); renderAll(); openRecipe(recipe.id); toast(t("addedToPantry"));
    }
  });

  $("#modalClose").addEventListener("click", closeModal);
  $("#recipeModal").addEventListener("click", event => { if (event.target === $("#recipeModal")) closeModal(); });
  $("#ingredientModalClose").addEventListener("click", () => closeIngredientModal(true));
  $("#ingredientModal").addEventListener("click", event => { if (event.target === $("#ingredientModal")) closeIngredientModal(true); });
  $("#floatingSearchClose").addEventListener("click", closeFloatingSearch);
  $("#floatingSearch").addEventListener("click", event => { if (event.target === $("#floatingSearch")) closeFloatingSearch(); });
  $("#floatingSearchInput").addEventListener("input", event => applyFloatingSearch(event.target.value));
  $("#floatingSearchForm").addEventListener("submit", event => { event.preventDefault(); closeFloatingSearch(); });
  document.addEventListener("keydown", event => {
    const search = $("#floatingSearch");
    const ingredient = $("#ingredientModal");
    const recipe = $("#recipeModal");
    let modal = null;
    let close = null;
    if (!search.classList.contains("hidden")) { modal = search; close = closeFloatingSearch; }
    else if (!ingredient.classList.contains("hidden")) { modal = ingredient; close = () => closeIngredientModal(true); }
    else if (!recipe.classList.contains("hidden")) { modal = recipe; close = closeModal; }
    if (!modal) return;
    if (event.key === "Escape") { event.preventDefault(); close(); return; }
    if (event.key !== "Tab") return;
    const focusable = Array.from(modal.querySelectorAll("button:not([disabled]), [href], input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex='-1'])"))
      .filter(element => element.offsetParent !== null);
    if (!focusable.length) { event.preventDefault(); modal.focus(); return; }
    const first = focusable[0];
    const last = focusable[focusable.length - 1];
    if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); }
    else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); }
  });
  $("#languageToggle").addEventListener("click", () => { state.settings.lang = state.settings.lang === "zh" ? "en" : "zh"; save(); renderPantry(); renderAll(); toast(t("languageChanged")); });
  ["quickRandomButton", "heroRandomButton"].forEach(id => $(`#${id}`).addEventListener("click", () => randomRecipe(false)));
  $("#partyRandomButton").addEventListener("click", () => randomRecipe(true));
  $("#refreshFeatured").addEventListener("click", () => { const recipes = allRecipes().filter(recipe => recipe.id !== featuredId); featuredId = recipes[Math.floor(Math.random() * recipes.length)].id; renderHome(); });
  $("#clearFilters").addEventListener("click", clearFilters);
  $("#webFilterToggle").addEventListener("click", () => {
    const filters = $("#recipeFilters");
    const open = filters.classList.toggle("hidden") === false;
    $("#webFilterToggle").setAttribute("aria-expanded", String(open));
  });
  $("#recipeSearch").addEventListener("input", renderRecipes);
  ["originFilter", "baseFilter", "tasteFilter", "difficultyFilter", "makeableFilter", "favoriteFilter"].forEach(id => $(`#${id}`).addEventListener("change", renderRecipes));
  $("#settingTheme").addEventListener("change", event => {
    state.settings.themeMode = event.target.value;
    save(); applyAppearance(); renderStorageStatus();
  });
  $("#settingFontScale").addEventListener("input", event => {
    const value = Number(event.target.value);
    state.settings.fontScale = value;
    $("#fontScaleOutput").textContent = `${Math.round(value * 100)}%`;
    applyAppearance();
  });
  $("#settingFontScale").addEventListener("change", () => { save(); renderAll(); });
  systemDark.addEventListener("change", () => { if (state.settings.themeMode === "system") { applyAppearance(); renderStorageStatus(); } });
  $("#pantrySearch").addEventListener("input", renderPantry);
  $("#pantryGroups").addEventListener("input", event => {
    const field = event.target.dataset.pantryField;
    const id = event.target.dataset.id;
    if (!field || !id) return;
    pantryEntry(id)[field] = field === "owned" ? event.target.checked : event.target.value;
    save();
  });
  $("#pantryGroups").addEventListener("change", event => {
    if (!event.target.dataset.pantryField) return;
    renderPantry(); renderHome(); renderRecipes(); toast(t("pantrySaved"));
  });
  $("#ingredientModalContent").addEventListener("input", event => {
    const field = event.target.dataset.ingredientField;
    const id = event.target.dataset.id;
    if (!field || !id) return;
    pantryEntry(id)[field] = field === "owned" ? event.target.checked : event.target.value;
    save();
  });
  $("#ingredientModalContent").addEventListener("change", event => {
    const field = event.target.dataset.ingredientField;
    const id = event.target.dataset.id;
    if (!field || !id) return;
    renderPantry(); renderHome(); renderRecipes();
    toast(t("pantrySaved"));
  });
  $("#selectCommonIngredients").addEventListener("click", () => {
    commonIngredients.forEach(id => { pantryEntry(id).owned = true; });
    save(); renderPantry(); renderHome(); renderRecipes(); toast(t("commonSelected"));
  });
  $("#addIngredientRow").addEventListener("click", () => addCustomIngredientRow());
  $("#customIngredientRows").addEventListener("change", event => { if (event.target.dataset.custom === "unit") updateCustomAmountState(event.target.closest(".custom-ingredient-row")); });

  $("#customRecipeForm").addEventListener("submit", event => {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const ingredients = $$("#customIngredientRows .custom-ingredient-row").map(row => {
      const id = row.querySelector('[data-custom="id"]').value;
      const unit = row.querySelector('[data-custom="unit"]').value;
      const amount = Number(row.querySelector('[data-custom="amount"]').value);
      if (!id) return null;
      if (unit === "top") return { id, unit: "top", topUp: true };
      if (unit === "fill") return { id, unit: "fill", fillTo: Math.max(0.1, Math.min(1, amount || 0.8)) };
      return { id, unit, amount: Math.max(0, amount || 0) };
    }).filter(Boolean);
    const zhSteps = String(form.get("stepsZh") || "").split(/\r?\n/).map(value => value.trim()).filter(Boolean);
    const enSteps = String(form.get("stepsEn") || "").split(/\r?\n/).map(value => value.trim()).filter(Boolean);
    if (!ingredients.length || !zhSteps.length || !enSteps.length) { toast(t("requiredFields")); return; }
    const id = `custom-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;
    state.customRecipes.unshift({
      id, rating: "", name: { zh: String(form.get("nameZh")).trim(), en: String(form.get("nameEn")).trim() }, origin: "custom",
      base: [String(form.get("base"))], taste: [String(form.get("taste") || "fresh")], difficulty: String(form.get("difficulty") || "easy"),
      colors: ["#693445", "#d98a54"], glassCapacity: Math.max(30, Number(form.get("glassCapacity")) || state.settings.glassCapacity), method: "build", ingredients,
      steps: { zh: zhSteps, en: enSteps }, description: { zh: "个人自定义配方。", en: "A personal custom recipe." }, createdAt: new Date().toISOString()
    });
    save(); event.currentTarget.reset(); $("#customIngredientRows").innerHTML = ""; addCustomIngredientRow(); renderAll(); toast(t("customSaved"));
  });

  $("#exportButton").addEventListener("click", async () => {
    const payload = JSON.stringify(storage.exportData(state), null, 2);
    const fileName = `dawnsdew-v0.3.3-backup-${new Date().toISOString().slice(0, 10)}.json`;
    try { if (await native.shareJson(fileName, payload)) { toast(t("exportSuccess")); return; } } catch (error) { console.warn("Native export failed, using browser download", error); }
    const blob = new Blob([payload], { type: "application/json;charset=utf-8" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = fileName;
    document.body.appendChild(link); link.click(); link.remove(); URL.revokeObjectURL(link.href); toast(t("exportSuccess"));
  });

  $("#importInput").addEventListener("change", event => {
    const file = event.target.files && event.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      try { state = storage.importData(JSON.parse(reader.result)); renderPantry(); renderAll(); toast(t("importSuccess")); }
      catch (error) { toast(t("importFailed")); }
      event.target.value = "";
    };
    reader.onerror = () => toast(t("importFailed"));
    reader.readAsText(file, "utf-8");
  });

  $("#clearTonightMenu").addEventListener("click", () => {
    if (!state.tonightMenu.length) return;
    state.tonightMenu = [];
    save(); renderAll(); toast(t("tonightCleared"));
  });

  $("#saveSettings").addEventListener("click", () => {
    state.settings.glassCapacity = Math.max(30, Number($("#settingGlassCapacity").value) || 300);
    state.settings.icedLiquidCapacity = Math.max(30, Number($("#settingIcedCapacity").value) || 150);
    state.settings.currency = String($("#settingCurrency").value || "¥").slice(0, 4);
    save(); renderAll(); toast(t("settingsSaved"));
  });

  $("#resetButton").addEventListener("click", () => {
    if (!window.confirm(t("resetConfirm"))) return;
    state = storage.reset(); featuredId = null; clearFilters(); renderPantry(); renderAll(); toast(t("resetSuccess"));
  });

  native.onBackButton(() => {
    if (!$("#floatingSearch").classList.contains("hidden")) { closeFloatingSearch(); return; }
    if (!$("#ingredientModal").classList.contains("hidden")) { closeIngredientModal(true); return; }
    if (!$("#recipeModal").classList.contains("hidden")) { closeModal(); return; }
    if (activeView !== "home") { navigate("home"); return; }
    native.exitApp();
  });

  addCustomIngredientRow();
  initializeParticles();
  renderPantry();
  renderAll();
  if (window.DD_COMPAT) window.DD_COMPAT.markReady();
}());
