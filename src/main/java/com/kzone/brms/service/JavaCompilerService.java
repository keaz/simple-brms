package com.kzone.brms.service;

import java.io.File;
import java.util.List;

public interface JavaCompilerService {

    String CLASS_DIR = "compiled";
    File CLASS = new File(CLASS_DIR);

    boolean compileRuleSets(File ruleSet, List<File> javSourceCodes);

}
