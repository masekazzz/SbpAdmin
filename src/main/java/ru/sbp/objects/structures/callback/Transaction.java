package ru.sbp.objects.structures.callback;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class Transaction {
    public double id;
    public String orderId;
    public Map<String, String> status;
    public String paymentMethod;
    public Map<String, String> paymentParams;
    public double amount;
}
