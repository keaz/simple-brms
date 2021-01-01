package com.kzone.brms.service.impl;

import com.kzone.brms.dto.request.CreateDomainRequest;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.request.DomainObject;
import com.kzone.brms.dto.response.CreateDomainResponse;
import com.kzone.brms.dto.response.RuleSetResponse;
import com.kzone.brms.exception.*;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public RuleSetResponse createRuleSet(CreateRuleSetRequest request) {
        String gitVersion = request.getRuleSetName() + DEFAULT_VERSION;
        if(ruleSetRepository.existsByName(request.getRuleSetName())){
            throw new RuleSetExistsException("Rule set already exists");
        }

        if (gitRepository.isGitTagExists(gitVersion)) {
            log.info("Git Tag {} already exists", gitVersion);
            throw new GitTagAlreadyExists(String.format("Git tag %s already exists ", gitVersion));
        }
        fileRepository.createRuleSet(request);
        RuleSet save = ruleSetRepository.save(convertToRuleSet(request));
        try {
            gitRepository.commitAddPush(request.getRuleSetName(), "Added rule set " + request.getRuleSetName(), gitVersion);
        } catch (GenericGitException gitException) {
            fileRepository.deleteClassDirectory(request.getRuleSetName());
            fileRepository.deleteRuleSet(request.getRuleSetName());
            throw new CommonRuleCreateException(gitException.getMessage());
        }
        return new RuleSetResponse(save.getId(), save.getName(), save.getDescription());
    }

    private RuleSet convertToRuleSet(CreateRuleSetRequest request) {
        return new RuleSet(request.getRuleSetName(), request.getPackageName(), request.getDescription());
    }

    @Override
    public CreateDomainResponse createDomainObject(String ruleId, CreateDomainRequest request) {
        log.debug("Searching rule set from id {}", ruleId);
        Optional<RuleSet> optionalRuleSet = ruleSetRepository.findById(ruleId);
        if (optionalRuleSet.isPresent()) {
            log.debug("Rule set found for id {}", ruleId);
            DomainObject domainObject = request.getDomainObject();
            RuleSet ruleSet = optionalRuleSet.get();
            String name = ruleSet.getName();
            String packageName = ruleSet.getPackageName();
            String sourceCode = sourceCodeService.createSourceCode(ruleSet, domainObject);
            fileRepository.getWriteSource(name, packageName, domainObject.getName(), sourceCode);
            gitRepository.commitAddPush(name, request.getMessage());
            return new CreateDomainResponse(ruleId, name, domainObject.getName(), sourceCode);
        }
        log.debug("Cannot find rule set for id {} ", ruleId);
        throw new RuleSetNotFoundException("Cannot find the rule set");
    }

    @Override
    public void compileRuleSet(String ruleId) {
        Optional<RuleSet> optionalRuleSet = ruleSetRepository.findById(ruleId);
        if (optionalRuleSet.isPresent()) {
            RuleSet ruleSet = optionalRuleSet.get();
            fileRepository.createClassDirectory(ruleSet.getName());
            File sourceDirectory = fileRepository.getSourceDirectory(ruleSet.getName());
            compilerService.compileRuleSets(sourceDirectory);
            return;
        }
        log.debug("Cannot find rule set for id {} ", ruleId);
        throw new RuleSetNotFoundException("Cannot find the rule set");
    }

    @Override
    public List<RuleSetResponse> getAll() {
        List<RuleSet> ruleSets = ruleSetRepository.findAll();
        if (ruleSets.isEmpty()) {
            log.debug("No result sets in the repository");
            return Collections.emptyList();
        }

        return ruleSets.stream().map(ruleSet ->
                new RuleSetResponse(ruleSet.getId(), ruleSet.getName(), ruleSet.getDescription())
        ).collect(Collectors.toList());
    }

}
