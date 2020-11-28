package com.kzone.brms.dto.request;

import lombok.Data;

@Data
public class CreateDomainAttributeRequest {

    // must be a valid java name;
    private String name;
    // must be a valid java data type
    private String dataType;

}
