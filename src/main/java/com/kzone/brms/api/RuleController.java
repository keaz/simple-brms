package com.kzone.brms.api;

import com.kzone.brms.dto.request.CreateDomainRequest;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.response.CreateDomainResponse;
import com.kzone.brms.dto.response.CreateRuleSetResponse;
import com.kzone.brms.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/rule")
@RequiredArgsConstructor
public class RuleController {

    public final RuleService ruleService;

    @PostMapping
    public ResponseEntity<CreateRuleSetResponse> createRuleSet(@RequestBody CreateRuleSetRequest request){
        return new ResponseEntity<>(ruleService.createRuleSet(request), HttpStatus.CREATED);
    }

    @PostMapping("{ruleId}/domain")
    public ResponseEntity<CreateDomainResponse> createDomain(@PathVariable("ruleId") String ruleId, @RequestBody CreateDomainRequest request){
        return new ResponseEntity<>(ruleService.createDomainObject(ruleId,request), HttpStatus.CREATED);
    }

    @PostMapping("{ruleId}/compile")
    public ResponseEntity<Void> compileRuleSet(@PathVariable("ruleId") String ruleId){
        ruleService.compileRuleSet(ruleId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
