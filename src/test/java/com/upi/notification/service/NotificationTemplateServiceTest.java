package com.upi.notification.service;

import com.upi.notification.dto.PaymentEvent;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationTemplateServiceTest {

    private final NotificationTemplateService templateService = new NotificationTemplateService();

    @Test
    public void testFormatPayerDebitSms() {
        PaymentEvent event = PaymentEvent.builder()
                .transactionId(UUID.fromString("f7b8c9d0-1a2b-3c4d-5e6f-7a8b9c0d1e2f"))
                .payerVpa("rahul@okaxis")
                .payeeVpa("mina@okhdfcbank")
                .amountPaise(125050L) // ₹1,250.50
                .payerRrn("RRN999999999999")
                .status("SUCCESS")
                .build();

        String sms = templateService.formatPayerDebitSms(event);
        assertNotNull(sms);
        assertTrue(sms.contains("debited by Rs. 1,250.50"));
        assertTrue(sms.contains("transfer to VPA mina@okhdfcbank"));
        assertTrue(sms.contains("RRN: RRN999999999999"));
    }

    @Test
    public void testFormatPayeeCreditSms() {
        PaymentEvent event = PaymentEvent.builder()
                .transactionId(UUID.fromString("f7b8c9d0-1a2b-3c4d-5e6f-7a8b9c0d1e2f"))
                .payerVpa("rahul@okaxis")
                .payeeVpa("mina@okhdfcbank")
                .amountPaise(50000L) // ₹500.00
                .payeeRrn("RRN888888888888")
                .status("SUCCESS")
                .build();

        String sms = templateService.formatPayeeCreditSms(event);
        assertNotNull(sms);
        assertTrue(sms.contains("credited with Rs. 500.00"));
        assertTrue(sms.contains("from VPA rahul@okaxis"));
        assertTrue(sms.contains("RRN: RRN888888888888"));
    }

    @Test
    public void testFormatReversalSms() {
        PaymentEvent event = PaymentEvent.builder()
                .transactionId(UUID.fromString("f7b8c9d0-1a2b-3c4d-5e6f-7a8b9c0d1e2f"))
                .payerVpa("rahul@okaxis")
                .payeeVpa("mina@okhdfcbank")
                .amountPaise(100000L) // ₹1,000.00
                .payerRrn("RRN111111111111")
                .status("REVERSED")
                .build();

        String sms = templateService.formatReversalSms(event);
        assertNotNull(sms);
        assertTrue(sms.contains("refunded/reversed"));
        assertTrue(sms.contains("Rs. 1,000.00"));
        assertTrue(sms.contains("RRN: RRN111111111111"));
    }
}
