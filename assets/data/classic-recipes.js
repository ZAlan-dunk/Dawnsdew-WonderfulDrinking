(function () {
  const recipe = (id, origin, zh, en, base, taste, difficulty, colors, ingredients, zhSteps, enSteps, descriptionZh, descriptionEn, extra = {}) => ({
    id, rating: "", name: { zh, en }, origin, base, taste, difficulty, colors,
    glassCapacity: extra.glassCapacity || 300, method: extra.method || "build", ingredients,
    steps: { zh: zhSteps, en: enSteps }, description: { zh: descriptionZh, en: descriptionEn }, ...extra
  });
  const ml = (id, amount, optional = false) => ({ id, amount, unit: "ml", optional });
  const piece = (id, amount, optional = false) => ({ id, amount, unit: "piece", optional });
  const top = id => ({ id, topUp: true, unit: "top" });

  window.DD_DATA.classicRecipes = [
    recipe("classic-mojito", "classic", "莫吉托", "Mojito", ["white_rum"], ["citrus", "fresh", "sparkling"], "beginner", ["#466a42", "#b6ce78"],
      [ml("white_rum", 45), ml("lime_juice", 20), ml("simple_syrup", 15), piece("mint", 8), top("soda_water")],
      ["轻拍薄荷后放入杯中，加入青柠汁与糖浆。", "加入白朗姆和碎冰，搅匀后用气泡水补满。"],
      ["Gently clap the mint and add it with lime juice and syrup.", "Add white rum and crushed ice, stir, then top with soda water."],
      "薄荷、青柠与朗姆构成清爽明亮的经典长饮。", "Mint, lime and rum form a bright, refreshing classic long drink.", { ice: "full" }),

    recipe("classic-daiquiri", "classic", "得其利", "Daiquiri", ["white_rum"], ["citrus", "fresh", "strong"], "beginner", ["#d7c46d", "#eff0bd"],
      [ml("white_rum", 60), ml("lime_juice", 25), ml("simple_syrup", 15)],
      ["所有材料加入装冰的摇酒壶。", "充分摇匀后滤入冰镇杯中。"],
      ["Add all ingredients to an ice-filled shaker.", "Shake well and strain into a chilled glass."],
      "结构简洁，却能清楚展现朗姆、酸度与甜度的平衡。", "A concise balance of rum, acidity and sweetness.", { method: "shake" }),

    recipe("classic-margarita", "classic", "玛格丽特", "Margarita", ["tequila", "triple_sec"], ["citrus", "strong"], "beginner", ["#b9a94c", "#e7dd91"],
      [ml("tequila", 50), ml("triple_sec", 25), ml("lime_juice", 25), piece("salt", 1, true)],
      ["可选：用青柠湿润杯口并蘸盐。", "其余材料加冰摇匀，滤入杯中。"],
      ["Optional: moisten the rim with lime and coat it with salt.", "Shake the remaining ingredients with ice and strain."],
      "龙舌兰、橙香与青柠组成鲜明有力的酸甜骨架。", "Tequila, orange and lime create a vivid, assertive sour profile.", { method: "shake" }),

    recipe("classic-whiskey-sour", "classic", "威士忌酸", "Whiskey Sour", ["whiskey"], ["citrus", "sweet", "strong"], "beginner", ["#a85f2a", "#e8c57c"],
      [ml("whiskey", 60), ml("lemon_juice", 30), ml("simple_syrup", 20), ml("egg_white", 20, true)],
      ["可选蛋清与其余材料先无冰摇匀。", "加入冰块再次摇匀，滤入装冰杯中。"],
      ["If using egg white, dry-shake it with the other ingredients first.", "Add ice, shake again, and strain over fresh ice."],
      "酸甜柔化威士忌棱角，可选蛋清带来绵密口感。", "Sweet and sour soften the whiskey; optional egg white adds a silky texture.", { method: "shake" }),

    recipe("classic-old-fashioned", "classic", "古典", "Old Fashioned", ["bourbon"], ["strong", "bitter", "sweet"], "beginner", ["#6d321b", "#d38a3f"],
      [ml("bourbon", 60), piece("sugar_cube", 1), piece("angostura", 3), piece("orange_slice", 1, true)],
      ["杯中放入方糖和苦精，压碎混合。", "加入大冰块与波本，缓慢搅拌，可用橙片装饰。"],
      ["Muddle the sugar cube with bitters in the glass.", "Add a large ice cube and bourbon, stir slowly, and optionally garnish with orange."],
      "用糖与苦精轻微修饰威士忌本身，是经典短饮。", "Sugar and bitters lightly frame the whiskey in this classic short drink.", { method: "stir" }),

    recipe("classic-negroni", "classic", "尼格罗尼", "Negroni", ["gin", "campari", "sweet_vermouth"], ["bitter", "strong", "herbal"], "beginner", ["#8b1f1f", "#df5b34"],
      [ml("gin", 30), ml("campari", 30), ml("sweet_vermouth", 30), piece("orange_slice", 1, true)],
      ["所有酒液加入装冰的杯中。", "充分搅拌，可用橙片装饰。"],
      ["Add all liquid ingredients to an ice-filled glass.", "Stir well and optionally garnish with orange."],
      "等比例带来浓郁苦甜、草本和柑橘层次。", "Equal parts deliver a bold bittersweet, herbal and citrus profile.", { method: "stir" }),

    recipe("classic-gin-tonic", "classic", "金汤力", "Gin & Tonic", ["gin"], ["bitter", "fresh", "sparkling"], "easy", ["#48665f", "#b8d2c8"],
      [ml("gin", 50), ml("tonic_water", 120), piece("lime", 1, true)],
      ["杯中装满冰块并加入金酒。", "沿杯壁加入汤力水，轻搅，可用青柠装饰。"],
      ["Fill the glass with ice and add gin.", "Pour in tonic water, stir gently, and optionally garnish with lime."],
      "清爽、微苦且香气突出，是容易上手的经典高球。", "Crisp, lightly bitter and aromatic—an approachable classic highball.", { ice: "full" }),

    recipe("classic-dry-martini", "classic", "干马天尼", "Dry Martini", ["gin", "dry_vermouth"], ["dry", "strong", "herbal"], "advanced", ["#a8aa91", "#e7e8cf"],
      [ml("gin", 60), ml("dry_vermouth", 10), piece("olive", 1, true)],
      ["金酒与干味美思加冰搅拌至充分冰镇。", "滤入冰镇杯中，可用橄榄装饰。"],
      ["Stir gin and dry vermouth with ice until thoroughly chilled.", "Strain into a chilled glass and optionally garnish with an olive."],
      "干爽、利落，以冰镇和稀释控制决定完成度。", "Dry and precise, with chilling and dilution defining the result.", { method: "stir" }),

    recipe("classic-moscow-mule", "classic", "莫斯科骡子", "Moscow Mule", ["vodka"], ["citrus", "spicy", "sparkling"], "easy", ["#806148", "#d19b61"],
      [ml("vodka", 45), ml("lime_juice", 15), ml("ginger_beer", 120)],
      ["杯中装冰，加入伏特加与青柠汁。", "加入姜汁啤酒并轻轻搅匀。"],
      ["Fill the glass with ice and add vodka and lime juice.", "Add ginger beer and stir gently."],
      "青柠的酸与姜的辛香让伏特加高球更有轮廓。", "Lime acidity and ginger spice give this vodka highball a clear edge.", { ice: "full" }),

    recipe("classic-tequila-sunrise", "classic", "龙舌兰日出", "Tequila Sunrise", ["tequila"], ["fruity", "sweet"], "easy", ["#d64c32", "#f5b24a"],
      [ml("tequila", 45), ml("orange_juice", 90), ml("simple_syrup", 10)],
      ["杯中加冰，加入龙舌兰与橙汁。", "沿杯壁缓慢加入糖浆，保留自然渐层。"],
      ["Add ice, tequila and orange juice to the glass.", "Slowly pour syrup down the side to preserve a sunrise gradient."],
      "橙汁与渐层色彩带来直观的日出意象。", "Orange juice and a natural gradient create an immediate sunrise image.", { ice: "full" }),

    recipe("classic-pina-colada", "classic", "椰林飘香", "Piña Colada", ["white_rum"], ["sweet", "creamy", "coconut"], "beginner", ["#d8b74f", "#f4e5a5"],
      [ml("white_rum", 50), ml("pineapple_juice", 90), ml("coconut_milk", 40)],
      ["所有材料与冰块加入摇酒壶或搅拌机。", "摇匀或搅打后倒入杯中。"],
      ["Add all ingredients and ice to a shaker or blender.", "Shake or blend, then pour into the glass."],
      "菠萝与椰香浓郁柔滑，带有明确的热带气息。", "Pineapple and coconut create a rich, smooth tropical profile.", { method: "shake" }),

    recipe("classic-cosmopolitan", "classic", "大都会", "Cosmopolitan", ["vodka", "triple_sec"], ["citrus", "fruity", "strong"], "beginner", ["#a42d4f", "#e87991"],
      [ml("vodka", 40), ml("triple_sec", 20), ml("cranberry_juice", 30), ml("lime_juice", 10)],
      ["所有材料加冰充分摇匀。", "滤入冰镇杯中。"],
      ["Shake all ingredients thoroughly with ice.", "Strain into a chilled glass."],
      "蔓越莓色泽明亮，酸甜与橙香包裹伏特加。", "Bright cranberry color, tart fruit and orange wrap around vodka.", { method: "shake" }),

    recipe("classic-white-russian", "classic", "白俄罗斯", "White Russian", ["vodka", "coffee_liqueur"], ["sweet", "creamy", "coffee"], "easy", ["#4b342b", "#ded2bd"],
      [ml("vodka", 40), ml("coffee_liqueur", 20), ml("cream", 30)],
      ["杯中加冰，加入伏特加与咖啡力娇酒。", "缓慢加入淡奶油，饮用前按喜好搅匀。"],
      ["Add ice, vodka and coffee liqueur to the glass.", "Float in cream and stir to your preference before drinking."],
      "咖啡、奶油与伏特加构成浓郁顺滑的餐后风格。", "Coffee, cream and vodka make a rich, smooth after-dinner drink.", { ice: "full" }),

    recipe("classic-manhattan", "classic", "曼哈顿", "Manhattan", ["whiskey", "sweet_vermouth"], ["strong", "herbal", "sweet"], "advanced", ["#6d2920", "#b75838"],
      [ml("whiskey", 60), ml("sweet_vermouth", 30), piece("angostura", 2)],
      ["所有材料加冰搅拌至冰镇。", "滤入冰镇杯中。"],
      ["Stir all ingredients with ice until chilled.", "Strain into a chilled glass."],
      "威士忌与甜味美思形成厚实、香料感明显的经典结构。", "Whiskey and sweet vermouth form a robust, spice-led classic.", { method: "stir" }),

    recipe("classic-sidecar", "classic", "边车", "Sidecar", ["brandy", "triple_sec"], ["citrus", "strong"], "beginner", ["#a6552b", "#e6aa5e"],
      [ml("brandy", 50), ml("triple_sec", 25), ml("lemon_juice", 25)],
      ["所有材料加冰充分摇匀。", "滤入冰镇杯中。"],
      ["Shake all ingredients thoroughly with ice.", "Strain into a chilled glass."],
      "白兰地的圆润与橙香、柠檬酸度形成清晰对比。", "Rounded brandy contrasts cleanly with orange and lemon acidity.", { method: "shake" }),

    recipe("corner-store-highball", "convenience", "月下威士忌高球", "Moonlit Whiskey Highball", ["whiskey"], ["fresh", "sparkling"], "easy", ["#4c3526", "#c69051"],
      [ml("whiskey", 40), top("soda_water"), piece("lemon", 1, true)],
      ["杯中装满冰块，加入威士忌。", "用冰镇气泡水补满，轻搅，可挤入少量柠檬。"],
      ["Fill the glass with ice and add whiskey.", "Top with chilled soda water, stir gently, and optionally squeeze in lemon."],
      "只需常见气泡水即可完成的清爽威士忌长饮。", "A refreshing whiskey long drink made with widely available soda water.", { ice: "full" }),

    recipe("apple-vodka-fizz", "convenience", "青苹果伏特加气泡", "Green Apple Spark", ["vodka"], ["fruity", "sweet", "sparkling"], "easy", ["#4f8a43", "#bdda71"],
      [ml("vodka", 30), ml("green_apple_fanta", 120), ml("lemon_juice", 10, true)],
      ["杯中装冰并加入伏特加。", "加入青苹果美年达，可选少量柠檬汁，轻搅。"],
      ["Add ice and vodka to the glass.", "Pour in green apple soda, optionally add lemon juice, and stir gently."],
      "青苹果汽水让伏特加变得直观、轻松，适合聚会快速制作。", "Green apple soda makes vodka bright, simple and party-friendly.", { ice: "full" }),

    recipe("baileys-milk-tea", "convenience", "百利甜奶茶", "Velvet Tea at Dusk", ["baileys"], ["sweet", "creamy"], "easy", ["#5a3a35", "#bc8d69"],
      [ml("baileys", 30), ml("assam_jasmine_milk_tea", 100)],
      ["杯中加入冰块和百利甜。", "倒入奶茶并轻轻搅匀。"],
      ["Add ice and Baileys to the glass.", "Pour in milk tea and stir gently."],
      "奶茶与百利甜交叠出柔滑甜香，像灯影落进一杯温柔的夜。", "Milk tea and Baileys meet in a smooth, gentle serve made for a quiet night.", { ice: "full" }),

    recipe("grapefruit-rum-fizz", "convenience", "西柚朗姆气泡", "Grapefruit Dawn Fizz", ["white_rum"], ["citrus", "fresh", "sparkling"], "easy", ["#d26b51", "#f2b28d"],
      [ml("white_rum", 30), ml("waterc_grapefruit", 90), top("soda_water")],
      ["杯中加满冰块并加入白朗姆。", "加入西柚饮料，用气泡水补满。"],
      ["Fill the glass with ice and add white rum.", "Add grapefruit drink and top with soda water."],
      "西柚的酸苦与轻盈气泡适合家庭随手调制。", "Grapefruit's tart bitterness and light bubbles suit an easy home mix.", { ice: "full" }),

    recipe("coconut-rum-cooler", "convenience", "椰子朗姆酷饮", "Coconut Moon Cooler", ["white_rum"], ["coconut", "fresh", "sparkling"], "easy", ["#567d86", "#ded6b3"],
      [ml("white_rum", 35), ml("coconut_water", 90), top("sprite"), ml("lime_juice", 10, true)],
      ["杯中装冰，加入白朗姆和椰子水。", "用雪碧补满，可选加入青柠汁，轻搅。"],
      ["Add ice, white rum and coconut water to the glass.", "Top with Sprite, optionally add lime juice, and stir gently."],
      "椰子水让朗姆更清透，少量汽水提供轻快甜感。", "Coconut water keeps rum light while a little soda adds an easy sweetness.", { ice: "full" })
  ];
}());
