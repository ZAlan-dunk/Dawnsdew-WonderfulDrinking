(function () {
  function ingredientMap() {
    return (window.DD_DATA && window.DD_DATA.ingredientMap) || {};
  }

  function effectiveCapacity(recipe, settings) {
    const regular = Number(recipe.glassCapacity) || Number(settings.glassCapacity) || 300;
    return recipe.ice === "full" ? (Number(settings.icedLiquidCapacity) || 150) : regular;
  }

  function estimateAmounts(recipe, settings) {
    const capacity = effectiveCapacity(recipe, settings || {});
    let total = 0;
    const amounts = (recipe.ingredients || []).map(item => {
      let volume = 0;
      if (item.unit === "ml") volume = Math.max(0, Number(item.amount) || 0);
      else if (item.unit === "fill") volume = Math.max(0, capacity * Math.max(0, Math.min(1, Number(item.fillTo) || 0)) - total);
      else if (item.unit === "top") volume = Math.max(0, capacity - total);
      total += volume;
      return Object.assign({}, item, { estimatedMl: volume });
    });
    return { capacity, total, amounts, exceedsCapacity: total > capacity + 0.01 };
  }

  function estimateAbv(recipe, settings, pantry) {
    const result = estimateAmounts(recipe, settings || {});
    const map = ingredientMap();
    let pureAlcohol = 0;
    result.amounts.forEach(item => {
      const stored = pantry && pantry[item.id] ? pantry[item.id] : {};
      const catalogAbv = map[item.id] ? Number(map[item.id].abv) || 0 : 0;
      const abv = stored.abv !== undefined && stored.abv !== "" ? Number(stored.abv) || 0 : catalogAbv;
      pureAlcohol += item.estimatedMl * abv / 100;
    });
    return Object.assign({}, result, { abv: result.total > 0 ? pureAlcohol / result.total * 100 : 0, pureAlcohol });
  }

  function estimateCost(recipe, settings, pantry) {
    const result = estimateAmounts(recipe, settings || {});
    let cost = 0;
    let pricedVolume = 0;
    let neededVolume = 0;
    result.amounts.forEach(item => {
      if (!item.estimatedMl) return;
      neededVolume += item.estimatedMl;
      const entry = pantry && pantry[item.id] ? pantry[item.id] : {};
      const price = Number(entry.price);
      const packSize = Number(entry.packSize);
      if (price >= 0 && packSize > 0 && entry.price !== "") {
        cost += price / packSize * item.estimatedMl;
        pricedVolume += item.estimatedMl;
      }
    });
    return Object.assign({}, result, { cost, coverage: neededVolume > 0 ? pricedVolume / neededVolume : 0, hasCost: pricedVolume > 0 });
  }

  function isOwned(pantry, id) {
    const entry = pantry && pantry[id];
    if (!entry || !entry.owned) return false;
    if (entry.stock === "" || entry.stock === undefined || entry.stock === null) return true;
    return Number(entry.stock) > 0;
  }

  function requiredIngredientIds(recipe) {
    return [...new Set((recipe.ingredients || [])
      .filter(item => !item.optional && item.id !== "ice")
      .map(item => item.id))];
  }

  function matchRecipe(recipe, pantry) {
    const required = requiredIngredientIds(recipe);
    const missing = required.filter(id => !isOwned(pantry || {}, id));
    const matched = required.length - missing.length;
    return {
      required,
      missing,
      matched,
      ratio: required.length ? matched / required.length : 1,
      status: missing.length === 0 ? "makeable" : (missing.length === 1 ? "almost" : "missing")
    };
  }

  function normalizeSearch(value) {
    return String(value || "")
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .toLowerCase()
      .replace(/[’'·()（）&+]/g, " ")
      .replace(/\s+/g, " ")
      .trim();
  }

  function searchableText(recipe, language) {
    const map = ingredientMap();
    const pieces = [recipe.id, recipe.name && recipe.name.zh, recipe.name && recipe.name.en]
      .concat(recipe.aliases || [])
      .concat((recipe.base || []).map(id => map[id] ? `${map[id].zh} ${map[id].en}` : id))
      .concat((recipe.ingredients || []).map(item => map[item.id] ? `${map[item.id].zh} ${map[item.id].en}` : item.id))
      .concat(recipe.taste || []);
    if (language) pieces.push(recipe.description && recipe.description[language]);
    return normalizeSearch(pieces.filter(Boolean).join(" "));
  }

  window.DD_CALC = { effectiveCapacity, estimateAmounts, estimateAbv, estimateCost, isOwned, requiredIngredientIds, matchRecipe, normalizeSearch, searchableText };
}());
