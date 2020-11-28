package com.kzone.brms.service.impl;

import com.kzone.brms.dto.request.CreateDomainRequest;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.request.DomainObject;
import com.kzone.brms.dto.response.CreateDomainResponse;
import com.kzone.brms.dto.response.CreateRuleSetResponse;
import com.kzone.brms.exception.CommonRuleCreateException;
import com.kzone.brms.exception.GenericGitException;
import com.kzone.brms.exception.GitTagAlreadyExists;
import com.kzone.brms.exception.RuleSetNotFoundException;
import com.kzone.brms.model.RuleSet;
import com.kzone.brms.repository.FileRepository;
import com.kzone.brms.repository.GitRepository;
import com.kzone.brms.repository.RuleSetRepository;
import com.kzone.brms.service.JavaCompilerService;
import com.kzone.brms.service.RuleService;
import com.kzone.brms.service.SourceCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Service
@Transactional
public class RuleServiceImpl implements RuleService {

    private static final String DEFAULT_VERSION = "/0.0.0";

    private final FileRepository fileRepository;
    private final JavaCompilerService compilerService;
    private final GitRepository gitRepository;
    private final RuleSetRepository ruleSetRepository;
    private final SourceCodeService sourceCodeService;

    @Override
    public CreateRuleSetResponse createRuleSet(CreateRuleSetRequest request) {
        String gitVersion = request.getRuleSetName()+DEFAULT_VERSION;
        if(gitRepository.isGitTagExists(gitVersion)){
            log.info("Git Tag {} already exists",gitVersion);
            throw new GitTagAlreadyExists(String.format("Git tag %s already exists ",gitVersion));
        }
        fileRepository.createRuleSet(request);
//        fileRepository.createClassDirectory(request.getRuleSetName());
        RuleSet save = ruleSetRepository.save(convertToRuleSet(request));
        try {
            gitRepository.commitAddPush(request.getRuleSetName(),"Added rule set " + request.getRuleSetName(), gitVersion);
        } catch (GenericGitException gitException) {
            fileRepository.deleteClassDirectory(request.getRuleSetName());
            fileRepository.deleteRuleSet(request.getRuleSetName());
            throw new CommonRuleCreateException(gitException.getMessage());
        }
        return new CreateRuleSetResponse(save.getId(),save.getName(),save.getDescription());
    }

    private RuleSet convertToRuleSet(CreateRuleSetRequest request){
        return new RuleSet(request.getRuleSetName(), request.getPackageName(),request.getDescription());
    }

    @Override
    public CreateDomainResponse createDomainObject(String ruleId,CreateDomainRequest request) {
        log.debug("Searching rule set from id {}",ruleId);
        Optional<RuleSet> optionalRuleSet = ruleSetRepository.findById(ruleId);
        if(optionalRuleSet.isPresent()){
            log.debug("Rule set found for id {}",ruleId);
            DomainObject domainObject = request.getDomainObject();
            RuleSet ruleSet = optionalRuleSet.get();
            String name = ruleSet.getName();
            String packageName = ruleSet.getPackageName();
            String sourceCode = sourceCodeService.createSourceCode(ruleSet, domainObject);
            fileRepository.getWriteSource(name,packageName, domainObject.getName(),sourceCode);
            gitRepository.commitAddPush(name,request.getMessage());
            return new CreateDomainResponse(ruleId,name, domainObject.getName(),sourceCode);
        }
        log.debug("Cannot find rule set for id {} ",ruleId);
        throw new RuleSetNotFoundException("Cannot find the rule set");
    }

    @Override
    public void compileRuleSet(String ruleId) {
        Optional<RuleSet> optionalRuleSet = ruleSetRepository.findById(ruleId);
        if(optionalRuleSet.isPresent()){
            RuleSet ruleSet = optionalRuleSet.get();
            fileRepository.createClassDirectory(ruleSet.getName());
            File sourceDirectory = fileRepository.getSourceDirectory(ruleSet.getName());
            compilerService.compileRuleSets(sourceDirectory);
            return;
        }
        log.debug("Cannot find rule set for id {} ",ruleId);
        throw new RuleSetNotFoundException("Cannot find the rule set");
    }

}
