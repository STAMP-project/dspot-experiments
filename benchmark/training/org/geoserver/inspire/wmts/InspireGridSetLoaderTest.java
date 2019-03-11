/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.inspire.wmts;


import org.geoserver.gwc.GWC;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing that INSPIRE grid set is correctly loaded and configured.
 */
public class InspireGridSetLoaderTest extends GeoServerSystemTestSupport {
    @Test
    public void testGridSetLoading() throws Exception {
        GWC gwc = GWC.get();
        // let's see if the inspire grid set has been correctly registered
        Assert.assertThat(gwc.getGridSetBroker().get(InspireGridSetLoader.INSPIRE_GRID_SET_NAME), CoreMatchers.notNullValue());
        Assert.assertThat(gwc.isInternalGridSet(InspireGridSetLoader.INSPIRE_GRID_SET_NAME), CoreMatchers.is(true));
    }
}

