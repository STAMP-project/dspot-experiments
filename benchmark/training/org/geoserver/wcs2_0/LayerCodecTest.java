/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0;


import java.util.List;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.ows.LocalWorkspace;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wcs2_0.util.NCNameResourceCodec;
import org.junit.Assert;
import org.junit.Test;


public class LayerCodecTest extends GeoServerSystemTestSupport {
    @Test
    public void testBasicKVP() throws Exception {
        {
            List<LayerInfo> list0 = NCNameResourceCodec.getLayers(getCatalog(), "pippo_topo");
            Assert.assertNotNull(list0);
            Assert.assertEquals(0, list0.size());
        }
        {
            List<LayerInfo> list1 = NCNameResourceCodec.getLayers(getCatalog(), "pippo__topo");
            Assert.assertNotNull(list1);
            Assert.assertEquals(0, list1.size());
        }
        {
            List<LayerInfo> list = NCNameResourceCodec.getLayers(getCatalog(), "wcs__BlueMarble");
            Assert.assertNotNull(list);
            Assert.assertEquals(1, list.size());
        }
        {
            // Setting the LocalWorkspace to WCS
            WorkspaceInfo ws = getCatalog().getWorkspaceByName("wcs");
            Assert.assertNotNull(ws);
            WorkspaceInfo oldWs = LocalWorkspace.get();
            LocalWorkspace.set(ws);
            List<LayerInfo> list = NCNameResourceCodec.getLayers(getCatalog(), "BlueMarble");
            Assert.assertNotNull(list);
            Assert.assertEquals(1, list.size());
            LocalWorkspace.set(oldWs);
        }
        {
            List<LayerInfo> list = NCNameResourceCodec.getLayers(getCatalog(), "BlueMarble");
            Assert.assertNotNull(list);
            Assert.assertEquals(1, list.size());
        }
    }
}

