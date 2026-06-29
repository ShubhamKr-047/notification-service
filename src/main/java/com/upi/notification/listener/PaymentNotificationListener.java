package com.upi.notification.listener;

import com.upi.notification.dto.PaymentEvent;
import com.upi.notification.service.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.awspring.cloud.sqs.annotation.SqsListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentNotificationListener {

    private final NotificationDispatcher notificationDispatcher;

    @SqsListener("${sqs.queue.payment-notifications}")
    public void onMessage(PaymentEvent event) {
        log.info("Received payment event from SQS: {}", event);

        // Simulated failure triggering mechanism for testing retries & DLQ
        if (event.getPayerVpa() != null && event.getPayerVpa().contains("fail")) {
            log.warn("🚨 [SIMULATED FAILURE] Processing failed for VPA: {}. Retrying...", event.getPayerVpa());
            throw new RuntimeException("Simulated SQS message processing failure");
        }

        notificationDispatcher.dispatch(event);
    }
}
