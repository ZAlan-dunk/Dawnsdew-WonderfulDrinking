(function () {
  "use strict";

  const data = window.DD_DATA;
  const i18n = window.DD_I18N;
  const storage = window.DD_STORAGE;
  const calc = window.DD_CALC;
  const $ = selector => document.querySelector(selector);
  const $$ = selector => Array.from(document.querySelectorAll(selector));
  const map = data.ingredientMap;
  const tasteKeys = ["sweet", "creamy", "fresh", "citrus", "fruity", "sparkling", "strong", "coconut", "bitter", "herbal", "dry", "spicy", "coffee"];
  const commonIngredients = ["white_rum", "vodka", "brandy", "baileys", "blue_curacao", "cola", "sprite", "soda_water", "orange_juice", "grape_juice", "lemon_juice", "lime_juice"];
  let state = storage.load();
  let featuredId = null;
  let activeView = "home";

  function t(key, values) { return i18n.t(key, state.settings.lang, values); }
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

  function toast(message) {
    const node = document.createElement("div");
    node.className = "toast";
    node.textContent = message;
    $("#toastRegion").appendChild(node);
    window.setTimeout(() => node.remove(), 2600);
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
    return `<article class="recipe-card" data-recipe-card="${esc(recipe.id)}">
      ${renderArt(recipe.colors, "recipe-art")}
      <div class="card-body">
        <div class="card-topline"><span>${esc(originName(recipe.origin))}</span>${rating}</div>
        <button class="recipe-card-button" type="button" data-open-recipe="${esc(recipe.id)}">
          <h3>${esc(langValue(recipe.name))}</h3><p class="english-name">${esc(state.settings.lang === "zh" ? recipe.name.en : recipe.name.zh)}</p>
        </button>
        <div class="meta-row">${tags}${matchBadge(recipe)}</div>
        <div class="card-metrics"><span>${esc(difficultyName(recipe.difficulty))}</span><span>${formatNumber(abv.abv, 1)}% ABV</span></div>
        <button class="favorite-button ${isFavorite(recipe.id) ? "active" : ""}" type="button" data-favorite="${esc(recipe.id)}" aria-label="${esc(favoriteLabel)}">${isFavorite(recipe.id) ? "♥" : "♡"}</button>
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

    const recent = state.recent.map(recipeById).filter(Boolean).slice(0, 4);
    $("#recentRecipes").innerHTML = recent.length ? recent.map(recipe => recipeCard(recipe, true)).join("") : `<div class="empty-state"><span>◇</span><p>${esc(t("noRecent"))}</p></div>`;
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
    $("#recipeResultCount").textContent = t("searchResults", { count: recipes.length });
    $("#recipeGrid").innerHTML = recipes.map(recipe => recipeCard(recipe, false)).join("");
    $("#recipeEmpty").classList.toggle("hidden", recipes.length > 0);
  }

  function pantryEntry(id) {
    const ingredient = map[id];
    if (!state.pantry[id]) state.pantry[id] = { owned: false, stock: "", price: "", packSize: ingredient.pack || "", abv: ingredient.abv || 0 };
    return state.pantry[id];
  }

  function renderPantry() {
    const query = calc.normalizeSearch($("#pantrySearch").value);
    const ingredients = data.ingredients.filter(item => !query || calc.normalizeSearch(`${item.zh} ${item.en}`).includes(query));
    const groups = {};
    ingredients.forEach(item => { (groups[item.category] = groups[item.category] || []).push(item); });
    $("#pantryGroups").innerHTML = Object.keys(data.categories).filter(category => groups[category] && groups[category].length).map(category => {
      const label = data.categories[category][state.settings.lang];
      const rows = groups[category].map(item => {
        const entry = pantryEntry(item.id);
        return `<div class="pantry-row" data-pantry-row="${esc(item.id)}">
          <label class="ingredient-check"><input type="checkbox" data-pantry-field="owned" data-id="${esc(item.id)}" ${entry.owned ? "checked" : ""}><span><b>${esc(item[state.settings.lang])}</b><small>${esc(state.settings.lang === "zh" ? item.en : item.zh)}</small></span></label>
          <label class="pantry-field"><span>${esc(t("pantryStock"))}</span><input type="number" min="0" step="1" data-pantry-field="stock" data-id="${esc(item.id)}" value="${esc(entry.stock)}" aria-label="${esc(t("pantryStock"))}"></label>
          <label class="pantry-field"><span>${esc(t("pantryPrice"))}</span><input type="number" min="0" step="0.01" data-pantry-field="price" data-id="${esc(item.id)}" value="${esc(entry.price)}" aria-label="${esc(t("pantryPrice"))}"></label>
          <label class="pantry-field"><span>${esc(t("pantryPack"))}</span><input type="number" min="0.01" step="1" data-pantry-field="packSize" data-id="${esc(item.id)}" value="${esc(entry.packSize)}" aria-label="${esc(t("pantryPack"))}"></label>
          <label class="pantry-field"><span>${esc(t("pantryAbv"))}</span><input type="number" min="0" max="100" step="0.1" data-pantry-field="abv" data-id="${esc(item.id)}" value="${esc(entry.abv)}" aria-label="${esc(t("pantryAbv"))}"></label>
        </div>`;
      }).join("");
      return `<section class="pantry-group panel"><h2>${esc(label)}</h2><div class="pantry-head"><span>${esc(t("pantryIngredient"))}</span><span>${esc(t("pantryStock"))}</span><span>${esc(t("pantryPrice"))}</span><span>${esc(t("pantryPack"))}</span><span>${esc(t("pantryAbv"))}</span></div>${rows}</section>`;
    }).join("");
    renderPantrySummary();
  }

  function renderPantrySummary() {
    const owned = Object.values(state.pantry).filter(entry => entry && entry.owned).length;
    const makeable = allRecipes().filter(recipe => calc.matchRecipe(recipe, state.pantry).status === "makeable").length;
    $("#pantrySummary").innerHTML = `<div class="summary-tile"><strong>${owned}</strong><span>${esc(t("pantryOwnedCount", { count: owned }))}</span></div><div class="summary-tile"><strong>${makeable}</strong><span>${esc(t("pantryMakeableCount", { count: makeable }))}</span></div>`;
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
    $("#modalContent").innerHTML = `<div class="modal-hero" style="--c1:${esc(colors[0])};--c2:${esc(colors[1])}"><div><div class="meta-row"><span class="tag">${esc(originName(recipe.origin))}</span>${recipe.rating ? `<span class="rating">${esc(recipe.rating)}</span>` : ""}${matchBadge(recipe)}</div><h2 id="modalTitle">${esc(langValue(recipe.name))}</h2><p class="english-name">${esc(state.settings.lang === "zh" ? recipe.name.en : recipe.name.zh)}</p></div><div class="art-glass"></div></div>
      <div class="modal-body"><p class="modal-summary">${esc(langValue(recipe.description))}</p>
      <div class="detail-grid"><div class="metric"><span>${esc(t("estimatedAbv"))}</span><strong>${formatNumber(abv.abv, 1)}%</strong></div><div class="metric"><span>${esc(t("estimatedCost"))}</span><strong>${costLabel}</strong></div><div class="metric"><span>${esc(t("totalVolume"))}</span><strong>${formatNumber(volume.total, 1)} ml</strong></div><div class="metric"><span>${esc(t("method"))}</span><strong>${esc(methodName(recipe.method))}</strong></div></div>
      <div class="detail-section"><h3>${esc(t("recipeIngredients"))}</h3><ul class="ingredient-list">${ingredients}</ul></div>
      <div class="detail-section"><h3>${esc(t("recipeSteps"))}</h3><ol class="step-list">${steps}</ol></div>
      ${missing.length ? `<div class="detail-section"><h3>${esc(t("missingIngredients"))}</h3><p>${esc(missing.join(state.settings.lang === "zh" ? "、" : ", "))}</p></div>` : ""}
      ${recipe.sourceText ? `<div class="original-note"><b>${esc(t("originalFormula"))}</b><br>${esc(recipe.sourceText)}</div>` : ""}
      <p class="warning-note">${esc(t("estimateOnly"))}</p>${volume.exceedsCapacity ? `<p class="warning-note">${esc(t("capacityWarning"))}</p>` : ""}
      <div class="modal-actions"><button class="button primary" type="button" data-favorite="${esc(recipe.id)}">${isFavorite(recipe.id) ? "♥ " + esc(t("unfavorite")) : "♡ " + esc(t("favorite"))}</button>${missing.length ? `<button class="button ghost" type="button" data-add-missing="${esc(recipe.id)}">${esc(t("addToPantry"))}</button>` : ""}</div></div>`;
    $("#recipeModal").classList.remove("hidden");
    document.body.style.overflow = "hidden";
    renderHome();
  }

  function closeModal() {
    $("#recipeModal").classList.add("hidden");
    document.body.style.overflow = "";
  }

  function toggleFavorite(id) {
    state.favorites = isFavorite(id) ? state.favorites.filter(value => value !== id) : state.favorites.concat(id);
    save();
    renderAll();
    if (!$("#recipeModal").classList.contains("hidden")) openRecipe(id);
  }

  function navigate(view, filterMode) {
    activeView = view;
    $$(".view").forEach(node => node.classList.toggle("active", node.id === `view-${view}`));
    $$('[data-nav]').forEach(node => node.classList.toggle("active", node.dataset.nav === view));
    if (filterMode === "makeable") $("#makeableFilter").checked = true;
    if (view === "recipes") renderRecipes();
    if (view === "pantry") renderPantry();
    $("#mainContent").focus({ preventScroll: true });
    window.scrollTo({ top: 0, behavior: "smooth" });
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
  }

  function applyLanguage() {
    i18n.apply(state.settings.lang);
    const spans = $$("#languageToggle span");
    spans[0].classList.toggle("active", state.settings.lang === "zh");
    spans[1].classList.toggle("active", state.settings.lang === "en");
    document.title = state.settings.lang === "zh" ? "破晓微醺 · Dawn's Dew" : "Dawn's Dew · 破晓微醺";
  }

  function renderAll() {
    applyLanguage();
    renderHome();
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
    const nav = event.target.closest("[data-nav]");
    if (nav) { navigate(nav.dataset.nav, nav.dataset.filterMode); return; }
    const open = event.target.closest("[data-open-recipe]");
    if (open) { openRecipe(open.dataset.openRecipe); return; }
    const favorite = event.target.closest("[data-favorite]");
    if (favorite) { toggleFavorite(favorite.dataset.favorite); return; }
    const removeRow = event.target.closest("[data-remove-row]");
    if (removeRow) { removeRow.closest(".custom-ingredient-row").remove(); return; }
    const deletion = event.target.closest("[data-delete-custom]");
    if (deletion && window.confirm(t("deleteConfirm"))) {
      const id = deletion.dataset.deleteCustom;
      state.customRecipes = state.customRecipes.filter(recipe => recipe.id !== id);
      state.favorites = state.favorites.filter(value => value !== id);
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
  document.addEventListener("keydown", event => { if (event.key === "Escape") closeModal(); });
  $("#languageToggle").addEventListener("click", () => { state.settings.lang = state.settings.lang === "zh" ? "en" : "zh"; save(); renderPantry(); renderAll(); toast(t("languageChanged")); });
  ["quickRandomButton", "heroRandomButton"].forEach(id => $(`#${id}`).addEventListener("click", () => randomRecipe(false)));
  $("#partyRandomButton").addEventListener("click", () => randomRecipe(true));
  $("#refreshFeatured").addEventListener("click", () => { const recipes = allRecipes().filter(recipe => recipe.id !== featuredId); featuredId = recipes[Math.floor(Math.random() * recipes.length)].id; renderHome(); });
  $("#clearFilters").addEventListener("click", clearFilters);
  $("#recipeSearch").addEventListener("input", renderRecipes);
  ["originFilter", "baseFilter", "tasteFilter", "difficultyFilter", "makeableFilter", "favoriteFilter"].forEach(id => $(`#${id}`).addEventListener("change", renderRecipes));
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
    renderPantrySummary(); renderHome(); renderRecipes(); toast(t("pantrySaved"));
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

  $("#exportButton").addEventListener("click", () => {
    const payload = JSON.stringify(storage.exportData(state), null, 2);
    const blob = new Blob([payload], { type: "application/json;charset=utf-8" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = `dawnsdew-backup-${new Date().toISOString().slice(0, 10)}.json`;
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

  addCustomIngredientRow();
  renderPantry();
  renderAll();
}());
