/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config;


import java.io.FileOutputStream;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class GeoServerPropertyConfigurerTest {
    ClassPathXmlApplicationContext ctx;

    @Test
    public void testDefaults() {
        GeoServerPropertyConfigurerTest.Foo f = ((GeoServerPropertyConfigurerTest.Foo) (ctx.getBean("myBean")));
        Assert.assertEquals("value1", f.getBar());
        Assert.assertEquals("value2", f.getBaz());
    }

    @Test
    public void testUserSpecified() throws Exception {
        Properties p = new Properties();
        p.put("prop1", "foobar");
        p.put("prop2", "barfoo");
        FileOutputStream out = new FileOutputStream("target/foo.properties");
        p.store(out, "");
        out.flush();
        out.close();
        ctx.refresh();
        GeoServerPropertyConfigurerTest.Foo f = ((GeoServerPropertyConfigurerTest.Foo) (ctx.getBean("myBean")));
        Assert.assertEquals("foobar", f.getBar());
        Assert.assertEquals("barfoo", f.getBaz());
    }

    static class Foo {
        String bar;

        String baz;

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public void setBaz(String baz) {
            this.baz = baz;
        }

        public String getBaz() {
            return baz;
        }
    }
}

