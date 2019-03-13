package controller.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


/**
 * Springmvc ?????
 *
 * @author 13 2015-8-17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath*:/spring-context.xml", "classpath*:/spring-context-mvc.xml", "classpath*:/mybatis-config.xml" })
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class StoreControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Test
    public void testSave() throws Exception {
        // ?????????
        // ?????post
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/store/save.do");
        // ?????MockMvc???
        mockHttpServletRequestBuilder.param("number", "MockMvc");
        // ?????
        mockHttpServletRequestBuilder.param("level", "2");
        mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void testList() throws Exception {
        // ?????????
        // ?????post
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/store/list.do");
        // ???????????????????????????????
        // status?0???
        // mockHttpServletRequestBuilder.param("status", "0");
        // ?????dd???
        // mockHttpServletRequestBuilder.param("number", "dd");
        // ???
        mockHttpServletRequestBuilder.param("page", "1");
        // ??10???
        mockHttpServletRequestBuilder.param("rows", "10");
        mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk()).andDo(print());
        // ???????????
        // MockHttpServletResponse:
        // Status = 200 ????????
        // ????
    }
}

