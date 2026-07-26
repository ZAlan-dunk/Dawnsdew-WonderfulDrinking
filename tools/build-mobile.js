const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const output = path.resolve(root, "www");
if (output === root || !output.startsWith(root + path.sep)) throw new Error("Unsafe mobile output path");

fs.rmSync(output, { recursive: true, force: true });
fs.mkdirSync(output, { recursive: true });
fs.copyFileSync(path.join(root, "index.html"), path.join(output, "index.html"));
fs.cpSync(path.join(root, "assets"), path.join(output, "assets"), { recursive: true });
fs.writeFileSync(path.join(output, "version.json"), JSON.stringify({ app: "朝露酒笺", version: "0.0.2-beta", builtAt: new Date().toISOString() }, null, 2) + "\n", "utf8");
console.log(JSON.stringify({ output, version: "0.0.2-beta" }, null, 2));
