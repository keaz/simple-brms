package com.kzone.brms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDomainAttributeRequest {

    // must be a valid java name;
    private String name;
    // must be a valid java data type
    private String dataType;

}
