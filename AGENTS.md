# AGENTS.md

本文件面向 AI 编程助手，汇总 ExplodeExtreme（ExplodeX）项目的架构、构建、开发约定与运行方式。请在进行代码修改前阅读本文件。

## 项目概述

ExplodeExtreme（ExplodeX）是一个用于实现 Dynamite 游戏后端服务的项目。Dynamite 是 TunerGames 开发的音乐游戏，已于 2022 年中期因非技术原因停止运营；本项目旨在为仍然希望游玩该游戏的玩家提供私有/社区后端服务。

- **组织方式**：多模块 Kotlin（为主）+ Java Gradle 项目。
- **根项目名称**：`explode2+`。
- **Group**：`explode`。
- **主版本**：`3.0.0`（根项目），`explode-all` 与 `explode-proxy` 当前版本为 `3.0.3`。

## 技术栈

- **构建工具**：Gradle 7.4.2（Wrapper）。
- **Kotlin 版本**：1.7.20（根项目），`resource` 模块额外使用 Kotlinx Serialization 插件 1.8.20。
- **JVM 目标**：17（`sourceCompatibility/targetCompatibility = 17`、`kotlinOptions.jvmTarget = "17"`、`options.release.set(17)`）。
- **HTTP 服务端**：Ktor 2.1.2（Netty 引擎）。
- **GraphQL**：`graphql-kotlin-server` 6.2.5、`graphql-java-extended-scalars` 20.0。
- **依赖注入**：Koin 3.4.x。
- **数据库**：MongoDB，通过 KMongo 4.7.1 访问。
- **事件总线**：greenrobot EventBus 3.3.1。
- **配置**：TConfig（`com.github.Taskeren:TConfig:1.2`）。
- **日志**：SLF4J 2.0.5 + Log4j2 2.20.0。
- **JSON**：Gson 2.10.1、Jackson 2.13.4。
- **控制台命令**：brigadierX 1.2.2。

## 模块划分

| 模块 | 路径 | 作用 |
|------|------|------|
| `gateau` | `gateau/` | 业务对象/领域模型，仅包含接口（`GameUser`、`SongSet`、`SongChart`、`GameRecord` 等）。 |
| `labyrinth` | `labyrinth/` | 数据访问抽象层，定义 Repository 接口与 `LabyrinthProvider` SPI。 |
| `labyrinth-mongodb` | `labyrinth-mongodb/` | `LabyrinthProvider` 的 MongoDB 实现，基于 KMongo。 |
| `resource` | `resource/` | 资源存储抽象层，定义 `ResourceProvider`、`ResourceReceiver` 等，内置 `FileSystemProvider`。 |
| `booster` | `booster/` | 加载器核心（类似 Minecraft 服务端核心），负责插件加载、Ktor 启动、Koin 注入、GraphQL 与资源路由。 |
| `gatekeeper` | `gatekeeper/` | Booster 插件，提供交互式控制台命令（`exit`、`labyrinth`、用户相关命令、示例生成器等）。 |
| `explode-all` | `explode-all/` | 完整服务端可执行产物，Shadow JAR 打包 booster + resource + labyrinth + labyrinth-mongodb + gatekeeper。 |
| `explode-proxy` | `explode-proxy/` | 实验性代理服务器，将客户端 GraphQL 与资源请求转发到远程 Explode 后端。 |
| `maintain` | `booster-plugins/maintain/` | 示例/玩笑插件，在根路径 `/` 显示维护页面。 |
| `url-redirect-resource` | `booster-plugins/url-redirect-resource/` | 资源插件，通过配置的重定向 URL 响应资源请求。 |
| `aliyun-oss-resource` | `booster-plugins/aliyun-oss-resource/` | 资源插件，使用阿里云 OSS 存储并返回签名 URL，支持上传。 |
| `dynamite-cli` | `devtools/dynamite-cli/` | 独立 GraphQL 客户端 CLI，用于手动测试。 |

## 代码组织

- **根包名**：`explode2.*`。
- `gateau`：`explode2.gateau.*`
- `labyrinth` 抽象层：`explode2.labyrinth.*`
- MongoDB 实现：`explode2.labyrinth.mongo.*`
- `booster` 核心：`explode2.booster.*`
- GraphQL 定义：`explode2.booster.graphql.definition.*`
- REST（Bomb）API：`explode2.booster.bomb.*`
- 资源相关：`explode2.booster.resource.*`
- 控制台插件：`explode2.booster.gatekeeper.*`
- 插件类：`explode2.booster.<plugin-name>.*`

## 构建与运行

### 常用 Gradle 命令

