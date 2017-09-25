package com.pchudzik.blog.example.springevents.more.error

import com.pchudzik.blog.example.springevents.more.AnyEvent
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import spock.lang.Specification

@SpringBootTest
class EventExceptionTest extends Specification {
	@AutowiredSpy
	EventHandler eventHandler

	@Autowired
	ApplicationEventPublisher eventPublisher

	def "Exception thrown from handler should break handlers chain"() {
		given:
		final anyEvent = new AnyEvent()

		when:
		try {
			eventPublisher.publishEvent(anyEvent)
		} catch (Exception ex) {
			assert ex.getCause().getMessage() == "expected exception"
		}

		then:
		0 * eventHandler.handleEvent(_)
	}

	@Configuration
	private static class Config {
		@Bean
		ExceptionThrowingEventHandler exceptionThrowingEventHandler() {
			new ExceptionThrowingEventHandler()
		}

		@Bean
		EventHandler eventHandler() {
			new EventHandler()
		}
	}

	private static class ExceptionThrowingEventHandler {
		@Order(1)
		@EventListener
		void handleEvent(AnyEvent anyEvent) throws Exception {
			println("exception handler " + anyEvent)
			throw new Exception("expected exception")
		}
	}

	private static class EventHandler {
		@Order(2)
		@EventListener
		void handleEvent(AnyEvent anyEvent) {
			println("default handler " + anyEvent)
		}
	}
}
