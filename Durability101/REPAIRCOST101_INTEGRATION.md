# 把新模组 RepairCost101 集成进 Stonecutter 仓库

`RepairCost101` 是一个**全新模组**，刻意保持与 `Durability101` **一致的命名规范、接口形式与错误处理**：
复用 `IItemDecorator` / `RegisterItemDecorationsEvent`（NeoForge/Forge）或 `DrawContext` Mixin（Fabric），
同样的 `format` 数字缩写工具、同样的 `@Mod` + `@Mod.EventBusSubscriber` + 静态 `@SubscribeEvent` 结构、
同样的 `com.shaybox.<modid>` 包约定。核心功能类似：在物品图标上**叠加显示铁砧修复代价**
（`ItemStack.getBaseRepairCost()`，>0 才绘制，≥40 用红色），纯客户端。

## 已加入的文件

- `settings.gradle.kts`：`create { }` 增加
  - `vers("repaircost101-neoforge-1.21-1", "1.21.1")`
  - `vers("repaircost101-fabric-1.21-1", "1.21")`
  （`pluginManagement` 的 NeoForge Maven 与 `net.neoforged.moddev` 插件声明此前已为 Durability101 的 NeoForge 版加好。）
- `versions/repaircost101-neoforge-1.21-1/`
  - `gradle.properties`：`mod_id=repaircost101`、`neoforge_version=21.1.80`、`java_version=21` 等
  - `src/main/java/com/shaybox/repaircost101/RepairCost101.java`
  - `src/main/resources/META-INF/neoforge.mods.toml`
- `versions/repaircost101-fabric-1.21-1/`
  - `gradle.properties`：`mod_id=repaircost101`、`minecraft_version=1.21`、`yarn_mappings`/`loader_version` 等
  - `src/main/java/com/shaybox/repaircost101/RepairCost101.java`（ClientModInitializer 空壳）
  - `src/main/java/com/shaybox/repaircost101/RepairCost101Mixin.java`（注入 `DrawContext.drawItemInSlot`）
  - `src/main/resources/fabric.mod.json`（带 `${...}` 令牌，由中央脚本 `processResources` 展开）
  - `src/main/resources/repaircost101.mixins.json`（**注意**：用字面量 `JAVA_21`，未用令牌，因为中央脚本的 expand 列表只覆盖 `fabric.mod.json` 与 `durability101.mixins.json`）

## 构建（仓库内）

```bash
cd Durability101
# 切到新模组的 NeoForge 版（编辑 stonecutter.gradle.kts 的 active）
#   stonecutter.active("repaircost101-neoforge-1.21-1")
./gradlew build
# 产物：versions/repaircost101-neoforge-1.21-1/build/libs/repaircost101-0.0.1.jar

# 或切到 Fabric 版
#   stonecutter.active("repaircost101-fabric-1.21-1")
./gradlew build
# 产物：versions/repaircost101-fabric-1.21-1/build/libs/repaircost101-0.0.1.jar
```

> 当前仓库 `stonecutter.active` 仍是 `neoforge-1.21-1`（Durability101）。新增的 `vers` 仅为注册，
> 不会在 `active` 未切换时被构建，因此不影响现有构建。

## 与原仓库中央脚本的衔接

- **NeoForge 分支**：中央 `version.gradle.kts` 此前已新增 `isNeoForge` 分支（应用 `net.neoforged.moddev`、设置 `neoForge.version`），
  新模组的 NeoForge 版直接复用，无需额外改动。
- **Fabric 分支**：中央脚本对 fabric 应用 loom、注入 `minecraft`/`yarn`/`loader` 依赖，并对 `fabric.mod.json` 做令牌展开——
  新模组在**自己的版本目录**提供 `fabric.mod.json` 与 `repaircost101.mixins.json`，因此与 Durability101 互不冲突
  （各版本目录资源相互独立）。`mod_id` 等元数据由新版本目录的 `gradle.properties` 覆盖。

## 兼容性 / 注意点（同 Durability101）

- MC 官方已逐步去混淆 → NeoForge 默认 official mappings，无需 mappings 配置、无需 `reobfJar`。
- 包名 `net.minecraftforge` → `net.neoforged`；清单 `mods.toml` → `META-INF/neoforge.mods.toml`。
- moddev 1.0.x 要求 **Gradle ≥ 8.8**（仓库 wrapper 现为 8.6，仅对旧 forge/fabric 适用；
  构建 NeoForge 版前需将根 `gradle/wrapper/gradle-wrapper.properties` 的 `distributionUrl` 改为 8.9，见 `STONECUTTER_INTEGRATION.md`）。
- `neoforge_version` 用 `21.1.80`（确定存在的 21.1.x）。若报版本不存在，到
  <https://maven.neoforged.net/releases/net/neoforged/neoforge/> 挑最新 `21.1.x` 替换。
- 完整编译需能访问 `maven.neoforged.net`（NeoForge）/ `maven.fabricmc.net`（Fabric）；
  沙箱内这些仓库被屏蔽，故仅在可联网机器上完成最终 `javac`。

## 独立构建（推荐，免改仓库）

仓库外另有 **`RepairCost101-neoforge-1.21.1/`** 独立工程（已通过 `./gradlew tasks` 验证配置），
直接 `cd RepairCost101-neoforge-1.21.1 && ./gradlew build` 即可产出 jar。
