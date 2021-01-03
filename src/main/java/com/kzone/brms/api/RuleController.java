package com.kzone.brms.api;

import com.kzone.brms.dto.request.CreateDomainRequest;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.request.UpdateRuleSetRequest;
import com.kzone.brms.dto.response.CreateDomainResponse;
import com.kzone.brms.dto.response.RuleSetResponse;
import com.kzone.brms.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/rule")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RuleController {

    public final RuleService ruleService;

    @PostMapping
    public ResponseEntity<RuleSetResponse> createRuleSet(@RequestBody CreateRuleSetRequest request){
        return new ResponseEntity<>(ruleService.createRuleSet(request), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<RuleSetResponse> updateRuleSet(@PathVariable("id") String id,@RequestBody UpdateRuleSetRequest request){
        return new ResponseEntity<>(ruleService.updateRuleSet(id,request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RuleSetResponse>> getAll(){
        return new ResponseEntity<>(ruleService.getAll(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<RuleSetResponse> getById(@PathVariable("id") String id){
        return new ResponseEntity<>(ruleService.getById(id), HttpStatus.OK);
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
