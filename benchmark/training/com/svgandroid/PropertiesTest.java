package com.svgandroid;


import SVGParser.Properties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Vlad Medvedev on 26.01.2016.
 * vladislav.medvedev@devfactory.com
 */
public class PropertiesTest {
    @Test
    public void getFloat_defValue() {
        SVGParser.Properties properties = new SVGParser.Properties(new AttributesMock(new AttributesMock.Pair("someProperty2", "2.0")));
        Assert.assertThat(properties.getFloat("someProperty", 1.0F), CoreMatchers.is(1.0F));
        Assert.assertThat(properties.getFloat("someProperty2", 1.0F), CoreMatchers.is(2.0F));
    }

    @Test
    public void getFloat() {
        SVGParser.Properties properties = new SVGParser.Properties(new AttributesMock(new AttributesMock.Pair("someProperty", "1.0")));
        Assert.assertThat(properties.getFloat("someProperty"), CoreMatchers.is(1.0F));
    }

    @Test
    public void getFloat_wrongFloatFormat() {
        SVGParser.Properties properties = new SVGParser.Properties(new AttributesMock(new AttributesMock.Pair("someProperty", "foo")));
        Assert.assertThat(properties.getFloat("someProperty"), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

