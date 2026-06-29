package com.upi.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    private UUID transactionId;
    private String payerVpa;
    private String payeeVpa;
    private Long amountPaise;
    private String status; // SUCCESS, FAILED, REVERSED
    private String payerRrn;
    private String payeeRrn;
    private String failureReason;
}
