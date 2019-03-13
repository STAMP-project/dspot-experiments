package com.xxl.job.admin.controller;


import javax.servlet.http.Cookie;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;


public class JobInfoControllerTest extends AbstractSpringMvcTest {
    private Cookie cookie;

    @Test
    public void testAdd() throws Exception {
        MultiValueMap<String, String> parameters = new org.springframework.util.LinkedMultiValueMap<String, String>();
        parameters.add("jobGroup", "1");
        MvcResult ret = mockMvc.perform(// .content(paramsJson)
        post("/jobinfo/pageList").contentType(MediaType.APPLICATION_FORM_URLENCODED).params(parameters).cookie(cookie)).andReturn();
        System.out.println(ret.getResponse().getContentAsString());
    }
}

