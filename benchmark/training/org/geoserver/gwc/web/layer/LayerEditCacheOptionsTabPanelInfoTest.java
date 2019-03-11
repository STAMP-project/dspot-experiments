/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web.layer;


import org.apache.wicket.model.IModel;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.config.GWCConfig;
import org.geoserver.gwc.layer.GeoServerTileLayer;
import org.geoserver.gwc.layer.GeoServerTileLayerInfo;
import org.geoserver.gwc.layer.GeoServerTileLayerInfoImpl;
import org.geoserver.gwc.layer.TileLayerInfoUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class LayerEditCacheOptionsTabPanelInfoTest {
    LayerEditCacheOptionsTabPanelInfo panelInfo;

    GWCConfig defaults;

    GWC gwc;

    IModel<? extends ResourceInfo> resourceModel;

    LayerInfo layer;

    IModel<LayerInfo> layerModel;

    @Test
    public void testCreateOwnModelNew() {
        final boolean isNew = true;
        IModel<GeoServerTileLayerInfo> ownModel;
        ownModel = panelInfo.createOwnModel(layerModel, isNew);
        Assert.assertNotNull(ownModel);
        GeoServerTileLayerInfoImpl expected = TileLayerInfoUtil.loadOrCreate(layer, defaults);
        Assert.assertEquals(expected, ownModel.getObject());
    }

    @Test
    public void testCreateOwnModelExisting() {
        final boolean isNew = false;
        IModel<GeoServerTileLayerInfo> ownModel;
        ownModel = panelInfo.createOwnModel(layerModel, isNew);
        Assert.assertNotNull(ownModel);
        GeoServerTileLayerInfo expected = TileLayerInfoUtil.loadOrCreate(layer, defaults);
        Assert.assertEquals(expected, ownModel.getObject());
        GeoServerTileLayer tileLayer = Mockito.mock(GeoServerTileLayer.class);
        expected = new GeoServerTileLayerInfoImpl();
        expected.setEnabled(true);
        Mockito.when(tileLayer.getInfo()).thenReturn(expected);
        Mockito.when(gwc.getTileLayer(ArgumentMatchers.same(layer))).thenReturn(tileLayer);
        ownModel = panelInfo.createOwnModel(layerModel, isNew);
        Assert.assertEquals(expected, ownModel.getObject());
    }
}

