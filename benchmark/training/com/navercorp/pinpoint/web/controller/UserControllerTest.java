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
import com.navercorp.pinpoint.web.dao.UserDao;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
public class UserControllerTest {
    private static final String USER_ID = "naver00";

    private static final String USER_NAME = "minwoo";

    private static final String USER_NAME_UPDATED = "minwoo.jung";

    private static final String USER_DEPARTMENT = "Web platfrom development team";

    private static final String USER_PHONENUMBER = "01012347890";

    private static final String USER_PHONENUMBER_UPDATED = "01000000000";

    private static final String USER_EMAIL = "min@naver.com";

    private static final String USER_EMAIL_UPDATED = "minwoo@naver.com";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserDao userDao;

    private MockMvc mockMvc;

    @Test
    public void insertAndSelectAndDeleteUser() throws Exception {
        String jsonParm = ((((((((((((((("{" + "\"userId\" : \"") + (UserControllerTest.USER_ID)) + "\",") + "\"name\" : \"") + (UserControllerTest.USER_NAME)) + "\",") + "\"department\" : \"") + (UserControllerTest.USER_DEPARTMENT)) + "\",") + "\"phoneNumber\" : \"") + (UserControllerTest.USER_PHONENUMBER)) + "\",") + "\"email\" : \"") + (UserControllerTest.USER_EMAIL)) + "\"") + "}";
        this.mockMvc.perform(post("/user.pinpoint").contentType(APPLICATION_JSON).content(jsonParm)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
        this.mockMvc.perform(get("/user.pinpoint").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$[0]", Matchers.hasKey("userId"))).andExpect(jsonPath("$[0]", Matchers.hasKey("name"))).andExpect(jsonPath("$[0]", Matchers.hasKey("department"))).andExpect(jsonPath("$[0]", Matchers.hasKey("phoneNumber"))).andExpect(jsonPath("$[0]", Matchers.hasKey("email"))).andReturn();
        this.mockMvc.perform(get(("/user.pinpoint?searchKey=" + (UserControllerTest.USER_NAME))).contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$[0]", Matchers.hasKey("userId"))).andExpect(jsonPath("$[0]", Matchers.hasKey("name"))).andExpect(jsonPath("$[0]", Matchers.hasKey("department"))).andExpect(jsonPath("$[0]", Matchers.hasKey("phoneNumber"))).andExpect(jsonPath("$[0]", Matchers.hasKey("email"))).andReturn();
        this.mockMvc.perform(get(("/user.pinpoint?userId=" + (UserControllerTest.USER_ID))).contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$[0]", Matchers.hasKey("userId"))).andExpect(jsonPath("$[0]", Matchers.hasKey("name"))).andExpect(jsonPath("$[0]", Matchers.hasKey("department"))).andExpect(jsonPath("$[0]", Matchers.hasKey("phoneNumber"))).andExpect(jsonPath("$[0]", Matchers.hasKey("email"))).andReturn();
        this.mockMvc.perform(get(("/user.pinpoint?searchKey=" + (UserControllerTest.USER_DEPARTMENT))).contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$[0]", Matchers.hasKey("userId"))).andExpect(jsonPath("$[0]", Matchers.hasKey("name"))).andExpect(jsonPath("$[0]", Matchers.hasKey("department"))).andExpect(jsonPath("$[0]", Matchers.hasKey("phoneNumber"))).andExpect(jsonPath("$[0]", Matchers.hasKey("email"))).andReturn();
        this.mockMvc.perform(delete("/user.pinpoint").contentType(APPLICATION_JSON).content((("{\"userId\" : \"" + (UserControllerTest.USER_ID)) + "\"}"))).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
    }

    @Test
    public void selectUser() throws Exception {
        String jsonParm = ((((((((((((((("{" + "\"userId\" : \"") + (UserControllerTest.USER_ID)) + "\",") + "\"name\" : \"") + (UserControllerTest.USER_NAME)) + "\",") + "\"department\" : \"") + (UserControllerTest.USER_DEPARTMENT)) + "\",") + "\"phoneNumber\" : \"") + (UserControllerTest.USER_PHONENUMBER)) + "\",") + "\"email\" : \"") + (UserControllerTest.USER_EMAIL)) + "\"") + "}";
        this.mockMvc.perform(post("/user.pinpoint").contentType(APPLICATION_JSON).content(jsonParm)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
        this.mockMvc.perform(get(("/user.pinpoint?userId=" + (UserControllerTest.USER_ID))).contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$[0]", Matchers.hasKey("userId"))).andExpect(jsonPath("$[0].userId").value(UserControllerTest.USER_ID)).andExpect(jsonPath("$[0]", Matchers.hasKey("name"))).andExpect(jsonPath("$[0]", Matchers.hasKey("department"))).andExpect(jsonPath("$[0]", Matchers.hasKey("phoneNumber"))).andExpect(jsonPath("$[0]", Matchers.hasKey("email"))).andReturn();
        this.mockMvc.perform(delete("/user.pinpoint").contentType(APPLICATION_JSON).content((("{\"userId\" : \"" + (UserControllerTest.USER_ID)) + "\"}"))).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
    }

    @Test
    public void updateUser() throws Exception {
        String jsonParamforUserInfo = ((((((((((((((("{" + "\"userId\" : \"") + (UserControllerTest.USER_ID)) + "\",") + "\"name\" : \"") + (UserControllerTest.USER_NAME)) + "\",") + "\"department\" : \"") + (UserControllerTest.USER_DEPARTMENT)) + "\",") + "\"phoneNumber\" : \"") + (UserControllerTest.USER_PHONENUMBER)) + "\",") + "\"email\" : \"") + (UserControllerTest.USER_EMAIL)) + "\"") + "}";
        this.mockMvc.perform(post("/user.pinpoint").contentType(APPLICATION_JSON).content(jsonParamforUserInfo)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
        String jsonParmForUserInfoUpdated = ((((((((((((((("{" + "\"userId\" : \"") + (UserControllerTest.USER_ID)) + "\",") + "\"name\" : \"") + (UserControllerTest.USER_NAME_UPDATED)) + "\",") + "\"department\" : \"") + (UserControllerTest.USER_DEPARTMENT)) + "\",") + "\"phoneNumber\" : \"") + (UserControllerTest.USER_PHONENUMBER_UPDATED)) + "\",") + "\"email\" : \"") + (UserControllerTest.USER_EMAIL_UPDATED)) + "\"") + "}";
        this.mockMvc.perform(put("/user.pinpoint").contentType(APPLICATION_JSON).content(jsonParmForUserInfoUpdated)).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
        this.mockMvc.perform(delete("/user.pinpoint").contentType(APPLICATION_JSON).content((("{\"userId\" : \"" + (UserControllerTest.USER_ID)) + "\"}"))).andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath("$", Matchers.hasKey("result"))).andExpect(jsonPath("$.result").value("SUCCESS")).andReturn();
    }
}

