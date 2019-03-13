/**
 * <p>Title: FastJsonJsonViewTest.java</p>
 * <p>Description: FastJsonJsonViewTest</p>
 * <p>Package: com.alibaba.json.bvt.support.spring.mock.testcase</p>
 * <p>Company: www.github.com/DarkPhoenixs</p>
 * <p>Copyright: Dark Phoenixs (Open-Source Organization) 2016</p>
 */
package com.alibaba.json.bvt.support.spring.mock.testcase;


import MediaType.APPLICATION_JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


/**
 * <p>Title: FastJsonJsonViewTest</p>
 * <p>Description: </p>
 *
 * @since 2016?4?20?
 * @author Victor.Zxy
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath*:/config/applicationContext-mvc2.xml" })
public class FastJsonJsonViewTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Test
    public void test1() throws Exception {
        JSONObject json = new JSONObject();
        json.put("id", 123);
        json.put("name", "???");
        // .andExpect(status().isOk())
        mockMvc.perform(post("/fastjson/test1").characterEncoding("UTF-8").content(json.toJSONString()).contentType(APPLICATION_JSON)).andDo(print());
    }

    @Test
    public void test2() throws Exception {
        String jsonStr = "[{\"name\":\"p1\",\"sonList\":[{\"name\":\"s1\"}]},{\"name\":\"p2\",\"sonList\":[{\"name\":\"s2\"},{\"name\":\"s3\"}]}]";
        // .andExpect(status().isOk())
        mockMvc.perform(post("/fastjson/test2").characterEncoding("UTF-8").content(jsonStr).contentType(APPLICATION_JSON)).andDo(print());
    }
}

