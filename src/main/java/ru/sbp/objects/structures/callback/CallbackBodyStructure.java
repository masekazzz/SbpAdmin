package ru.sbp.objects.structures.callback;

public class CallbackBodyStructure {
    public String event;
    public Transaction transaction;

    public CallbackBodyStructure() {
    }

    public String getStatus() {
        return this.transaction.status.get("value");
    }

    public String getQrId() {
        return this.transaction.paymentParams.get("qrId");
    }

    public String getOrderId() {
        return this.transaction.orderId;
    }

    public double getAmount() {
        return this.transaction.amount;
    }
}
