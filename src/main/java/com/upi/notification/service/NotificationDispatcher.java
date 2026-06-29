package com.upi.notification.service;

import com.upi.notification.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final NotificationTemplateService templateService;

    public void dispatch(PaymentEvent event) {
        log.info("🔔 Processing payment event notification dispatcher for Status: {}", event.getStatus());

        switch (event.getStatus().toUpperCase()) {
            case "SUCCESS" -> {
                // Send SMS to Payer
                String payerSms = templateService.formatPayerDebitSms(event);
                sendMockSms(event.getPayerVpa(), payerSms);

                // Send SMS/Push to Payee
                String payeeSms = templateService.formatPayeeCreditSms(event);
                sendMockSms(event.getPayeeVpa(), payeeSms);
                sendMockPush(event.getPayeeVpa(), "Rs. " + (event.getAmountPaise() / 100.0) + " received!");
            }
            case "REVERSED" -> {
                // Send Reversal SMS to Payer
                String reversalSms = templateService.formatReversalSms(event);
                sendMockSms(event.getPayerVpa(), reversalSms);
            }
            case "FAILED" -> {
                // Send Failure SMS to Payer
                String failureSms = templateService.formatFailureSms(event);
                sendMockSms(event.getPayerVpa(), failureSms);
            }
            default -> log.warn("Unsupported payment event status received: {}", event.getStatus());
        }
    }

    private void sendMockSms(String vpa, String message) {
        log.info("📲 [MOCK SMS SENT TO VPA: {}] ──────► {}", vpa, message);
    }

    private void sendMockPush(String vpa, String title) {
        log.info("📱 [MOCK PUSH SENT TO VPA: {}] ──────► {}", vpa, title);
    }
}
