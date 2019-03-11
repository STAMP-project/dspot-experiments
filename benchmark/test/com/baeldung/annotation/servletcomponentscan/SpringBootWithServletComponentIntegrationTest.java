package com.baeldung.annotation.servletcomponentscan;


import HttpStatus.OK;
import SpringBootTest.WebEnvironment;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SpringBootAnnotatedApp.class)
public class SpringBootWithServletComponentIntegrationTest {
    @Autowired
    private ServletContext servletContext;

    @Test
    public void givenServletContext_whenAccessAttrs_thenFoundAttrsPutInServletListner() {
        Assert.assertNotNull(servletContext);
        Assert.assertNotNull(servletContext.getAttribute("servlet-context-attr"));
        Assert.assertEquals("test", servletContext.getAttribute("servlet-context-attr"));
    }

    @Test
    public void givenServletContext_whenCheckHelloFilterMappings_thenCorrect() {
        Assert.assertNotNull(servletContext);
        FilterRegistration filterRegistration = servletContext.getFilterRegistration("hello filter");
        Assert.assertNotNull(filterRegistration);
        Assert.assertTrue(filterRegistration.getServletNameMappings().contains("echo servlet"));
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void givenServletFilter_whenGetHello_thenRequestFiltered() {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/hello", String.class);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertEquals("filtering hello", responseEntity.getBody());
    }

    @Test
    public void givenFilterAndServlet_whenPostEcho_thenEchoFiltered() {
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity("/echo", "echo", String.class);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertEquals("filtering echo", responseEntity.getBody());
    }
}

