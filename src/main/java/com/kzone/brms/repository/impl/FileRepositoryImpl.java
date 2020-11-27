package com.kzone.brms.repository.impl;

import com.kzone.brms.config.GitConfigs;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.exception.CommonFileException;
import com.kzone.brms.exception.RuleSetExistsException;
import com.kzone.brms.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static com.kzone.brms.service.JavaCompilerService.CLASS;

@RequiredArgsConstructor
@Log4j2
@Repository
public class FileRepositoryImpl implements FileRepository {

    @Autowired
    private final GitConfigs gitConfigs;

    @Override
    public File createRuleSet(CreateRuleSetRequest request) {
        File gitRepoDir = new File(SOURCE_DIR, gitConfigs.getDir());
        File ruleSetDir = new File(gitRepoDir, request.getRuleSetName());
        if (ruleSetDir.exists()) {
            log.info("Rule set {} already exists",request.getRuleSetName());
            throw new RuleSetExistsException("Rule set already exists");
        }
        ruleSetDir.mkdirs();
        createReadMe(ruleSetDir,request);
        createPackages(ruleSetDir,request.getPackageName());
        return ruleSetDir;
    }

    @Override
    public void deleteRuleSet(String ruleSetName) {
        File gitRepoDir = new File(SOURCE_DIR, gitConfigs.getDir());
        File ruleSetDir = new File(gitRepoDir, ruleSetName);
        try {
            Files.deleteIfExists(ruleSetDir.toPath());
        } catch (IOException e) {
            log.error("Failed to delete source directory for rule set",e);
            throw new CommonFileException("Failed to delete rule set directory");
        }
    }

    private void createPackages(File ruleSetDir,String packageName){
        String packagePath = packageName.replace('.', '/');
        File packages = new File(ruleSetDir,packagePath);
        packages.mkdirs();
        try {
            new File(packages,"placeholder.txt").createNewFile();
        } catch (IOException e) {
            log.error("Failed to create placeholder file",e);
            throw new CommonFileException("Failed to create placeholder file");
        }
    }

    private void createReadMe(File ruleSetDir,CreateRuleSetRequest request){
        File readMe = new File(ruleSetDir, README_FILE);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(readMe))){
            bufferedWriter.write("#" + request.getRuleSetName());
            bufferedWriter.newLine();
            bufferedWriter.write(request.getDescription());
            bufferedWriter.flush();
        } catch (IOException e) {
            log.error("Failed to create read me file in rule set {}",request.getRuleSetName());
            throw new CommonFileException("Failed to create read me file in rule set");
        }
    }

    @Override
    public File createClassDirectory(String ruleSetName) {
        File ruleSetDir = new File(CLASS, ruleSetName);
        if(!ruleSetDir.exists()){
            ruleSetDir.mkdirs();
        }

        return ruleSetDir;
    }

    @Override
    public void deleteClassDirectory(String ruleSetName) {
        File ruleSetDir = new File(CLASS, ruleSetName);
        try {
            Files.deleteIfExists(ruleSetDir.toPath());
        } catch (IOException e) {
            log.error("Failed to delete class directory for rule set",e);
            throw new CommonFileException("Failed to delete class directory");
        }
    }

}
