const fs = require("fs");
const path = require("path");
const crypto = require("crypto");

const root = path.resolve(__dirname, "..");
const version = "0.0.2";
const packageDir = path.join(root, "packages", "DawnsDew-v0.0.2-Beta-Standalone");
const outputName = "DawnsDew-v0.0.2-Beta-Standalone.html";
const outputPath = path.join(packageDir, outputName);
const scriptOrder = [
  "assets/js/compat.js",
  "assets/data/ingredients.js",
  "assets/data/personal-recipes.js",
  "assets/data/classic-recipes.js",
  "assets/js/i18n.js",
  "assets/js/storage.js",
  "assets/js/calculators.js",
  "assets/js/native.js",
  "assets/js/app.js"
];

function read(relativePath) {
  return fs.readFileSync(path.join(root, relativePath), "utf8").replace(/^\uFEFF/, "");
}

let html = read("index.html");
const css = read("assets/css/styles.css");
html = html.replace(
  '<link rel="stylesheet" href="assets/css/styles.css">',
  `<style>\n/* Dawn's Dew v${version} Beta — embedded visual system */\n${css}\n</style>`
);

for (const relativePath of scriptOrder) {
  const tag = `<script src="${relativePath}"></script>`;
  const source = read(relativePath);
  if (!html.includes(tag)) throw new Error(`Missing source tag: ${tag}`);
  if (source.toLowerCase().includes("</script")) throw new Error(`Unsafe closing script sequence in ${relativePath}`);
  html = html.replace(tag, `<script>\n/* Embedded from ${relativePath} */\n${source}\n</script>`);
}

html = html.replace("<!doctype html>", "<!doctype html>\n<!-- Self-contained share edition: styles, artwork, recipes and scripts are embedded. -->");
html = html.replace("<head>", '<head>\n  <meta name="dawnsdew-package" content="v0.0.2-beta-standalone">');
if (/<(?:link|script)\b[^>]+(?:href|src)="assets\//i.test(html)) throw new Error("Standalone package still contains local asset references");

fs.mkdirSync(packageDir, { recursive: true });
fs.writeFileSync(outputPath, html, "utf8");
const sha256 = crypto.createHash("sha256").update(fs.readFileSync(outputPath)).digest("hex");
const guide = `朝露酒笺 · Dawn's Dew v0.0.2 Beta 单文件封装版\n\n` +
  `直接分享文件：${outputName}\n\n` +
  `使用方法：\n1. 将上面的 HTML 文件直接发送给对方。\n2. 对方下载后，用手机或电脑浏览器打开即可。\n3. 不需要同时发送 assets 文件夹，也不需要安装任何软件。\n\n` +
  `封装内容：\n- 完整金黑主题、CSS 杯形美术与 SVG 品牌标志\n- 45 杯内置配方和全部中英文数据\n- 今夜酒单、酒柜、匹配、收藏、随机、自定义、导入导出、ABV 与成本估算\n- 手机底部导航、安全区域、触控尺寸和小屏布局适配\n\n` +
  `注意：\n- 用户数据保存在打开该文件的浏览器本机。\n- 微信、聊天软件或文件管理器的预览页可能不执行 JavaScript；若按钮无响应，请先下载文件，再通过“打开方式”明确选择 Safari、Chrome、Edge 或 Firefox。\n- 建议在数据管理页面定期导出 JSON 备份。\n\n` +
  `SHA-256：${sha256}\n`;
fs.writeFileSync(path.join(packageDir, "使用说明.txt"), guide, "utf8");
fs.writeFileSync(path.join(packageDir, "SHA256.txt"), `${sha256}  ${outputName}\n`, "utf8");
console.log(JSON.stringify({ outputPath, bytes: fs.statSync(outputPath).size, sha256 }, null, 2));
