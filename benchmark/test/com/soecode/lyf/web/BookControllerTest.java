package com.soecode.lyf.web;


import MediaType.APPLICATION_JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Kemin
 */
// ???????,????????????,???????????
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class BookControllerTest extends AbstractContextControllerTests {
    private MockMvc mockMvc;

    private String listUrl = "/book/list";

    private String detailUrl = "/book/{bookId}/detail";

    private String appointUrl = "/book/{bookId}/appoint";

    private long bookId = 1000;

    @Test
    public void list() throws Exception {
        this.mockMvc.perform(get(listUrl)).andExpect(view().name("list"));
    }

    @Test
    public void existDetail() throws Exception {
        this.mockMvc.perform(get(detailUrl, bookId)).andExpect(view().name("detail")).andExpect(model().attributeExists("book"));
    }

    @Test
    public void notExistDetail() throws Exception {
        this.mockMvc.perform(get(detailUrl, 1100)).andExpect(forwardedUrl("/book/list"));
    }

    @Test
    public void appointTest() throws Exception {
        this.mockMvc.perform(post(appointUrl, bookId).param("studentId", "1").accept(APPLICATION_JSON)).andExpect(content().contentType("application/json;charset=utf-8"));
    }
}

