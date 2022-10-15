package explode2.booster.event

data class KtorInitEvent(
	var bindAddr: String = "0.0.0.0",
	var bindPort: Int = 10443,
)