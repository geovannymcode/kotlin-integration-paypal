package com.geovannycode.paypal.integration.controller

import com.geovannycode.paypal.integration.dto.request.CreatePaymentRequest
import com.geovannycode.paypal.integration.service.PaypalService
import com.paypal.base.rest.PayPalRESTException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView

@Controller
class PaypalController(private val paypalService: PaypalService){

    @GetMapping("/")
    fun home(): String {
        return "index"
    }

    @PostMapping("/payment/create")
    fun createPayment(
        @RequestParam("method") method: String,
        @RequestParam("amount") amount: String,
        @RequestParam("currency") currency: String,
        @RequestParam("description") description: String
    ): RedirectView {
        try {
            val cancelUrl = "http://localhost:8080/payment/cancel"
            val successUrl = "http://localhost:8080/payment/success"
            val request = CreatePaymentRequest(
                total = amount.toBigDecimal(),
                currency = currency,
                method = method,
                intent = "sale",
                description = description,
                cancelUrl = cancelUrl,
                successUrl = successUrl
            )
            val payment = paypalService.createPayment(request)
                for (links in payment.links ?: emptyList()) {
                    if (links.rel == "approval_url") {
                        return RedirectView(links.href)
                    }
                }
        } catch (ex: PayPalRESTException) {
            logger.error(LOG_ERROR_CREATE_PAYMENT, ex.message)
        }
        return RedirectView("/payment/error")
    }

    @GetMapping("/payment/success")
    fun paymentSuccess(
        @RequestParam("paymentId") paymentId: String,
        @RequestParam("PayerID") payerId: String
    ): String {
        try {
            val payment = paypalService.executePayment(paymentId, payerId)
            if (payment.state == "approved") {
                return "paymentSuccess"
            }
        } catch (ex: PayPalRESTException) {
            logger.error(LOG_ERROR_SUCCESS_PAYMENT, ex.message)
        }
        return "paymentSuccess"
    }

    @GetMapping("/payment/cancel")
    fun paymentCancel(): String {
        return "paymentCancel"
    }

    @GetMapping("/payment/error")
    fun paymentError(): String {
        return "paymentError"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PaypalController::class.java)
        private const val LOG_ERROR_CREATE_PAYMENT = "PaypalController::createPayment Error occurred - error: [{}]"
        private const val LOG_ERROR_SUCCESS_PAYMENT = "PaypalController::paymentSuccess Error occurred - error: [{}]"
    }
}