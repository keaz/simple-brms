package com.kzone.brms.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateDomainRequest {

    private String ruleSetId;
    private String name;
    private List<CreateDomainAttributeRequest> attributes;

}
