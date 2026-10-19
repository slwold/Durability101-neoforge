# 把 NeoForge 1.21.1 接入原 Stonecutter 仓库

原仓库（ShayBox/Durability101）是 `dev.kikugie.stonecutter` 多加载器/多版本工程，
中央脚本 `version.gradle.kts` 只认 `fabric` / `forge`。本文件说明如何把 NeoForge 版加进仓库。

> 想最快拿到可编译产物，直接用仓库外的 **`Durability101-neoforge-1.21.1/`** 独立工程（已配好 moddev，无需改中央脚本）。

## 已完成的改动

1. **`settings.gradle.kts`**
   - `pluginManagement.repositories` 增加 NeoForge Maven：`https://maven.neoforged.net/releases/`
   - `plugins` 增加 `id("net.neoforged.moddev") version "1.0.24" apply false`
   - `stonecutter` 的 `create { }` 增加 `vers("neoforge-1.21-1", "1.21.1")`

2. **`stonecutter.gradle.kts`**
   - `stonecutter.active("neoforge-1.21-1")` —— 现在仓库根 `./gradlew build` 会构建 NeoForge 版。
     想恢复默认可改回 `fabric-1.21.6-11`。

3. **`version.gradle.kts`**（中央脚本）
   - 新增 `val isNeoForge = loader == "neoforge"`
   - 未知加载器判定加入 `!isNeoForge`
   - Java 工具链条件从 `if (isForge)` 改为 `if (isForge || isNeoForge)`
   - 新增 NeoForge 分支：应用 `net.neoforged.moddev`、设置 `neoForge.version`、加入 NeoForge Maven 仓库。

4. **`versions/neoforge-1.21-1/`**（新增目录）
   - `gradle.properties`：`minecraft_version=1.21.1`、`neoforge_version=21.1.80`、`java_version=21`
   - `src/main/java/com/shaybox/durability101/Durability101.java`：模组源码（包名已迁移到 `net.neoforged`）
   - `src/main/resources/META-INF/neoforge.mods.toml`：NeoForge 模组清单

## 构建（仓库内）

```bash
cd Durability101
./gradlew build        # 仅构建 active 的 neoforge-1.21-1
# 产物：versions/neoforge-1.21-1/build/libs/durability101-0.0.5.jar

# 仅切换当前构建目标（不改动文件）也可用 stonecutter 的切换任务
```

## 兼容性要点（同独立工程 README）

- Minecraft 官方已逐步去混淆 → NeoForge 默认用 **official mappings**，无需配置、无需 `reobfJar`。
- 包名 `net.minecraftforge` → `net.neoforged`（见源码导入）。
- 清单 `mods.toml` → `META-INF/neoforge.mods.toml`，`loaderVersion="[4,)"` + `[[dependencies]]` 声明 neoforge `[21.1,)`。
- `IItemDecorator` / `RegisterItemDecorationsEvent` 在 NeoForge 中完整保留，逻辑与 Forge 1.21-1 一致。
- Java 21 工具链。

## 注意事项

- `neoforge_version` 用 `21.1.80`（确定存在的 21.1.x）。若构建报版本不存在，到
  <https://maven.neoforged.net/releases/net/neoforged/neoforge/> 挑最新的 `21.1.x` 替换。
- 首次构建需联网下载 NeoForge / Minecraft（几百 MB），且必须能访问 `maven.neoforged.net`。
- 中央脚本对 NeoForge 分支使用 `withGroovyBuilder { setProperty("version", ...) }` 设置 `neoForge.version`，
  与现有 Forge 分支设置 `minecraft` 扩展的写法一致；若 moddev 升级到 2.0.x，建议改用其新版 DSL 重写该分支。
