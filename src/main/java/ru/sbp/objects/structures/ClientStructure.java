package ru.sbp.objects.structures;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ClientStructure {
    private String email;
    private String name;

    private String phone;

    public ClientStructure() {
    }

    public ClientStructure(ClientStructure other) {
        this.email = other.getEmail();
        this.name = other.getName();
    }

    public ClientStructure(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
