package ru.sbp.objects.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "qr")
@Data
@NoArgsConstructor
public class QR {
    @Id
    private String id;
    @JsonIgnore
    private String currentOrderId;
    private String redirectUrlId;
    @JsonIgnore
    private Integer orderCount;
    @JsonIgnore
    private Double totalSum;
    private String fiscType;
    @Transient
    private String additionalInfo;
    @Transient
    private String paymentDetails;

    public void updateQr(QR qr) {
        this.currentOrderId = qr.getCurrentOrderId();
        this.redirectUrlId = qr.getRedirectUrlId();
        this.orderCount = qr.getOrderCount();
        this.totalSum = qr.getTotalSum();
        this.fiscType = qr.getFiscType();
    }

    public QR(String id, String currentOrderId) {
        this.id = id;
        this.currentOrderId = currentOrderId;
    }

    public QR(String id, String currentOrderId, Integer orderCount, Double totalSum) {
        this.id = id;
        this.currentOrderId = currentOrderId;
        this.orderCount = orderCount;
        this.totalSum = totalSum;
    }

    public QR(String id) {
        this.id = id;
    }

}
