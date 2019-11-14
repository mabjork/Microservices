package no.mabjork.zipkin_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ZipkinServiceApplication

fun main(args: Array<String>) {
	runApplication<ZipkinServiceApplication>(*args)
}
