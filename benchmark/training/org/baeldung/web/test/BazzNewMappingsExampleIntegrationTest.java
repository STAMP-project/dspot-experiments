package org.baeldung.web.test;


import org.baeldung.config.WebConfig;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebConfig.class)
@WebAppConfiguration
@AutoConfigureWebClient
public class BazzNewMappingsExampleIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void whenGettingAllBazz_thenSuccess() throws Exception {
        mockMvc.perform(get("/bazz")).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.hasSize(4))).andExpect(jsonPath("$[1].id", Matchers.is("2"))).andExpect(jsonPath("$[1].name", Matchers.is("Bazz2")));
    }

    @Test
    public void whenGettingABazz_thenSuccess() throws Exception {
        mockMvc.perform(get("/bazz/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id", Matchers.is("1"))).andExpect(jsonPath("$.name", Matchers.is("Bazz1")));
    }

    @Test
    public void whenAddingABazz_thenSuccess() throws Exception {
        mockMvc.perform(post("/bazz").param("name", "Bazz5")).andExpect(status().isOk()).andExpect(jsonPath("$.id", Matchers.is("5"))).andExpect(jsonPath("$.name", Matchers.is("Bazz5")));
    }

    @Test
    public void whenUpdatingABazz_thenSuccess() throws Exception {
        mockMvc.perform(put("/bazz/5").param("name", "Bazz6")).andExpect(status().isOk()).andExpect(jsonPath("$.id", Matchers.is("5"))).andExpect(jsonPath("$.name", Matchers.is("Bazz6")));
    }

    @Test
    public void whenDeletingABazz_thenSuccess() throws Exception {
        mockMvc.perform(delete("/bazz/5")).andExpect(status().isOk()).andExpect(jsonPath("$.id", Matchers.is("5")));
    }
}

