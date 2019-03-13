/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.layer;


import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.event.CatalogModifyEvent;
import org.geoserver.catalog.event.CatalogPostModifyEvent;
import org.geoserver.catalog.event.impl.CatalogAddEventImpl;
import org.geoserver.catalog.event.impl.CatalogRemoveEventImpl;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.config.GWCConfig;
import org.geowebcache.grid.GridSetBroker;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;


/**
 *
 *
 * @author groldan
 */
public class CatalogLayerEventListenerTest {
    private static final String NAMESPACE_PREFIX = "mock";

    private static final String RESOURCE_NAME = "Layer";

    private static final String PREFIXED_RESOURCE_NAME = "mock:Layer";

    private static final String LAYER_GROUP_NAME = "LayerGroupName";

    private GWC mockMediator;

    private LayerInfo mockLayerInfo;

    private FeatureTypeInfo mockResourceInfo;

    private NamespaceInfo mockNamespaceInfo;

    private LayerGroupInfo mockLayerGroupInfo;

    private CatalogLayerEventListener listener;

    private StyleInfo mockDefaultStyle;

    private Set<StyleInfo> mockStyles;

    @Test
    public void testLayerInfoAdded() throws Exception {
        CatalogAddEventImpl event = new CatalogAddEventImpl();
        event.setSource(mockLayerInfo);
        listener.handleAddEvent(event);
        Mockito.verify(mockMediator).add(Mockito.any(GeoServerTileLayer.class));
    }

    @Test
    public void testLayerGroupInfoAdded() throws Exception {
        CatalogAddEventImpl event = new CatalogAddEventImpl();
        event.setSource(mockLayerGroupInfo);
        listener.handleAddEvent(event);
        Mockito.verify(mockMediator).add(Mockito.any(GeoServerTileLayer.class));
    }

