package com.zalandunk.dawnsdew.data

data class BrandProfile(
    val ingredientId: String,
    val brand: String,
    val product: String,
    val zhFlavor: String,
    val enFlavor: String,
    val tone: String
) {
    fun flavor(language: String): String = if (language == "en") enFlavor else zhFlavor
}

object BrandProfiles {
    private val profiles = listOf(
        BrandProfile("vodka", "Absolut", "原味伏特加", "干净、中性，适合果汁和气泡调酒", "Clean and neutral for juices and spritzes", "clear"),
        BrandProfile("vodka", "Smirnoff", "No.21", "轻盈直接，适合日常长饮", "Light and direct for everyday highballs", "silver"),
        BrandProfile("vodka", "Finlandia", "Classic", "干爽，尾段利落", "Dry with a crisp finish", "clear"),
        BrandProfile("white_rum", "Bacardi", "Carta Blanca", "清爽甘蔗香，适合莫希托和自由古巴", "Light sugarcane notes for Mojitos and Cuba Libres", "silver"),
        BrandProfile("white_rum", "Havana Club", "3 Años", "更有甘蔗和橡木层次", "More sugarcane and light oak depth", "gold"),
        BrandProfile("white_rum", "Planteray", "3 Stars", "干净但带一点热带果香", "Clean with a touch of tropical fruit", "gold"),
        BrandProfile("tequila", "Olmeca", "Blanco", "明亮龙舌兰与青柑橘气息", "Bright agave with green citrus", "silver"),
        BrandProfile("tequila", "Espolòn", "Blanco", "植物感明显，收口清爽", "Botanical and fresh on the finish", "silver"),
        BrandProfile("tequila", "José Cuervo", "Especial Silver", "直接、易搭配，适合入门", "Approachable and easy to mix", "silver"),
        BrandProfile("bourbon", "Jim Beam", "White Label", "焦糖、香草和烘烤橡木", "Caramel, vanilla and toasted oak", "amber"),
        BrandProfile("whiskey", "Jim Beam", "White Label", "偏焦糖的波本风格", "Caramel-led bourbon style", "amber"),
        BrandProfile("whiskey", "Jameson", "Irish Whiskey", "柔和、谷物感轻，适合长饮", "Soft and grain-forward for highballs", "gold"),
        BrandProfile("whiskey", "Johnnie Walker", "Black Label", "烟熏、果干和橡木", "Smoke, dried fruit and oak", "dark"),
        BrandProfile("gin", "Beefeater", "London Dry", "杜松子清晰，适合经典配方", "Clear juniper for classic recipes", "clear"),
        BrandProfile("gin", "Tanqueray", "London Dry", "植物感更强，酒体饱满", "More botanical and full-bodied", "green"),
        BrandProfile("gin", "Bombay Sapphire", "London Dry", "柑橘与柔和香料感", "Citrus and gentle spice", "blue"),
        BrandProfile("brandy", "St-Rémy", "VSOP", "成熟果香和温和橡木", "Ripe fruit and gentle oak", "amber"),
        BrandProfile("brandy", "Martell", "VS", "果香明亮，适合酸甜型调酒", "Bright fruit for sweet-sour drinks", "gold"),
        BrandProfile("triple_sec", "Cointreau", "L'Unique", "干净的橙皮香和清晰收口", "Clean orange peel and a clear finish", "orange"),
        BrandProfile("coffee_liqueur", "Kahlúa", "Coffee Liqueur", "咖啡、焦糖和可可", "Coffee, caramel and cocoa", "coffee"),
        BrandProfile("dry_vermouth", "Martini", "Extra Dry", "草本、干爽，适合马天尼方向", "Herbal and dry for Martini-style drinks", "silver"),
        BrandProfile("sweet_vermouth", "Cinzano", "Rosso", "甜香料和红果气息", "Sweet spice and red fruit", "red"),
        BrandProfile("blue_curacao", "Bols", "Blue Curaçao", "橙皮香明显，颜色稳定", "Pronounced orange peel with a vivid color", "blue"),
        BrandProfile("prosecco", "Mionetto", "Prosecco Brut", "青苹果、白花和细致气泡", "Green apple, white flowers and fine bubbles", "gold"),
        BrandProfile("baileys", "Baileys", "Original Irish Cream", "奶油、可可和咖啡", "Cream, cocoa and coffee", "cream"),
        BrandProfile("aperol", "Aperol", "Aperitivo", "轻苦、橙皮和草本", "Lightly bitter with orange and herbs", "orange"),
        BrandProfile("campari", "Campari", "Bitter Aperitivo", "苦橙、草本和较强苦味", "Bitter orange, herbs and a firm bitterness", "red"),
        BrandProfile("jagermeister", "Jägermeister", "Herbal Liqueur", "深色草本、香料和焦糖", "Dark herbs, spice and caramel", "dark"),
        BrandProfile("angostura", "Angostura", "Aromatic Bitters", "香料、树皮和烘烤感", "Spice, bark and toasted notes", "red")
    )

    private val byIngredient = profiles.groupBy { it.ingredientId }

    fun suggestions(recipe: Recipe): List<BrandProfile> = recipe.base
        .asSequence()
        .flatMap { byIngredient[it].orEmpty().asSequence() }
        .distinctBy { it.brand + it.product }
        .take(3)
        .toList()

    fun primary(recipe: Recipe): BrandProfile? = suggestions(recipe).firstOrNull()

    fun forIngredient(id: String): List<BrandProfile> = byIngredient[id].orEmpty()
}
