package ru.sbp.objects.db;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qr_user_mapping")
@Data
@NoArgsConstructor
public class QrUserMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long userId;
    private String qrId;

    public QrUserMapping(Long userId, String qrId){
        this.userId = userId;
        this.qrId = qrId;
    }
}
