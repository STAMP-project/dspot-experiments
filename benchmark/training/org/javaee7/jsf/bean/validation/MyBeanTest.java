package org.javaee7.jsf.bean.validation;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.IOException;
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
public class MyBeanTest {
    @ArquillianResource
    private URL base;

    WebClient webClient;

    private static final String WEBAPP_SRC = "src/main/webapp";

    HtmlPage page;

    HtmlTextInput nameInputText;

    HtmlTextInput ageInputText;

    HtmlTextInput zipInputText;

    HtmlSubmitInput button;

    @Test
    public void testNameLessCharacters() throws IOException {
        nameInputText.setText("ab");
        ageInputText.setText("20");
        zipInputText.setText("12345");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("nameMessage")));
        Assert.assertEquals("At least 3 characters", span.asText());
    }

    @Test
    public void testNameBoundary() throws IOException {
        nameInputText.setText("abc");
        ageInputText.setText("20");
        zipInputText.setText("12345");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("nameMessage")));
        Assert.assertEquals("", span.asText());
    }

    @Test
    public void testAgeLessThan() throws IOException {
        nameInputText.setText("abc");
        ageInputText.setText("16");
        zipInputText.setText("12345");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("ageMessage")));
        Assert.assertEquals("must be greater than or equal to 18", span.asText());
    }

    @Test
    public void testAgeLowBoundary() throws IOException {
        nameInputText.setText("abc");
        ageInputText.setText("18");
        zipInputText.setText("12345");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("ageMessage")));
        Assert.assertEquals("", span.asText());
    }

    @Test
    public void testAgeHighBoundary() throws IOException {
        nameInputText.setText("abc");
        ageInputText.setText("25");
        zipInputText.setText("12345");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("ageMessage")));
        Assert.assertEquals("", span.asText());
    }

    @Test
    public void testAgeGreaterThan() throws IOException {
        nameInputText.setText("abc");
        ageInputText.setText("26");
        zipInputText.setText("12345");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("ageMessage")));
        Assert.assertEquals("must be less than or equal to 25", span.asText());
    }

    @Test
    public void testZipAlphabets() throws IOException {
        nameInputText.setText("abc");
        ageInputText.setText("20");
        zipInputText.setText("abcde");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("zipMessage")));
        Assert.assertEquals("must match \"[0-9]{5}\"", span.asText());
    }

    @Test
    public void testZipLessNumbers() throws IOException {
        nameInputText.setText("abc");
        ageInputText.setText("20");
        zipInputText.setText("1234");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("zipMessage")));
        Assert.assertEquals("must match \"[0-9]{5}\"", span.asText());
    }

    @Test
    public void testZipMoreNumbers() throws IOException {
        nameInputText.setText("abc");
        ageInputText.setText("20");
        zipInputText.setText("123456");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("zipMessage")));
        Assert.assertEquals("must match \"[0-9]{5}\"", span.asText());
    }

    @Test
    public void testZipBoundary() throws IOException {
        nameInputText.setText("abc");
        ageInputText.setText("20");
        zipInputText.setText("12345");
        HtmlPage result = button.click();
        HtmlSpan span = ((HtmlSpan) (result.getElementById("zipMessage")));
        Assert.assertEquals("", span.asText());
    }
}

