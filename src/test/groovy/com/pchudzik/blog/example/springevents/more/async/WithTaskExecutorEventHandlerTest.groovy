package com.pchudzik.blog.example.springevents.more.async

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.EventListener
import org.springframework.context.event.SimpleApplicationEventMulticaster
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
class WithTaskExecutorEventHandlerTest extends Specification {
	@Autowired
	ApplicationEventPublisher eventPublisher

	@AutowiredSpy
	DoNothingHandler doNothingHandler

	@AutowiredSpy
	ExceptionThrowingHandler exceptionThrowingHandler

	def "should execute all asynchronous handlers"() {
		given:
		final latch = new CountDownLatch(2)

		when:
		eventPublisher.publishEvent(latch)

		then:
		latch.await(2, TimeUnit.SECONDS)

		then:
		1 * exceptionThrowingHandler.handleEvent(latch)
		1 * doNothingHandler.handleEvent(latch)
	}

	@Configuration
	private static class Config {
		private static final int FOUR_THREADS = 4

		@Bean
		ExceptionThrowingHandler exceptionThrowingHandler() {
			new ExceptionThrowingHandler()
		}

		@Bean
		DoNothingHandler doNothingHandler() {
			new DoNothingHandler()
		}

		@Bean
		ApplicationEventMulticaster applicationEventMulticaster() {
			final multicaster = new SimpleApplicationEventMulticaster()
			multicaster.setErrorHandler({ ex -> ex.printStackTrace()})
			multicaster.setTaskExecutor(Executors.newFixedThreadPool(FOUR_THREADS))
			return multicaster
		}
	}

	@Slf4j
	static class ExceptionThrowingHandler {
		@EventListener
		void handleEvent(CountDownLatch latch) {
			log.info("Exception throwing handler " + latch.toString())
			latch.countDown()
			throw new Exception("Expected exception")
		}
	}

	@Slf4j
	static class DoNothingHandler {
		@EventListener
		void handleEvent(CountDownLatch latch) {
			log.info("Do nothing handler " + latch.toString())
			latch.countDown()
		}
	}
}
