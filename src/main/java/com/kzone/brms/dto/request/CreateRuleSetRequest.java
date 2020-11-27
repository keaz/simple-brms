package com.kzone.brms.dto.request;

import lombok.Data;

@Data
public class CreateRuleSetRequest {

    private String ruleSetName;
    private String description;
    private String packageName;


}
