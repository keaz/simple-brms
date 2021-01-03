package com.kzone.brms.service.impl;

import com.kzone.brms.exception.CompileException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.tools.JavaCompiler;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static com.kzone.brms.service.JavaCompilerService.CLASS_DIR;

@RunWith(MockitoJUnitRunner.class)
public class JavaCompilerServiceImplTests {

    @InjectMocks
    private JavaCompilerServiceImpl compilerService;

    @Mock
    private JavaCompiler javaCompiler;

    @Mock
    private File ruleSet;
    @Mock
    private File sourceFile;

    private File[] fList;
    private String [] compilerArguments = {"-d",CLASS_DIR+File.separator+"Test","RuleDao.java"};

    @Captor
    private ArgumentCaptor<String []> argumentCaptor;

    @Before
    public void setup(){
        fList = new File[1];
        fList[0] = sourceFile;
    }

    @Test(expected = CompileException.class)
    public void compileRuleSetsCompileFailedTest(){
        Mockito.when(ruleSet.getName()).thenReturn("Test");
        Mockito.when(sourceFile.getPath()).thenReturn("RuleDao.java");
        Mockito.when(javaCompiler.run(null,null,null,compilerArguments)).thenReturn(1);
        compilerService.compileRuleSets(ruleSet, Arrays.asList(sourceFile));
    }

    @Test
    public void compileRuleSetsTest(){
        Mockito.when(ruleSet.getName()).thenReturn("Test");
        boolean compiled = compilerService.compileRuleSets(ruleSet, Collections.EMPTY_LIST);
        Assert.assertTrue(compiled);
    }
}
