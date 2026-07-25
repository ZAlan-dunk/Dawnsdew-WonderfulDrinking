(function () {
  const dictionaries = {
    zh: {
      appName: "破晓微醺", navHome: "首页", navRecipes: "配方库", navPantry: "我的酒柜", navCustom: "自定义", navData: "数据管理",
      localOnly: "所有数据仅保存在本机", heroEyebrow: "HOME MIXOLOGY · LOCAL FIRST", heroTitleA: "在破晓之前，", heroTitleB: "调一杯属于自己的微醺。",
      heroBody: "从便利店材料到经典配方，管理你的酒柜，发现今晚刚好能做的那一杯。", browseRecipes: "浏览配方", randomPick: "随机一杯",
      tonightLabel: "TONIGHT'S POUR", todayRecommend: "今晚推荐", changeOne: "换一杯", quickEntry: "QUICK ENTRY", startFromNeed: "从你的需要开始",
      canMakeNow: "现在能做什么", matchFromPantry: "根据酒柜材料匹配", managePantry: "整理我的酒柜", recordStockPrice: "记录库存与价格",
      partyRandom: "聚会随机推荐", avoidRepeat: "优先可制作且避免重复", createRecipe: "记录我的配方", customAutoSave: "自定义并自动保存", recentViewed: "最近查看",
      recipeLibrary: "配方库", recipeSubtitle: "破晓原创、经典配方与便利店灵感。", searchPlaceholder: "搜索中文名、英文名或材料", clearFilters: "清除筛选",
      filterOrigin: "分类", filterBase: "基酒", filterTaste: "口味", filterDifficulty: "难度", onlyMakeable: "只看可制作", onlyFavorites: "只看收藏", noRecipes: "没有找到合适的配方", tryFilters: "尝试清除筛选或补充酒柜材料。",
      myPantry: "我的酒柜", pantrySubtitle: "勾选现有材料，填写库存、购入价格与包装容量，系统将自动估算可制作配方和单杯成本。", selectCommon: "快速选择常用材料", findMakeable: "查看可制作配方", searchIngredient: "搜索材料",
      customRecipes: "自定义配方", customSubtitle: "记录自己的灵感。保存后会与内置配方一起参与搜索、收藏和材料匹配。", nameZh: "中文名称", nameEn: "英文名称", baseSpirit: "基酒", taste: "口味标签", difficulty: "难度", glassCapacity: "杯容量（ml）", ingredients: "材料", addIngredient: "＋ 添加材料", stepsZh: "中文制作步骤", stepsEn: "英文制作步骤", saveRecipe: "保存自定义配方", savedCustom: "已保存的自定义配方",
      dataManagement: "数据管理", dataSubtitle: "所有数据默认仅保存在当前浏览器。定期导出可避免浏览器清理造成丢失。", exportData: "导出数据", exportDesc: "导出酒柜、收藏、自定义配方、价格与设置。", exportJson: "导出 JSON", importData: "导入数据", importDesc: "导入前会自动备份当前数据，并检查文件版本。", chooseJson: "选择 JSON 文件", preferences: "估算设置", defaultGlass: "默认杯容量", icedCapacity: "满冰可用液体容量", currency: "货币符号", saveSettings: "保存设置", resetData: "重置本地数据", resetDesc: "清空酒柜、收藏、自定义配方和设置，不影响内置配方。", resetAll: "清空全部本地数据",
      all: "全部", allOrigins: "全部分类", allBases: "全部基酒", allTastes: "全部口味", allDifficulties: "全部难度", originPersonal: "个人酒单", originClassic: "经典配方", originConvenience: "便利店灵感", originCustom: "我的自定义",
      easy: "简单", beginner: "入门", advanced: "进阶", tasteSweet: "甜润", tasteCreamy: "奶香", tasteFresh: "清爽", tasteCitrus: "柑橘", tasteFruity: "果香", tasteSparkling: "气泡", tasteStrong: "酒感明显", tasteCoconut: "椰香", tasteBitter: "苦甜", tasteHerbal: "草本", tasteDry: "干爽", tasteSpicy: "辛香", tasteCoffee: "咖啡", tasteBalanced: "平衡",
      statRecipes: "内置配方", statMakeable: "当前可制作", statFavorites: "收藏", statPantry: "酒柜材料", recipeCount: "{count} 杯配方", personalRating: "个人评级 {rating}",
      makeable: "可制作", almost: "差 1 种材料", missingCount: "缺 {count} 种", favorite: "收藏", unfavorite: "取消收藏", viewRecipe: "查看配方", randomFromAll: "已从全部配方中推荐", partyFromPantry: "优先从可制作配方中推荐",
      estimatedAbv: "估算酒精度", estimatedCost: "估算成本", totalVolume: "估算酒液", unknownCost: "尚无价格", estimateOnly: "酒精度与成本仅为估算值；冰块融水、品牌规格和实际倒入量会影响结果。", capacityWarning: "配方固定用量超过当前可用容量，请使用更大杯具或减少冰量。",
      recipeIngredients: "所需材料", recipeSteps: "制作步骤", missingIngredients: "缺少材料", originalFormula: "原始记录", method: "方式", methodBuild: "杯中直调", methodShake: "摇和", methodStir: "搅拌", fullIce: "满冰", noIce: "冰量按需",
      amountMl: "{amount} ml", amountPiece: "{amount} 份", fillRatio: "加至 {ratio} 分满", topUp: "补满", optional: "可选", noRecent: "还没有查看记录", noCustom: "还没有自定义配方",
      pantryOwned: "有", pantryIngredient: "材料", pantryStock: "库存 ml/份", pantryPrice: "购入价", pantryPack: "包装容量", pantryAbv: "酒精度 %", pantryOwnedCount: "已拥有 {count} 种材料", pantryMakeableCount: "可完整制作 {count} 杯", pantrySaved: "酒柜已自动保存", commonSelected: "已选择常用材料",
      amountType: "用量方式", fixedMl: "固定 ml", fixedPiece: "份/个", fillTo: "加至杯量", topToFull: "补满", amount: "用量", ingredient: "材料", remove: "移除", selectIngredient: "请选择材料", selectBase: "请选择基酒", selectTaste: "请选择口味",
      customSaved: "自定义配方已保存", customDeleted: "自定义配方已删除", requiredFields: "请填写名称、步骤，并至少添加一种材料", deleteConfirm: "确定删除这杯自定义配方吗？",
      settingsSaved: "估算设置已保存", exportSuccess: "数据已导出", importSuccess: "数据导入成功", importFailed: "导入失败：文件格式或版本不正确", resetConfirm: "确定清空全部本地数据吗？此操作不会删除内置配方。", resetSuccess: "本地数据已重置",
      storageAvailable: "本地自动保存正常", storageUnavailable: "当前环境无法使用本地存储；刷新页面后数据可能丢失", storageVersion: "数据版本", storageSize: "当前数据约 {size}", lastSaved: "最近保存：{time}", neverSaved: "尚未产生本地修改",
      searchResults: "找到 {count} 杯", languageChanged: "语言已切换", copied: "已复制", close: "关闭", delete: "删除", addToPantry: "加入酒柜", addedToPantry: "已加入酒柜", noneMissing: "材料齐全", unknownIngredient: "未知材料", custom: "自定义"
    },
    en: {
      appName: "Dawn's Dew", navHome: "Home", navRecipes: "Recipes", navPantry: "My Pantry", navCustom: "Custom", navData: "Data",
      localOnly: "All data stays on this device", heroEyebrow: "HOME MIXOLOGY · LOCAL FIRST", heroTitleA: "Before the dawn,", heroTitleB: "mix a gentle buzz of your own.",
      heroBody: "From corner-store ingredients to timeless classics, organize your pantry and discover what you can make tonight.", browseRecipes: "Browse recipes", randomPick: "Pick one",
      tonightLabel: "TONIGHT'S POUR", todayRecommend: "Tonight's pick", changeOne: "Try another", quickEntry: "QUICK ENTRY", startFromNeed: "Start with what you need",
      canMakeNow: "What can I make?", matchFromPantry: "Match from your pantry", managePantry: "Organize my pantry", recordStockPrice: "Track stock and prices",
      partyRandom: "Party randomizer", avoidRepeat: "Prefer makeable drinks and avoid repeats", createRecipe: "Record my recipe", customAutoSave: "Create and save locally", recentViewed: "Recently viewed",
      recipeLibrary: "Recipe library", recipeSubtitle: "Dawn originals, classics and corner-store inspiration.", searchPlaceholder: "Search names or ingredients", clearFilters: "Clear filters",
      filterOrigin: "Origin", filterBase: "Base", filterTaste: "Taste", filterDifficulty: "Difficulty", onlyMakeable: "Makeable only", onlyFavorites: "Favorites only", noRecipes: "No matching recipes", tryFilters: "Clear filters or add ingredients to your pantry.",
      myPantry: "My pantry", pantrySubtitle: "Mark ingredients you own and enter stock, purchase price and package size. The app estimates makeable recipes and per-glass cost.", selectCommon: "Select common ingredients", findMakeable: "Find makeable recipes", searchIngredient: "Search ingredients",
      customRecipes: "Custom recipes", customSubtitle: "Capture your own ideas. Saved recipes join built-ins in search, favorites and pantry matching.", nameZh: "Chinese name", nameEn: "English name", baseSpirit: "Base spirit", taste: "Taste tags", difficulty: "Difficulty", glassCapacity: "Glass capacity (ml)", ingredients: "Ingredients", addIngredient: "+ Add ingredient", stepsZh: "Chinese steps", stepsEn: "English steps", saveRecipe: "Save custom recipe", savedCustom: "Saved custom recipes",
      dataManagement: "Data management", dataSubtitle: "Data is stored only in this browser by default. Export regularly to avoid loss after browser cleanup.", exportData: "Export data", exportDesc: "Export pantry, favorites, custom recipes, prices and settings.", exportJson: "Export JSON", importData: "Import data", importDesc: "Your current data is backed up and the file version is checked before import.", chooseJson: "Choose JSON file", preferences: "Estimate settings", defaultGlass: "Default glass capacity", icedCapacity: "Liquid capacity with full ice", currency: "Currency symbol", saveSettings: "Save settings", resetData: "Reset local data", resetDesc: "Clear pantry, favorites, custom recipes and settings without affecting built-in recipes.", resetAll: "Clear all local data",
      all: "All", allOrigins: "All origins", allBases: "All bases", allTastes: "All tastes", allDifficulties: "All difficulties", originPersonal: "Personal list", originClassic: "Classics", originConvenience: "Corner-store ideas", originCustom: "My custom recipes",
      easy: "Easy", beginner: "Beginner", advanced: "Advanced", tasteSweet: "Sweet", tasteCreamy: "Creamy", tasteFresh: "Fresh", tasteCitrus: "Citrus", tasteFruity: "Fruity", tasteSparkling: "Sparkling", tasteStrong: "Spirit-forward", tasteCoconut: "Coconut", tasteBitter: "Bittersweet", tasteHerbal: "Herbal", tasteDry: "Dry", tasteSpicy: "Spicy", tasteCoffee: "Coffee", tasteBalanced: "Balanced",
      statRecipes: "Built-in recipes", statMakeable: "Makeable now", statFavorites: "Favorites", statPantry: "Pantry items", recipeCount: "{count} recipes", personalRating: "Personal rating {rating}",
      makeable: "Makeable", almost: "Missing 1 ingredient", missingCount: "Missing {count}", favorite: "Favorite", unfavorite: "Remove favorite", viewRecipe: "View recipe", randomFromAll: "Recommended from all recipes", partyFromPantry: "Recommended from your makeable recipes",
      estimatedAbv: "Estimated ABV", estimatedCost: "Estimated cost", totalVolume: "Estimated liquid", unknownCost: "No price data", estimateOnly: "ABV and cost are estimates. Ice dilution, brands and actual pours will change the result.", capacityWarning: "Fixed measures exceed the current liquid capacity. Use a larger glass or less ice.",
      recipeIngredients: "Ingredients", recipeSteps: "Method", missingIngredients: "Missing ingredients", originalFormula: "Original note", method: "Method", methodBuild: "Build in glass", methodShake: "Shake", methodStir: "Stir", fullIce: "Full ice", noIce: "Ice as needed",
      amountMl: "{amount} ml", amountPiece: "{amount} portion(s)", fillRatio: "Fill to {ratio}/10", topUp: "Top up", optional: "Optional", noRecent: "No recently viewed recipes", noCustom: "No custom recipes yet",
      pantryOwned: "Own", pantryIngredient: "Ingredient", pantryStock: "Stock ml/units", pantryPrice: "Purchase price", pantryPack: "Package size", pantryAbv: "ABV %", pantryOwnedCount: "You own {count} ingredients", pantryMakeableCount: "You can make {count} recipes", pantrySaved: "Pantry saved automatically", commonSelected: "Common ingredients selected",
      amountType: "Amount type", fixedMl: "Fixed ml", fixedPiece: "Piece/portion", fillTo: "Fill ratio", topToFull: "Top up", amount: "Amount", ingredient: "Ingredient", remove: "Remove", selectIngredient: "Choose an ingredient", selectBase: "Choose a base", selectTaste: "Choose a taste",
      customSaved: "Custom recipe saved", customDeleted: "Custom recipe deleted", requiredFields: "Enter names and steps, with at least one ingredient", deleteConfirm: "Delete this custom recipe?",
      settingsSaved: "Estimate settings saved", exportSuccess: "Data exported", importSuccess: "Data imported", importFailed: "Import failed: invalid file format or version", resetConfirm: "Clear all local data? Built-in recipes will remain.", resetSuccess: "Local data reset",
      storageAvailable: "Local auto-save is working", storageUnavailable: "Local storage is unavailable; data may be lost after refresh", storageVersion: "Data version", storageSize: "Current data is about {size}", lastSaved: "Last saved: {time}", neverSaved: "No local changes yet",
      searchResults: "{count} recipes found", languageChanged: "Language switched", copied: "Copied", close: "Close", delete: "Delete", addToPantry: "Add to pantry", addedToPantry: "Added to pantry", noneMissing: "All ingredients ready", unknownIngredient: "Unknown ingredient", custom: "Custom"
    }
  };

  const format = (text, values) => String(text).replace(/\{(\w+)\}/g, (_, key) => values && values[key] !== undefined ? values[key] : `{${key}}`);
  window.DD_I18N = {
    dictionaries,
    t(key, lang, values) {
      const language = dictionaries[lang] ? lang : "zh";
      return format(dictionaries[language][key] !== undefined ? dictionaries[language][key] : (dictionaries.zh[key] || key), values);
    },
    apply(lang) {
      document.documentElement.lang = lang === "en" ? "en" : "zh-CN";
      document.querySelectorAll("[data-i18n]").forEach(element => {
        element.textContent = this.t(element.dataset.i18n, lang);
      });
      document.querySelectorAll("[data-i18n-placeholder]").forEach(element => {
        element.placeholder = this.t(element.dataset.i18nPlaceholder, lang);
      });
    }
  };
}());
