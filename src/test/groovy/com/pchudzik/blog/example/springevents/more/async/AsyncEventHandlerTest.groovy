package com.pchudzik.blog.example.springevents.more.async

import com.pchudzik.blog.example.springevents.more.AnyEvent
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Component
import spock.lang.Specification

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest
class AsyncEventHandlerTest extends Specification {
	@Autowired
	ApplicationEventPublisher eventPublisher

	@Autowired
	CountDownLatch latch

	def "should execute all asynchronous handlers"() {
		given:
		final event = new AnyEvent()

		when:
		eventPublisher.publishEvent(event)

		then:
		latch.await(2, TimeUnit.SECONDS)
	}

	@Configuration
	@EnableAsync
	private static class Config {
		@Bean
		CountDownLatch latch() {
			new CountDownLatch(2)
		}

		@Bean
		DoNothingHandler doNothingHandler() {
			new DoNothingHandler(latch())
		}

		@Bean
		AnotherDoNothingHandler anotherDoNothingHandler() {
			new AnotherDoNothingHandler(latch())
		}
	}

	@Slf4j
	@TupleConstructor
	private static class DoNothingHandler {
		CountDownLatch latch

		@Async
		@EventListener
		void handleEvent(AnyEvent event) {
			log.info("Do nothing handler {}", event)
			latch.countDown()
		}
	}

	@Slf4j
	@TupleConstructor
	private static class AnotherDoNothingHandler {
		CountDownLatch latch

		@Async
		@EventListener
		void handleEvent(AnyEvent event) {
			log.info("Another do nothing handler {}", event)
			latch.countDown()
		}
	}
}
