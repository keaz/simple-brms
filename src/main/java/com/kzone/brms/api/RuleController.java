package com.kzone.brms.api;

import com.kzone.brms.dto.request.CreateDomainRequest;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.response.CreateDomainResponse;
import com.kzone.brms.dto.response.CreateRuleSetResponse;
import com.kzone.brms.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/rule")
@RequiredArgsConstructor
public class RuleController {

    public final RuleService ruleService;

    @PostMapping
    public ResponseEntity<CreateRuleSetResponse> createRuleSet(@RequestBody CreateRuleSetRequest request){
        return new ResponseEntity<CreateRuleSetResponse>(ruleService.createRuleSet(request), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<CreateDomainResponse> createDomain(@RequestBody CreateDomainRequest request){
        return new ResponseEntity<CreateDomainResponse>(ruleService.createDomainObject(request), HttpStatus.CREATED);
    }


}
