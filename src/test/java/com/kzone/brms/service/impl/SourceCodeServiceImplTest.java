package com.kzone.brms.service.impl;

import com.kzone.brms.dto.request.CreateDomainAttributeRequest;
import com.kzone.brms.dto.request.DomainObject;
import com.kzone.brms.model.RuleSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class SourceCodeServiceImplTest {

    @InjectMocks
    private SourceCodeServiceImpl sourceCodeService;

    private String expectedValue = "";

    private DomainObject domainObject;

    @Before
    public void setup(){
        domainObject = new DomainObject();
        domainObject.setName("RuleDao");
        ArrayList<CreateDomainAttributeRequest> attributeRequests = new ArrayList<>();
        attributeRequests.add(new CreateDomainAttributeRequest("name","String"));
        attributeRequests.add(new CreateDomainAttributeRequest("age","int"));
        attributeRequests.add(new CreateDomainAttributeRequest("flag","boolean"));
        domainObject.setAttributes(attributeRequests);
    }

    @Test
    public void createSourceCodeTest(){
        RuleSet ruleSet =  new RuleSet();
        ruleSet.setName("TestRule");
        ruleSet.setPackageName("com.kzone.brms.rule");
        String sourceCode = sourceCodeService.createSourceCode(ruleSet, domainObject);
        Assert.assertFalse(sourceCode.isEmpty());
    }

}
