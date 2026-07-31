package com.zalandunk.dawnsdew.data

enum class BottleShape {
    STANDARD,
    ROUND_SHOULDER,
    SQUARE,
    TALL,
    APOTHECARY,
    CHAMPAGNE,
    BITTERS,
    DECANTER
}

data class IngredientMilestone(
    val year: String,
    val title: LocalizedText,
    val detail: LocalizedText
)

data class IngredientProfile(
    val ingredientId: String,
    val shape: BottleShape,
    val accent: Long,
    val monogram: String,
    val introduction: LocalizedText,
    val origin: LocalizedText,
    val milestones: List<IngredientMilestone>,
    val sourceNote: LocalizedText
)

object IngredientProfiles {
    private fun text(zh: String, en: String) = LocalizedText(zh, en)

    private fun milestone(year: String, zhTitle: String, enTitle: String, zh: String, en: String) =
        IngredientMilestone(year, text(zhTitle, enTitle), text(zh, en))

    private val profiles = listOf(
        IngredientProfile(
            "vodka", BottleShape.TALL, 0xFFB8D3E3, "V",
            text("以干净、中性的酒体承接果汁、气泡与香料，是现代调酒中最灵活的基酒之一。", "A clean, neutral spirit that carries fruit, bubbles and spice with very little interference."),
            text("伏特加的早期脉络横跨波兰、俄罗斯与东欧谷物蒸馏传统，确切起源至今仍有争论。", "Vodka grew from grain-distilling traditions across Poland, Russia and Eastern Europe; its exact birthplace remains disputed."),
            listOf(
                milestone("15–16 世纪", "谷物蒸馏走向日常", "Everyday grain spirit", "东欧蒸馏酒从药用与宫廷场景进入日常饮用。", "Eastern European grain spirits moved beyond medicinal and courtly use."),
                milestone("20 世纪", "成为全球调酒基酒", "A global cocktail base", "连续式蒸馏与过滤塑造了清洁风格，莫斯科骡子等配方推动其流行。", "Column distillation and filtration shaped the clean style, while drinks such as the Moscow Mule broadened its reach.")
            ),
            text("资料线索：东欧蒸馏史与国际伏特加品类资料。", "Reference trail: Eastern European distilling history and international vodka category records.")
        ),
        IngredientProfile(
            "white_rum", BottleShape.STANDARD, 0xFFE8D7A8, "R",
            text("白朗姆保留轻盈甘蔗香，常以过滤或短期熟成获得清澈外观，适合清新长饮。", "White rum keeps a light sugarcane character and is often filtered or briefly rested for a clear, mixable style."),
            text("朗姆源于加勒比甘蔗种植与糖蜜蒸馏传统，随后沿海运贸易传播到世界各地。", "Rum emerged from Caribbean sugarcane and molasses distilling traditions before travelling through maritime trade."),
            listOf(
                milestone("17 世纪", "加勒比朗姆成形", "Caribbean rum takes shape", "糖蜜发酵与蒸馏发展为稳定的烈酒生产。", "Fermented molasses developed into a durable distilled-spirit trade."),
                milestone("19 世纪", "轻盈风格兴起", "The lighter style", "连续式蒸馏、熟成与木炭过滤让白朗姆更适合莫希托和得其利。", "Column stills, resting and charcoal filtration made white rum ideal for Mojitos and Daiquiris.")
            ),
            text("资料线索：加勒比糖业史与朗姆酒生产史。", "Reference trail: Caribbean sugar history and rum production records.")
        ),
        IngredientProfile(
            "tequila", BottleShape.SQUARE, 0xFF9BB37A, "T",
            text("以蓝色龙舌兰为核心，带有烘烤植物、胡椒与青柑橘气息，是玛格丽特的灵魂。", "Built on blue agave, with roasted plant, pepper and green-citrus notes at the heart of a Margarita."),
            text("龙舌兰酒扎根墨西哥哈利斯科及周边法定产区，连接原住民发酵饮品与殖民时期蒸馏技术。", "Tequila is rooted in Jalisco and other protected Mexican regions, linking indigenous agave fermentation with colonial-era distillation."),
            listOf(
                milestone("16–17 世纪", "龙舌兰进入蒸馏时代", "Agave meets distillation", "蒸馏技术与本地龙舌兰发酵传统结合。", "Distillation met established local traditions of fermenting agave."),
                milestone("1974", "原产地名称确立", "Designation protected", "墨西哥确立 Tequila 原产地名称，产区与原料规则逐步标准化。", "Mexico established the Tequila designation of origin, formalising region and raw-material rules.")
            ),
            text("资料线索：墨西哥龙舌兰酒监管与产区历史。", "Reference trail: Mexican tequila regulation and regional history.")
        ),
        IngredientProfile(
            "bourbon", BottleShape.STANDARD, 0xFFC68745, "B",
            text("以玉米为主的美国威士忌，常见焦糖、香草与新橡木桶烘烤香。", "An American corn-led whiskey known for caramel, vanilla and charred new-oak character."),
            text("波本在美国肯塔基及更广泛的早期谷物蒸馏文化中成形，但依法可在全美国生产。", "Bourbon took shape in Kentucky and the wider early American grain-distilling culture, though it may legally be made anywhere in the United States."),
            listOf(
                milestone("18–19 世纪", "玉米威士忌定型", "Corn whiskey develops", "移民蒸馏经验、新橡木桶与美国玉米共同塑造品类。", "Immigrant distilling knowledge, new oak and American corn shaped the category."),
                milestone("1964", "美国独特产品", "A distinctive U.S. product", "美国国会确认波本为美国独特产品，现代身份更加清晰。", "The U.S. Congress recognised bourbon as a distinctive product of the United States.")
            ),
            text("资料线索：美国威士忌法规与肯塔基蒸馏史。", "Reference trail: U.S. whiskey regulation and Kentucky distilling history.")
        ),
        IngredientProfile(
            "whiskey", BottleShape.STANDARD, 0xFFB77943, "W",
            text("跨越苏格兰、爱尔兰、美国与日本等产区，风格从柔和谷物到泥煤烟熏皆有。", "A broad family spanning Scotland, Ireland, the United States, Japan and beyond, from soft grain to peat smoke."),
            text("威士忌源于不列颠群岛的谷物蒸馏传统，数百年间随税制、橡木熟成和工业技术演进。", "Whiskey grew from grain distilling in the British Isles and evolved through taxation, oak maturation and industrial technology."),
            listOf(
                milestone("15 世纪", "早期文字记录", "Early written records", "苏格兰与爱尔兰留下谷物生命之水的早期记录。", "Scotland and Ireland recorded early grain-based aqua vitae."),
                milestone("1830", "连续式蒸馏改变行业", "The column still", "科菲式蒸馏器提高产量，并推动调和威士忌兴起。", "The Coffey still increased output and helped blended whisky flourish.")
            ),
            text("资料线索：苏格兰与爱尔兰威士忌档案。", "Reference trail: Scottish and Irish whiskey archives.")
        ),
        IngredientProfile(
            "gin", BottleShape.APOTHECARY, 0xFF7CAA83, "G",
            text("以杜松子为法定核心，柑橘皮、芫荽籽与花草赋予不同植物层次。", "Juniper leads by definition, while citrus peel, coriander and botanicals build the house style."),
            text("金酒承接荷兰 Genever 传统，在英格兰发展并最终形成 London Dry 等现代风格。", "Gin inherited the Dutch genever tradition, developed in England and later crystallised into styles such as London Dry."),
            listOf(
                milestone("17 世纪", "Genever 影响英国", "Genever reaches Britain", "荷兰杜松蒸馏酒影响英国本土生产。", "Dutch juniper spirit influenced domestic English production."),
                milestone("19 世纪", "干型金酒成熟", "Dry gin matures", "蒸馏技术提升，让更清晰、干爽的植物风格成为经典。", "Improved distillation enabled the clear, dry botanical profile now considered classic.")
            ),
            text("资料线索：Genever 与英国金酒发展史。", "Reference trail: Genever and British gin history.")
        ),
        IngredientProfile(
            "brandy", BottleShape.DECANTER, 0xFFC17549, "B",
            text("由葡萄酒或其他果酒蒸馏而来，熟成后常见果干、香草与温暖橡木气息。", "Distilled from wine or other fruit ferments, often showing dried fruit, vanilla and warm oak after maturation."),
            text("Brandy 一词来自荷兰语 brandewijn，意为“烧过的酒”，与欧洲葡萄酒运输和保存密切相关。", "The word brandy comes from Dutch brandewijn, or 'burnt wine', and is tied to the transport and preservation of European wine."),
            listOf(
                milestone("15–17 世纪", "烧酒用于运输", "Wine distilled for travel", "浓缩葡萄酒便于运输，也逐渐成为独立饮品。", "Concentrating wine aided transport and gradually became a drink in its own right."),
                milestone("20 世纪", "产区规则成熟", "Regional rules mature", "干邑、雅文邑等产区建立细致的原料、蒸馏与熟成规范。", "Regions such as Cognac and Armagnac formalised detailed production and ageing rules.")
            ),
            text("资料线索：欧洲葡萄蒸馏酒与干邑产区档案。", "Reference trail: European grape-spirit and Cognac regional archives.")
        ),
        IngredientProfile(
            "triple_sec", BottleShape.SQUARE, 0xFFE89A45, "3S",
            text("干型橙味力娇酒，以甜橙与苦橙皮提供清晰、明亮的柑橘骨架。", "A dry orange liqueur using sweet and bitter orange peel for a bright citrus backbone."),
            text("Triple sec 在 19 世纪法国橙味力娇酒传统中成形，后来成为边车与玛格丽特的关键材料。", "Triple sec developed in nineteenth-century French orange-liqueur traditions and became central to Sidecars and Margaritas."),
            listOf(
                milestone("19 世纪", "法国橙酒兴起", "French orange liqueur rises", "蒸馏商以橙皮浸渍与再蒸馏制作清澈橙酒。", "Distillers macerated and redistilled orange peel into clear liqueurs."),
                milestone("20 世纪初", "进入经典鸡尾酒", "A cocktail essential", "边车、白色佳人等配方巩固了其调酒地位。", "The Sidecar and White Lady secured its place behind the bar.")
            ),
            text("资料线索：法国橙味力娇酒与经典鸡尾酒史。", "Reference trail: French orange liqueur and classic cocktail history.")
        ),
        IngredientProfile(
            "coffee_liqueur", BottleShape.ROUND_SHOULDER, 0xFF7A4D38, "K",
            text("把咖啡烘焙、可可与焦糖甜感带入酒体，适合奶油型与餐后调酒。", "Brings roasted coffee, cocoa and caramel sweetness to creamy and after-dinner drinks."),
            text("现代咖啡力娇酒与拉丁美洲咖啡、糖和朗姆传统关系密切，20 世纪中叶快速进入国际酒吧。", "Modern coffee liqueur is closely tied to Latin American coffee, sugar and rum traditions and spread internationally in the mid-twentieth century."),
            listOf(
                milestone("1930 年代", "墨西哥咖啡酒商业化", "Mexican coffee liqueur", "咖啡、甘蔗烈酒与甜味结合为稳定商品。", "Coffee, cane spirit and sweetness became a stable commercial style."),
                milestone("20 世纪后半", "餐后调酒流行", "After-dinner cocktails", "黑俄罗斯、白俄罗斯与浓缩咖啡马天尼扩大了品类影响。", "Black Russians, White Russians and Espresso Martinis expanded the category.")
            ),
            text("资料线索：墨西哥咖啡产业与咖啡鸡尾酒史。", "Reference trail: Mexican coffee production and coffee-cocktail history.")
        ),
        IngredientProfile(
            "dry_vermouth", BottleShape.TALL, 0xFFC9C49E, "DV",
            text("以葡萄酒为基底的干型加香酒，草本、白花与轻苦感适合马天尼方向。", "A dry aromatised wine with herbs, white flowers and gentle bitterness for Martini-style drinks."),
            text("现代味美思在意大利都灵与法国尚贝里传统中发展，干型风格与法国尤其相关。", "Modern vermouth developed around Turin and Chambéry, with the dry style particularly associated with France."),
            listOf(
                milestone("18 世纪末", "现代味美思成形", "Modern vermouth emerges", "加香葡萄酒从药用配方走向开胃酒。", "Aromatised wine moved from medicinal recipes to the aperitif table."),
                milestone("19 世纪", "进入马天尼谱系", "The Martini family", "干味美思与金酒组合，形成影响深远的鸡尾酒家族。", "Dry vermouth met gin and created the influential Martini family.")
            ),
            text("资料线索：都灵和尚贝里加香葡萄酒史。", "Reference trail: Turin and Chambéry aromatised-wine history.")
        ),
        IngredientProfile(
            "sweet_vermouth", BottleShape.TALL, 0xFF9B4D54, "SV",
            text("甜型加香葡萄酒带来香料、红果、焦糖与柔和苦味，是曼哈顿和尼格罗尼的支柱。", "A sweet aromatised wine bringing spice, red fruit, caramel and gentle bitterness to Manhattans and Negronis."),
            text("甜味美思与 18 世纪末都灵的商业化加香酒传统关系最深。", "Sweet vermouth is most closely tied to the commercial aromatised-wine tradition of late-eighteenth-century Turin."),
            listOf(
                milestone("1780 年代", "都灵风格商业化", "Turin style commercialised", "草本、葡萄酒与甜味形成可复制的开胃酒风格。", "Herbs, wine and sweetness became a repeatable aperitif style."),
                milestone("19 世纪末", "进入美式经典调酒", "Classic American cocktails", "曼哈顿等配方让甜味美思成为国际酒吧常备。", "The Manhattan and related drinks made sweet vermouth a global bar staple.")
            ),
            text("资料线索：皮埃蒙特味美思与经典鸡尾酒史。", "Reference trail: Piedmont vermouth and classic cocktail history.")
        ),
        IngredientProfile(
            "blue_curacao", BottleShape.TALL, 0xFF398DC5, "BC",
            text("以库拉索橙皮风味为核心，蓝色主要承担视觉表达，味道仍以甜苦橙香为主。", "An orange-peel liqueur whose blue colour is primarily visual; the flavour remains sweet-bitter citrus."),
            text("库拉索酒源于加勒比库拉索岛拉拉哈柑橘皮传统，蓝色版本在现代酒吧文化中扩展。", "Curaçao liqueur grew from laraha orange peel on the Caribbean island of Curaçao; the blue style expanded in modern bar culture."),
            listOf(
                milestone("19 世纪", "库拉索橙酒传播", "Curaçao liqueur spreads", "橙皮浸渍与蒸馏形成具有产地联想的力娇酒。", "Orange-peel maceration and distillation produced a liqueur strongly associated with the island."),
                milestone("20 世纪", "蓝色成为视觉符号", "Blue becomes an icon", "鲜明色彩推动热带与派对型配方流行。", "Vivid colour helped tropical and party cocktails flourish.")
            ),
            text("资料线索：库拉索岛拉拉哈柑橘与荷兰力娇酒史。", "Reference trail: Curaçao laraha citrus and Dutch liqueur history.")
        ),
        IngredientProfile(
            "prosecco", BottleShape.CHAMPAGNE, 0xFFB6A45B, "P",
            text("来自意大利东北部的清新起泡酒，常见青苹果、梨、白花与轻盈气泡。", "A fresh sparkling wine from north-eastern Italy, often showing green apple, pear, white flowers and light bubbles."),
            text("普罗塞克与威尼托、弗留利地区及 Glera 葡萄密切相关，现代风格多借助罐式二次发酵。", "Prosecco is tied to Veneto, Friuli and the Glera grape, with most modern examples using tank secondary fermentation."),
            listOf(
                milestone("19–20 世纪", "罐式起泡法成熟", "Tank method matures", "Martinotti–Charmat 工艺强化了新鲜果香与稳定产量。", "The Martinotti-Charmat method preserved fresh fruit and enabled consistent production."),
                milestone("2009", "DOC 体系重整", "DOC system revised", "产区与名称保护重整，Conegliano Valdobbiadene 等核心产区更受重视。", "Designation rules were reorganised, reinforcing core areas such as Conegliano Valdobbiadene.")
            ),
            text("资料线索：意大利 Prosecco DOC 与产区资料。", "Reference trail: Italian Prosecco DOC and regional records.")
        ),
        IngredientProfile(
            "baileys", BottleShape.ROUND_SHOULDER, 0xFF7C5A4D, "B",
            text("爱尔兰奶油力娇酒把威士忌、奶油、可可与香草融合为柔滑甜润的餐后风格。", "Irish cream liqueur combining whiskey, cream, cocoa and vanilla in a smooth after-dinner style."),
            text("Baileys 于 1970 年代在爱尔兰商业化，圆肩深色瓶与浅色标签成为其鲜明轮廓。", "Baileys was commercialised in Ireland in the 1970s; its rounded dark bottle and pale label created a recognisable silhouette."),
            listOf(
                milestone("1974", "爱尔兰奶油上市", "Irish cream launches", "乳制品技术与爱尔兰威士忌结合，建立全新品类。", "Dairy technology and Irish whiskey combined to establish a new category."),
                milestone("20 世纪末", "进入家庭与甜品场景", "Home and dessert use", "冰饮、咖啡和甜品搭配让奶油力娇酒走出传统酒吧。", "Serving over ice, in coffee and with desserts moved Irish cream beyond the cocktail bar.")
            ),
            text("资料线索：Baileys 品牌公开历史与爱尔兰奶油品类资料。", "Reference trail: Baileys public brand history and Irish cream category records.")
        ),
        IngredientProfile(
            "aperol", BottleShape.TALL, 0xFFF06E3C, "A",
            text("低酒精度橙色开胃酒，带甜橙、草本与轻柔苦味，是 Spritz 的核心。", "A low-ABV orange aperitif with sweet citrus, herbs and gentle bitterness at the centre of the Spritz."),
            text("Aperol 由 Barbieri 兄弟在意大利帕多瓦创制，高直透明瓶强化了明亮橙色酒液。", "Aperol was created by the Barbieri brothers in Padua, Italy; the tall clear bottle showcases its vivid orange liquid."),
            listOf(
                milestone("1919", "帕多瓦首次亮相", "Debut in Padua", "经过多年配方实验，Aperol 在帕多瓦展会上推出。", "After years of development, Aperol debuted at a Padua trade fair."),
                milestone("21 世纪", "Spritz 全球流行", "The global Spritz", "起泡酒、苏打水与 Aperol 的组合成为现代开胃酒象征。", "Prosecco, soda and Aperol became a modern aperitivo symbol.")
            ),
            text("资料线索：Aperol 品牌公开历史与意大利开胃酒文化。", "Reference trail: Aperol public brand history and Italian aperitivo culture.")
        ),
        IngredientProfile(
            "campari", BottleShape.TALL, 0xFFC6383D, "C",
            text("鲜红色意大利苦味开胃酒，以苦橙、草本与香料构成尼格罗尼的力量核心。", "A vivid red Italian bitter aperitif whose orange, herb and spice profile anchors the Negroni."),
            text("Gaspare Campari 于 19 世纪在意大利诺瓦拉与米兰发展配方，后来成为米兰酒吧文化标志。", "Gaspare Campari developed the recipe in nineteenth-century Novara and Milan, where it became a symbol of bar culture."),
            listOf(
                milestone("1860", "Campari 配方成形", "The recipe takes shape", "Gaspare Campari 推出具有鲜明苦甜风格的开胃酒。", "Gaspare Campari introduced the distinct bittersweet aperitif."),
                milestone("1915", "Camparino 开业", "Camparino opens", "米兰大教堂广场的 Camparino 将品牌与城市生活紧密连接。", "Camparino in Milan's Piazza Duomo tied the brand closely to city life.")
            ),
            text("资料线索：Campari 品牌公开档案与米兰开胃酒史。", "Reference trail: Campari public archives and Milanese aperitivo history.")
        ),
        IngredientProfile(
            "jagermeister", BottleShape.SQUARE, 0xFF315E47, "J",
            text("德国草本力娇酒，以深色香草、甘草、柑橘与香料呈现浓郁苦甜层次。", "A German herbal liqueur with dark herbs, liquorice, citrus and spice in a concentrated bittersweet profile."),
            text("Jägermeister 于 1930 年代在德国下萨克森推出，厚重方瓶源于耐用运输与药剂瓶般的秩序感。", "Jägermeister launched in Lower Saxony in the 1930s; its heavy square bottle reflects durability and an apothecary-like order."),
            listOf(
                milestone("1935", "德国市场推出", "German launch", "Curt Mast 将长期草本实验发展为商业产品。", "Curt Mast turned years of herbal experimentation into a commercial product."),
                milestone("20 世纪后半", "从餐后酒到夜生活", "From digestif to nightlife", "冰镇饮用与混饮让其进入更年轻的音乐和夜生活场景。", "Chilled serves and mixed drinks brought it into music and nightlife culture.")
            ),
            text("资料线索：Jägermeister 品牌公开历史与德国草本酒资料。", "Reference trail: Jägermeister public history and German herbal-liqueur records.")
        ),
        IngredientProfile(
            "angostura", BottleShape.BITTERS, 0xFFA24B43, "A",
            text("高浓度芳香苦精，只需数滴即可带来肉桂、丁香、树皮与烘烤香料的结构。", "A concentrated aromatic bitter: a few dashes add cinnamon, clove, bark and toasted-spice structure."),
            text("Johann Gottlieb Benjamin Siegert 医生于 19 世纪在委内瑞拉安戈斯图拉城研制配方，品牌后来迁至特立尼达。", "Dr Johann Gottlieb Benjamin Siegert developed the formula in nineteenth-century Angostura, Venezuela; the company later moved to Trinidad."),
            listOf(
                milestone("1824", "芳香苦精诞生", "Aromatic bitters created", "Siegert 医生为当时的航海与医疗环境调制浓缩草本苦精。", "Dr Siegert created a concentrated herbal bitter in a maritime and medicinal setting."),
                milestone("1875", "迁往特立尼达", "Move to Trinidad", "生产基地迁移，超大纸标签逐渐成为独特瓶身识别。", "Production moved to Trinidad and the oversized paper label became a defining bottle cue.")
            ),
            text("资料线索：House of Angostura 公开历史。", "Reference trail: House of Angostura public history.")
        )
    ).associateBy { it.ingredientId }

