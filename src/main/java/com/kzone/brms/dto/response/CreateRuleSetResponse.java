package com.kzone.brms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateRuleSetResponse {

    private String id;
    private String ruleSetName;
    private String packageName;


}
