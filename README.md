# 朝露酒笺 · Dawn's Dew

> 面向家庭调酒爱好者的本地优先调酒助手。当前版本：**v0.0.2 Beta**。

[🍎 **iPhone 网页 App 安装指南**](docs/iphone-pwa-guide.md) · [📱 **下载 Android 测试版（GitHub Releases）**](https://github.com/ZAlan-dunk/Dawnsdew-WonderfulDrinking/releases/tag/v0.0.2-beta) · [🌐 **打开在线网页版**](https://zalan-dunk.github.io/Dawnsdew-WonderfulDrinking/)

朝露酒笺以“便利店材料也能调一杯”为核心方向，提供配方浏览、材料匹配、个人酒柜、今夜酒单、聚会推荐、收藏、自定义配方，以及酒精度与成本估算。网页版可直接离线打开，也可通过 Capacitor 封装为 Android APK。

## v0.0.2 Beta 功能

- 45 杯内置配方
  - 25 杯个人酒单，保留原名称、个人评级与原始记录
  - 15 杯经典鸡尾酒
  - 5 杯便利店友好灵感配方
- **今夜酒单**
  - 从任意内置或自定义配方中加入
  - 支持在配方卡片、配方详情和独立页面中添加或移除
  - 首页展示最多 4 杯今夜候选
  - 支持一键清空，并自动保存在本机
- 中英文界面切换，个人酒名采用意境化英文译名
- 名称、英文名及材料搜索
- 按来源、基酒、口味、难度、收藏和可制作状态筛选
- 配方详情、材料用量与双语制作步骤
- 个人酒柜：已有材料、库存、购入价、包装容量和酒精度
- 根据酒柜匹配“可制作 / 差一种 / 缺少多种材料”
- 普通随机与聚会防重复随机推荐
- 收藏、最近查看、创建及删除自定义配方
- 本机自动保存，以及版本化 JSON 导入与导出
- 自动迁移 v0.0.1 的酒柜、收藏、自定义配方和历史数据
- 估算单杯酒精度、酒液体积和材料成本
- 电脑与手机响应式布局
- Android 返回键依次执行：关闭配方详情、返回首页、退出应用
- 可安装 PWA：支持添加到 iPhone/Android/电脑桌面，首次完整载入后可离线使用内置功能

## 使用方式

### 1. 普通网页版

克隆或下载仓库后，双击根目录的 `index.html`。该方式无需安装依赖，也无需服务器。

为保证本地数据持续可用，建议不要频繁移动文件，并定期在“数据管理”中导出 JSON 备份。

### 2. 可直接分享的单文件封装版

只需发送下面这一个 HTML 文件：

```text
packages/DawnsDew-v0.0.2-Beta-Standalone/DawnsDew-v0.0.2-Beta-Standalone.html
```

该文件已经内置全部样式、杯形美术、SVG 标志、配方数据和程序脚本，不依赖 `assets` 文件夹。

微信等聊天软件的内置预览器、手机文件管理器的文档预览页可能不会执行 JavaScript，表现为按钮无响应。请先把文件下载到手机，再用 Chrome、Edge、Firefox 或 Safari 等完整浏览器打开；如需更稳定的手机体验，优先使用 APK 或 GitHub Pages 在线版。

重新生成封装版：

```powershell
npm run build:standalone
```

### 3. iPhone 网页 App（PWA，推荐）

无需下载 IPA，也无需 App Store。使用 iPhone 的 **Safari** 打开在线版：

<https://zalan-dunk.github.io/Dawnsdew-WonderfulDrinking/>

然后依次执行：

1. 点击 Safari 的“分享”按钮。
2. 选择“添加到主屏幕”。
3. 确认名称并点击“添加”。
4. 从桌面图标全屏打开朝露酒笺。

首次完整载入后，内置配方、界面资源和主要功能可离线打开；酒柜、收藏、今夜酒单与自定义配方仍只保存在当前设备。详细说明见 [iPhone 网页 App 安装指南](docs/iphone-pwa-guide.md)。

### 4. Android APK

> [前往 v0.0.2 Beta 发布页下载 APK](https://github.com/ZAlan-dunk/Dawnsdew-WonderfulDrinking/releases/tag/v0.0.2-beta)

Android 应用信息：

```text
应用名称：朝露酒笺
应用 ID：com.zalandunk.dawnsdew
版本：0.0.2-beta
versionCode：2
最低 Android 版本：Android 7.0（API 24）
目标 SDK：Android 16 / API 36
```

当前生成的是便于测试和直接分享的 **debug APK**。首次安装时，Android 可能要求为文件管理器或浏览器开启“允许安装未知来源应用”。debug APK 适合内部测试；正式对外发布时应使用私有签名密钥构建 release APK/AAB，并妥善保管密钥。

## Android 构建

环境要求：Node.js 22 或更高版本、JDK 21、Android SDK 36。

```powershell
npm install
npm run android:sync
cd android
.\gradlew.bat assembleDebug
```

生成位置：

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

项目不会提交 `node_modules`、`www`、Android 本地 SDK 路径和构建产物。

## 默认估算规则

- 默认杯容量：`300 ml`
- 满冰时默认可容纳酒液：`150 ml`
- “八分满 / 六分满 / 五分满”按当前有效酒液容量的 `80% / 60% / 50%` 计算
- “补满”按配方顺序补至有效酒液容量
- 酒精度：`Σ(材料体积 × 材料酒精度) ÷ 总酒液体积`
- 成本：`购入价 ÷ 包装容量 × 本杯用量`

酒精度、体积和成本均为估算值。冰块融水、实际杯型、品牌酒精度、倾倒误差及材料密度会影响结果；固定用量超过满冰容量时，软件会保留原始配方并提示更换杯具或减少冰量。

## 数据与隐私

- 没有账号、后端、统计脚本或云同步。
- 酒柜、价格、收藏、今夜酒单、自定义配方和设置只保存在当前浏览器或 APK 的本地存储中。
- 从 v0.0.1 升级时会自动迁移原有数据，并初始化空的今夜酒单。
- 清理应用数据、卸载 APK、清理浏览器数据或更换设备可能导致数据丢失。
- 建议定期导出 JSON；v0.0.1 导出的 JSON 仍可导入 v0.0.2。

## 项目结构

```text
Dawnsdew-WonderfulDrinking/
├─ index.html
├─ manifest.webmanifest          # PWA 安装信息
├─ sw.js                         # 离线缓存
├─ assets/
│  ├─ icons/                     # PWA 与 iPhone 桌面图标
│  ├─ css/styles.css
│  ├─ data/
│  └─ js/
│     ├─ i18n.js
│     ├─ storage.js
│     ├─ native.js
│     ├─ calculators.js
│     └─ app.js
├─ android/                    # Capacitor Android 工程
├─ packages/
│  └─ DawnsDew-v0.0.2-Beta-Standalone/
├─ tools/
│  ├─ build-mobile.js
│  └─ build-standalone.js
├─ docs/
├─ capacitor.config.json
├─ package.json
├─ CHANGELOG.md
└─ README.md
```

## 已保留的未来酒单名称

以下名称不作为当前应用主名称，保留给后续主题酒单、活动或配方合集使用：

- 酌光
- 浮光酒笺
- 微醺拾光
- 杯中晚霞
- 饮风集
- 暮色调
- 雾饮
- 一盏星河
- 浮生一酌
- 破晓酒笺

## v0.0.2 Beta 已知限制

- 数据仅在单个浏览器或 APK 本地保存，暂不支持账号和多设备自动同步。
- 更换 APK 签名或卸载应用后，本机数据可能无法保留，请先导出备份。
- 配方图片使用原创 CSS 图形，暂未提供照片上传。
- 成本估算按容量比例计算；水果、装饰物和“份/个”材料暂不进行精细单价换算。
- 库存暂用于“是否拥有”与展示，制作后不会自动扣减。
- 自定义配方暂不支持编辑，只支持新建和删除。
- 经典配方为家庭制作参考比例，不替代专业酒吧标准或特定品牌官方配方。

## 文档

- [iPhone 网页 App 安装指南](docs/iphone-pwa-guide.md)
- [产品详细方案](docs/product-plan.md)
- [配方数据结构](docs/recipe-schema.md)
- [版本记录](CHANGELOG.md)

## 理性饮酒

请根据个人身体状况适量饮用，饮酒后请勿驾驶。未成年人请勿饮酒。
