package com.soundlab.core;

import java.time.Instant;

import lombok.Data;

@Data
public class GetParameterResponseDTO {
    private String name;
    private String arn;
    private String dataType;
    private String value;
    private Instant lastModifiedDate;
}