```bash
# 构建全部模块（CI 使用，跳过测试）
./gradlew build -x test

# 运行测试
./gradlew test

# 运行完整服务端（工作目录会自动切换到 run/）
./gradlew :explode-all:run

# 构建并收集产物到 build/gather-builds/<git-version>/
./gradlew build
```

### 产物说明

构建完成后主要产物位于各模块 `build/libs/` 下，`gatherArtifact` 任务会将它们收集到 `build/gather-builds/<git-version>/`。

- `explode-all-<version>-all.jar`：完整服务端 Fat JAR，部署时使用。
- `explode-all-<version>.jar`：非 Shadow JAR。
- `explode-proxy-<version>-all.jar`：代理服务端 Fat JAR。
- `booster-plugins/*/<version>.jar`：插件 JAR。

### 运行服务端

```bash
java -cp explode-all-<version>-all.jar explode2.booster.BoosterMainKt
```

- 插件 JAR 需要放在 `./plugins/` 目录下（与主 JAR 同目录）。
- `explode-all` 的 `application` 插件默认将 `gradlew run` 的工作目录设为 `run/`。

### 运行代理

```bash
java -cp explode-proxy-<version>-all.jar explode2.booster.BoosterMainKt
```

代理同样使用 `explode2.booster.BoosterMainKt` 入口，但类路径上加载的是代理版 `MazeProvider` 与 `ResourceProvider`。

## 配置与环境变量

### 环境变量

- `DB_URL`：MongoDB 连接字符串（优先级高于 `labyrinth.cfg` 中的 `connection-string`）。
- `DB_NAME`：MongoDB 数据库名（优先级高于 `labyrinth.cfg` 中的 `database-name`）。

### 系统属性

- `ex.addr`：覆盖监听地址。
- `ex.port`：覆盖监听端口。

### 运行时生成的配置文件

- `explode.cfg`：监听地址/端口（`general.addr`、`general.port`）。
- `labyrinth.cfg`：MongoDB 连接字符串与数据库名。
- `basic-maze.cfg`：提交过期时间、排行榜刷新间隔等。
- `bomb-api.cfg`：Bomb API 路由前缀、Superstar 后门开关等。
- 插件自有配置：如 `redirect.resource.cfg`、`aliyun-oss.resource.cfg`、`proxy_maze.cfg` 等。

## 插件系统

项目使用 **Java `ServiceLoader` + 运行时追加 classpath** 的方式加载插件。

### 开发一个 Booster 插件

1. 在 `booster-plugins/` 下新建模块，或在 `settings.gradle.kts` 中通过 `includePlugin("name")` 注册。
2. 模块依赖 `project(":booster")`。
3. 创建一个实现 `explode2.booster.BoosterPlugin` 的类，并提供无参构造函数。
4. 在 `src/main/resources/META-INF/services/explode2.booster.BoosterPlugin` 中写入该类的全限定名。
5. 插件生命周期方法：`onPreInit()`、`onInit()`、`onPostInit()`。
6. 插件默认是 Koin 组件，可通过依赖注入获取 Repository；可通过 `subscribeEvents()` 注册 EventBus 监听器。

### 其他 SPI

除 `BoosterPlugin` 外，以下接口也通过 `META-INF/services` 注册：

- `explode2.labyrinth.LabyrinthProvider`：数据后端（MongoDB 实现、代理 no-op 实现）。
- `explode2.booster.resource.ResourceProvider`：资源后端（文件系统、URL 重定向、阿里云 OSS、代理）。
- `explode2.booster.graphql.MazeProvider`：GraphQL Query/Mutation 提供者（默认 `BasicMaze`、代理 `ProxyMaze`）。

## 运行时架构

### 启动流程（`explode2.booster.BoosterMainKt`）

1. 初始化 `Explode` 单例（配置、EventBus、Koin 模块）。
2. 扫描 `./plugins/*.jar` 并通过 `ClassLoaderUtils` 追加到 classpath。
3. 通过 `ServiceLoader` 实例化所有 `BoosterPlugin`。
4. 校验至少存在一个插件，否则退出。
5. 依次派发 `onPreInit()`、`onInit()`、`onPostInit()`。
6. 保存所有配置。
7. 在独立线程中启动 Netty/Ktor，默认监听 `0.0.0.0:10443`。

### HTTP API

同时暴露两套 API：

1. **GraphQL API**：默认路径 `/graphql`，另有 `/sdl`（打印 Schema）、`/playground`。
   - 认证通过请求头 `x-soudayo` 传递 token，解析为当前 `GameUser`。
   - Query 包括 `self`、`set`、`userByUsername`、`playRank`、`assessmentGroup` 等。
   - Mutation 包括 `loginUser`、`registerUser`、`exchangeSet`、`submitBeforePlay`、`submitAfterPlay` 等。

