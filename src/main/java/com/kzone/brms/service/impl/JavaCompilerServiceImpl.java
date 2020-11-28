package com.kzone.brms.service.impl;

import com.kzone.brms.exception.CompileException;
import com.kzone.brms.service.JavaCompilerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class JavaCompilerServiceImpl implements JavaCompilerService {

    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    @Override
    public void compileRuleSets(File ruleSet) {
        log.info("Start compiling rule set {}",ruleSet.getName());
        List<File> javSourceCodes = new ArrayList<>();
        javaSourceFiles(ruleSet, javSourceCodes);

        List<String> compilerArguments = javSourceCodes.stream().map(File::getPath).collect(Collectors.toList());
        compilerArguments.add(0,"-d");
        compilerArguments.add(1,CLASS_DIR+File.separator+ruleSet.getName());

        int exitCode = compiler.run(null, null, null, compilerArguments.toArray(new String[0]));
        if(exitCode != 0){
            log.error("Compiler returns exit code 0");
            throw new CompileException("Failed to compile source files");
        }

    }

    private void javaSourceFiles(File directory, List<File> files) {
        // Get all java files from a directory.
        log.debug("Fetching Source files from directory {}",directory.getName());
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile() && file.getName().endsWith(".java")) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    javaSourceFiles(file, files);
                }
            }
        }
    }

}
