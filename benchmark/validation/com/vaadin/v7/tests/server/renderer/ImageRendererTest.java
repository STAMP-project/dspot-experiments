package com.vaadin.v7.tests.server.renderer;


import FontAwesome.AMBULANCE;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.v7.ui.renderers.ImageRenderer;
import elemental.json.JsonValue;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class ImageRendererTest {
    private ImageRenderer renderer;

    @Test
    public void testThemeResource() {
        JsonValue v = renderer.encode(new ThemeResource("foo.png"));
        Assert.assertEquals("theme://foo.png", getUrl(v));
    }

    @Test
    public void testExternalResource() {
        JsonValue v = renderer.encode(new ExternalResource("http://example.com/foo.png"));
        Assert.assertEquals("http://example.com/foo.png", getUrl(v));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileResource() {
        renderer.encode(new FileResource(new File("/tmp/foo.png")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassResource() {
        renderer.encode(new ClassResource("img/foo.png"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFontIcon() {
        renderer.encode(AMBULANCE);
    }
}