    fun find(id: String): IngredientProfile? = profiles[id]

    fun forIngredient(ingredient: Ingredient): IngredientProfile = profiles[ingredient.id] ?: fallback(ingredient)

    private fun fallback(ingredient: Ingredient): IngredientProfile {
        val (zhIntro, enIntro, zhOrigin, enOrigin) = when (ingredient.category) {
            "juice" -> listOf("果汁提供酸甜、香气与颜色，是平衡酒精感最直接的材料。", "Juice contributes acidity, sweetness, aroma and colour, directly balancing spirit strength.", "榨汁、保存与商业包装的发展，让稳定果味进入家庭调酒。", "Juicing, preservation and commercial packaging made consistent fruit flavour available to home mixing.")
            "soda" -> listOf("气泡饮料拉长酒体、带来清爽触感，也能突出柑橘与草本香。", "Carbonated mixers lengthen a drink, add lift and reveal citrus or herbal notes.", "人工碳酸化在 18 世纪后发展，瓶装汽水随后成为高球文化的重要基础。", "Artificial carbonation developed after the eighteenth century and bottled soda became central to highball culture.")
            "dairy" -> listOf("乳制品与植物奶带来柔滑质地，适合甜口、咖啡与餐后方向。", "Dairy and plant milks create a soft texture suited to sweet, coffee and after-dinner drinks.", "奶油调酒与甜品饮品长期交织，现代冷链让这类材料更易在家庭使用。", "Cream drinks have long overlapped with desserts; modern refrigeration made them easier to use at home.")
            "fruit" -> listOf("新鲜水果与香草主要提供香气、酸度和杯面装饰。", "Fresh fruit and herbs contribute aroma, acidity and garnish.", "从早期宾治到现代鸡尾酒，时令果实一直连接饮品与地域风味。", "From early punches to modern cocktails, seasonal produce has always connected drinks with place.")
            "tea" -> listOf("茶能带来单宁、花香与烘焙层次，是低负担长饮的良好骨架。", "Tea adds tannin, floral aroma and roasted depth, making a versatile base for lighter long drinks.", "茶与酒的混合从宾治时代延续至现代茶调酒。", "Tea and alcohol have been mixed since the punch era and continue in modern tea cocktails.")
            else -> listOf("这项材料用于调整甜度、质地、香气或稀释度，让配方更完整。", "This ingredient adjusts sweetness, texture, aroma or dilution to complete a recipe.", "辅助材料随家庭厨房、汽水工业与现代酒吧技术共同演变。", "Supporting ingredients evolved alongside home kitchens, the soda industry and modern bar technique.")
        }
        return IngredientProfile(
            ingredient.id,
            BottleShape.APOTHECARY,
            0xFF7A8F88,
            ingredient.name.en.take(2).uppercase(),
            text(zhIntro, enIntro),
            text(zhOrigin, enOrigin),
            listOf(
                milestone("早期", "进入饮品传统", "Part of drink tradition", zhOrigin, enOrigin),
                milestone("现代", "成为家庭常备", "A home-bar staple", "标准化包装与冷藏保存让它更容易用于日常配方。", "Standard packaging and refrigeration made it practical for everyday recipes.")
            ),
            text("资料线索：通用饮品史与材料生产资料。", "Reference trail: general beverage history and ingredient production records.")
        )
    }
}
