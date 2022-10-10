# 开发用 README

## 子模块命名规范

### 加载器 Booster

`booster` 是加载器模块，可以类比与 Minecraft 的服务器核心，以 `booster-` 开头的模块就是在加载器下面跑的具体内容，类似于服务器中的插件。

在插件模块中需要有一个类继承 `explode2.booster.BoosterPlugin`，且有无参构造函数，然后在 `META-INF/services` 中添加文件 `explode2.booster.BoosterPlugin`，内容为该类的位置。

### Gateau Business Objects

原文应该是 `gâteau`，内容是业务对象。

### 数据提供 Labyrinth 

