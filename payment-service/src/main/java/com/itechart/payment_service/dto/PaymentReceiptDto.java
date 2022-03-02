package com.itechart.payment_service.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Set;

@Data
public class PaymentReceiptDto
{
    private Long id;

    @NotNull(message = "Order ID is required")
    @NotBlank(message = "Order ID can't be empty")
    @Min(value = 1L, message = "Order ID min limit exceeded")
    @Max(value = Long.MAX_VALUE, message = "Order ID max limit exceeded")
    private Long orderId;

    @NotNull(message = "Receipt status is required")
    @NotBlank(message = "Status can't be empty")
    @Size(max = 20, message = "Status string length exceeded")
    private String receiptStatus;

    private Set<PaymentAttemptDto> paymentAttempts;
}
