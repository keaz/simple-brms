package com.kzone.brms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RuleSetResponse {

    private String id;
    private String ruleSetName;
    private String description;


}