    @Test
    public void testLayerInfoRemoved() throws Exception {
        CatalogRemoveEventImpl event = new CatalogRemoveEventImpl();
        event.setSource(mockLayerInfo);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockLayerInfo))).thenReturn(true);
        listener.handleRemoveEvent(event);
        Mockito.verify(mockMediator).removeTileLayers(ArgumentMatchers.eq(Arrays.asList(mockResourceInfo.prefixedName())));
    }

    @Test
    public void testLayerGroupInfoRemoved() throws Exception {
        CatalogRemoveEventImpl event = new CatalogRemoveEventImpl();
        event.setSource(mockLayerGroupInfo);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockLayerGroupInfo))).thenReturn(true);
        listener.handleRemoveEvent(event);
        Mockito.verify(mockMediator).removeTileLayers(ArgumentMatchers.eq(Arrays.asList(GWC.tileLayerName(mockLayerGroupInfo))));
    }

    @Test
    public void testResourceInfoRenamed() throws Exception {
        final String oldTileLayerName = mockResourceInfo.prefixedName();
        final String renamedResouceName = (CatalogLayerEventListenerTest.RESOURCE_NAME) + "_Renamed";
        final String renamedPrefixedResouceName = (CatalogLayerEventListenerTest.PREFIXED_RESOURCE_NAME) + "_Renamed";
        // rename mockResourceInfo
        Mockito.when(mockResourceInfo.getName()).thenReturn(renamedResouceName);
        Mockito.when(mockResourceInfo.prefixedName()).thenReturn(renamedPrefixedResouceName);
        CatalogModifyEvent modifyEvent = Mockito.mock(CatalogModifyEvent.class);
        Mockito.when(modifyEvent.getSource()).thenReturn(mockResourceInfo);
        Mockito.when(modifyEvent.getPropertyNames()).thenReturn(Arrays.asList("name"));
        Mockito.when(modifyEvent.getOldValues()).thenReturn(Arrays.asList(((Object) (CatalogLayerEventListenerTest.RESOURCE_NAME))));
        Mockito.when(modifyEvent.getNewValues()).thenReturn(Arrays.asList(((Object) (renamedResouceName))));
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(mockLayerInfo, GWCConfig.getOldDefaults());
        GeoServerTileLayer tileLayer = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockResourceInfo))).thenReturn(true);
        Mockito.when(tileLayer.getInfo()).thenReturn(info);
        Mockito.when(tileLayer.getLayerInfo()).thenReturn(mockLayerInfo);
        Mockito.when(mockMediator.getTileLayer(ArgumentMatchers.same(mockResourceInfo))).thenReturn(tileLayer);
        Mockito.when(mockMediator.getTileLayerByName(ArgumentMatchers.eq(oldTileLayerName))).thenReturn(tileLayer);
        listener.handleModifyEvent(modifyEvent);
        Mockito.verify(mockMediator, Mockito.times(1)).hasTileLayer(ArgumentMatchers.same(mockResourceInfo));
        CatalogPostModifyEvent postModifyEvent = Mockito.mock(CatalogPostModifyEvent.class);
        Mockito.when(postModifyEvent.getSource()).thenReturn(mockResourceInfo);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator).rename(oldTileLayerName, renamedPrefixedResouceName);
        GeoServerTileLayer saved = mockMediator.getTileLayer(mockResourceInfo);
        Assert.assertNotNull(saved);
        Assert.assertNotNull(saved.getInfo());
        GeoServerTileLayerInfo savedInfo = saved.getInfo();
        Assert.assertSame(info, savedInfo);
        Assert.assertEquals(renamedPrefixedResouceName, savedInfo.getName());
    }

    @Test
    public void testCqlFilterChanged() throws Exception {
        // change the cql filter
        String cqlFilter = "name LIKE 'Foo%'";
        Mockito.when(mockResourceInfo.getCqlFilter()).thenReturn(cqlFilter);
        CatalogModifyEvent modifyEvent = Mockito.mock(CatalogModifyEvent.class);
        Mockito.when(modifyEvent.getSource()).thenReturn(mockResourceInfo);
        Mockito.when(modifyEvent.getPropertyNames()).thenReturn(Arrays.asList("cqlFilter"));
        Mockito.when(modifyEvent.getOldValues()).thenReturn(Arrays.asList(((Object) (null))));
        Mockito.when(modifyEvent.getNewValues()).thenReturn(Arrays.asList(((Object) (cqlFilter))));
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(mockLayerInfo, GWCConfig.getOldDefaults());
        GeoServerTileLayer tileLayer = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockResourceInfo))).thenReturn(true);
        Mockito.when(tileLayer.getInfo()).thenReturn(info);
        Mockito.when(tileLayer.getLayerInfo()).thenReturn(mockLayerInfo);
        Mockito.when(mockMediator.getTileLayer(ArgumentMatchers.same(mockResourceInfo))).thenReturn(tileLayer);
        String resourceName = mockResourceInfo.prefixedName();
        Mockito.when(mockMediator.getTileLayerByName(ArgumentMatchers.eq(resourceName))).thenReturn(tileLayer);
        listener.handleModifyEvent(modifyEvent);
        Mockito.verify(mockMediator, Mockito.times(1)).hasTileLayer(ArgumentMatchers.same(mockResourceInfo));
        CatalogPostModifyEvent postModifyEvent = Mockito.mock(CatalogPostModifyEvent.class);
        Mockito.when(postModifyEvent.getSource()).thenReturn(mockResourceInfo);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator).truncate(ArgumentMatchers.eq(resourceName));
    }

    @Test
    public void testLayerGroupInfoRenamed() throws Exception {
        final String oldGroupName = CatalogLayerEventListenerTest.LAYER_GROUP_NAME;
        final String renamedGroupName = (CatalogLayerEventListenerTest.LAYER_GROUP_NAME) + "_Renamed";
        CatalogModifyEvent modifyEvent = Mockito.mock(CatalogModifyEvent.class);
        Mockito.when(modifyEvent.getSource()).thenReturn(mockLayerGroupInfo);
        Mockito.when(modifyEvent.getPropertyNames()).thenReturn(Arrays.asList("name"));
        Mockito.when(modifyEvent.getOldValues()).thenReturn(Arrays.asList(((Object) (CatalogLayerEventListenerTest.LAYER_GROUP_NAME))));
        Mockito.when(modifyEvent.getNewValues()).thenReturn(Arrays.asList(((Object) (renamedGroupName))));
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(mockLayerGroupInfo, GWCConfig.getOldDefaults());
        GeoServerTileLayer tileLayer = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(tileLayer.getInfo()).thenReturn(info);
        Mockito.when(tileLayer.getLayerGroupInfo()).thenReturn(mockLayerGroupInfo);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockLayerGroupInfo))).thenReturn(true);
        Mockito.when(mockMediator.getTileLayer(ArgumentMatchers.same(mockLayerGroupInfo))).thenReturn(tileLayer);
        Mockito.when(mockMediator.getTileLayerByName(ArgumentMatchers.eq(oldGroupName))).thenReturn(tileLayer);
        // rename mockResourceInfo
        Mockito.when(GWC.tileLayerName(mockLayerGroupInfo)).thenReturn(renamedGroupName);
        listener.handleModifyEvent(modifyEvent);
        Mockito.verify(mockMediator, Mockito.times(1)).hasTileLayer(ArgumentMatchers.same(mockLayerGroupInfo));
        Mockito.verify(mockMediator, Mockito.times(1)).getTileLayer(ArgumentMatchers.same(mockLayerGroupInfo));
        CatalogPostModifyEvent postModifyEvent = Mockito.mock(CatalogPostModifyEvent.class);
        Mockito.when(postModifyEvent.getSource()).thenReturn(mockLayerGroupInfo);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator).rename(oldGroupName, renamedGroupName);
        GeoServerTileLayer saved = mockMediator.getTileLayer(mockLayerGroupInfo);
        Assert.assertNotNull(saved);
        Assert.assertNotNull(saved.getInfo());
        GeoServerTileLayerInfo savedInfo = saved.getInfo();
        Assert.assertSame(info, savedInfo);
        Assert.assertEquals(renamedGroupName, savedInfo.getName());
    }

    @Test
    public void testLayerGroupInfoRenamedDueToWorkspaceChanged() throws Exception {
        WorkspaceInfo workspace = Mockito.mock(WorkspaceInfo.class);
        Mockito.when(workspace.getName()).thenReturn("mockWs");
        CatalogModifyEvent modifyEvent = Mockito.mock(CatalogModifyEvent.class);
        Mockito.when(modifyEvent.getSource()).thenReturn(mockLayerGroupInfo);
        Mockito.when(modifyEvent.getPropertyNames()).thenReturn(Arrays.asList("workspace"));
        Mockito.when(modifyEvent.getOldValues()).thenReturn(Arrays.asList(((Object) (null))));
        Mockito.when(modifyEvent.getNewValues()).thenReturn(Arrays.asList(((Object) (workspace))));
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(mockLayerGroupInfo, GWCConfig.getOldDefaults());
        GeoServerTileLayer tileLayer = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(tileLayer.getInfo()).thenReturn(info);
        Mockito.when(tileLayer.getLayerGroupInfo()).thenReturn(mockLayerGroupInfo);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockLayerGroupInfo))).thenReturn(true);
        Mockito.when(mockMediator.getTileLayer(ArgumentMatchers.same(mockLayerGroupInfo))).thenReturn(tileLayer);
        final String oldLayerName = GWC.tileLayerName(mockLayerGroupInfo);
        Mockito.when(mockMediator.getTileLayerByName(ArgumentMatchers.eq(oldLayerName))).thenReturn(tileLayer);
        listener.handleModifyEvent(modifyEvent);
        Mockito.verify(mockMediator, Mockito.times(1)).hasTileLayer(ArgumentMatchers.same(mockLayerGroupInfo));
        Mockito.verify(mockMediator, Mockito.times(1)).getTileLayer(ArgumentMatchers.same(mockLayerGroupInfo));
        CatalogPostModifyEvent postModifyEvent = Mockito.mock(CatalogPostModifyEvent.class);
        Mockito.when(postModifyEvent.getSource()).thenReturn(mockLayerGroupInfo);
        // change group workspace
        Mockito.when(mockLayerGroupInfo.getWorkspace()).thenReturn(workspace);
        String prefixedName = ((workspace.getName()) + ":") + (mockLayerGroupInfo.getName());
        Mockito.when(mockLayerGroupInfo.prefixedName()).thenReturn(prefixedName);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator).rename(oldLayerName, prefixedName);
        GeoServerTileLayer saved = mockMediator.getTileLayer(mockLayerGroupInfo);
        Assert.assertNotNull(saved);
        Assert.assertNotNull(saved.getInfo());
        GeoServerTileLayerInfo savedInfo = saved.getInfo();
        Assert.assertSame(info, savedInfo);
        String tileLayerName = GWC.tileLayerName(mockLayerGroupInfo);
        String actual = savedInfo.getName();
        Assert.assertEquals(tileLayerName, actual);
    }

    @Test
    public void testResourceInfoNamespaceChanged() throws Exception {
        NamespaceInfo newNamespace = Mockito.mock(NamespaceInfo.class);
        Mockito.when(newNamespace.getPrefix()).thenReturn("newMock");
        final String oldPrefixedName = mockResourceInfo.prefixedName();
        final String newPrefixedName = ((newNamespace.getPrefix()) + ":") + (mockResourceInfo.getName());
        // set the new namespace
        Mockito.when(mockResourceInfo.getNamespace()).thenReturn(newNamespace);
        Mockito.when(mockResourceInfo.prefixedName()).thenReturn(newPrefixedName);
        CatalogModifyEvent modifyEvent = Mockito.mock(CatalogModifyEvent.class);
        Mockito.when(modifyEvent.getSource()).thenReturn(mockResourceInfo);
        Mockito.when(modifyEvent.getPropertyNames()).thenReturn(Arrays.asList("namespace"));
        Mockito.when(modifyEvent.getOldValues()).thenReturn(Arrays.asList(((Object) (mockNamespaceInfo))));
        Mockito.when(modifyEvent.getNewValues()).thenReturn(Arrays.asList(((Object) (newNamespace))));
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(mockLayerInfo, GWCConfig.getOldDefaults());
        GeoServerTileLayer tileLayer = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(tileLayer.getInfo()).thenReturn(info);
        Mockito.when(tileLayer.getLayerInfo()).thenReturn(mockLayerInfo);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockResourceInfo))).thenReturn(true);
        Mockito.when(mockMediator.getTileLayer(ArgumentMatchers.same(mockResourceInfo))).thenReturn(tileLayer);
        Mockito.when(mockMediator.getTileLayerByName(ArgumentMatchers.eq(oldPrefixedName))).thenReturn(tileLayer);
        listener.handleModifyEvent(modifyEvent);
        CatalogPostModifyEvent postModifyEvent = Mockito.mock(CatalogPostModifyEvent.class);
        Mockito.when(postModifyEvent.getSource()).thenReturn(mockResourceInfo);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator).rename(oldPrefixedName, newPrefixedName);
        GeoServerTileLayer saved = mockMediator.getTileLayer(mockResourceInfo);
        Assert.assertNotNull(saved);
        Assert.assertNotNull(saved.getInfo());
        GeoServerTileLayerInfo savedInfo = saved.getInfo();
        Assert.assertSame(info, savedInfo);
        Assert.assertEquals(newPrefixedName, savedInfo.getName());
    }

    @Test
    public void testLayerGroupInfoLayersChanged() throws Exception {
        CatalogModifyEvent modifyEvent = Mockito.mock(CatalogModifyEvent.class);
        Mockito.when(modifyEvent.getSource()).thenReturn(mockLayerGroupInfo);
        Mockito.when(modifyEvent.getPropertyNames()).thenReturn(Arrays.asList("layers"));
        List<LayerInfo> oldLayers = Collections.emptyList();
        List<LayerInfo> newLayers = Collections.singletonList(mockLayerInfo);
        Mockito.when(modifyEvent.getOldValues()).thenReturn(Collections.singletonList(((Object) (oldLayers))));
        Mockito.when(modifyEvent.getNewValues()).thenReturn(Collections.singletonList(((Object) (newLayers))));
        // the tile layer must exist otherwise the event will be ignored
        GeoServerTileLayerInfo tileLayerInfo = TileLayerInfoUtil.loadOrCreate(mockLayerGroupInfo, mockMediator.getConfig());
        GridSetBroker gridsets = new GridSetBroker(true, true);
        GeoServerTileLayer tileLayer = new GeoServerTileLayer(mockLayerGroupInfo, gridsets, tileLayerInfo);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockLayerGroupInfo))).thenReturn(true);
        Mockito.when(mockMediator.getTileLayer(ArgumentMatchers.same(mockLayerGroupInfo))).thenReturn(tileLayer);
        listener.handleModifyEvent(modifyEvent);
        CatalogPostModifyEvent postModifyEvent = Mockito.mock(CatalogPostModifyEvent.class);
        Mockito.when(postModifyEvent.getSource()).thenReturn(mockLayerGroupInfo);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator).truncate(ArgumentMatchers.eq(CatalogLayerEventListenerTest.LAYER_GROUP_NAME));
    }

    @Test
    public void testLayerGroupInfoStylesChanged() throws Exception {
        CatalogModifyEvent modifyEvent = Mockito.mock(CatalogModifyEvent.class);
        Mockito.when(modifyEvent.getSource()).thenReturn(mockLayerGroupInfo);
        Mockito.when(modifyEvent.getPropertyNames()).thenReturn(Arrays.asList("styles"));
        List<StyleInfo> oldStyles = Collections.emptyList();
        StyleInfo newStyle = Mockito.mock(StyleInfo.class);
        List<StyleInfo> newStyles = Collections.singletonList(newStyle);
        Mockito.when(modifyEvent.getOldValues()).thenReturn(Collections.singletonList(((Object) (oldStyles))));
        Mockito.when(modifyEvent.getNewValues()).thenReturn(Collections.singletonList(((Object) (newStyles))));
        // the tile layer must exist on the layer metadata otherwise the event will be ignored
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(mockLayerGroupInfo, GWCConfig.getOldDefaults());
        GeoServerTileLayer tileLayer = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(tileLayer.getInfo()).thenReturn(info);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockLayerGroupInfo))).thenReturn(true);
        Mockito.when(mockMediator.getTileLayer(ArgumentMatchers.same(mockLayerGroupInfo))).thenReturn(tileLayer);
        listener.handleModifyEvent(modifyEvent);
        CatalogPostModifyEvent postModifyEvent = Mockito.mock(CatalogPostModifyEvent.class);
        Mockito.when(postModifyEvent.getSource()).thenReturn(mockLayerGroupInfo);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator).truncate(ArgumentMatchers.eq(CatalogLayerEventListenerTest.LAYER_GROUP_NAME));
    }

    @Test
    public void testLayerInfoDefaultStyleChanged() throws Exception {
        final String oldName = "oldStyle";
        final String newName = "newStyle";
        StyleInfo oldStyle = Mockito.mock(StyleInfo.class);
        Mockito.when(oldStyle.prefixedName()).thenReturn(oldName);
        StyleInfo newStyle = Mockito.mock(StyleInfo.class);
        Mockito.when(newStyle.prefixedName()).thenReturn(newName);
        Mockito.when(mockLayerInfo.getDefaultStyle()).thenReturn(newStyle);
        CatalogModifyEvent modifyEvent = Mockito.mock(CatalogModifyEvent.class);
        Mockito.when(modifyEvent.getSource()).thenReturn(mockLayerInfo);
        Mockito.when(modifyEvent.getPropertyNames()).thenReturn(Arrays.asList("defaultStyle"));
        Mockito.when(modifyEvent.getOldValues()).thenReturn(Collections.singletonList(((Object) (oldStyle))));
        Mockito.when(modifyEvent.getNewValues()).thenReturn(Collections.singletonList(((Object) (newStyle))));
        GeoServerTileLayer tileLayer = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(mockMediator.getTileLayerByName(ArgumentMatchers.eq(CatalogLayerEventListenerTest.PREFIXED_RESOURCE_NAME))).thenReturn(tileLayer);
        GeoServerTileLayer lgTileLayer = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(mockMediator.getTileLayer(mockLayerGroupInfo)).thenReturn(lgTileLayer);
        // the tile layer must exist on the layer metadata otherwise the event will be ignored
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(mockLayerInfo, GWCConfig.getOldDefaults());
        Mockito.when(tileLayer.getInfo()).thenReturn(info);
        // same goes for the group layer
        GeoServerTileLayerInfo groupInfo = TileLayerInfoUtil.loadOrCreate(mockLayerGroupInfo, GWCConfig.getOldDefaults());
        Mockito.when(lgTileLayer.getInfo()).thenReturn(groupInfo);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.same(mockLayerInfo))).thenReturn(true);
        Mockito.when(mockMediator.getTileLayer(ArgumentMatchers.same(mockLayerInfo))).thenReturn(tileLayer);
        listener.handleModifyEvent(modifyEvent);
        CatalogPostModifyEvent postModifyEvent = Mockito.mock(CatalogPostModifyEvent.class);
        Mockito.when(postModifyEvent.getSource()).thenReturn(mockLayerInfo);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator).truncateByLayerDefaultStyle(ArgumentMatchers.eq(CatalogLayerEventListenerTest.PREFIXED_RESOURCE_NAME));
        // both the layer group and the layer got saved
        Mockito.verify(mockMediator, Mockito.times(2)).save(ArgumentMatchers.any(GeoServerTileLayer.class));
        // verify the layer group was also truncated
        Mockito.verify(mockMediator).truncate(CatalogLayerEventListenerTest.LAYER_GROUP_NAME);
    }

    @Test
    public void testLayerInfoAlternateStylesChanged() throws Exception {
        StyleInfo removedStyle = Mockito.mock(StyleInfo.class);
        Mockito.when(removedStyle.prefixedName()).thenReturn("removedStyleName");
        StyleInfo remainingStyle = Mockito.mock(StyleInfo.class);
        Mockito.when(remainingStyle.prefixedName()).thenReturn("remainingStyle");
        final Set<StyleInfo> oldStyles = new HashSet<StyleInfo>(Arrays.asList(remainingStyle, removedStyle));
        Mockito.when(mockLayerInfo.getStyles()).thenReturn(oldStyles);
        StyleInfo addedStyle = Mockito.mock(StyleInfo.class);
        Mockito.when(addedStyle.prefixedName()).thenReturn("addedStyleName");
        final Set<StyleInfo> newStyles = new HashSet<StyleInfo>(Arrays.asList(addedStyle, remainingStyle));
        CatalogModifyEvent modifyEvent = Mockito.mock(CatalogModifyEvent.class);
        Mockito.when(modifyEvent.getSource()).thenReturn(mockLayerInfo);
        Mockito.when(modifyEvent.getPropertyNames()).thenReturn(Arrays.asList("styles"));
        Mockito.when(modifyEvent.getOldValues()).thenReturn(Collections.singletonList(((Object) (oldStyles))));
        Mockito.when(modifyEvent.getNewValues()).thenReturn(Collections.singletonList(((Object) (newStyles))));
        GeoServerTileLayerInfo info = Mockito.mock(GeoServerTileLayerInfo.class);
        Mockito.when(info.cachedStyles()).thenReturn(ImmutableSet.of("remainingStyle", "removedStyleName"));
        Mockito.when(info.isAutoCacheStyles()).thenReturn(true);
        GeoServerTileLayer tileLayer = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(tileLayer.getInfo()).thenReturn(info);
        Mockito.when(mockMediator.hasTileLayer(ArgumentMatchers.any(CatalogInfo.class))).thenReturn(true);
        Mockito.when(mockMediator.getTileLayer(ArgumentMatchers.any(CatalogInfo.class))).thenReturn(tileLayer);
        listener.handleModifyEvent(modifyEvent);
        CatalogPostModifyEvent postModifyEvent = Mockito.mock(CatalogPostModifyEvent.class);
        // the tile layer must exist on the layer metadata otherwise the event will be ignored
        Mockito.when(mockLayerInfo.getStyles()).thenReturn(newStyles);
        Mockito.when(postModifyEvent.getSource()).thenReturn(mockLayerInfo);
        listener.handlePostModifyEvent(postModifyEvent);
        // check removedStyleName was truncated
        Mockito.verify(mockMediator).truncateByLayerAndStyle(ArgumentMatchers.eq(CatalogLayerEventListenerTest.PREFIXED_RESOURCE_NAME), ArgumentMatchers.eq("removedStyleName"));
        // check no other style was truncated
        Mockito.verify(mockMediator, Mockito.atMost(1)).truncateByLayerAndStyle(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        // verify only got modified
        Mockito.verify(mockMediator).save(MockitoHamcrest.argThat(new BaseMatcher<GeoServerTileLayer>() {
            @Override
            public boolean matches(Object item) {
                GeoServerTileLayer tl = ((GeoServerTileLayer) (item));
                LayerInfo li = tl.getLayerInfo();
                return li == (mockLayerInfo);
            }

            @Override
            public void describeTo(Description description) {
                // TODO Auto-generated method stub
            }
        }));
    }
}

