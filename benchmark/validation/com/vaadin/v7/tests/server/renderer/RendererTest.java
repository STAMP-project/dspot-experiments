package com.vaadin.v7.tests.server.renderer;


import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.renderers.ButtonRenderer;
import com.vaadin.v7.ui.renderers.DateRenderer;
import com.vaadin.v7.ui.renderers.HtmlRenderer;
import com.vaadin.v7.ui.renderers.NumberRenderer;
import com.vaadin.v7.ui.renderers.TextRenderer;
import elemental.json.JsonValue;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class RendererTest {
    private static class TestBean {
        int i = 42;

        @Override
        public String toString() {
            return ("TestBean [" + (i)) + "]";
        }
    }

    private static class ExtendedBean extends RendererTest.TestBean {
        float f = 3.14F;
    }

    private static class TestRenderer extends TextRenderer {
        @Override
        public JsonValue encode(String value) {
            return super.encode((("renderer(" + value) + ")"));
        }
    }

    private static class TestConverter implements Converter<String, RendererTest.TestBean> {
        @Override
        public RendererTest.TestBean convertToModel(String value, Class<? extends RendererTest.TestBean> targetType, Locale locale) throws ConversionException {
            return null;
        }

        @Override
        public String convertToPresentation(RendererTest.TestBean value, Class<? extends String> targetType, Locale locale) throws ConversionException {
            if (value instanceof RendererTest.ExtendedBean) {
                return ((("ExtendedBean(" + (value.i)) + ", ") + (((RendererTest.ExtendedBean) (value)).f)) + ")";
            } else {
                return ("TestBean(" + (value.i)) + ")";
            }
        }

        @Override
        public Class<RendererTest.TestBean> getModelType() {
            return RendererTest.TestBean.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    }

    private Grid grid;

    private Column intColumn;

    private Column textColumn;

    private Column beanColumn;

    private Column htmlColumn;

    private Column numberColumn;

    private Column dateColumn;

    private Column extendedBeanColumn;

    private Column buttonColumn;

    @Test
    public void testDefaultRendererAndConverter() throws Exception {
        Assert.assertSame(TextRenderer.class, intColumn.getRenderer().getClass());
        Assert.assertSame(StringToIntegerConverter.class, intColumn.getConverter().getClass());
        Assert.assertSame(TextRenderer.class, textColumn.getRenderer().getClass());
        // String->String; converter not needed
        Assert.assertNull(textColumn.getConverter());
        Assert.assertSame(TextRenderer.class, beanColumn.getRenderer().getClass());
        // MyBean->String; converter not found
        Assert.assertNull(beanColumn.getConverter());
    }

    @Test
    public void testFindCompatibleConverter() throws Exception {
        intColumn.setRenderer(renderer());
        Assert.assertSame(StringToIntegerConverter.class, intColumn.getConverter().getClass());
        textColumn.setRenderer(renderer());
        Assert.assertNull(textColumn.getConverter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotFindConverter() {
        beanColumn.setRenderer(renderer());
    }

    @Test
    public void testExplicitConverter() throws Exception {
        beanColumn.setRenderer(renderer(), converter());
        extendedBeanColumn.setRenderer(renderer(), converter());
    }

    @Test
    public void testEncoding() throws Exception {
        Assert.assertEquals("42", render(intColumn, 42).asString());
        intColumn.setRenderer(renderer());
        Assert.assertEquals("renderer(42)", render(intColumn, 42).asString());
        Assert.assertEquals("2.72", render(textColumn, "2.72").asString());
        textColumn.setRenderer(new RendererTest.TestRenderer());
        Assert.assertEquals("renderer(2.72)", render(textColumn, "2.72").asString());
    }

    @Test
    public void testEncodingWithoutConverter() throws Exception {
        Assert.assertEquals("TestBean [42]", render(beanColumn, new RendererTest.TestBean()).asString());
    }

    @Test
    public void testBeanEncoding() throws Exception {
        beanColumn.setRenderer(renderer(), converter());
        extendedBeanColumn.setRenderer(renderer(), converter());
        Assert.assertEquals("renderer(TestBean(42))", render(beanColumn, new RendererTest.TestBean()).asString());
        Assert.assertEquals("renderer(ExtendedBean(42, 3.14))", render(beanColumn, new RendererTest.ExtendedBean()).asString());
        Assert.assertEquals("renderer(ExtendedBean(42, 3.14))", render(extendedBeanColumn, new RendererTest.ExtendedBean()).asString());
    }

    @Test
    public void testNullEncoding() {
        textColumn.setRenderer(new TextRenderer());
        htmlColumn.setRenderer(new HtmlRenderer());
        numberColumn.setRenderer(new NumberRenderer());
        dateColumn.setRenderer(new DateRenderer());
        buttonColumn.setRenderer(new ButtonRenderer());
        Assert.assertEquals("", textColumn.getRenderer().encode(null).asString());
        Assert.assertEquals("", htmlColumn.getRenderer().encode(null).asString());
        Assert.assertEquals("", numberColumn.getRenderer().encode(null).asString());
        Assert.assertEquals("", dateColumn.getRenderer().encode(null).asString());
        Assert.assertEquals("", buttonColumn.getRenderer().encode(null).asString());
    }

    @Test
    public void testNullEncodingWithDefault() {
        textColumn.setRenderer(new TextRenderer("default value"));
        htmlColumn.setRenderer(new HtmlRenderer("default value"));
        numberColumn.setRenderer(new NumberRenderer("%s", Locale.getDefault(), "default value"));
        dateColumn.setRenderer(new DateRenderer("%s", "default value"));
        buttonColumn.setRenderer(new ButtonRenderer("default value"));
        Assert.assertEquals("default value", textColumn.getRenderer().encode(null).asString());
        Assert.assertEquals("default value", htmlColumn.getRenderer().encode(null).asString());
        Assert.assertEquals("default value", numberColumn.getRenderer().encode(null).asString());
        Assert.assertEquals("default value", dateColumn.getRenderer().encode(null).asString());
        Assert.assertEquals("default value", buttonColumn.getRenderer().encode(null).asString());
    }
}

