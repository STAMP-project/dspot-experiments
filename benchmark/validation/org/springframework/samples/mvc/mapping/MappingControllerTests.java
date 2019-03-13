package org.springframework.samples.mvc.mapping;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.samples.mvc.AbstractContextControllerTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringJUnit4ClassRunner.class)
public class MappingControllerTests extends AbstractContextControllerTests {
    private MockMvc mockMvc;

    @Test
    public void byPath() throws Exception {
        this.mockMvc.perform(get("/mapping/path")).andExpect(content().string("Mapped by path!"));
    }

    @Test
    public void byPathPattern() throws Exception {
        this.mockMvc.perform(get("/mapping/path/wildcard")).andExpect(content().string("Mapped by path pattern ('/mapping/path/wildcard')"));
    }

    @Test
    public void byMethod() throws Exception {
        this.mockMvc.perform(get("/mapping/method")).andExpect(content().string("Mapped by path + method"));
    }

    @Test
    public void byParameter() throws Exception {
        this.mockMvc.perform(get("/mapping/parameter?foo=bar")).andExpect(content().string("Mapped by path + method + presence of query parameter!"));
    }

    @Test
    public void byNotParameter() throws Exception {
        this.mockMvc.perform(get("/mapping/parameter")).andExpect(content().string("Mapped by path + method + not presence of query parameter!"));
    }

    @Test
    public void byHeader() throws Exception {
        this.mockMvc.perform(get("/mapping/header").header("FooHeader", "foo")).andExpect(content().string("Mapped by path + method + presence of header!"));
    }

    @Test
    public void byHeaderNegation() throws Exception {
        this.mockMvc.perform(get("/mapping/header")).andExpect(content().string("Mapped by path + method + absence of header!"));
    }

    @Test
    public void byConsumes() throws Exception {
        this.mockMvc.perform(post("/mapping/consumes").contentType(APPLICATION_JSON).content("{ \"foo\": \"bar\", \"fruit\": \"apple\" }".getBytes())).andExpect(content().string(Matchers.startsWith("Mapped by path + method + consumable media type (javaBean")));
    }

    @Test
    public void byProducesAcceptJson() throws Exception {
        this.mockMvc.perform(get("/mapping/produces").accept(APPLICATION_JSON)).andExpect(jsonPath("$.foo").value("bar")).andExpect(jsonPath("$.fruit").value("apple"));
    }

    @Test
    public void byProducesAcceptXml() throws Exception {
        this.mockMvc.perform(get("/mapping/produces").accept(APPLICATION_XML)).andExpect(xpath("/javaBean/foo").string("bar")).andExpect(xpath("/javaBean/fruit").string("apple"));
    }

    @Test
    public void byProducesJsonExtension() throws Exception {
        this.mockMvc.perform(get("/mapping/produces.json")).andExpect(jsonPath("$.foo").value("bar")).andExpect(jsonPath("$.fruit").value("apple"));
    }

    @Test
    public void byProducesXmlExtension() throws Exception {
        this.mockMvc.perform(get("/mapping/produces.xml")).andExpect(xpath("/javaBean/foo").string("bar")).andExpect(xpath("/javaBean/fruit").string("apple"));
    }
}

