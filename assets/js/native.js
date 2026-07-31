(function () {
  "use strict";

  function capacitor() { return window.Capacitor || null; }
  function isNative() { const cap = capacitor(); return Boolean(cap && typeof cap.isNativePlatform === "function" && cap.isNativePlatform()); }
  function plugin(name) { const cap = capacitor(); return cap && cap.Plugins ? cap.Plugins[name] : null; }

  async function shareJson(fileName, content) {
    if (!isNative()) return false;
    const Filesystem = plugin("Filesystem");
    const Share = plugin("Share");
    if (!Filesystem || !Share) return false;
    const written = await Filesystem.writeFile({ path: fileName, data: content, directory: "CACHE", encoding: "utf8", recursive: true });
    await Share.share({ title: fileName, text: "朝露酒笺 v0.3.3 Beta 数据备份", files: [written.uri], dialogTitle: "导出并分享酒单备份" });
    return true;
  }

  function onBackButton(handler) {
    const App = plugin("App");
    if (!isNative() || !App || typeof App.addListener !== "function") return;
    App.addListener("backButton", handler);
  }

  function exitApp() {
    const App = plugin("App");
    if (App && typeof App.exitApp === "function") App.exitApp();
  }

  if (isNative() && document.body) document.body.classList.add("capacitor-native");
  window.DD_NATIVE = { isNative, shareJson, onBackButton, exitApp };
}());
