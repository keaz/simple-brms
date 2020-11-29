package com.kzone.brms.repository.impl;

import com.kzone.brms.config.GitConfigs;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.exception.CommonFileException;
import com.kzone.brms.exception.FailedToCreateSourceFile;
import com.kzone.brms.exception.RuleSetExistsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static com.kzone.brms.service.JavaCompilerService.CLASS_DIR;

@RunWith(MockitoJUnitRunner.class)
public class FileRepositoryImplTest {

    @InjectMocks
    @Spy
    private FileRepositoryImpl fileRepository;
    @Mock
    private GitConfigs gitConfigs;
    @Mock
    private File ruleSetDir;

    private CreateRuleSetRequest request = new CreateRuleSetRequest();

    private String ruleSetName = "RuleSet";
    private String className = "Test";

    @Captor
    private ArgumentCaptor<CreateRuleSetRequest> requestArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<File> fileArgumentCaptor;

    @Before
    public void setup(){
        request.setRuleSetName(ruleSetName);
        request.setDescription("Rule Set description");
        request.setPackageName("com.brms.rules");
    }

    @Test(expected = RuleSetExistsException.class)
    public void createRuleSetRuleExistsTest(){
        Mockito.doReturn(ruleSetDir).when(fileRepository).getSourceDirectory(ruleSetName);
        Mockito.when(ruleSetDir.exists()).thenReturn(true);
        fileRepository.createRuleSet(request);
    }

    @Test
    public void createRuleSetTest(){
        Mockito.doReturn(ruleSetDir).when(fileRepository).getSourceDirectory(ruleSetName);
        Mockito.doNothing().when(fileRepository).createReadMe(ruleSetDir,request);
        Mockito.doNothing().when(fileRepository).createPackages(ruleSetDir,request.getPackageName());
        Mockito.when(ruleSetDir.exists()).thenReturn(false);
        Mockito.when(ruleSetDir.mkdirs()).thenReturn(true);

        fileRepository.createRuleSet(request);
        Mockito.verify(fileRepository).createReadMe(Mockito.any(File.class), requestArgumentCaptor.capture());
        Mockito.verify(fileRepository).createPackages(Mockito.any(File.class),stringArgumentCaptor.capture());

        Assert.assertEquals(request.getPackageName(),stringArgumentCaptor.getValue());
        Assert.assertEquals(request.getRuleSetName(), requestArgumentCaptor.getValue().getRuleSetName());
    }

    @Test(expected = CommonFileException.class)
    public void deleteRuleSetFileNotFoundTest(){

        Mockito.doReturn(ruleSetDir).when(fileRepository).getSourceDirectory(ruleSetName);
        Mockito.when(ruleSetDir.toPath()).thenReturn(Paths.get(""));

        fileRepository.deleteRuleSet(ruleSetName);
    }

    @Test
    public void deleteRuleSetTest(){
        Mockito.doReturn(ruleSetDir).when(fileRepository).getSourceDirectory(ruleSetName);
        Mockito.when(ruleSetDir.toPath()).thenReturn(Paths.get("test_file"));

        fileRepository.deleteRuleSet(ruleSetName);
    }

    @Test
    public void createClassDirectoryTest(){
        Mockito.doReturn(ruleSetDir).when(fileRepository).getClassClassDirectory(ruleSetName);
        Mockito.when(ruleSetDir.exists()).thenReturn(false);
        Mockito.when(ruleSetDir.mkdirs()).thenReturn(true);

        File actual = fileRepository.createClassDirectory(ruleSetName);
        Assert.assertEquals(ruleSetDir,actual);
    }

    @Test(expected = CommonFileException.class)
    public void deleteClassDirectoryFileNotFoundTest(){
        Mockito.doReturn(ruleSetDir).when(fileRepository).getClassClassDirectory(ruleSetName);
        Mockito.when(ruleSetDir.toPath()).thenReturn(Paths.get(""));

        fileRepository.deleteClassDirectory(ruleSetName);
    }

