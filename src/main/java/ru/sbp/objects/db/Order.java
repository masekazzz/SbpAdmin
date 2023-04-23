package ru.sbp.objects.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    @Id
    private String orderId;
    private String qrId;
    private String fiscalization;
    private String receiptId;
    private String phone;
    private boolean isDone;
    private Timestamp buyDate;
    private double amount;


    public boolean getIsDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public void updateOrder(Order order) {
        this.qrId = order.getQrId();
        this.fiscalization = order.getFiscalization();
        this.receiptId = order.getReceiptId();
        this.phone = order.getPhone();
        this.isDone = order.getIsDone();
        this.buyDate = order.getBuyDate();
        this.amount = order.getAmount();
    }
}
