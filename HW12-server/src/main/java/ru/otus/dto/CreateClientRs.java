package ru.otus.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateClientRs {
    private String name;
    private String phone;
    private String street;
}
