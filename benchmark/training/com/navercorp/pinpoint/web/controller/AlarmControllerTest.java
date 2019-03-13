/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.controller;


import MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.web.dao.AlarmDao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 *
 * @author minwoo.jung
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:servlet-context.xml", "classpath:applicationContext-web.xml" })
public class AlarmControllerTest {
    private static final String APPLICATION_ID = "test-application";

    private static final String APPLICATION_ID_UPDATED = "test-application-tomcat";

    private static final String SERVICE_TYPE = "tomcat";

    private static final String USER_GROUP_ID = "test-pinpoint_group";

    private static final String USER_GROUP_ID_UPDATED = "test-pinpoint_team";

    private static final String CHECKER_NAME = "ERROR_COUNT";

    private static final String CHECKER_NAME_UPDATED = "SLOW_COUNT";

    private static final int THRESHOLD = 100;

    private static final int THRESHOLD_UPDATED = 10;

    private static final boolean SMS_SEND = false;

    private static final boolean SMS_SEND_UPDATED = true;

    private static final boolean EMAIL_SEND = true;

    private static final boolean EMAIL_SEND_UPDATED = false;

    private static final String NOTES = "for unit test";

    private static final String NOTES_UPDATED = "";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private AlarmDao alarmDao;

    private MockMvc mockMvc;

    @SuppressWarnings("unchecked")
    @Test
    public void insertAndSelectAndDeleteRule() throws Exception {
        String jsonParm = (((((((((((((((((((((((("{" + "\"applicationId\" : \"") + (AlarmControllerTest.APPLICATION_ID)) + "\",") + "\"serviceType\" : \"") + (AlarmControllerTest.SERVICE_TYPE)) + "\",") + "\"userGroupId\" : \"") + (AlarmControllerTest.USER_GROUP_ID)) + "\",") + "\"checkerName\" : \"") + (AlarmControllerTest.CHECKER_NAME)) + "\",") + "\"threshold\" : ") + (AlarmControllerTest.THRESHOLD)) + ",") + "\"smsSend\" : ") + (AlarmControllerTest.SMS_SEND)) + ",") + "\"emailSend\" : \"") + (AlarmControllerTest.EMAIL_SEND)) + "\",") + "\"notes\" : \"") + (AlarmControllerTest.NOTES)) + "\"") + "}";
        MvcResult result = this.mockMvc.perform(post("/alarmRule.pinpoint").contentType(APPLICATION_JSON).content(jsonParm)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> resultMap = objectMapper.readValue(content, HashMap.class);
        Assert.assertEquals(resultMap.get("result"), "SUCCESS");
        Assert.assertNotNull(resultMap.get("ruleId"));
        this.mockMvc.perform(get(("/alarmRule.pinpoint?userGroupId=" + (AlarmControllerTest.USER_GROUP_ID)))).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$[0]", Matchers.hasKey("applicationId"))).andExpect(jsonPath("$[0]", Matchers.hasKey("serviceType"))).andExpect(jsonPath("$[0]", Matchers.hasKey("userGroupId"))).andExpect(jsonPath("$[0]", Matchers.hasKey("checkerName"))).andExpect(jsonPath("$[0]", Matchers.hasKey("threshold"))).andExpect(jsonPath("$[0]", Matchers.hasKey("smsSend"))).andExpect(jsonPath("$[0]", Matchers.hasKey("emailSend"))).andExpect(jsonPath("$[0]", Matchers.hasKey("notes"))).andReturn();
        this.mockMvc.perform(delete("/alarmRule.pinpoint").contentType(APPLICATION_JSON).content((("{\"ruleId\" : \"" + (resultMap.get("ruleId"))) + "\"}"))).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateRule() throws Exception {
        String jsonParm = (((((((((((((((((((((((("{" + "\"applicationId\" : \"") + (AlarmControllerTest.APPLICATION_ID)) + "\",") + "\"serviceType\" : \"") + (AlarmControllerTest.SERVICE_TYPE)) + "\",") + "\"userGroupId\" : \"") + (AlarmControllerTest.USER_GROUP_ID)) + "\",") + "\"checkerName\" : \"") + (AlarmControllerTest.CHECKER_NAME)) + "\",") + "\"threshold\" : ") + (AlarmControllerTest.THRESHOLD)) + ",") + "\"smsSend\" : ") + (AlarmControllerTest.SMS_SEND)) + ",") + "\"emailSend\" : \"") + (AlarmControllerTest.EMAIL_SEND)) + "\",") + "\"notes\" : \"") + (AlarmControllerTest.NOTES)) + "\"") + "}";
        MvcResult result = this.mockMvc.perform(post("/alarmRule.pinpoint").contentType(APPLICATION_JSON).content(jsonParm)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> resultMap = objectMapper.readValue(content, HashMap.class);
        Assert.assertEquals(resultMap.get("result"), "SUCCESS");
        Assert.assertNotNull(resultMap.get("ruleId"));
        String updatedJsonParm = ((((((((((((((((((((((((((("{" + "\"ruleId\" : \"") + (resultMap.get("ruleId"))) + "\",") + "\"applicationId\" : \"") + (AlarmControllerTest.APPLICATION_ID_UPDATED)) + "\",") + "\"serviceType\" : \"") + (AlarmControllerTest.SERVICE_TYPE)) + "\",") + "\"userGroupId\" : \"") + (AlarmControllerTest.USER_GROUP_ID_UPDATED)) + "\",") + "\"checkerName\" : \"") + (AlarmControllerTest.CHECKER_NAME_UPDATED)) + "\",") + "\"threshold\" : ") + (AlarmControllerTest.THRESHOLD_UPDATED)) + ",") + "\"smsSend\" : ") + (AlarmControllerTest.SMS_SEND_UPDATED)) + ",") + "\"emailSend\" : \"") + (AlarmControllerTest.EMAIL_SEND_UPDATED)) + "\",") + "\"notes\" : \"") + (AlarmControllerTest.NOTES_UPDATED)) + "\"") + "}";
        this.mockMvc.perform(put("/alarmRule.pinpoint").contentType(APPLICATION_JSON).content(updatedJsonParm)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
        this.mockMvc.perform(delete("/alarmRule.pinpoint").contentType(APPLICATION_JSON).content((("{\"ruleId\" : \"" + (resultMap.get("ruleId"))) + "\"}"))).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
    }

    @Test
    public void checkerTest() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/alarmRule/checker.pinpoint").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$").isArray()).andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> checkerList = objectMapper.readValue(content, List.class);
        Assert.assertNotEquals(checkerList.size(), 0);
    }
}