    @Test
    public void deleteClassDirectoryTest(){
        Mockito.doReturn(ruleSetDir).when(fileRepository).getClassClassDirectory(ruleSetName);
        Mockito.when(ruleSetDir.toPath()).thenReturn(Paths.get("test_file"));

        fileRepository.deleteClassDirectory(ruleSetName);
        Mockito.verify(fileRepository).getClassClassDirectory(stringArgumentCaptor.capture());

        Assert.assertEquals(ruleSetName,stringArgumentCaptor.getValue());
    }

    @Test(expected = FailedToCreateSourceFile.class)
    public void  getWriteSourceIOExceptionTest(){
        String className = "Test";
        File javaFile = new File("");
        javaFile.deleteOnExit();
        Mockito.doReturn(ruleSetDir).when(fileRepository).getSourceDirectory(ruleSetName);
        Mockito.doReturn(javaFile).when(fileRepository).getJavaFile(request.getPackageName(),className,ruleSetDir);

        fileRepository.getWriteSource(ruleSetName,request.getPackageName(),className,"");

    }

    @Test
    public void  getWriteSourceTest(){

        File javaFile = new File(className+".java");
        javaFile.deleteOnExit();
        Mockito.doReturn(ruleSetDir).when(fileRepository).getSourceDirectory(ruleSetName);
        Mockito.doReturn(javaFile).when(fileRepository).getJavaFile(request.getPackageName(),className,ruleSetDir);

        fileRepository.getWriteSource(ruleSetName,request.getPackageName(),className,"");

        Mockito.verify(fileRepository).getJavaFile(stringArgumentCaptor.capture(),stringArgumentCaptor.capture(), fileArgumentCaptor.capture());

        List<String> stringValues = stringArgumentCaptor.getAllValues();
        Assert.assertEquals(request.getPackageName(),stringValues.get(0));
        Assert.assertEquals(className,stringValues.get(1));
        Assert.assertEquals(ruleSetDir,fileArgumentCaptor.getValue());
    }

    @Test
    public void getSourceDirectoryTest(){
        String testRepoDir = "test_repo";
        Mockito.when(gitConfigs.getDir()).thenReturn(testRepoDir);
        File sourceDirectory = fileRepository.getSourceDirectory(ruleSetName);
        Assert.assertEquals(ruleSetName,sourceDirectory.getName());
    }

    @Test
    public void getJavaFileTest(){
        File javaFile = fileRepository.getJavaFile(request.getPackageName(), className, new File("test_rule_set"));
        Assert.assertEquals("test_rule_set/com/brms/rules/Test.java",javaFile.getPath());
    }

    @Test
    public void getClassClassDirectoryTest(){
        File classClassDirectory = fileRepository.getClassClassDirectory(ruleSetName);
        Assert.assertEquals(CLASS_DIR+"/"+ruleSetName,classClassDirectory.getPath());
    }

    @Test(expected = CommonFileException.class)
    public void createPackagesErrorTest(){
        Mockito.doReturn(false).when(fileRepository).createPlaceholder(ruleSetDir);
        Mockito.doReturn(ruleSetDir).when(fileRepository).getPackageDirectory(request.getPackageName(),ruleSetDir);
        Mockito.when(ruleSetDir.mkdirs()).thenReturn(true);

        fileRepository.createPackages(ruleSetDir,request.getPackageName());
    }

    @Test
    public void createPackagesTest(){
        Mockito.doReturn(true).when(fileRepository).createPlaceholder(ruleSetDir);
        Mockito.doReturn(ruleSetDir).when(fileRepository).getPackageDirectory(request.getPackageName(),ruleSetDir);
        Mockito.when(ruleSetDir.mkdirs()).thenReturn(true);

        fileRepository.createPackages(ruleSetDir,request.getPackageName());
        Mockito.verify(fileRepository).createPlaceholder(fileArgumentCaptor.capture());

        Assert.assertEquals(ruleSetDir,fileArgumentCaptor.getValue());
    }

    @Test(expected = CommonFileException.class)
    public void createPlaceholderTest(){
        File packages = new File("");
        fileRepository.createPlaceholder(packages);
    }
}
