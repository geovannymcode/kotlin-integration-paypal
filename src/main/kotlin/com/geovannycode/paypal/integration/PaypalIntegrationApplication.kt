package com.geovannycode.paypal.integration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaypalIntegrationApplication

fun main(args: Array<String>) {
	runApplication<PaypalIntegrationApplication>(*args)
}
