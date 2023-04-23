package ru.sbp.objects.structures;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sbp.objects.db.QR;

@Data
@NoArgsConstructor
public class OrderStructure {

    private String id;
    private double amount;
    private QR qr;
    private StatusStructure status;

    public OrderStructure(QR qr) {
        this.qr = qr;
    }

}
