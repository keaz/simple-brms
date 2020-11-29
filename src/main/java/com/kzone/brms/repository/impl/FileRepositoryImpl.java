package com.kzone.brms.repository.impl;

import com.kzone.brms.config.GitConfigs;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.exception.CommonFileException;
import com.kzone.brms.exception.FailedToCreateSourceFile;
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

    private static final String PLACEHOLDER_ERROR_MESSAGE = "Failed to create placeholder file";

    @Autowired
    private final GitConfigs gitConfigs;

    @Override
    public File createRuleSet(CreateRuleSetRequest request) {
        File ruleSetDir = getSourceDirectory(request.getRuleSetName());
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
        File ruleSetDir = getSourceDirectory(ruleSetName);
        try {
            Files.deleteIfExists(ruleSetDir.toPath());
        } catch (IOException e) {
            log.error("Failed to delete source directory for rule set",e);
            throw new CommonFileException("Failed to delete rule set directory");
        }
    }

    protected void createPackages(File ruleSetDir,String packageName){
        File packages = getPackageDirectory(packageName, ruleSetDir);
        packages.mkdirs();

        boolean newFile = createPlaceholder(packages);
        if(!newFile){
            log.error("Failed to create placeholder file not created in rule set {} ",ruleSetDir.getName());
            throw new CommonFileException(PLACEHOLDER_ERROR_MESSAGE);
        }

    }

    protected boolean createPlaceholder(File packages){
        try {
            return new File(packages, "placeholder.txt").createNewFile();
        } catch (IOException e) {
            log.error(PLACEHOLDER_ERROR_MESSAGE,e);
            throw new CommonFileException(PLACEHOLDER_ERROR_MESSAGE);
        }
    }

    protected void createReadMe(File ruleSetDir,CreateRuleSetRequest request){
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
        File ruleSetDir = getClassClassDirectory(ruleSetName);
        if(!ruleSetDir.exists()){
            ruleSetDir.mkdirs();
        }
        return ruleSetDir;
    }

    @Override
    public void deleteClassDirectory(String ruleSetName) {
        File ruleSetDir = getClassClassDirectory(ruleSetName);
        try {
            Files.deleteIfExists(ruleSetDir.toPath());
        } catch (IOException e) {
            log.error("Failed to delete class directory for rule set",e);
            throw new CommonFileException("Failed to delete class directory");
        }
    }

    protected File getClassClassDirectory(String ruleSetName){
        return new File(CLASS, ruleSetName);
    }

    @Override
    public void getWriteSource(String ruleSetName, String packageName, String className,String sourceCode){

        File ruleSetDirectory = getSourceDirectory(ruleSetName);
        File javaFile = getJavaFile(packageName, className, ruleSetDirectory);

        try(FileWriter fileWriter = new FileWriter(javaFile)){
            fileWriter.write(sourceCode);
        }catch (IOException e){
            throw new FailedToCreateSourceFile("Failed to create source file");
        }

    }


    protected File getJavaFile(String packageName, String className, File ruleSetDirectory) {
        File packageDirectory = getPackageDirectory(packageName, ruleSetDirectory);
        File javaFile = new File(packageDirectory, className + ".java");
        return javaFile;
    }

    protected File getPackageDirectory(String packageName, File ruleSetDirectory) {
        String packagePath = packageName.replace('.', '/');
        return new File(ruleSetDirectory, packagePath);
    }

    @Override
    public File getSourceDirectory(String ruleSetName) {
        File gitRepoDir = new File(SOURCE_DIR, gitConfigs.getDir());
        return new File(gitRepoDir,ruleSetName);
    }


}
