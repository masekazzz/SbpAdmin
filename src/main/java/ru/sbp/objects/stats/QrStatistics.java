package ru.sbp.objects.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sbp.objects.db.Order;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrStatistics {

    private Double totalSum;
    private Integer orderCount;
    private List<Order> orders;
}
