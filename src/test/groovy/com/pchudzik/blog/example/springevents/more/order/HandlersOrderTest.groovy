package com.pchudzik.blog.example.springevents.more.order

import com.pchudzik.blog.example.springevents.more.AnyEvent
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest
class HandlersOrderTest extends Specification {
	@Autowired
	ApplicationEventPublisher publisher

	@AutowiredSpy
	SecondHandler secondHandler

	@AutowiredSpy
	FirstHandler firstHandler

	def "handlers should be invoked in order"() {
		given:
		final event = new AnyEvent()

		when:
		publisher.publishEvent(event)

		then:
		1 * firstHandler.handleEvent(event)

		then:
		1 * secondHandler.handleEvent(event)
	}

	@Configuration
	private static class Config {
	}

	@Component
	private static class FirstHandler {
		@EventListener
		@Order(1)
		void handleEvent(AnyEvent event) {
			println("First handler " + event)
		}
	}

	@Component
	private static class SecondHandler {
		@EventListener
		@Order(2)
		void handleEvent(AnyEvent event) {
			println("Second handler " + event)
		}
	}
}
