package com.geovannycode.paypal.integration.config

import com.paypal.base.rest.APIContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PaypalConfig(
    @Value("\${paypal.client-id}")
    private val clientId: String,
    @Value("\${paypal.client-secret}")
    private val clientSecret: String,
    @Value("\${paypal.mode}")
    private val mode: String
){
    @Bean
    fun apiContext(): APIContext {
        return APIContext(clientId, clientSecret, mode)
    }
}