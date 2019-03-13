package com.alibaba.json.bvt.support.spring.mock.testcase;


import MediaType.APPLICATION_JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.spring.FastJsonViewResponseBodyAdvice;
import com.alibaba.fastjson.support.spring.JSONPResponseBodyAdvice;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class FastJsonHttpMessageConverterJSONPCaseTest {
    private static final MediaType APPLICATION_JAVASCRIPT = new MediaType("application", "javascript");

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @ComponentScan(basePackages = "com.alibaba.json.bvt.support.spring.mock.controller")
    @EnableWebMvc
    @Configuration
    protected static class Config extends WebMvcConfigurerAdapter {
        @Bean
        public JSONPResponseBodyAdvice jsonpResponseBodyAdvice() {
            return new JSONPResponseBodyAdvice();
        }

        @Bean
        FastJsonViewResponseBodyAdvice fastJsonViewResponseBodyAdvice() {
            return new FastJsonViewResponseBodyAdvice();
        }

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(0, new FastJsonHttpMessageConverter());
            super.extendMessageConverters(converters);
        }
    }

    @Test
    public void isInjectComponent() {
        wac.getBean(JSONPResponseBodyAdvice.class);
        wac.getBean(FastJsonViewResponseBodyAdvice.class);
    }

    @Test
    public void test1() throws Exception {
        mockMvc.perform(post("/jsonp-fastjsonview/test1").characterEncoding("UTF-8").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void test1_2() throws Exception {
        ResultActions actions = mockMvc.perform(post("/jsonp-fastjsonview/test1?callback=fnUpdateSome").characterEncoding("UTF-8").contentType(APPLICATION_JSON));
        actions.andDo(print());
        actions.andExpect(status().isOk()).andExpect(content().contentType(FastJsonHttpMessageConverterJSONPCaseTest.APPLICATION_JAVASCRIPT)).andExpect(content().string("/**/fnUpdateSome({\"id\":100,\"name\":\"\u6d4b\u8bd5\"})"));
    }

    @Test
    public void test2() throws Exception {
        mockMvc.perform(post("/jsonp-fastjsonview/test2").characterEncoding("UTF-8").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void test2_2() throws Exception {
        ResultActions actions = mockMvc.perform(post("/jsonp-fastjsonview/test2?callback=fnUpdateSome").characterEncoding("UTF-8").contentType(APPLICATION_JSON));
        actions.andDo(print());
        actions.andExpect(status().isOk()).andExpect(content().contentType(FastJsonHttpMessageConverterJSONPCaseTest.APPLICATION_JAVASCRIPT)).andExpect(content().string("/**/fnUpdateSome({\"description\":\"fastjsonview\u6ce8\u89e3\u6d4b\u8bd5\",\"stock\":\"haha\"})"));
    }

    @Test
    public void test3() throws Exception {
        List<Object> list = this.mockMvc.perform(post("/jsonp-fastjsonview/test3")).andReturn().getResponse().getHeaderValues("Content-Length");
        Assert.assertNotEquals(list.size(), 0);
    }

    @Test
    public void test3_Jsonp_ContentLength() throws Exception {
        ResultActions actions1 = this.mockMvc.perform(post("/jsonp-fastjsonview/test3?callback=func")).andDo(print());
        Object obj1 = actions1.andReturn().getResponse().getHeaderValue("Content-Length");
        Assert.assertNotNull(obj1);
        Assert.assertEquals(85, obj1);
        ResultActions actions2 = this.mockMvc.perform(post("/jsonp-fastjsonview/test3?callback=fnUpdateSome")).andDo(print());
        Object obj2 = actions2.andReturn().getResponse().getHeaderValue("Content-Length");
        Assert.assertNotNull(obj2);
        Assert.assertEquals(93, obj2);
    }

    @Test
    public void test3_2() throws Exception {
        ResultActions actions = this.mockMvc.perform(post("/jsonp-fastjsonview/test3?callback=fnUpdateSome"));
        actions.andDo(print());
        actions.andExpect(status().isOk()).andExpect(content().contentType(FastJsonHttpMessageConverterJSONPCaseTest.APPLICATION_JAVASCRIPT)).andExpect(content().string("/**/fnUpdateSome({\"id\":100,\"name\":\"\u6d4b\u8bd5\",\"rootDepartment\":{\"description\":\"\u90e8\u95e81\u63cf\u8ff0\"}})"));
    }

    @Test
    public void test4() throws Exception {
        mockMvc.perform(post("/jsonp-fastjsonview/test4").characterEncoding("UTF-8").contentType(APPLICATION_JSON)).andDo(print());
    }

    @Test
    public void test4_2() throws Exception {
        ResultActions actions = mockMvc.perform(post("/jsonp-fastjsonview/test4?callback=myUpdate").characterEncoding("UTF-8").contentType(APPLICATION_JSON));
        actions.andDo(print());
        actions.andExpect(status().isOk()).andExpect(content().contentType(FastJsonHttpMessageConverterJSONPCaseTest.APPLICATION_JAVASCRIPT)).andExpect(content().string("/**/myUpdate({\"id\":100,\"name\":\"\u6d4b\u8bd5\",\"rootDepartment\":{\"id\":1,\"members\":[],\"name\":\"\u90e8\u95e81\"}})"));
    }

    @Test
    public void test5() throws Exception {
        String jsonStr = "{\"packet\":{\"smsType\":\"USER_LOGIN\"}}";
        mockMvc.perform(post("/jsonp-fastjsonview/test5").characterEncoding("UTF-8").content(jsonStr).contentType(APPLICATION_JSON)).andDo(print());
    }

    @Test
    public void test5_2() throws Exception {
        String jsonStr = "{\"packet\":{\"smsType\":\"USER_LOGIN\"}}";
        ResultActions actions = mockMvc.perform(post("/jsonp-fastjsonview/test5?callback=myUpdate").characterEncoding("UTF-8").content(jsonStr).contentType(APPLICATION_JSON));
        actions.andDo(print());
        actions.andExpect(status().isOk()).andExpect(content().contentType(FastJsonHttpMessageConverterJSONPCaseTest.APPLICATION_JAVASCRIPT)).andExpect(content().string("/**/myUpdate(\"{\\\"packet\\\":{\\\"smsType\\\":\\\"USER_LOGIN\\\"}}\")"));
    }

    @Test
    public void test7() throws Exception {
        ResultActions actions = this.mockMvc.perform(post("/jsonp-fastjsonview/test7?customizedCallbackParamName=fnUpdateSome"));
        actions.andDo(print());
        actions.andExpect(status().isOk()).andExpect(content().contentType(FastJsonHttpMessageConverterJSONPCaseTest.APPLICATION_JAVASCRIPT)).andExpect(content().string("/**/fnUpdateSome({})"));
    }

    @Test
    public void test8() throws Exception {
        String invalidMethodName = "--methodName";
        ResultActions actions = this.mockMvc.perform(post(("/jsonp-fastjsonview/test7?customizedCallbackParamName=" + invalidMethodName)));
        actions.andDo(print());
        actions.andExpect(status().isOk()).andExpect(content().contentType(FastJsonHttpMessageConverterJSONPCaseTest.APPLICATION_JAVASCRIPT)).andExpect(content().string("/**/null({})"));
    }

    @Test
    public void test9() throws Exception {
        ResultActions actions = this.mockMvc.perform(post("/jsonp-fastjsonview/test9?callback=fnUpdateSome"));
        actions.andDo(print());
        actions.andExpect(status().isOk()).andExpect(content().contentType(FastJsonHttpMessageConverterJSONPCaseTest.APPLICATION_JAVASCRIPT)).andExpect(content().string("/**/fnUpdateSome({\"id\":100})"));
    }
}

