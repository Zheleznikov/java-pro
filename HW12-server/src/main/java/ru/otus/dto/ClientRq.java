package ru.otus.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ClientRq {
    private long id;
    private String name;
    private String address;
    private List<String> phones;

}
