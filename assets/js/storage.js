(function () {
  const VERSION = "0.2";
  const KEY = "dawnsdew.v0.2.state";
  const BACKUP_KEY = "dawnsdew.v0.2.backup";
  const LEGACY_KEYS = ["dawnsdew.v0.0.2.state", "dawnsdew.v0.0.1.state"];
  const SUPPORTED_IMPORT_VERSIONS = ["0.0.1", "0.0.2", VERSION];
  const clone = value => JSON.parse(JSON.stringify(value));
  const defaults = {
    version: VERSION,
    savedAt: null,
    settings: { lang: "zh", glassCapacity: 300, icedLiquidCapacity: 150, currency: "¥" },
    pantry: {},
    favorites: [],
    customRecipes: [],
    tonightMenu: [],
    recent: [],
    partyHistory: []
  };

  function canUseStorage() {
    try {
      const testKey = "__dawnsdew_storage_test__";
      localStorage.setItem(testKey, "1");
      localStorage.removeItem(testKey);
      return true;
    } catch (error) {
      return false;
    }
  }

  function uniqueIds(values, limit) {
    const ids = Array.isArray(values) ? [...new Set(values.filter(value => typeof value === "string"))] : [];
    return limit ? ids.slice(0, limit) : ids;
  }

  function normalize(raw) {
    const source = raw && typeof raw === "object" ? raw : {};
    const state = clone(defaults);
    state.version = VERSION;
    state.savedAt = typeof source.savedAt === "string" ? source.savedAt : null;
    state.settings = Object.assign({}, defaults.settings, source.settings || {});
    state.settings.lang = state.settings.lang === "en" ? "en" : "zh";
    state.settings.glassCapacity = Math.max(30, Number(state.settings.glassCapacity) || 300);
    state.settings.icedLiquidCapacity = Math.max(30, Number(state.settings.icedLiquidCapacity) || 150);
    state.settings.currency = String(state.settings.currency || "¥").slice(0, 4);
    state.pantry = source.pantry && typeof source.pantry === "object" && !Array.isArray(source.pantry) ? source.pantry : {};
    state.favorites = uniqueIds(source.favorites);
    state.customRecipes = Array.isArray(source.customRecipes) ? source.customRecipes.filter(recipe => recipe && typeof recipe === "object" && recipe.id && recipe.name) : [];
    state.tonightMenu = uniqueIds(source.tonightMenu, 24);
    state.recent = uniqueIds(source.recent, 8);
    state.partyHistory = uniqueIds(source.partyHistory, 12);
    return state;
  }

  function save(state) {
    const normalized = normalize(state);
    normalized.savedAt = new Date().toISOString();
    if (canUseStorage()) localStorage.setItem(KEY, JSON.stringify(normalized));
    return normalized;
  }

  function load() {
    if (!canUseStorage()) return clone(defaults);
    try {
      const stored = localStorage.getItem(KEY);
      if (stored) return normalize(JSON.parse(stored));
      for (const legacyKey of LEGACY_KEYS) {
        const legacy = localStorage.getItem(legacyKey);
        if (legacy) return save(normalize(JSON.parse(legacy)));
      }
      return clone(defaults);
    } catch (error) {
      return clone(defaults);
    }
  }

  function exportData(state) {
    return {
      app: "Dawn's Dew / 朝露酒笺",
      schemaVersion: VERSION,
      exportedAt: new Date().toISOString(),
      state: normalize(state)
    };
  }

  function validateImport(data) {
    if (!data || typeof data !== "object") return false;
    if (!SUPPORTED_IMPORT_VERSIONS.includes(data.schemaVersion) || !data.state || typeof data.state !== "object") return false;
    const candidate = data.state;
    if (!(candidate.settings && candidate.pantry && Array.isArray(candidate.favorites) && Array.isArray(candidate.customRecipes))) return false;
    return candidate.customRecipes.every(recipe => recipe && typeof recipe.id === "string" && recipe.name && recipe.name.zh && recipe.name.en && Array.isArray(recipe.ingredients) && recipe.steps && Array.isArray(recipe.steps.zh) && Array.isArray(recipe.steps.en));
  }

  function importData(data) {
    if (!validateImport(data)) throw new Error("INVALID_IMPORT");
    if (canUseStorage()) {
      const current = localStorage.getItem(KEY);
      if (current) localStorage.setItem(BACKUP_KEY, current);
    }
    return save(normalize(data.state));
  }

  function reset() {
    if (canUseStorage()) {
      const current = localStorage.getItem(KEY);
      if (current) localStorage.setItem(BACKUP_KEY, current);
      localStorage.removeItem(KEY);
    }
    return clone(defaults);
  }

  window.DD_STORAGE = { VERSION, KEY, BACKUP_KEY, LEGACY_KEYS, defaults: clone(defaults), canUseStorage, normalize, load, save, exportData, validateImport, importData, reset };
}());
