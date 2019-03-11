/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.layer;


import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.event.impl.CatalogModifyEventImpl;
import org.geoserver.catalog.event.impl.CatalogPostModifyEventImpl;
import org.geoserver.gwc.GWC;
import org.geowebcache.filter.parameters.ParameterFilter;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CatalogStyleChangeListenerTest {
    private final String STYLE_NAME = "highways";

    private String STYLE_NAME_MODIFIED = (STYLE_NAME) + "_modified";

    private final String PREFIXED_RESOURCE_NAME = "mock:Layer";

    private GWC mockMediator;

    private ResourceInfo mockResourceInfo;

    private LayerInfo mockLayerInfo;

    private StyleInfo mockStyle;

    private GeoServerTileLayer mockTileLayer;

    private GeoServerTileLayerInfoImpl mockTileLayerInfo;

    private CatalogModifyEventImpl styleNameModifyEvent;

    private CatalogStyleChangeListener listener;

    @Test
    public void testIgnorableChange() throws Exception {
        // not a name change
        styleNameModifyEvent.setPropertyNames(Arrays.asList("fileName"));
        listener.handleModifyEvent(styleNameModifyEvent);
        // name didn't change at all
        styleNameModifyEvent.setPropertyNames(Arrays.asList("name"));
        styleNameModifyEvent.setOldValues(Arrays.asList(STYLE_NAME));
        styleNameModifyEvent.setNewValues(Arrays.asList(STYLE_NAME));
        listener.handleModifyEvent(styleNameModifyEvent);
        // not a style change
        styleNameModifyEvent.setSource(Mockito.mock(LayerInfo.class));
        listener.handleModifyEvent(styleNameModifyEvent);
        // a change in the name of the default style should not cause a truncate
        Mockito.verify(mockMediator, Mockito.never()).truncateByLayerAndStyle(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        // nor a save, as the default style name is dynamic
        Mockito.verify(mockMediator, Mockito.never()).save(((GeoServerTileLayer) (ArgumentMatchers.anyObject())));
        Mockito.verify(mockTileLayer, Mockito.never()).getInfo();
        Mockito.verify(mockTileLayerInfo, Mockito.never()).cachedStyles();
    }

    @Test
    public void testRenameDefaultStyle() throws Exception {
        // this is another case of an ignorable change. Renaming the default style shall have no
        // impact.
        listener.handleModifyEvent(styleNameModifyEvent);
        // a change in the name of the default style should not cause a truncate
        Mockito.verify(mockMediator, Mockito.never()).truncateByLayerAndStyle(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        // nor a save, as the default style name is dynamic
        Mockito.verify(mockMediator, Mockito.never()).save(((GeoServerTileLayer) (ArgumentMatchers.anyObject())));
        Mockito.verify(mockTileLayer, Mockito.atLeastOnce()).getInfo();
        Mockito.verify(mockTileLayerInfo, Mockito.atLeastOnce()).cachedStyles();
    }

    @Test
    public void testRenameAlternateStyle() throws Exception {
        Set<ParameterFilter> params = new HashSet<ParameterFilter>();
        StyleParameterFilter newStyleFilter = new StyleParameterFilter();
        newStyleFilter.setStyles(ImmutableSet.of(STYLE_NAME));
        params.add(newStyleFilter);
        TileLayerInfoUtil.setCachedStyles(mockTileLayerInfo, null, ImmutableSet.of(STYLE_NAME));
        Mockito.verify(mockTileLayerInfo).addParameterFilter(((ParameterFilter) (ArgumentMatchers.argThat(Matchers.allOf(Matchers.hasProperty("key", Matchers.is("STYLES")), Matchers.hasProperty("styles", Matchers.is(ImmutableSet.of(STYLE_NAME))))))));
        ImmutableSet<String> styles = ImmutableSet.of(STYLE_NAME);
        Mockito.when(mockTileLayerInfo.cachedStyles()).thenReturn(styles);
        listener.handleModifyEvent(styleNameModifyEvent);
        Mockito.verify(mockTileLayerInfo).addParameterFilter(((ParameterFilter) (ArgumentMatchers.argThat(Matchers.allOf(Matchers.hasProperty("key", Matchers.is("STYLES")), Matchers.hasProperty("styles", Matchers.is(ImmutableSet.of(STYLE_NAME_MODIFIED))))))));
        Mockito.verify(mockTileLayer, Mockito.times(1)).resetParameterFilters();
        Mockito.verify(mockMediator, Mockito.times(1)).truncateByLayerAndStyle(ArgumentMatchers.eq(PREFIXED_RESOURCE_NAME), ArgumentMatchers.eq(STYLE_NAME));
        Mockito.verify(mockMediator, Mockito.times(1)).save(ArgumentMatchers.same(mockTileLayer));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLayerInfoDefaultOrAlternateStyleChanged() throws Exception {
        Mockito.when(mockMediator.getLayerInfosFor(ArgumentMatchers.same(mockStyle))).thenReturn(Collections.singleton(mockLayerInfo));
        Mockito.when(mockMediator.getLayerGroupsFor(ArgumentMatchers.same(mockStyle))).thenReturn(Collections.EMPTY_LIST);
        CatalogPostModifyEventImpl postModifyEvent = new CatalogPostModifyEventImpl();
        postModifyEvent.setSource(mockStyle);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator, Mockito.times(1)).truncateByLayerAndStyle(ArgumentMatchers.eq(PREFIXED_RESOURCE_NAME), ArgumentMatchers.eq(STYLE_NAME));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLayerGroupInfoImplicitOrExplicitStyleChanged() throws Exception {
        LayerGroupInfo mockGroup = Mockito.mock(LayerGroupInfo.class);
        Mockito.when(GWC.tileLayerName(mockGroup)).thenReturn("mockGroup");
        Mockito.when(mockMediator.getLayerInfosFor(ArgumentMatchers.same(mockStyle))).thenReturn(Collections.EMPTY_LIST);
        Mockito.when(mockMediator.getLayerGroupsFor(ArgumentMatchers.same(mockStyle))).thenReturn(Collections.singleton(mockGroup));
        CatalogPostModifyEventImpl postModifyEvent = new CatalogPostModifyEventImpl();
        postModifyEvent.setSource(mockStyle);
        listener.handlePostModifyEvent(postModifyEvent);
        Mockito.verify(mockMediator, Mockito.times(1)).truncate(ArgumentMatchers.eq("mockGroup"));
    }

    @Test
    public void testChangeWorkspaceWithoutName() {
        CatalogModifyEventImpl modifyEvent = new CatalogModifyEventImpl();
        modifyEvent.setSource(mockStyle);
        modifyEvent.setPropertyNames(Collections.singletonList("workspace"));
        modifyEvent.setOldValues(Collections.singletonList(""));
        modifyEvent.setNewValues(Collections.singletonList("test"));
        // should occur without exception
        listener.handleModifyEvent(modifyEvent);
    }
}

