package com.kzone.brms.dto.request;

import lombok.Data;

import java.util.stream.Stream;

@Data
public class CreateDomainAttributeRequest {

    // must be a valid java name;
    private Stream name;
    // must be a valid java data type
    private String dataType;

}
