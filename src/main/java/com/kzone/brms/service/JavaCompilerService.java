package com.kzone.brms.service;

import java.io.File;

public interface JavaCompilerService {

    String CLASS_DIR = "compiled";
    File CLASS = new File(CLASS_DIR);

    void compileRuleSets(File ruleSet);

}
