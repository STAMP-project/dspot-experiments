package com.vip.saturn.job.console.springboot.test;


import com.alibaba.fastjson.JSONObject;
import com.vip.saturn.job.console.AbstractSaturnConsoleTest;
import com.vip.saturn.job.console.controller.rest.DiscoveryRestApiController;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@RunWith(SpringRunner.class)
@WebMvcTest(DiscoveryRestApiController.class)
public class DiscoveryRestApiContorllerTest extends AbstractSaturnConsoleTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void disoverSuccessfully() throws Exception {
        MvcResult result = mvc.perform(get("/rest/v1/discovery?namespace=mydomain")).andExpect(status().isOk()).andReturn();
        String body = result.getResponse().getContentAsString();
        Map<String, Object> resultMap = JSONObject.parseObject(body, Map.class);
        Assert.assertEquals("localhost:2181", resultMap.get("zkConnStr"));
        Assert.assertEquals("dev", resultMap.get("env"));
    }
}

