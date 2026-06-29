package com.upi.notification.service;

import com.upi.notification.dto.PaymentEvent;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@Service
public class NotificationTemplateService {

    private final DecimalFormat rupeeFormat = new DecimalFormat("##,##,##0.00");

    public String formatPayerDebitSms(PaymentEvent event) {
        double amountRupees = event.getAmountPaise() / 100.0;
        return String.format(
                "Dear Customer, your account has been debited by Rs. %s for transfer to VPA %s. Txn ID: %s. RRN: %s.",
                rupeeFormat.format(amountRupees),
                event.getPayeeVpa(),
                event.getTransactionId(),
                event.getPayerRrn() != null ? event.getPayerRrn() : "N/A"
        );
    }

    public String formatPayeeCreditSms(PaymentEvent event) {
        double amountRupees = event.getAmountPaise() / 100.0;
        return String.format(
                "Dear Customer, your account has been credited with Rs. %s from VPA %s. Txn ID: %s. RRN: %s.",
                rupeeFormat.format(amountRupees),
                event.getPayerVpa(),
                event.getTransactionId(),
                event.getPayeeRrn() != null ? event.getPayeeRrn() : "N/A"
        );
    }

    public String formatReversalSms(PaymentEvent event) {
        double amountRupees = event.getAmountPaise() / 100.0;
        return String.format(
                "Dear Customer, Rs. %s debited from your account has been refunded/reversed due to credit timeout to VPA %s. Txn ID: %s. RRN: %s.",
                rupeeFormat.format(amountRupees),
                event.getPayeeVpa(),
                event.getTransactionId(),
                event.getPayerRrn() != null ? event.getPayerRrn() : "N/A"
        );
    }

    public String formatFailureSms(PaymentEvent event) {
        double amountRupees = event.getAmountPaise() / 100.0;
        return String.format(
                "Dear Customer, transaction of Rs. %s to VPA %s failed. Reason: %s. Txn ID: %s.",
                rupeeFormat.format(amountRupees),
                event.getPayeeVpa(),
                event.getFailureReason() != null ? event.getFailureReason() : "Declined",
                event.getTransactionId()
        );
    }
}
