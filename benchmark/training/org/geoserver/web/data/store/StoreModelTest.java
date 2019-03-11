/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import java.util.List;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.workspace.WorkspaceDetachableModel;
import org.junit.Assert;
import org.junit.Test;


public class StoreModelTest extends GeoServerWicketTestSupport {
    @Test
    public void testStoreModel() throws Exception {
        DataStoreInfo s = getFeatureTypeInfo(MockData.PRIMITIVEGEOFEATURE).getStore();
        StoreModel<DataStoreInfo> model = new StoreModel<DataStoreInfo>(s);
        model = serializeDeserialize(model);
        Assert.assertEquals(s, model.getObject());
        model.detach();
        Assert.assertEquals(s, model.getObject());
    }

    @Test
    public void testStoreModelSetNull() throws Exception {
        DataStoreInfo s = getFeatureTypeInfo(MockData.PRIMITIVEGEOFEATURE).getStore();
        StoreModel<DataStoreInfo> model = new StoreModel<DataStoreInfo>(s);
        model = serializeDeserialize(model);
        Assert.assertEquals(s, model.getObject());
        model.detach();
        Assert.assertEquals(s, model.getObject());
        model.setObject(null);
        Assert.assertNull(model.getObject());
        model = serializeDeserialize(model);
        model.detach();
        Assert.assertNull(model.getObject());
    }

    @Test
    public void testStoresModel() throws Exception {
        WorkspaceDetachableModel ws = new WorkspaceDetachableModel(getCatalog().getWorkspaceByName("sf"));
        StoresModel model = new StoresModel(ws);
        List<StoreInfo> stores = getCatalog().getStoresByWorkspace("ws", StoreInfo.class);
        for (StoreInfo s : stores) {
            Assert.assertTrue(model.getObject().contains(s));
        }
        model.detach();
        for (StoreInfo s : stores) {
            Assert.assertTrue(model.getObject().contains(s));
        }
        model = serializeDeserialize(model);
        for (StoreInfo s : stores) {
            Assert.assertTrue(model.getObject().contains(s));
        }
    }
}

