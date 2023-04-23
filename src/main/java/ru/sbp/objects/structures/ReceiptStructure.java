package ru.sbp.objects.structures;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "fisc.receipt-template")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@Data
public class ReceiptStructure {
    private String status;
    private String receiptNumber;
    private ClientStructure client;
    private List<Item> items;
    private double total;

    public ReceiptStructure(ReceiptStructure other) {
        this.client = new ClientStructure(other.getClient());
    }

}

