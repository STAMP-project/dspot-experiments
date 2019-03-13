package org.javaee7.servlet.security.form.based;


import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import java.net.URL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class FormTest {
    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL base;

    private HtmlForm loginForm;

    @Test
    public void testGetWithCorrectCredentials() throws Exception {
        loginForm.getInputByName("j_username").setValueAttribute("u1");
        loginForm.getInputByName("j_password").setValueAttribute("p1");
        HtmlSubmitInput submitButton = loginForm.getInputByName("submitButton");
        HtmlPage page2 = submitButton.click();
        Assert.assertEquals("Form-based Security - Success", page2.getTitleText());
    }

    @Test
    public void testGetWithIncorrectCredentials() throws Exception {
        loginForm.getInputByName("j_username").setValueAttribute("random");
        loginForm.getInputByName("j_password").setValueAttribute("random");
        HtmlSubmitInput submitButton = loginForm.getInputByName("submitButton");
        HtmlPage page2 = submitButton.click();
        Assert.assertEquals("Form-Based Login Error Page", page2.getTitleText());
    }
}

