/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.wicket.property;


import java.util.Iterator;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class PropertyEditorFormComponentTest extends GeoServerWicketTestSupport {
    Foo foo;

    @Test
    @SuppressWarnings("TryFailThrowable")
    public void testRemove() {
        foo.getProps().put("foo", "bar");
        foo.getProps().put("bar", "baz");
        foo.getProps().put("baz", "foo");
        startPage();
        GeoServerWicketTestSupport.tester.assertComponent("form:props:container:list:0:remove", AjaxLink.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:props:container:list:1:remove", AjaxLink.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:props:container:list:2:remove", AjaxLink.class);
        try {
            GeoServerWicketTestSupport.tester.assertComponent("form:props:container:list:3:remove", AjaxLink.class);
            Assert.fail();
        } catch (AssertionError e) {
        }
        ListView list = ((ListView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:props:container:list")));
        Assert.assertNotNull(list);
        int i = 0;
        for (Iterator<Component> it = list.iterator(); it.hasNext(); i++) {
            if ("baz".equals(it.next().get("key").getDefaultModelObjectAsString())) {
                break;
            }
        }
        Assert.assertFalse((i == 3));
        GeoServerWicketTestSupport.tester.clickLink((("form:props:container:list:" + i) + ":remove"), true);
        GeoServerWicketTestSupport.tester.newFormTester("form").submit();
        Assert.assertEquals(2, foo.getProps().size());
        Assert.assertEquals("bar", foo.getProps().get("foo"));
        Assert.assertEquals("baz", foo.getProps().get("bar"));
        Assert.assertFalse(foo.getProps().containsKey("baz"));
    }

    @Test
    public void testAddRemove() {
        startPage();
        GeoServerWicketTestSupport.tester.clickLink("form:props:add", true);
        GeoServerWicketTestSupport.tester.assertComponent("form:props:container:list:0:key", TextField.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:props:container:list:0:value", TextField.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:props:container:list:0:remove", AjaxLink.class);
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("props:container:list:0:key", "foo");
        form.setValue("props:container:list:0:value", "bar");
        GeoServerWicketTestSupport.tester.clickLink("form:props:container:list:0:remove", true);
        Assert.assertNull(form.getForm().get("props:container:list:0:key"));
        Assert.assertNull(form.getForm().get("props:container:list:0:value"));
        Assert.assertNull(form.getForm().get("props:container:list:0:remove"));
        form.submit();
        Assert.assertTrue(foo.getProps().isEmpty());
    }
}

