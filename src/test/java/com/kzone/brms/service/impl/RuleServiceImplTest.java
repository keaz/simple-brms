package com.kzone.brms.service.impl;

import com.kzone.brms.dto.request.CreateDomainAttributeRequest;
import com.kzone.brms.dto.request.CreateDomainRequest;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.request.DomainObject;
import com.kzone.brms.dto.response.CreateDomainResponse;
import com.kzone.brms.dto.response.CreateRuleSetResponse;
import com.kzone.brms.exception.CommonRuleCreateException;
import com.kzone.brms.exception.GenericGitException;
import com.kzone.brms.exception.GitTagAlreadyExists;
import com.kzone.brms.exception.RuleSetNotFoundException;
import com.kzone.brms.model.RuleSet;
import com.kzone.brms.repository.FileRepository;
import com.kzone.brms.repository.GitRepository;
import com.kzone.brms.repository.RuleSetRepository;
import com.kzone.brms.service.JavaCompilerService;
import com.kzone.brms.service.SourceCodeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class RuleServiceImplTest {

    @InjectMocks
    private RuleServiceImpl ruleService;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private GitRepository gitRepository;
    @Mock
    private RuleSetRepository ruleSetRepository;
    @Mock
    private SourceCodeService sourceCodeService;

    private CreateRuleSetRequest request = new CreateRuleSetRequest();
    private CreateDomainRequest createDomainRequest = new CreateDomainRequest();
    private RuleSet ruleSet;

    @Before
    public void setup(){
        request.setRuleSetName("RueSetName");
        request.setDescription("Rule Set description");
        request.setPackageName("com.brms.rules");
        ruleSet = new RuleSet(request.getRuleSetName(), request.getPackageName(), request.getDescription());

        createDomainRequest.setMessage("Test Domain Object");
        DomainObject domainObject = new DomainObject();
        domainObject.setName("TestDomain");
        domainObject.setName("RuleDao");
        ArrayList<CreateDomainAttributeRequest> attributeRequests = new ArrayList<>();
        attributeRequests.add(new CreateDomainAttributeRequest("name","String"));
        attributeRequests.add(new CreateDomainAttributeRequest("age","int"));
        attributeRequests.add(new CreateDomainAttributeRequest("flag","boolean"));
        domainObject.setAttributes(attributeRequests);
        createDomainRequest.setDomainObject(domainObject);
    }

    @Test(expected = GitTagAlreadyExists.class)
    public void createRuleSetGitTagExistsTest(){
        Mockito.when(gitRepository.isGitTagExists(Mockito.anyString())).thenReturn(true);
        ruleService.createRuleSet(request);
    }

    @Test(expected = CommonRuleCreateException.class)
    public void createRuleSetGitPushErrorTest(){
        Mockito.when(gitRepository.isGitTagExists(Mockito.anyString())).thenReturn(false);
        Mockito.when(fileRepository.createRuleSet(request)).thenReturn(new File(""));
        Mockito.doThrow(GenericGitException.class).when(gitRepository).commitAddPush(Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
        ruleService.createRuleSet(request);
    }

    @Test
    public void createRuleSetTest(){
        Mockito.when(gitRepository.isGitTagExists(Mockito.anyString())).thenReturn(false);
        Mockito.when(fileRepository.createRuleSet(request)).thenReturn(new File(""));
        Mockito.doNothing().when(gitRepository).commitAddPush(Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());


        Mockito.when(ruleSetRepository.save(ruleSet)).thenReturn(ruleSet);
        CreateRuleSetResponse expected = ruleService.createRuleSet(request);
        Assert.assertEquals(ruleSet.getName(),expected.getRuleSetName());
    }

    @Test(expected = RuleSetNotFoundException.class)
    public void createDomainObjectNoRuleSetTest(){
        Mockito.when(ruleSetRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        ruleService.createDomainObject(Mockito.anyString(),createDomainRequest);
    }

    @Test
    public void createDomainObjectTest(){
        Mockito.when(ruleSetRepository.findById(Mockito.anyString())).thenReturn(Optional.of(ruleSet));
        Mockito.when(sourceCodeService.createSourceCode(ruleSet, createDomainRequest.getDomainObject())).thenReturn("");
        Mockito.doNothing().when(fileRepository).getWriteSource(ruleSet.getName(),ruleSet.getPackageName(),
                createDomainRequest.getDomainObject().getName(),"");
        Mockito.doNothing().when(gitRepository).commitAddPush(ruleSet.getName(),createDomainRequest.getMessage());
        CreateDomainResponse actual = ruleService.createDomainObject("ruleid", createDomainRequest);
        Assert.assertEquals(createDomainRequest.getDomainObject().getName(),actual.getDomainObject());
    }

    @Test(expected = RuleSetNotFoundException.class)
    public void compileRuleSetNoRuleSetTest(){
        Mockito.when(ruleSetRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        ruleService.compileRuleSet("");
    }

    @Captor
    ArgumentCaptor<String> ruleSetName;


    @Test
    public void compileRuleSetTest(){
        Mockito.when(ruleSetRepository.findById(Mockito.anyString())).thenReturn(Optional.of(ruleSet));
        Mockito.when(fileRepository.createClassDirectory(ruleSet.getName())).thenReturn(new File(""));
        ruleService.compileRuleSet("");
        Mockito.verify(fileRepository).createClassDirectory(ruleSetName.capture());
        Mockito.verify(fileRepository).getSourceDirectory(ruleSetName.capture());
        List<String> allValues = ruleSetName.getAllValues();
        Assert.assertEquals(ruleSet.getName(),allValues.get(0));
        Assert.assertEquals(ruleSet.getName(),allValues.get(1));

    }

}
