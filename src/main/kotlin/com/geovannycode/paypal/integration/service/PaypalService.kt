package com.geovannycode.paypal.integration.service

import com.geovannycode.paypal.integration.dto.request.CreatePaymentRequest
import com.paypal.api.payments.Payment
import com.paypal.api.payments.Payer
import com.paypal.api.payments.Transaction
import com.paypal.api.payments.Amount
import com.paypal.api.payments.RedirectUrls
import com.paypal.api.payments.PaymentExecution
import com.paypal.base.rest.APIContext
import com.paypal.base.rest.PayPalRESTException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class PaypalService(private val apiContext: APIContext? = null) {

   fun createPayment(request: CreatePaymentRequest): Payment {
       logger.info(LOG_INIT_CREATE_PAYMENT, request)
       try {
           return Payment().apply {
               intent = request.intent
               payer = Payer().apply {
                   paymentMethod = request.method
               }
               transactions = mutableListOf(Transaction().apply {
                   description = request.description
                   amount = Amount().apply {
                       currency = request.currency
                       total = String.format(Locale.forLanguageTag(request.currency), "%.2f", request.total)
                   }
               })
               redirectUrls = RedirectUrls().apply {
                   cancelUrl = request.cancelUrl
                   returnUrl = request.successUrl
               }
           }.run {
               create(apiContext).also {
                   logger.info(LOG_END_CREATE_PAYMENT,apiContext)
               }
           }
       } catch (ex: PayPalRESTException) {
           throw Exception(LOG_ERROR_CREATING_PAYMENT, ex)
       }
   }


    fun executePayment(paymentId: String, payerId: String): Payment {
        logger.info(LOG_INIT_EXECUTE_PAYMENT, paymentId, payerId)
        try {
            return Payment().apply { id = paymentId }.run {
                val paymentExecution = PaymentExecution().apply { this.payerId = payerId }
                execute(apiContext, paymentExecution)
            }.also {
                logger.info(LOG_END_EXECUTE_PAYMENT, paymentId, payerId)
            }
        } catch (ex: PayPalRESTException) {
            throw Exception(LOG_ERROR_EXECUTE_PAYMENT, ex)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PaypalService::class.java)
        private const val LOG_INIT_CREATE_PAYMENT = "PaypalService::createPayment -- INIT - request: [{}]"
        private const val LOG_ERROR_CREATING_PAYMENT = "Error creating payment - error: [{}]"
        private const val LOG_END_CREATE_PAYMENT = "PaypalService::createPayment -- END - response: [{}]"
        private const val LOG_ERROR_EXECUTE_PAYMENT = "Error when executing payment - error: [{}]"
        private const val LOG_INIT_EXECUTE_PAYMENT = "PaypalService::executePayment -- INIT - paymentId: [{}] - payerId: [{}]"
        private const val LOG_END_EXECUTE_PAYMENT = "PaypalService::executePayment -- END - paymentId: [{}] - payerId: [{}]"
    }
}