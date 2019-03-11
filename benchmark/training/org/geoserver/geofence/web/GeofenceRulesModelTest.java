/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.web;


import java.util.Arrays;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Geofence Rules Model Test
 *
 * @author Niels Charlier
 */
public class GeofenceRulesModelTest extends GeoServerWicketTestSupport {
    @Test
    public void testRulesModel() {
        GeofenceRulesModel model = new GeofenceRulesModel();
        ShortRule rule1 = model.newRule();
        rule1.setUserName("pipo");
        model.save(rule1);
        ShortRule rule2 = model.newRule();
        rule2.setUserName("jantje");
        model.save(rule2);
        ShortRule rule3 = model.newRule();
        rule3.setUserName("oen");
        model.save(rule3);
        Assert.assertEquals(3, model.getItems().size());
        Assert.assertEquals(rule1, model.getItems().get(2));
        Assert.assertEquals(rule2, model.getItems().get(1));
        Assert.assertEquals(rule3, model.getItems().get(0));
        Assert.assertEquals(0, rule3.getPriority());
        Assert.assertEquals(1, rule2.getPriority());
        Assert.assertEquals(2, rule1.getPriority());
        assertSynchronized(model);
        Assert.assertFalse(model.canDown(rule1));
        Assert.assertFalse(model.canUp(rule3));
        Assert.assertTrue(model.canDown(rule2));
        Assert.assertTrue(model.canUp(rule2));
        model.moveDown(rule2);
        model.moveUp(rule3);
        Assert.assertEquals(rule3, model.getItems().get(0));
        Assert.assertEquals(rule1, model.getItems().get(1));
        Assert.assertEquals(rule2, model.getItems().get(2));
        Assert.assertEquals(0, rule3.getPriority());
        Assert.assertEquals(1, rule1.getPriority());
        Assert.assertEquals(2, rule2.getPriority());
        rule2.setService("WFS");
        model.save(rule2);
        model.remove(Arrays.asList(rule1, rule3));
        Assert.assertEquals(1, model.getItems().size());
        assertSynchronized(model);
    }
}

