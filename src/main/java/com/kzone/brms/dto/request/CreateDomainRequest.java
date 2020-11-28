package com.kzone.brms.dto.request;

import lombok.Data;

@Data
public class CreateDomainRequest {

    private String message;
    private DomainObject domainObject;

}
