package com.alibaba.json.bvt.support.spring.mock.testcase;


import MediaType.APPLICATION_JSON;
import com.alibaba.fastjson.support.spring.FastJsonViewResponseBodyAdvice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


/**
 * FastJsonView????
 * Created by yanquanyu on 17-5-31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath*:/config/applicationContext-mvc5.xml" })
public class FastJsonViewTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Test
    public void isInjectComponent() {
        wac.getBean(FastJsonViewResponseBodyAdvice.class);
    }

    /**
     * ???????????????include??
     */
    @Test
    public void test1() throws Exception {
        mockMvc.perform(post("/fastjsonview/test1").characterEncoding("UTF-8").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andExpect(content().string("{\"id\":100,\"name\":\"\u6d4b\u8bd5\"}"));
    }

    /**
     * ???????????????exclude??
     */
    @Test
    public void test2() throws Exception {
        mockMvc.perform(post("/fastjsonview/test2").characterEncoding("UTF-8").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andExpect(content().string("{\"description\":\"fastjsonview\u6ce8\u89e3\u6d4b\u8bd5\",\"stock\":\"haha\"}"));
    }

    /**
     * ???????Department???Company??????????include??
     */
    @Test
    public void test3() throws Exception {
        mockMvc.perform(post("/fastjsonview/test3").characterEncoding("UTF-8").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andExpect(content().string("{\"id\":100,\"name\":\"\u6d4b\u8bd5\",\"rootDepartment\":{\"description\":\"\u90e8\u95e81\u63cf\u8ff0\"}}"));
    }

    /**
     * ???????Department???Company???????????include?exclude??
     */
    @Test
    public void test4() throws Exception {
        mockMvc.perform(post("/fastjsonview/test4").characterEncoding("UTF-8").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andExpect(content().string("{\"id\":100,\"name\":\"\u6d4b\u8bd5\",\"rootDepartment\":{\"children\":[],\"id\":1,\"members\":[],\"name\":\"\u90e8\u95e81\"}}"));
    }

    /**
     * ???????Department???Company???Department??exclude??
     */
    @Test
    public void test5() throws Exception {
        mockMvc.perform(post("/fastjsonview/test5").characterEncoding("UTF-8").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andExpect(content().string("{\"description\":\"fastjsonview\u6ce8\u89e3\u6d4b\u8bd5\",\"id\":100,\"name\":\"\u6d4b\u8bd5\",\"rootDepartment\":{\"children\":[],\"id\":1,\"members\":[],\"name\":\"\u90e8\u95e81\"},\"stock\":\"haha\"}"));
    }

    /**
     * ???????????????include?exclude??
     */
    @Test
    public void test6() throws Exception {
        mockMvc.perform(post("/fastjsonview/test6").characterEncoding("UTF-8").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andExpect(content().string("{\"id\":100}"));
    }
}

