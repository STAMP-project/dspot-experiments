package com.airbnb.deeplinkdispatch;


import DeepLinkEntry.Type;
import java.net.MalformedURLException;
import javax.lang.model.element.TypeElement;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


public class DeepLinkAnnotatedElementTest {
    @Mock
    TypeElement element;

    @Test
    public void testValidUri() throws MalformedURLException {
        DeepLinkAnnotatedElement annotatedElement = new DeepLinkAnnotatedElement("airbnb://example.com/{foo}/bar", element, Type.CLASS);
        assertThat(annotatedElement.getUri()).isEqualTo("airbnb://example.com/{foo}/bar");
    }

    @Test
    public void testQueryParam() throws MalformedURLException {
        DeepLinkAnnotatedElement annotatedElement = new DeepLinkAnnotatedElement("airbnb://classDeepLink?foo=bar", element, Type.CLASS);
        assertThat(annotatedElement.getUri()).isEqualTo("airbnb://classDeepLink?foo=bar");
    }

    @Test
    public void testInvalidUri() {
        try {
            new DeepLinkAnnotatedElement("http", element, Type.CLASS);
            Assert.fail();
        } catch (MalformedURLException ignored) {
        }
    }

    @Test
    public void testMissingScheme() {
        try {
            new DeepLinkAnnotatedElement("example.com/something", element, Type.CLASS);
            Assert.fail();
        } catch (MalformedURLException ignored) {
        }
    }
}

