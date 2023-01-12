
rootProject.name = "explode2"
include("gateau")
include("labyrinth")
include("labyrinth-mongodb")
include("booster")
include("booster-graphql")
include("booster-maintain")
include("gatekeeper")
include("explode-all")
include("booster-resource")
include("explode-proxy")
include("booster-bomb")
include("booster-resource-redirect")

include("dynamite-cli")
project(":dynamite-cli").projectDir = file("devtools/dynamite-cli")

include("aliyun-oss-resource")
project(":aliyun-oss-resource").projectDir = file("booster-plugins/aliyun-oss-resource")
