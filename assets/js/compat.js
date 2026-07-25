(function () {
  "use strict";

  function define(target, name, value) {
    if (!target[name]) Object.defineProperty(target, name, { configurable: true, writable: true, value: value });
  }

  define(Array, "from", function (value) {
    return Array.prototype.slice.call(value);
  });

  define(Object, "assign", function (target) {
    if (target === null || target === undefined) throw new TypeError("Cannot convert undefined or null to object");
    var output = Object(target);
    for (var sourceIndex = 1; sourceIndex < arguments.length; sourceIndex += 1) {
      var source = arguments[sourceIndex];
      if (source === null || source === undefined) continue;
      Object.keys(Object(source)).forEach(function (key) { output[key] = source[key]; });
    }
    return output;
  });

  define(Object, "values", function (object) {
    return Object.keys(Object(object)).map(function (key) { return object[key]; });
  });

  define(Object, "fromEntries", function (entries) {
    var result = {};
    Array.from(entries).forEach(function (entry) { result[entry[0]] = entry[1]; });
    return result;
  });

  define(Array.prototype, "find", function (predicate, thisArg) {
    for (var index = 0; index < this.length; index += 1) {
      if (predicate.call(thisArg, this[index], index, this)) return this[index];
    }
    return undefined;
  });

  define(Array.prototype, "includes", function (value, fromIndex) {
    var length = this.length >>> 0;
    var index = Math.max(Number(fromIndex) || 0, 0);
    while (index < length) {
      if (this[index] === value || (this[index] !== this[index] && value !== value)) return true;
      index += 1;
    }
    return false;
  });

  define(Array.prototype, "flatMap", function (callback, thisArg) {
    return Array.prototype.concat.apply([], this.map(callback, thisArg));
  });

  if (window.Element && !Element.prototype.matches) {
    Element.prototype.matches = Element.prototype.msMatchesSelector || Element.prototype.webkitMatchesSelector;
  }
  if (window.Element && !Element.prototype.closest) {
    Element.prototype.closest = function (selector) {
      var node = this;
      while (node && node.nodeType === 1) {
        if (node.matches(selector)) return node;
        node = node.parentElement || node.parentNode;
      }
      return null;
    };
  }

  function notice() { return document.getElementById("runtimeNotice"); }
  function markFailed(event) {
    var node = notice();
    if (!node) return;
    node.className = "runtime-notice runtime-notice-error";
    node.setAttribute("data-state", "error");
    var detail = node.querySelector("small");
    var message = event && (event.message || (event.reason && event.reason.message));
    if (detail && message) detail.textContent = "错误信息 / Error: " + message;
  }
  function markReady() {
    var node = notice();
    if (node) node.className = "runtime-notice hidden";
    document.documentElement.setAttribute("data-app-ready", "true");
  }

  window.addEventListener("error", markFailed, false);
  window.addEventListener("unhandledrejection", markFailed, false);
  window.DD_COMPAT = { markReady: markReady, markFailed: markFailed };
}());
