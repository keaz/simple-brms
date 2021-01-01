package com.kzone.brms.api;

import com.kzone.brms.config.GitConfigs;
import com.kzone.brms.dto.request.CreateRuleSetRequest;
import com.kzone.brms.dto.response.RuleSetResponse;
import com.kzone.brms.service.RuleService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
//@WebMvcTest(RuleController.class)
public class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    public RuleService ruleService;

    @MockBean
    public GitConfigs gitConfigs;

    private CreateRuleSetRequest request;
    private RuleSetResponse ruleSetResponse;

    @Before
    public void setup() {
        String ruleName = "Rule Set";
        String packageName = "com.kzone.brms";
        request = new CreateRuleSetRequest();
        request.setRuleSetName(ruleName);
        request.setPackageName(packageName);
        request.setDescription("This is a description");

        ruleSetResponse = new RuleSetResponse(UUID.randomUUID().toString(), ruleName, packageName);
    }

//    @Test
    public void createRuleSetTest() throws Exception {
        Mockito.when(ruleService.createRuleSet(request)).thenReturn(ruleSetResponse);
        mockMvc.perform(post("/v1/rule")).andDo(print()).andExpect(status().isOk());
    }


}
