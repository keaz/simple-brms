package com.kzone.brms.service.impl;

import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.response.CreateRuleSetResponse;
import com.kzone.brms.exception.CommonRuleCreateException;
import com.kzone.brms.exception.GenericGitException;
import com.kzone.brms.exception.GitTagAlreadyExists;
import com.kzone.brms.repository.FileRepository;
import com.kzone.brms.repository.GitRepository;
import com.kzone.brms.service.JavaCompilerService;
import com.kzone.brms.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
@Service
public class RuleServiceImpl implements RuleService {

    private static final String DEFAULT_VERSION = "/0.0.0";

    private final FileRepository fileRepository;
    private final JavaCompilerService compilerService;
    private final GitRepository gitRepository;
    //TODO add db repository

    @Override
    public CreateRuleSetResponse createRuleSet(CreateRuleSetRequest request) {
        String gitVersion = request.getRuleSetName()+DEFAULT_VERSION;
        if(gitRepository.isGitTagExists(gitVersion)){
            log.info("Git Tag {} already exists",gitVersion);
            throw new GitTagAlreadyExists(String.format("Git tag %s already exists ",gitVersion));
        }
        fileRepository.createRuleSet(request);
        fileRepository.createClassDirectory(request.getRuleSetName());
        try {
            gitRepository.commitAddPush(request.getRuleSetName(),"Added rule set " + request.getRuleSetName(), gitVersion);
        } catch (GenericGitException gitException) {
            fileRepository.deleteClassDirectory(request.getRuleSetName());
            fileRepository.deleteRuleSet(request.getRuleSetName());
            throw new CommonRuleCreateException(gitException.getMessage());
        }
        return new CreateRuleSetResponse(UUID.randomUUID().toString(),request.getRuleSetName(),request.getDescription());
    }

}
