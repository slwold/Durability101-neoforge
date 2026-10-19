# Durability101 — NeoForge 1.21.1

[Minecraft](https://www.minecraft.net/) **1.21.1** + [NeoForge](https://neoforged.net/) **21.1.x** 版本的
[Durability101](https://github.com/ShayBox/Durability101) 移植仓库。

模组会在物品图标上以数字显示**剩余耐久度**，数值经「不毁」附魔等级修正，超过千位自动折算为 `k` / `m` / `b`。

上游仓库是一个 Stonecutter 多加载器工程（fabric / forge 多版本），其 README 明确写着 *NeoForge 不支持*；
本仓库提供可独立编译的 NeoForge 1.21.1 实现。

## 仓库结构

| 路径 | 说明 |
|------|------|
| `Durability101-neoforge-1.21.1/` | **本仓库主体**：自包含的 NeoForge 1.21.1 工程，可直接 `./gradlew build` |
| `Durability101/` | 上游 ShayBox 的多加载器源码（Stonecutter，fabric + forge 多版本），保留作为移植来源与参考 |

## 快速开始

需要 **Java 21**，并能访问 `maven.neoforged.net`；Gradle 8.9 由 Wrapper 自带，无需单独安装。

```bash
cd Durability101-neoforge-1.21.1
./gradlew build          # Windows：gradlew build
```

产物为 `Durability101-neoforge-1.21.1/build/libs/durability101.jar`，
放进 `mods/` 目录即可在 **Minecraft 1.21.1 + NeoForge 21.1.x** 上使用（客户端模组）。

开发环境、依赖版本、构建步骤与 1.21.1 / NeoForge 兼容性差异（包名迁移、事件注解、附魔 API、模组清单等）
详见 [`Durability101-neoforge-1.21.1/README.md`](Durability101-neoforge-1.21.1/README.md)。

## 许可

本仓库沿用上游的 **MIT** 许可，全文见 [`Durability101/LICENSE`](Durability101/LICENSE)（Copyright (c) 2019 Durability101 / ShayBox）。