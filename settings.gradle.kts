
rootProject.name = "explode2+"
include("gateau")
include("labyrinth")
include("labyrinth-mongodb")
include("booster")
include("resource")
includePlugin("maintain")  // booster-plugins/maintain -> :maintain
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
