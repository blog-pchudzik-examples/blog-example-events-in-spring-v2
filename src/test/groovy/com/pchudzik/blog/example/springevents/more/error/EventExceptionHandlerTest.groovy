package com.pchudzik.blog.example.springevents.more.error

import com.pchudzik.blog.example.springevents.more.AnyEvent
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.EventListener
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.core.annotation.Order
import org.springframework.scheduling.support.TaskUtils
import org.springframework.stereotype.Component
import spock.lang.Specification

@SpringBootTest
class EventExceptionHandlerTest extends Specification {
	@AutowiredSpy
	SimpleEventHandler defaultEventHandler

	@Autowired
	ApplicationEventPublisher eventPublisher

	def "Exception thrown from handler should break handlers chain"() {
		given:
		final anyEvent = new AnyEvent()

		when:
		eventPublisher.publishEvent(anyEvent)

		then:
		1 * defaultEventHandler.handleEvent(_)
	}

	@Configuration
	private static class Config {
		@Bean
		ApplicationEventMulticaster applicationEventMulticaster() {
			final multicaster = new SimpleApplicationEventMulticaster()
			multicaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER)
			return multicaster
		}
	}

	@Component
	private static class ExceptionThrowingEventHandler {
		@Order(1)
		@EventListener
		void handleEvent(AnyEvent anyEvent) throws Exception {
			println("exception handler " + anyEvent)
			throw new Exception("expected exception")
		}
	}

	@Component
	private static class SimpleEventHandler {
		@Order(2)
		@EventListener
		void handleEvent(AnyEvent anyEvent) {
			println("default handler " + anyEvent)
		}
	}
}
