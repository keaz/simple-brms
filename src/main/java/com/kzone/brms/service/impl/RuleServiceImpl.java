package com.kzone.brms.service.impl;

import com.kzone.brms.dto.request.CreateDomainRequest;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.request.DomainObject;
import com.kzone.brms.dto.request.UpdateRuleSetRequest;
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
    public static final String CANNOT_FIND_THE_RULE_SET = "Cannot find the rule set";

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
        return new RuleSetResponse(save.getId(), save.getName(), save.getDescription(),save.getPackageName());
    }

    @Override
    public RuleSetResponse updateRuleSet(String id,UpdateRuleSetRequest request) {
        Optional<RuleSet> optional = ruleSetRepository.findById(id);
        RuleSet ruleSet = optional.orElseThrow(() -> new RuleSetNotFoundException(CANNOT_FIND_THE_RULE_SET));

        File sourceDirectory = fileRepository.getSourceDirectory(ruleSet.getName());
        List<File> javaSourceFiles = fileRepository.getJavaSourceFiles(sourceDirectory);
        //We don't allow to change package name when there is a java source file
        if(!javaSourceFiles.isEmpty() && !ruleSet.getPackageName().equals(request.getPackageName())){
            throw new RuleUpdateException("Cannot update package when having source files");
        }
        ruleSet.setDescription(request.getDescription());
        ruleSet.setPackageName(request.getPackageName());
        RuleSet save = ruleSetRepository.save(ruleSet);

        return new RuleSetResponse(save.getId(), save.getName(), save.getDescription(),save.getPackageName());
    }

    private RuleSet convertToRuleSet(CreateRuleSetRequest request) {
        return new RuleSet(request.getRuleSetName(), request.getPackageName(), request.getDescription());
    }

    @Override
    public CreateDomainResponse createDomainObject(String ruleId, CreateDomainRequest request) {
        log.debug("Searching rule set from id {}", ruleId);
        Optional<RuleSet> optionalRuleSet = ruleSetRepository.findById(ruleId);
        RuleSet ruleSet = optionalRuleSet.orElseThrow(() -> new RuleSetNotFoundException(CANNOT_FIND_THE_RULE_SET));

        log.debug("Rule set found for id {}", ruleId);
        DomainObject domainObject = request.getDomainObject();
        String name = ruleSet.getName();
        String packageName = ruleSet.getPackageName();
        String sourceCode = sourceCodeService.createSourceCode(ruleSet, domainObject);
        fileRepository.getWriteSource(name, packageName, domainObject.getName(), sourceCode);
        gitRepository.commitAddPush(name, request.getMessage());
        return new CreateDomainResponse(ruleId, name, domainObject.getName(), sourceCode);
    }

    @Override
    public void compileRuleSet(String ruleId) {
        Optional<RuleSet> optionalRuleSet = ruleSetRepository.findById(ruleId);
        RuleSet ruleSet = optionalRuleSet.orElseThrow(() -> new RuleSetNotFoundException(CANNOT_FIND_THE_RULE_SET));
        fileRepository.createClassDirectory(ruleSet.getName());
        File sourceDirectory = fileRepository.getSourceDirectory(ruleSet.getName());
        List<File> javaSourceFiles = fileRepository.getJavaSourceFiles(sourceDirectory);
        compilerService.compileRuleSets(sourceDirectory,javaSourceFiles);
    }

    @Override
    public List<RuleSetResponse> getAll() {
        List<RuleSet> ruleSets = ruleSetRepository.findAll();
        if (ruleSets.isEmpty()) {
            log.debug("No result sets in the repository");
            return Collections.emptyList();
        }

        return ruleSets.stream().map(ruleSet ->
                new RuleSetResponse(ruleSet.getId(), ruleSet.getName(), ruleSet.getDescription(),ruleSet.getPackageName())
        ).collect(Collectors.toList());
    }

    @Override
    public RuleSetResponse getById(String id) {
        Optional<RuleSet> optional = ruleSetRepository.findById(id);
        RuleSet ruleSet = optional.orElseThrow(() -> new RuleSetNotFoundException(CANNOT_FIND_THE_RULE_SET));
        return new RuleSetResponse(ruleSet.getId(), ruleSet.getName(), ruleSet.getDescription(),ruleSet.getPackageName());
    }
}
