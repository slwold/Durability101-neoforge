# Durability101 — NeoForge 1.21.1 构建方案

把原 [ShayBox/Durability101](https://github.com/ShayBox/Durability101)（一个多加载器、多版本的 Stonecutter 项目，
官方 README 明确写 *NeoForge 不支持*）改造成**可独立编译的 NeoForge 1.21.1 模组**。

本目录是一个**自包含、可直接 `./gradlew build`** 的 NeoForge 工程，不依赖原仓库的 Stonecutter 中央脚本，
因此构建路径最短、最不容易踩坑。

仓库地址：<https://github.com/slwold/Durability101-neoforge>

模组功能：在物品图标右下角以数字显示剩余耐久度，数值经「不毁」附魔等级修正，超过千位自动折算为 `k` / `m` / `b`。

---

## 1. 开发环境

| 组件 | 要求 | 说明 |
|------|------|------|
| **JDK** | **Java 21**（必须） | 1.21.x 强制要求 Java 21。实测可用 Azul Zulu 21（Windows 默认路径 `C:\Program Files\Zulu\zulu-21`），或任何 OpenJDK 21。 |
| **Gradle** | 8.9（由 Wrapper 自带） | 已随工程提供 `gradlew` / `gradlew.bat`，无需单独安装。**moddev 1.0.x 硬性要求 Gradle ≥ 8.8**（实测 8.6 会被拒绝）。 |
| **网络** | 能访问 `maven.neoforged.net` 与 Gradle Plugin Portal | 用于下载 NeoForge、Minecraft 与 moddev 插件（首次构建约几百 MB）。 |
| **OS** | Windows / Linux / macOS | 跨平台，命令以 `./gradlew`（*nix）或 `gradlew`（Windows）为准。 |

---

## 2. 依赖项

| 依赖 | 版本 | 来源 | 作用 |
|------|------|------|------|
| `net.neoforged.moddev` (Gradle 插件) | `1.0.24` | Gradle Plugin Portal | NeoForge 官方构建插件，负责拉取 MC + NeoForge、配置 runs、打 jar。 |
| `net.neoforged:neoforge` (游戏本体) | `21.1.80`（21.1.x 线） | `maven.neoforged.net` | 1.21.1 对应的 NeoForge。可在 `gradle.properties` 的 `neoforge_version` 中替换为任意 `21.1.x`。 |
| **官方 mappings（official）** | 随 NeoForge 自带 | — | 见下方「兼容性」第 1 点。无需额外配置。 |

`build.gradle.kts` 仅声明 `id("net.neoforged.moddev")`，其余依赖与仓库由插件自动注入，`neoforge.mods.toml` 中通过 `[[dependencies]]` 声明对 `neoforge` / `minecraft` 的版本约束。

---

## 3. Gradle 构建配置（逐文件）

- **`settings.gradle.kts`** — `pluginManagement` 中加入 NeoForge Maven 与 Plugin Portal，确保能解析 moddev 插件与 NeoForge 本体。
- **`gradle.properties`** — `neoforge_version`（NeoForge 版本）、模组元数据（`mod_id` / `mod_version` / `mod_authors` 等）、JVM 内存。
- **`build.gradle.kts`** — 应用 moddev 插件；`java { toolchain { languageVersion = 21 } }`；`neoForge { version = ... }` 指定 NeoForge 版本。无 `reobfJar`（见兼容性第 4 点）。
- **`src/main/resources/META-INF/neoforge.mods.toml`** — NeoForge 的模组清单（替代 Forge 的 `mods.toml`）。
- **`src/main/java/.../Durability101.java`** — 模组源码。

---

## 4. 具体构建步骤

```bash
# 1) 进入工程目录
cd Durability101-neoforge-1.21.1

# 2) 编译并打 jar（首次会下载 Gradle 分发、NeoForge、Minecraft，耗时几分钟）
./gradlew build        # Windows 用：gradlew build

# 产物位置：
#   build/libs/durability101.jar

# 3)（可选）启动游戏客户端做开发期测试
./gradlew runClient

# 4)（可选）仅生成 IDE 运行配置
./gradlew genIntellijRuns   # 或 eclipse：./gradlew genEclipseRuns
```

把 `build/libs/durability101.jar` 放进任意 1.21.1 + NeoForge 客户端的 `mods/` 目录即可。

### 若报 “NeoForge 版本不存在”
`21.1.80` 是确定存在的 21.1.x。若想用最新版，打开
<https://maven.neoforged.net/releases/net/neoforged/neoforge/> 挑最高的 `21.1.x`，
改 `gradle.properties` 里的 `neoforge_version` 即可（任意 21.1.x 都兼容 1.21.1）。

---

## 5. 1.21.1 与 NeoForge 兼容性处理（重点）

原仓库 Forge 1.21-1 用 `IItemDecorator` + `RegisterItemDecorationsEvent`（Decorator 方案），
NeoForge **完整保留**这套客户端 API，因此移植成本极低，主要差异如下：

1. **Minecraft 官方已逐步去混淆（official mappings）**
   1.20.5/1.21 起 Mojang 在官方渠道提供 mappings，运行时大量类不再混淆。
   → **NeoForge 默认使用 official mappings**，无需 Yarn / SRG，`build.gradle` 里**不用再配 mappings**。
   本模组只用稳定公开 API（`GuiGraphics` / `Font` / `ItemStack` / `IItemDecorator` / `BuiltInRegistries`），全部已在 official mappings 中，无混淆风险。

2. **包名迁移 `net.minecraftforge` → `net.neoforged`**
   所有 Forge 导入改成 NeoForge 对应包：
   - `net.minecraftforge.fml.common.Mod` → `net.neoforged.fml.common.Mod`
   - `net.minecraftforge.api.distmarker.Dist` → `net.neoforged.api.distmarker.Dist`
   - `net.minecraftforge.bus.api.SubscribeEvent` → `net.neoforged.bus.api.SubscribeEvent`
   - `net.minecraftforge.client.IItemDecorator` → `net.neoforged.neoforge.client.IItemDecorator`
   - `net.minecraftforge.client.event.RegisterItemDecorationsEvent` → `net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent`
   - 注册表遍历改用原生 `BuiltInRegistries.ITEM`，避免加载器专属 API 差异。
   - 事件订阅注解变化：`@Mod.EventBusSubscriber` 在 NeoForge 中是**顶层注解**
     `net.neoforged.fml.common.EventBusSubscriber`，且 `value()` 无默认值，需显式写
     `@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)`。
   - 附魔等级读取变化：`Enchantments.UNBREAKING` 是 `ResourceKey<Enchantment>`，需先经
     `level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(...)`
     取回 `Holder<Enchantment>`，再调用 `ItemStack#getEnchantmentLevel(Holder)`。

3. **模组清单：`mods.toml` → `neoforge.mods.toml`**
   NeoForge 使用 `META-INF/neoforge.mods.toml`。`loaderVersion` 为 FML 版本区间 `[4,)`，
   并把 NeoForge 版本约束写进 `[[dependencies.<modid>]]`（`versionRange="[21.1,)"`），
   同时声明 `minecraft`（`[1.21.1,)`）。

   > ⚠️ **不要声明 `modId="java"` 依赖**。NeoForge 并不提供名为 `java` 的模组，一旦声明，
   > 游戏会把该依赖判为 `[MISSING]` 并直接崩溃（报错形如
   > `Mod durability101 requires java 21 or above / Currently, java is not installed`）。
   > Java 版本由 `build.gradle.kts` 的 toolchain 约束即可，无需写入清单。

4. **不再需要 `reobfJar`**
   旧 Forge 需要把 jar 从 SRG 反混淆回 official；NeoForge 直接以 official mappings 产出，
   **没有 reobf 步骤**，`build` 任务直接产出可用 jar。

5. **Java 21 工具链**
   1.21.x 强制 Java 21，`build.gradle.kts` 已通过 `toolchain { languageVersion = 21 }` 固定。

---

## 6. 与原 Stonecutter 仓库的关系

原仓库是 `dev.kikugie.stonecutter` 多版本工程，中央脚本 `version.gradle.kts` 只认 `fabric` / `forge`。
本目录是**独立于 Stonecutter** 的等价实现，便于直接构建。
如需把 NeoForge 版也塞回原仓库（作为 `versions/neoforge-1.21-1/`），
请参照本仓库 `Durability101/STONECUTTER_INTEGRATION.md`（含对 `settings.gradle.kts` 与 `version.gradle.kts` 的改法）。