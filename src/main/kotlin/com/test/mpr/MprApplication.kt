package com.test.mpr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MprApplication

fun main(args: Array<String>) {
	runApplication<MprApplication>(*args)
}
