package com.kzone.brms.service;

import com.kzone.brms.dto.request.CreateDomainRequest;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.request.UpdateRuleSetRequest;
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

    /**
     * Update the rule set
     * @param request to update rule set
     * @param id of the rule set
     * @return Updated value
     */
    RuleSetResponse updateRuleSet(String id,UpdateRuleSetRequest request);

    /**
     *
     * @param ruleId
     * @param request
     * @return
     */
    CreateDomainResponse createDomainObject(String ruleId,CreateDomainRequest request);

    /**
     * Compile a rule set for the given id
     * @param ruleId of the rule set
     */
    void compileRuleSet(String ruleId);

    /**
     * Select all active rule sets
     * @return list of Rule Sets
     */
    List<RuleSetResponse> getAll();

    /**
     *
     * @param id of the rules set
     * @return a RuleSetResponse if there is a rule set
     */
    RuleSetResponse getById(String id);

}
