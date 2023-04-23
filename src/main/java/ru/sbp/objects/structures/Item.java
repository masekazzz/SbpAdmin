package ru.sbp.objects.structures;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Item {
    private String name;
    private double price;
    private double quantity;
    private double amount;
    private String vatType;

    public Item() {}
    public Item(Item other) {
        this.vatType = other.getVatType();
        this.quantity = other.getQuantity();
        this.name = other.getName();
    }
}
