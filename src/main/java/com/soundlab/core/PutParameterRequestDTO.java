package com.soundlab.core;

import lombok.Data;

@Data
public class PutParameterRequestDTO {
    private String name;
    private String description;
    private String value;
}
