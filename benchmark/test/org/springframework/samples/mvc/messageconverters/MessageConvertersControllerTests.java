package org.springframework.samples.mvc.messageconverters;


import MediaType.APPLICATION_ATOM_XML;
import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.samples.mvc.AbstractContextControllerTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringJUnit4ClassRunner.class)
public class MessageConvertersControllerTests extends AbstractContextControllerTests {
    private static String URI = "/messageconverters/{action}";

    private MockMvc mockMvc;

    @Test
    public void readString() throws Exception {
        this.mockMvc.perform(post(MessageConvertersControllerTests.URI, "string").content("foo".getBytes())).andExpect(content().string("Read string 'foo'"));
    }

    @Test
    public void writeString() throws Exception {
        this.mockMvc.perform(get(MessageConvertersControllerTests.URI, "string")).andExpect(content().string("Wrote a string"));
    }

    @Test
    public void readForm() throws Exception {
        this.mockMvc.perform(post(MessageConvertersControllerTests.URI, "form").contentType(APPLICATION_FORM_URLENCODED).param("foo", "bar").param("fruit", "apple")).andExpect(content().string("Read x-www-form-urlencoded: JavaBean {foo=[bar], fruit=[apple]}"));
    }

    @Test
    public void writeForm() throws Exception {
        this.mockMvc.perform(get(MessageConvertersControllerTests.URI, "form")).andExpect(content().contentType(APPLICATION_FORM_URLENCODED)).andExpect(content().string("foo=bar&fruit=apple"));
    }

    private static String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<javaBean><foo>bar</foo><fruit>apple</fruit></javaBean>";

    @Test
    public void readXml() throws Exception {
        this.mockMvc.perform(post(MessageConvertersControllerTests.URI, "xml").contentType(APPLICATION_XML).content(MessageConvertersControllerTests.XML.getBytes())).andExpect(content().string("Read from XML: JavaBean {foo=[bar], fruit=[apple]}"));
    }

    @Test
    public void writeXml() throws Exception {
        this.mockMvc.perform(get(MessageConvertersControllerTests.URI, "xml").accept(APPLICATION_XML)).andExpect(content().xml(MessageConvertersControllerTests.XML));
    }

    @Test
    public void readJson() throws Exception {
        this.mockMvc.perform(post(MessageConvertersControllerTests.URI, "json").contentType(APPLICATION_JSON).content("{ \"foo\": \"bar\", \"fruit\": \"apple\" }".getBytes())).andExpect(content().string("Read from JSON: JavaBean {foo=[bar], fruit=[apple]}"));
    }

    @Test
    public void writeJson() throws Exception {
        this.mockMvc.perform(get(MessageConvertersControllerTests.URI, "json").accept(APPLICATION_JSON)).andExpect(jsonPath("$.foo").value("bar")).andExpect(jsonPath("$.fruit").value("apple"));
    }

    @Test
    public void writeJson2() throws Exception {
        this.mockMvc.perform(get(MessageConvertersControllerTests.URI, "json").accept(APPLICATION_JSON)).andExpect(jsonPath("$.foo").value("bar")).andExpect(jsonPath("$.fruit").value("apple"));
    }

    private static String ATOM_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<feed xmlns=\"http://www.w3.org/2005/Atom\"><title>My Atom feed</title></feed>";

    @Test
    public void readAtom() throws Exception {
        this.mockMvc.perform(post(MessageConvertersControllerTests.URI, "atom").contentType(APPLICATION_ATOM_XML).content(MessageConvertersControllerTests.ATOM_XML.getBytes())).andExpect(content().string("Read My Atom feed"));
    }

    @Test
    public void writeAtom() throws Exception {
        this.mockMvc.perform(get(MessageConvertersControllerTests.URI, "atom").accept(APPLICATION_ATOM_XML)).andExpect(content().xml(MessageConvertersControllerTests.ATOM_XML));
    }

    @Test
    public void readRss() throws Exception {
        String rss = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <rss version=\"2.0\">" + "<channel><title>My RSS feed</title></channel></rss>";
        this.mockMvc.perform(post(MessageConvertersControllerTests.URI, "rss").contentType(MediaType.valueOf("application/rss+xml")).content(rss.getBytes())).andExpect(content().string("Read My RSS feed"));
    }

    @Test
    public void writeRss() throws Exception {
        String rss = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<rss version=\"2.0\"><channel><title>My RSS feed</title><link>http://localhost:8080/mvc-showcase/rss</link><description>Description</description></channel></rss>";
        this.mockMvc.perform(get(MessageConvertersControllerTests.URI, "rss").accept(MediaType.valueOf("application/rss+xml"))).andExpect(content().xml(rss));
    }
}

