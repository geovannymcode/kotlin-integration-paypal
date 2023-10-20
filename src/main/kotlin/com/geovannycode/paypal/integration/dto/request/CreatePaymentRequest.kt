package com.geovannycode.paypal.integration.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreatePaymentRequest(
    val total : BigDecimal,
    val currency: String,
    val method: String,
    val intent: String,
    val description: String,
    val cancelUrl: String,
    val successUrl: String
)
