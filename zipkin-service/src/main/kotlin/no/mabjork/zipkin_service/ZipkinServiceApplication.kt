package no.mabjork.zipkin_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import zipkin2.server.internal.EnableZipkinServer


@SpringBootApplication
@EnableZipkinServer
class ZipkinServiceApplication

fun main(args: Array<String>) {
	runApplication<ZipkinServiceApplication>(*args)
}
