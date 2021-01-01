package com.kzone.brms.service;

import com.kzone.brms.dto.request.CreateDomainRequest;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.response.CreateDomainResponse;
import com.kzone.brms.dto.response.RuleSetResponse;

import java.util.List;

public interface RuleService {


    /**
     * Use to create a new rule set. This will create a new folder in the git repo.
     * @param request to create new rule set
     * @return the response
     */
    RuleSetResponse createRuleSet(CreateRuleSetRequest request);

    CreateDomainResponse createDomainObject(String ruleId,CreateDomainRequest request);

    void compileRuleSet(String ruleId);

    /**
     * Select all active rule sets
     * @return list of Rule Sets
     */
    List<RuleSetResponse> getAll();

}
