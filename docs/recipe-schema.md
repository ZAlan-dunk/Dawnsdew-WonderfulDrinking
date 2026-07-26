# 配方数据结构 v0.0.2

配方通过普通脚本加载到 `window.DD_DATA`，以便项目直接通过 `file://` 打开，不依赖模块服务器或 `fetch()`。

## 配方对象

```js
{
  id: "mirror-of-sky",              // 稳定、唯一、建议使用 kebab-case
  rating: "S",                      // 个人口味评级；无评级为空字符串
  name: {
    zh: "天空之境",
    en: "Mirror of the Sky"
  },
  origin: "personal",               // personal | classic | convenience | custom
  base: ["white_rum"],              // 材料目录中的 ID，可多基酒
  taste: ["citrus", "sparkling"],
  difficulty: "easy",               // easy | beginner | advanced
  colors: ["#3c88a2", "#9e7cc2"],
  glassCapacity: 300,
  ice: "full",                      // full 时采用设置中的满冰有效容量
  method: "build",                  // build | shake | stir
  ingredients: [],
  steps: {
    zh: ["步骤一", "步骤二"],
    en: ["Step one", "Step two"]
  },
  description: {
    zh: "中文介绍",
    en: "English description"
  },
  sourceText: "用户原始记录",        // 个人配方可选
  aliases: ["别名"]                  // 可选，用于搜索
}
```

## 材料用量

### 固定毫升

```js
{ id: "vodka", amount: 30, unit: "ml", optional: false }
```

### 份、个或颗

```js
{ id: "frozen_grapes", amount: 6, unit: "piece", optional: false }
```

首版中该类型不进入液体体积和按容量计算的成本。

### 加至杯量比例

```js
{ id: "waterc_lemon", fillTo: 0.6, unit: "fill" }
```

`0.6` 表示把当前杯中液体补到有效容量的六分满。若前序材料已经达到或超过目标，则该材料估算用量为 0。

### 补满

```js
{ id: "grape_soda", topUp: true, unit: "top" }
```

按材料顺序把杯中液体补到有效容量。

### 可选材料

```js
{ id: "lemon_juice", amount: 10, unit: "ml", optional: true }
```

可选材料不作为“可制作”判断的硬性要求，但如果存在固定毫升用量，仍参与体积和酒精度估算。

## 材料对象

```js
{
  id: "white_rum",
  zh: "白朗姆",
  en: "White Rum",
  category: "spirit",
  abv: 40,
  pack: 700,
  unit: "ml" // 可选，默认按液体理解
}
```

配方必须只引用材料目录中存在的 ID。材料 ID 一旦发布应尽量保持稳定，因为酒柜状态按 ID 保存。

## 本地状态

```js
{
  version: "0.0.2",
  savedAt: "ISO-8601 time or null",
  settings: {
    lang: "zh",
    glassCapacity: 300,
    icedLiquidCapacity: 150,
    currency: "¥"
  },
  pantry: {
    white_rum: {
      owned: true,
      stock: "500",
      price: "79",
      packSize: "700",
      abv: "40"
    }
  },
  favorites: ["mirror-of-sky"],
  tonightMenu: ["mirror-of-sky"],
  customRecipes: [],
  recent: [],
  partyHistory: []
}
```

## 导入导出包

```js
{
  app: "Dawn's Dew / 朝露酒笺",
  schemaVersion: "0.0.2",
  exportedAt: "ISO-8601 time",
  state: { /* 本地状态 */ }
}
```

v0.0.2 支持导入同版本数据，也支持迁移 v0.0.1 状态与备份。未来如修改结构，应增加迁移函数，而不是直接复用旧版本键。
