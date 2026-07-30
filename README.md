# 朝露酒笺 · Dawn's Dew

> 面向家庭调酒爱好者的本地优先调酒助手。当前采用双轨发行：**Web/PWA v0.2 Beta** 与 **Android 原生 v0.3.0-alpha01**。

[🍎 iPhone 网页 App 安装指南](docs/iphone-pwa-guide.md) · [📱 Android v0.3 测试版](https://github.com/ZAlan-dunk/Dawnsdew-WonderfulDrinking/releases/tag/v0.3-test) · [🌐 在线网页版](https://zalan-dunk.github.io/Dawnsdew-WonderfulDrinking/) · [📋 原生 Android 迁移计划](docs/native-android-plan.md)

朝露酒笺以“便利店材料也能调一杯”为核心方向。网页版继续提供完整的本地优先功能；Android v0.3 已不再使用 Capacitor 或 WebView 加载 HTML，而是使用 Kotlin 与 Jetpack Compose 重写的原生应用。目前原生版处于 Alpha，适合开发验证和内部测试，尚未与 Web v0.2 完全功能对等。

## 当前发行形态

### Web/PWA v0.2 Beta

- 45 杯内置配方：25 杯个人酒单、15 杯经典鸡尾酒、5 杯便利店灵感配方。
- 中英文界面、搜索、来源/基酒/口味/难度/收藏/可制作筛选。
- 配方详情、酒柜匹配、今夜酒单、随机与聚会推荐、收藏、最近查看和自定义配方。
- 酒精度、酒液体积和成本估算。
- 版本化 JSON 导入导出及旧版数据迁移。
- 可直接打开 `index.html`，也可作为 PWA 安装并在首次完整加载后离线使用。
- 桌面、平板和手机响应式布局，并支持系统“减少动态效果”偏好。

### Android 原生 v0.3.0-alpha01

已实现：

- Kotlin + Jetpack Compose 原生界面，不加载 HTML，不依赖 WebView 作为应用主体。
- 首页、配方浏览、搜索、中英文切换、配方详情、收藏与今夜酒单。
- 收藏、今夜酒单和语言设置通过 SharedPreferences 持久化。
- `< 840dp` 使用底部导航，`≥ 840dp` 使用 Navigation Rail。
- 配方区使用自适应网格，窄屏统计卡和详情操作区会自动重排。
- 原生 Canvas 杯型、酒液渐变、黎明光晕、页面切换、卡片分批入场和按压反馈。
- 系统关闭动画或启用减少动态效果时，应用会同步减少自定义动效。

尚未实现或尚未完成对等：

- 完整酒柜、可制作匹配、成本和 ABV 计算。
- 自定义配方、随机/聚会推荐、最近查看。
- JSON 文件导入导出和旧 Capacitor localStorage 数据迁移。
- 正式 release 签名、AAB 发布配置、实体设备兼容性与升级验收。

## 使用方式

### 1. 普通网页版

克隆或下载仓库后，双击根目录的 `index.html`。该方式无需安装运行时依赖，也无需服务器。

为保证本地数据持续可用，建议不要频繁移动文件，并定期在“数据管理”中导出 JSON 备份。

### 2. 单文件封装版

运行：

```powershell
npm run build:standalone
```

默认输出到工作区内的：

```text
packages/DawnsDew-v0.2-Beta-Standalone/
```

聊天软件内置预览器或手机文件管理器的文档预览页可能不会执行 JavaScript。请先下载文件，再使用 Chrome、Edge、Firefox 或 Safari 等完整浏览器打开。

### 3. iPhone 网页 App（PWA）

使用 Safari 打开在线网页版，依次选择“分享”与“添加到主屏幕”。详细步骤见 [iPhone 网页 App 安装指南](docs/iphone-pwa-guide.md)。

### 4. Android 原生 Alpha

> [下载 Android v0.3 测试版](https://github.com/ZAlan-dunk/Dawnsdew-WonderfulDrinking/releases/tag/v0.3-test)

应用信息：

```text
应用名称：朝露酒笺
应用 ID：com.zalandunk.dawnsdew
版本：0.3.0-alpha01
versionCode：3
最低 Android 版本：Android 7.0（API 24）
目标 SDK：Android 16 / API 36
```

当前调试包生成位置：

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

Debug APK 仅适合内部测试。正式对外发布应使用受控的私有密钥签名 release APK/AAB，并完成数据迁移、设备兼容和覆盖升级测试。

## 开发与验证

环境要求：Node.js 22 或更高版本、JDK 21、Android SDK 36。

### Web 数据和静态检查

```powershell
npm install
npm test
```

### 同步 Web 配方目录到原生资源

```powershell
npm run sync:native-data
```

生成文件：

```text
android/app/src/main/assets/catalog.json
```

### 构建 Android Debug APK

```powershell
npm run android:apk
```

或分步执行：

```powershell
npm run sync:native-data
cd android
.\gradlew.bat assembleDebug
```

### 执行 Android 单元测试

```powershell
npm run android:test
```

项目不会提交依赖缓存、Android SDK 本地路径和构建产物。

## 数据与升级说明

- Web/PWA 数据保存在浏览器 localStorage；Android 原生 Alpha 数据保存在 SharedPreferences。
- v0.3 Alpha 暂不能自动读取旧 Capacitor/WebView localStorage，不能视为可直接覆盖 v0.2 的正式升级包。
- 正式覆盖升级前，至少应实现“Web 导出 JSON → Android 系统文件选择器导入 JSON”，或提供一次性的旧数据迁移器。
- 要在 Android 上原地升级旧 APK，新包必须继续使用相同 applicationId，并使用与旧包一致的正式签名证书。
- 卸载应用、清除应用数据、更换签名或清理浏览器数据都可能造成数据丢失。
- 项目没有账号、后端、统计脚本或自动云同步。

## 视觉、响应式与可访问性

- 品牌主题为“朝露金夜”：暮色紫、酒红、黎明橙和金色高光。
- Web 和 Android 都使用程序化杯型与渐变美术，避免依赖在线图片。
- Web 断点覆盖桌面、平板、常见手机和超窄屏；移动表单字号避免 iOS 自动缩放。
- Web 弹窗支持 Escape、焦点循环、关闭后焦点恢复和 `aria-hidden` 同步。
- 触屏设备会减少无意义的 hover 效果；两端均尊重减少动态效果偏好。
- 当前已完成代码级响应式与构建验证；像素级浏览器截图和 APK 模拟器视觉验收仍需单独执行。

## 项目结构

```text
Dawnsdew-WonderfulDrinking/
├─ index.html
├─ manifest.webmanifest
├─ sw.js
├─ assets/
│  ├─ icons/
│  ├─ css/styles.css
│  ├─ data/
│  └─ js/
├─ android/                       # Kotlin + Jetpack Compose 原生工程
│  └─ app/src/main/
│     ├─ assets/catalog.json
│     └─ java/com/zalandunk/dawnsdew/
│        ├─ data/
│        └─ ui/
├─ tools/
│  ├─ build-standalone.js
│  ├─ export-native-catalog.js
│  └─ verify-project.js
├─ docs/
│  ├─ iphone-pwa-guide.md
│  ├─ native-android-plan.md
│  ├─ product-plan.md
│  └─ recipe-schema.md
├─ package.json
├─ CHANGELOG.md
└─ README.md
```

## 已知限制

- Android v0.3 Alpha 与 Web v0.2 尚未功能完全对等，也没有旧数据自动迁移。
- Web 数据仅保存在当前浏览器；原生 Alpha 数据仅保存在当前应用安装中。
- 自定义配方在 Web v0.2 中暂不支持编辑，只支持新建和删除。
- 成本估算按容量比例计算，水果、装饰物和“份/个”材料暂不进行精细单价换算。
- 库存暂用于“是否拥有”和展示，制作后不会自动扣减。
- 经典配方为家庭制作参考比例，不替代专业酒吧标准或特定品牌官方配方。

## 文档

- [原生 Android 迁移计划](docs/native-android-plan.md)
- [iPhone 网页 App 安装指南](docs/iphone-pwa-guide.md)
- [产品详细方案](docs/product-plan.md)
- [配方数据结构](docs/recipe-schema.md)
- [版本记录](CHANGELOG.md)

## 理性饮酒

请根据个人身体状况适量饮用，饮酒后请勿驾驶。未成年人请勿饮酒。