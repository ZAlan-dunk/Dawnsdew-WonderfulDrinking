(function () {
  "use strict";

  const protocol = window.location.protocol;
  const isNative = Boolean(window.Capacitor && typeof window.Capacitor.isNativePlatform === "function" && window.Capacitor.isNativePlatform());
  const installCard = document.getElementById("pwaInstallCard");
  const installButton = document.getElementById("pwaInstallButton");
  const guide = document.getElementById("pwaInstallGuide");
  const guideClose = document.getElementById("pwaGuideClose");
  const guideDone = document.getElementById("pwaGuideDone");
  let deferredPrompt = null;

  function isStandalone() {
    return window.matchMedia("(display-mode: standalone)").matches || window.navigator.standalone === true;
  }

  function isAppleMobile() {
    return /iphone|ipad|ipod/i.test(navigator.userAgent) || (navigator.platform === "MacIntel" && navigator.maxTouchPoints > 1);
  }

  function showCard() {
    if (installCard && !isStandalone()) installCard.classList.remove("hidden");
  }

  function hideCard() {
    if (installCard) installCard.classList.add("hidden");
  }

  function openGuide() {
    if (!guide) return;
    guide.classList.remove("hidden");
    document.body.classList.add("modal-open");
    if (guideClose) guideClose.focus();
  }

  function closeGuide() {
    if (!guide) return;
    guide.classList.add("hidden");
    document.body.classList.remove("modal-open");
    if (installButton) installButton.focus();
  }

  if (protocol === "file:" || isNative || !installCard || !installButton) {
    hideCard();
    return;
  }

  if (isAppleMobile()) showCard();

  window.addEventListener("beforeinstallprompt", event => {
    event.preventDefault();
    deferredPrompt = event;
    showCard();
  });

  window.addEventListener("appinstalled", () => {
    deferredPrompt = null;
    hideCard();
  });

  installButton.addEventListener("click", async () => {
    if (!deferredPrompt) {
      openGuide();
      return;
    }
    deferredPrompt.prompt();
    const choice = await deferredPrompt.userChoice;
    deferredPrompt = null;
    if (choice && choice.outcome === "accepted") hideCard();
  });

  [guideClose, guideDone].forEach(button => {
    if (button) button.addEventListener("click", closeGuide);
  });
  if (guide) guide.addEventListener("click", event => { if (event.target === guide) closeGuide(); });
  document.addEventListener("keydown", event => {
    if (event.key === "Escape" && guide && !guide.classList.contains("hidden")) closeGuide();
  });

  if ("serviceWorker" in navigator && (protocol === "https:" || window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1")) {
    window.addEventListener("load", () => {
      navigator.serviceWorker.register("./sw.js", { scope: "./" })
        .then(registration => registration.update())
        .catch(error => console.warn("PWA service worker registration failed", error));
    });
  }
}());
