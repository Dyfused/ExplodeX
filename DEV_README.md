# 开发用 README

## 子模块命名规范

### 加载器 Booster

`booster` 是加载器模块，可以类比与 Minecraft 的服务器核心，以 `booster-` 开头的模块就是在加载器下面跑的具体内容，类似于服务器中的插件。

在插件模块中需要有一个类继承 `explode2.booster.BoosterPlugin`，且有无参构造函数，
然后在 `META-INF/services` 中添加文件 `explode2.booster.BoosterPlugin`，内容为该类的位置。

### Gateau Business Objects

原文应该是 `gâteau`，内容是业务对象。

### 数据提供 Labyrinth

## Artifacts

The final JARs you can get from this project are:

- explode-all-(version).jar
- explode-all-(version)-all.jar
- explode-proxy-(version).jar
- explode-proxy-(version)-all.jar
- plugins/\*.jar

For server deploying, use explode-all-(version)-all.jar, and run it with
 `java -cp explode-all-(version)-ALL.jar explode2.booster.BoosterMainKt`.

Btw the plugins are stored in the "plugins" folder. You need to put the
"plugins" folder under the same directory as the main server JAR in order
to load all plugin JARs.

## Bomb API

根请求地址与配置有关，默认模板为 `bomb/v{version}`，支持两个变量 `{version}` 是大版本号，目前是 2，
`{version_patch}` 是小版本号，目前是 0。

随意最最最默认的情况是 `http://localhost:10443/bomb/v2`

### 用户相关

#### GET /user/me 获取自己

需要登录

```json
{"success":true,"data":{"id":"3fc753db-f5c3-4089-8163-51eb8b37c77a","username":"Taskeren","coin":10,"diamond":0,"pptime":"2022-10-06T11:16:04.943Z","reviewer":false,"bought_sets":["7mscbkbzzwo7dnje6o93pyxn","h381k5vz12pldemmk548psl9","n7era7tdc895yms24gdsb9my","rz9vw2uhlcxdi4t9j6urlgw7"],"r":0,"highest_golden_medal":0}}
```

#### GET /user/{id} 获取用户

```json
{"success":true,"data":{"id":"3fc753db-f5c3-4089-8163-51eb8b37c77a","username":"Taskeren","coin":10,"diamond":0,"pptime":"2022-10-06T11:16:04.943Z","reviewer":false,"bought_sets":["7mscbkbzzwo7dnje6o93pyxn","h381k5vz12pldemmk548psl9","n7era7tdc895yms24gdsb9my","rz9vw2uhlcxdi4t9j6urlgw7"],"r":391,"highest_golden_medal":0}}
```

#### GET /user/{id}/best 获取最好成绩

默认依照 R 排序

```json
{"success":true,"data":[{"id":"e577720d-fcd7-44e1-bd9f-d5d7896bd605","player_id":"3fc753db-f5c3-4089-8163-51eb8b37c77a","chart_id":"ZwyQjZxkzLJbQFF8fv7rYCMD","perfect":104,"good":5,"miss":1197,"score":76906,"upload_time":"2022-10-16T08:21:36.207Z","r":391}]}
```

使用分数排序 `?by=score`

```json
{"success":true,"data":[{"id":"2ae222e9-8bc7-4c40-a4ed-2f32683203a0","player_id":"3fc753db-f5c3-4089-8163-51eb8b37c77a","chart_id":"ZwyQjZxkzLJbQFF8fv7rYCMD","perfect":304,"good":5,"miss":1197,"score":96906,"upload_time":"2022-10-16T08:38:16.207Z","r":129}]}
```

#### GET /user/{id}/last 获取最近成绩

返回格式和获取最好成绩相同

#### PATCH /user/{id}/username

负载

```json
{
  "password": "******",
  "newUsername": "Taskeren-3"
}
```

返回

