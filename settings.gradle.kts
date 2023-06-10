
rootProject.name = "explode3"
include("gateau")
include("labyrinth")
include("labyrinth-mongodb")
include("booster")
include("resource")
include("booster-maintain")
include("gatekeeper")
include("explode-all")
include("explode-proxy")

include("dynamite-cli")
project(":dynamite-cli").projectDir = file("devtools/dynamite-cli")

fun includePlugin(pluginName: String) {
	include(pluginName)
	project(":$pluginName").projectDir = file("booster-plugins/$pluginName")
}

includePlugin("maintain")
includePlugin("url-redirect-resource")
includePlugin("aliyun-oss-resource")

// TunerGames 加密解密库
include("tunergames-encryption")
project(":tunergames-encryption").projectDir = file("TunerGamesEncryption")
