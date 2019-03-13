/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.script.web;


import ScriptType.WPS;
import org.apache.wicket.markup.repeater.data.DataView;
import org.geoserver.script.ScriptManager;
import org.geoserver.script.ScriptType;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class ScriptPageTest extends GeoServerWicketTestSupport {
    private ScriptManager scriptManager;

    @Test
    public void testLoad() {
        tester.assertRenderedPage(ScriptPage.class);
        tester.assertNoErrorMessage();
        DataView dv = ((DataView) (tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Assert.assertEquals(dv.size(), 2);
        Script script = ((Script) (dv.getDataProvider().iterator(0, 1).next()));
        Assert.assertEquals("buffer", script.getName());
        Assert.assertEquals(WPS, ScriptType.getByLabel(script.getType()));
    }
}

