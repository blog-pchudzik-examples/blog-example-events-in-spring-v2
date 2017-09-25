package com.pchudzik.blog.example.springevents.more.order

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import spock.lang.Specification

@SpringBootTest
class EventFromHandlerTest extends Specification {
	@AutowiredSpy
	EventHandler eventHandler

	@Autowired
	ApplicationEventPublisher eventPublisher

	def "should handle events in order"() {
		given:
		final event = new FirstEvent()

		when:
		eventPublisher.publishEvent(event)

		then:
		1 * eventHandler.handleFirstEvent(event)

		then:
		1 * eventHandler.handleAnotherEvent({ YetAnotherEvent anotherEvent ->
			anotherEvent.originalEvent == event
		})
	}


	@Configuration
	private static class Config {
	}

	static class FirstEvent {
	}

	static class YetAnotherEvent {
		private final FirstEvent originalEvent

		YetAnotherEvent(FirstEvent originalEvent) {
			this.originalEvent = originalEvent
		}
	}

	@Service
	private static class EventHandler {
		@EventListener
		YetAnotherEvent handleFirstEvent(FirstEvent event) {
			println("First event " + event)
			new YetAnotherEvent(event)
		}

		@EventListener
		void handleAnotherEvent(YetAnotherEvent event) {
			println("Another event " + event)
		}
	}
}
