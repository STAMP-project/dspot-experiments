package com.alibaba.json.bvt.issue_1300;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import java.io.Serializable;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * Created by songlingdong on 8/5/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class Issue1367 {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    public static class AbstractController<ID extends Serializable, PO extends Issue1367.GenericEntity<ID>> {
        @PostMapping(path = "/typeVariableBean", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public PO save(@RequestBody
        PO dto) {
            // do something
            return dto;
        }
    }

    @RestController
    @RequestMapping
    public static class BeanController extends Issue1367.AbstractController<Long, Issue1367.TypeVariableBean> {
        @PostMapping(path = "/parameterizedTypeBean", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public String parameterizedTypeBean(@RequestBody
        Issue1367.ParameterizedTypeBean<String> parameterizedTypeBean) {
            return parameterizedTypeBean.t;
        }
    }

    @ComponentScan(basePackages = "com.alibaba.json.bvt.issue_1300")
    @Configuration
    @Order((Ordered.LOWEST_PRECEDENCE) + 1)
    @EnableWebMvc
    public static class WebMvcConfig extends WebMvcConfigurerAdapter {
        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
            converters.add(converter);
        }
    }

    @Test
    public void testParameterizedTypeBean() throws Exception {
        mockMvc.perform(post("/parameterizedTypeBean").characterEncoding("UTF-8").contentType(APPLICATION_JSON_UTF8_VALUE).content("{\"t\": \"neil dong\"}")).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void testTypeVariableBean() throws Exception {
        mockMvc.perform(post("/typeVariableBean").characterEncoding("UTF-8").contentType(APPLICATION_JSON_UTF8_VALUE).content("{\"id\": 1}")).andExpect(status().isOk()).andDo(print());
    }

    abstract static class GenericEntity<ID extends Serializable> {
        public abstract ID getId();
    }

    static class TypeVariableBean extends Issue1367.GenericEntity<Long> {
        private Long id;

        @Override
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    static class ParameterizedTypeBean<T> {
        private T t;

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }
}

