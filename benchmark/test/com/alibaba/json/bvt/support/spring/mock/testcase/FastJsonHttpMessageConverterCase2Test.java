package com.alibaba.json.bvt.support.spring.mock.testcase;


import FastJsonHttpMessageConverter.APPLICATION_JAVASCRIPT;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.spring.FastJsonViewResponseBodyAdvice;
import com.alibaba.fastjson.support.spring.JSONPResponseBodyAdvice;
import java.util.List;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class FastJsonHttpMessageConverterCase2Test {
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
    public void test8() throws Exception {
        mockMvc.perform(post("/jsonp-fastjsonview/test8").characterEncoding("UTF-8").contentType(FastJsonHttpMessageConverter.APPLICATION_JAVASCRIPT)).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void test8_2() throws Exception {
        // ResultActions actions = mockMvc.perform((post("/jsonp-fastjsonview/test8?callback=fnUpdateSome").characterEncoding(
        // "UTF-8")));
        // actions.andDo(print());
        // actions.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JAVASCRIPT))
        // .andExpect(content().string("fnUpdateSome({\"id\":100,\"name\":\"??\"})"));
        MvcResult mvcResult = mockMvc.perform(post("/jsonp-fastjsonview/test8?callback=fnUpdateSome").characterEncoding("UTF-8")).andExpect(request().asyncStarted()).andReturn();
        mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andExpect(content().contentType(FastJsonHttpMessageConverter.APPLICATION_JAVASCRIPT)).andExpect(content().string("/**/fnUpdateSome({})"));
    }
}

