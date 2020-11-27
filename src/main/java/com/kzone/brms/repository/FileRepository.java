package com.kzone.brms.repository;

import com.kzone.brms.dto.request.CreateRuleSetRequest;

import java.io.File;

public interface FileRepository {

    String SOURCE_DIR = "source";
    String README_FILE = "README.md";

    /**
     * Create a new directories for rule set and populate README.md from description
     * @param request is used to create the root directory, packages, README.md
     */
    File createRuleSet(CreateRuleSetRequest request);

    /**
     * Delete rule set directory
     * @param ruleSetName
     * @return
     */
    void deleteRuleSet(String ruleSetName);

    /**
     * Creates a directory to put compiled classes
     * @param ruleSetName name of the rule set
     * @return {@link File} object created for rule set
     */
    File createClassDirectory(String ruleSetName);

    /**
     * Delete class directory of the rule set
     * @param ruleSetName
     * @return
     */
    void deleteClassDirectory(String ruleSetName);

}
