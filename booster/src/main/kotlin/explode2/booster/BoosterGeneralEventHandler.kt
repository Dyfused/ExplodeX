package explode2.booster

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.SubscriberExceptionEvent
import org.slf4j.MarkerFactory

object BoosterGeneralEventHandler {

	private val SubscriberExceptionMarker = MarkerFactory.getMarker("SubscriberException")

	@Subscribe
	fun onException(e: SubscriberExceptionEvent) {
		val event = e.causingEvent
		val subscriber = e.causingSubscriber
		val exception = e.throwable
		logger.warn(
			SubscriberExceptionMarker,
			"Exception occurred when dispatching event ${event.javaClass.simpleName} to ${subscriber.javaClass.simpleName}.",
			exception
		)
	}

}