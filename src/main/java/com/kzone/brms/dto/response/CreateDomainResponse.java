package com.kzone.brms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateDomainResponse {

    private String ruleId;
    private String ruleSetName;
    private String domainObject;
    private String generatedSource;

}