```json
{"success":true,"data":[{"id":"2ae222e9-8bc7-4c40-a4ed-2f32683203a0","player_id":"3fc753db-f5c3-4089-8163-51eb8b37c77a","chart_id":"ZwyQjZxkzLJbQFF8fv7rYCMD","perfect":304,"good":5,"miss":1197,"score":96906,"upload_time":"2022-10-16T08:38:16.207Z","r":129}]}
```

#### PATCH /user/{id}/password

负载

```json
{
  "oldPassword": "******",
  "newPassword": "************"
}
```

返回

```json
{"success":true,"data":[{"id":"2ae222e9-8bc7-4c40-a4ed-2f32683203a0","player_id":"3fc753db-f5c3-4089-8163-51eb8b37c77a","chart_id":"ZwyQjZxkzLJbQFF8fv7rYCMD","perfect":304,"good":5,"miss":1197,"score":96906,"upload_time":"2022-10-16T08:38:16.207Z","r":129}]}
```

### 曲目和谱面相关

#### GET /set/{id} 获取曲目信息

```json
{"success":true,"data":{"id":"rz9vw2uhlcxdi4t9j6urlgw7","music_name":"TestMusic","music_composer":"Me","introduction":"","coin_price":0,"noter_name":"NoterName","child_charts":["QLi2mcC76xK9aZ8bwFDyUBCE"],"play_count":0,"publish_time":"2022-10-06T11:16:02.478Z","category":0,"hidden":false,"reviewing":false}}
```

#### GET /chart/{id} 获取谱面信息

```json
{"success":true,"data":{"id":"QLi2mcC76xK9aZ8bwFDyUBCE","difficulty_class":5,"difficulty_value":15}}
```

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

### 运行服务端

```bash
java -cp explode-all-<version>-all.jar explode2.booster.BoosterMainKt
```

插件 JAR 需要放在 `./plugins/` 目录下（与主 JAR 同目录）。
`explode-all` 的 `application` 插件默认将 `gradlew run` 的工作目录设为 `run/`。

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

## 代码风格

参考 `CONTRIBUTING.md` 与 `.editorconfig`。

- 缩进使用 **Tab**（`indent_style = tab`），而非空格。
- Kotlin 基本遵循旧版 IntelliJ IDEA Kotlin codestyle，例外：
  - 枚举与常量命名允许 `UpperCamelCase` 或 `UPPER_SNAKE_CASE`
    （正则 `[A-Z][_a-zA-Z\d]*`）。
  - 控制关键字与左括号之间**不加空格**：
    `if(true)`、`for(...)`、`while(...)`、`catch(...)`、`when(...)`。
  - 注解与被注解代码**换行**。
  - 限制 Top Level Function 的使用范围。
- Java：IntelliJ 默认规范 + Tab 缩进 + 控制关键字前不加空格。
- Markdown：尽量遵循 Google Markdown Style Guide。
- 编码：UTF-8。

## 安全与敏感信息

- MongoDB 连接字符串等敏感信息通过环境变量或本地配置文件传入，不应提交到仓库。
- `explode-proxy` 会将远程登录凭据缓存在运行目录的 `.proxy_cache` 文件中，该文件**不得上传或共享**；重置代理服务器时应删除它。
- Bomb API 存在 `Superstar` 后门配置：启用后允许一个随机 UUID 账户以任意用户身份登录，仅应在受控测试环境使用。

## 其他注意事项

- 项目声明了一个 Git 子模块 `TunerGamesEncryption`
  （指向 `git@github.com:Taskeren/Explode-Crypto.git`），
  但在当前工作树中可能未检出（路径 `TunerGamesEncryption` 不存在）。
- `src/main/kotlin/explode2/tests/Main.kt` 是开发用的临时/草稿代码，不是正式测试。
- 新开发优先参考现有模块（如 `gatekeeper` 或 `booster-plugins/maintain`）
  的目录结构与 `META-INF/services` 注册方式。
