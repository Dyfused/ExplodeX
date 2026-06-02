# 开发用 README

## 子模块命名规范

### 加载器 Booster

`booster` 是加载器模块，可以类比与 Minecraft 的服务器核心，以 `booster-` 开头的模块就是在加载器下面跑的具体内容，类似于服务器中的插件。

在插件模块中需要有一个类继承 `explode2.booster.BoosterPlugin`，且有无参构造函数，然后在 `META-INF/services` 中添加文件 `explode2.booster.BoosterPlugin`，内容为该类的位置。

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

根请求地址与配置有关，默认模板为 `bomb/v{version}`，支持两个变量 `{version}` 是大版本号，目前是 2，`{version_patch}` 是小版本号，目前是 0。

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