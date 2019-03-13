package com.baeldung.spring;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import ratpack.test.MainClassApplicationUnderTest;


/**
 *
 *
 * @author aiet
 */
public class EmbedRatpackAppIntegrationTest {
    MainClassApplicationUnderTest appUnderTest = new MainClassApplicationUnderTest(EmbedRatpackApp.class);

    @Test
    public void whenSayHello_thenGotWelcomeMessage() {
        Assert.assertEquals("hello baeldung!", appUnderTest.getHttpClient().getText("/hello"));
    }

    @Test
    public void whenRequestList_thenGotArticles() throws IOException {
        Assert.assertEquals(3, appUnderTest.getHttpClient().getText("/list").split(",").length);
    }

    @Test
    public void whenRequestStaticResource_thenGotStaticContent() {
        Assert.assertThat(appUnderTest.getHttpClient().getText("/"), CoreMatchers.containsString("page is static"));
    }
}