2. **Bomb REST API**：默认根路径 `/bomb/v2`（可在 `bomb-api.cfg` 中配置模板 `bomb/v{version}`）。
   - 用户相关：`/user/me`、`/user/{id}`、`/user/{id}/best`、`/user/{id}/last`、修改用户名/密码、注册、搜索等。
   - 曲目/谱面：`/set/{id}`、`/chart/{id}`。
   - 上传：`/upload`（需要 `bomb.upload` 权限）。
   - 迁移：`/migrate`。

3. **资源服务**：`/download/...` 提供音乐、预览、封面、谱面 XML、商店封面、头像等资源；当 `ResourceProvider` 为 `RedirectResourceProvider` 时返回重定向，为 `ByteArrayResourceProvider` 时直接返回内容。支持 `POST` 上传（若 provider 实现 `ResourceReceiver`）。

## 数据层

- **gateau**：纯领域接口，无实现。
- **labyrinth**：Repository 接口与 `LabyrinthProvider`。
- **labyrinth-mongodb**：
  - `LabyrinthMongoProvider` 通过 `DB_URL`/`DB_NAME` 或 `labyrinth.cfg` 连接 MongoDB。
  - `MongoManager` 实现所有 Repository 接口，集合包括 `Sets`、`Charts`、`Users`、`GameRecords`、`AssessRecords`、`AssessInfo`。
  - 使用委托模式（`SongSetDelegate`、`SongChartDelegate`、`GameUserDelegate` 等）将 `gateau` 接口与 MongoDB 文档桥接，修改后写回数据库。
  - 排行榜使用 MongoDB 聚合管道（`setWindowFields`、`rank`）计算。

## 测试

项目当前测试覆盖较少：

- `booster/src/test/kotlin/TestColorOutput.kt`：JUnit 5 日志颜色冒烟测试。
- `devtools/dynamite-cli/src/test/kotlin/TestGraphqlRequests.kt`：`kotlin.test` 集成测试，默认标记为 `@Ignore`，目标为 `localhost:10443/graphql`。
- CI 使用 `./gradlew build -x test`，因此构建不会阻塞于测试。

## 代码风格

参考 `CONTRIBUTING.md`、`DEV_README.md` 与 `.editorconfig`。

- 缩进使用 **Tab**（`indent_style = tab`），而非空格。
- Kotlin 基本遵循旧版 IntelliJ IDEA Kotlin codestyle，例外：
  - 枚举与常量命名允许 `UpperCamelCase` 或 `UPPER_SNAKE_CASE`（正则 `[A-Z][_a-zA-Z\d]*`）。
  - 控制关键字与左括号之间**不加空格**：`if(true)`、`for(...)`、`while(...)`、`catch(...)`、`when(...)`。
  - 注解与被注解代码**换行**。
  - 限制 Top Level Function 的使用范围。
- Java：IntelliJ 默认规范 + Tab 缩进 + 控制关键字前不加空格。
- Markdown：尽量遵循 Google Markdown Style Guide。
- 编码：UTF-8。

## 部署与 CI/CD

- GitHub Actions 配置：`.github/workflows/gradle.yml`。
- 触发条件：`v3` 分支的 push 与 pull_request。
- 运行环境：Ubuntu Latest、JDK 17 Temurin、Gradle setup action v6。
- 构建命令：`./gradlew build -x test`。
- 上传产物：`explode-all/build/libs/`、`explode-proxy/build/libs/`、`booster-plugins/*/build/libs/`。

## 安全与敏感信息

- MongoDB 连接字符串等敏感信息通过环境变量或本地配置文件传入，不应提交到仓库。
- `explode-proxy` 会将远程登录凭据缓存在运行目录的 `.proxy_cache` 文件中，该文件**不得上传或共享**；重置代理服务器时应删除它。
- Bomb API 存在 `Superstar` 后门配置：启用后允许一个随机 UUID 账户以任意用户身份登录，仅应在受控测试环境使用。

## 其他注意事项

- 项目声明了一个 Git 子模块 `TunerGamesEncryption`（指向 `git@github.com:Taskeren/Explode-Crypto.git`），但在当前工作树中可能未检出（路径 `TunerGamesEncryption` 不存在）。
- `src/main/kotlin/explode2/tests/Main.kt` 是开发用的临时/草稿代码，不是正式测试。
- 新开发优先参考现有模块（如 `gatekeeper` 或 `booster-plugins/maintain`）的目录结构与 `META-INF/services` 注册方式。
