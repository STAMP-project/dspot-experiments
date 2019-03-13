/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver;


import java.util.List;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.ModuleStatus;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class ModuleStatusTest extends GeoServerSystemTestSupport {
    @Test
    public void ModuleStatusTest() {
        GeoServerExtensions gse = new GeoServerExtensions();
        List<ModuleStatus> statusBeans = gse.extensions(ModuleStatus.class);
        Assert.assertEquals("gs-main", statusBeans.get(0).getModule());
        Assert.assertEquals("GeoServer Main", statusBeans.get(0).getName());
    }
}

