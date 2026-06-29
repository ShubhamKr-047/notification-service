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
        notificationDispatcher.dispatch(event);
    }
}
