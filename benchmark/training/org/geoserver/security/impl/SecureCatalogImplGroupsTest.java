/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import Mode.NAMED;
import Mode.OPAQUE_CONTAINER;
import Mode.SINGLE;
import java.util.Arrays;
import org.geoserver.catalog.LayerGroupInfo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * A variant to {@link SecureCatalogImplTest} allowing a per test setup of group nesting and rules
 *
 * @author Andrea Aime - GeoSolutions
 */
public class SecureCatalogImplGroupsTest extends AbstractAuthorizationTest {
    private static final String NAMED_GROUP_NAME = "named";

    private static final String OPAQUE_GROUP_NAME = "opaque";

    private static final String SINGLE_GROUP_NAME = "single";

    private static final String NESTED_GROUP_NAME = "nested";

    private static final String[] DEFAULT_RULES = new String[]{ "*.*.r=*", "*.*.w=*" };

    @Test
    public void testWmsStandaloneOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        LayerGroupInfo opaque = prepareStandaloneOpaqueGroup();
        // direct access to layers not allowed
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // however we can access the group and the layers through it
        LayerGroupInfo securedGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedGroup);
        Assert.assertEquals(2, securedGroup.getLayers().size());
    }

    @Test
    public void testWfsStandaloneOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WFS");
        LayerGroupInfo opaque = prepareStandaloneOpaqueGroup();
        // direct access to layers is allowed in this case
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // and we can access the group and the layers through it
        LayerGroupInfo securedGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedGroup);
        Assert.assertEquals(2, securedGroup.getLayers().size());
    }

    @Test
    public void testWmsNamedOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        LayerGroupInfo opaque = prepareNamedAndOpaqueGroup();
        // direct access to layers allowed because of the named group
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // we can access the group and the layers through it
        LayerGroupInfo securedGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedGroup);
        Assert.assertEquals(2, securedGroup.getLayers().size());
    }

    @Test
    public void testWfsNamedOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WFS");
        LayerGroupInfo opaque = prepareNamedAndOpaqueGroup();
        // direct access to layers allowed
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // and we can access the group and the layers through it
        LayerGroupInfo securedGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedGroup);
        Assert.assertEquals(2, securedGroup.getLayers().size());
    }

    @Test
    public void testWmsSingleAndOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, statesLayer, roadsLayer);
        LayerGroupInfo single = buildLayerGroup(SecureCatalogImplGroupsTest.SINGLE_GROUP_NAME, SINGLE, null, statesLayer, roadsLayer);
        layerGroups = Arrays.asList(single, opaque);
        populateCatalog();
        // setup security
        buildManager(SecureCatalogImplGroupsTest.DEFAULT_RULES);
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // direct access to layers not allowed because the only container is in opaque mode
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // we can access the group and the layers through it
        LayerGroupInfo opaqueSecuredGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(opaqueSecuredGroup);
        Assert.assertEquals(2, opaqueSecuredGroup.getLayers().size());
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(single.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.getLayers().size());
    }

    @Test
    public void testWmsMilitaryNamedAndPublicOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, statesLayer, roadsLayer);
        LayerGroupInfo named = buildLayerGroup(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME, NAMED, null, statesLayer, roadsLayer);
        layerGroups = Arrays.asList(named, opaque);
        populateCatalog();
        // setup security
        buildManager(new String[]{ "named.r=MILITARY" });
        // try the ro user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // ... direct access to layers not allowed because the named group is not visible
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // ... but we can access the opaque group and the layers through it
        LayerGroupInfo securedGroup = sc.getLayerGroupByName(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME);
        Assert.assertNotNull(securedGroup);
        Assert.assertEquals(2, securedGroup.getLayers().size());
        // now try with the military one
        SecurityContextHolder.getContext().setAuthentication(milUser);
        // ... direct access to layers allowed because the named group is visible to the mil user
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
    }

    @Test
    public void testWmsPublicSingleAndSecuredOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, forestsLayer, roadsLayer);
        LayerGroupInfo single = buildLayerGroup(SecureCatalogImplGroupsTest.SINGLE_GROUP_NAME, SINGLE, null, statesLayer, roadsLayer);
        layerGroups = Arrays.asList(single, opaque);
        populateCatalog();
        // setup security, single allowed to everybody, opaque to military only
        buildManager(((SecureCatalogImplGroupsTest.SINGLE_GROUP_NAME) + ".r=*"), ((SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME) + ".r=MILITARY"));
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // try the ro user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // ... the only directly available layer should be states, contained in single
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // ... and even via groups it's the same
        Assert.assertNull(sc.getLayerGroupByName(opaque.prefixedName()));
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(single.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(1, securedSingleGroup.getLayers().size());
        // however switching to mil user
        SecurityContextHolder.getContext().setAuthentication(milUser);
        // ... same as above for direct access
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // but the opaque group is now visible along with its layers
        securedSingleGroup = sc.getLayerGroupByName(single.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.getLayers().size());
        LayerGroupInfo securedOpaqueGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedOpaqueGroup);
        Assert.assertEquals(2, securedOpaqueGroup.getLayers().size());
    }

    @Test
    public void testWmsSecuredSingleAndPublicOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, forestsLayer, roadsLayer);
        LayerGroupInfo single = buildLayerGroup(SecureCatalogImplGroupsTest.SINGLE_GROUP_NAME, SINGLE, null, statesLayer, roadsLayer);
        layerGroups = Arrays.asList(single, opaque);
        populateCatalog();
        // setup security, single allowed to everybody, opaque to military only
        buildManager(((SecureCatalogImplGroupsTest.SINGLE_GROUP_NAME) + ".r=MILITARY"), ((SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME) + ".r=*"));
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // try the ro user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // ... states should be visible since "single" auth does not cascade to contained layers
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // ... the single group is not available, the opaque allows access to all its layers, even
        // the ones shared with the single
        Assert.assertNull(sc.getLayerGroupByName(single.prefixedName()));
        LayerGroupInfo securedOpaqueGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedOpaqueGroup);
        Assert.assertEquals(2, securedOpaqueGroup.getLayers().size());
        // however switching to mil user
        SecurityContextHolder.getContext().setAuthentication(milUser);
        // ... now states becomes visible
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // but the opaque group is now visible along with its layers
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(single.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.getLayers().size());
        securedOpaqueGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedOpaqueGroup);
        Assert.assertEquals(2, securedOpaqueGroup.getLayers().size());
    }

    @Test
    public void testNestedOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, statesLayer, roadsLayer);
        LayerGroupInfo named = buildLayerGroup(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME, NAMED, null, forestsLayer, opaque);
        layerGroups = Arrays.asList(named, opaque);
        populateCatalog();
        // setup security
        buildManager(SecureCatalogImplGroupsTest.DEFAULT_RULES);
        // direct access forests allowed but not states and roads
        Assert.assertNotNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // however via group access we can see all layers
        LayerGroupInfo securedNamedGroup = sc.getLayerGroupByName(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME);
        Assert.assertEquals(3, securedNamedGroup.layers().size());
        LayerGroupInfo securedOpaqueGroup = sc.getLayerGroupByName(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME);
        Assert.assertEquals(2, securedOpaqueGroup.layers().size());
    }

    @Test
    public void testNestedOpaqueDenyNestedGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, statesLayer, roadsLayer);
        LayerGroupInfo named = buildLayerGroup(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME, NAMED, null, forestsLayer, opaque);
        layerGroups = Arrays.asList(named, opaque);
        populateCatalog();
        // setup security, disallow nested group
        buildManager(new String[]{ (SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME) + ".r=MILITARY" });
        // try the ro user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // direct access forests allowed but not states and roads
        Assert.assertNotNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // and via group access we cannot reach the nested one either
        LayerGroupInfo securedNamedGroup = sc.getLayerGroupByName(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME);
        Assert.assertEquals(1, securedNamedGroup.layers().size());
        Assert.assertEquals(forestsLayer.getName(), securedNamedGroup.getLayers().get(0).getName());
        // nested nor accessible directly either
        Assert.assertNull(sc.getLayerGroupByName(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME));
    }

    @Test
    public void testNestedOpaqueDenyContainerGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, statesLayer, roadsLayer);
        LayerGroupInfo named = buildLayerGroup(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME, NAMED, null, forestsLayer, opaque);
        layerGroups = Arrays.asList(named, opaque);
        populateCatalog();
        // setup security, disallow nested group
        buildManager(new String[]{ (SecureCatalogImplGroupsTest.NAMED_GROUP_NAME) + ".r=MILITARY" });
        // try the ro user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // ... direct access not available for any
        Assert.assertNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // ... and via group access we cannot reach the nested one either
        Assert.assertNull(sc.getLayerGroupByName(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME));
        // ... nested nor accessible directly either
        Assert.assertNull(sc.getLayerGroupByName(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME));
        // switch to the mil user
        SecurityContextHolder.getContext().setAuthentication(milUser);
        // ... direct access available for forests
        Assert.assertNotNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // ... however as mil via group access we can see all layers
        LayerGroupInfo securedNamedGroup = sc.getLayerGroupByName(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME);
        Assert.assertEquals(3, securedNamedGroup.layers().size());
        LayerGroupInfo securedOpaqueGroup = sc.getLayerGroupByName(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME);
        Assert.assertEquals(2, securedOpaqueGroup.layers().size());
    }

    /**
     * Same as {@link #testWmsStandaloneOpaqueGroup()} but with a nested group as the testing target
     */
    @Test
    public void testWmsNestedInStandaloneOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup group
        LayerGroupInfo nested = buildLayerGroup(SecureCatalogImplGroupsTest.NESTED_GROUP_NAME, NAMED, null, statesLayer);
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, nested, roadsLayer);
        layerGroups = Arrays.asList(nested, opaque);
        populateCatalog();
        // setup security
        buildManager(SecureCatalogImplGroupsTest.DEFAULT_RULES);
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // direct access to nested group not allowed, nor to its layers
        Assert.assertNull(sc.getLayerGroupByName(SecureCatalogImplGroupsTest.NESTED_GROUP_NAME));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // however we can access the group as part of the opaque one
        LayerGroupInfo securedGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedGroup);
        Assert.assertEquals(2, securedGroup.getLayers().size());
        Assert.assertThat(securedGroup.getLayers(), Matchers.contains(nested, roadsLayer));
        Assert.assertThat(securedGroup.layers(), Matchers.contains(statesLayer, roadsLayer));
    }

    /**
     * Same as {@link #testWmsNamedOpaqueGroup()} but with a nested group as the testing target
     */
    @Test
    public void testWmsNestedInNamedOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup group
        LayerGroupInfo nested = buildLayerGroup(SecureCatalogImplGroupsTest.NESTED_GROUP_NAME, NAMED, null, statesLayer);
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, nested, roadsLayer);
        LayerGroupInfo named = buildLayerGroup(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME, NAMED, null, nested, roadsLayer);
        layerGroups = Arrays.asList(nested, named, opaque);
        populateCatalog();
        // setup security
        buildManager(SecureCatalogImplGroupsTest.DEFAULT_RULES);
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // direct access to layers and nested group allowed because of the named group
        Assert.assertNotNull(sc.getLayerGroupByName(nested.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // we can access the group and the layers through it
        LayerGroupInfo securedOpaqueGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedOpaqueGroup);
        Assert.assertEquals(2, securedOpaqueGroup.getLayers().size());
        Assert.assertThat(securedOpaqueGroup.getLayers(), Matchers.contains(nested, roadsLayer));
        // and same for named
        LayerGroupInfo securedNamedGroup = sc.getLayerGroupByName(named.prefixedName());
        Assert.assertNotNull(securedNamedGroup);
        Assert.assertEquals(2, securedNamedGroup.getLayers().size());
        Assert.assertThat(securedNamedGroup.getLayers(), Matchers.contains(nested, roadsLayer));
    }

    /**
     * Same as {@link #testWmsSingleAndOpaqueGroup()} but with a nested group as the testing target
     */
    @Test
    public void testWmsNestedInSingleAndOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo nested = buildLayerGroup(SecureCatalogImplGroupsTest.NESTED_GROUP_NAME, NAMED, null, statesLayer);
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, nested, roadsLayer);
        LayerGroupInfo single = buildLayerGroup(SecureCatalogImplGroupsTest.SINGLE_GROUP_NAME, SINGLE, null, nested, roadsLayer);
        layerGroups = Arrays.asList(nested, single, opaque);
        populateCatalog();
        // setup security
        buildManager(SecureCatalogImplGroupsTest.DEFAULT_RULES);
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // direct access to layers and nested group not allowed because the only container is in
        // opaque mode
        Assert.assertNull(sc.getLayerGroupByName(nested.prefixedName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // we can access the group and the layers through it
        LayerGroupInfo opaqueSecuredGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(opaqueSecuredGroup);
        Assert.assertEquals(2, opaqueSecuredGroup.getLayers().size());
        Assert.assertThat(opaqueSecuredGroup.getLayers(), Matchers.contains(nested, roadsLayer));
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(single.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.getLayers().size());
        Assert.assertThat(securedSingleGroup.getLayers(), Matchers.contains(nested, roadsLayer));
    }

    /**
     * Same as {@link #testWmsMilitaryNamedAndPublicOpaqueGroup()} but with a nested group as the
     * testing target
     */
    @Test
    public void testWmsNestedInMilitaryNamedAndPublicOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo nested = buildLayerGroup(SecureCatalogImplGroupsTest.NESTED_GROUP_NAME, NAMED, null, statesLayer);
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, nested, roadsLayer);
        LayerGroupInfo named = buildLayerGroup(SecureCatalogImplGroupsTest.NAMED_GROUP_NAME, NAMED, null, nested, roadsLayer);
        layerGroups = Arrays.asList(nested, opaque, named);
        populateCatalog();
        // setup security
        buildManager(new String[]{ "named.r=MILITARY" });
        // try the ro user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // ... direct access to layers not allowed because the named group is not visible
        Assert.assertNull(sc.getLayerGroupByName(nested.prefixedName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(roadsLayer.prefixedName()));
        // ... but we can access the opaque group and the layers through it
        LayerGroupInfo securedGroup = sc.getLayerGroupByName(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME);
        Assert.assertNotNull(securedGroup);
        Assert.assertEquals(2, securedGroup.getLayers().size());
        Assert.assertThat(securedGroup.getLayers(), Matchers.contains(nested, roadsLayer));
        // now try with the military one
        SecurityContextHolder.getContext().setAuthentication(milUser);
        // ... direct access to layers allowed because the named group is visible to the mil user
        Assert.assertNotNull(sc.getLayerGroupByName(nested.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
    }

    /**
     * Same as {@link #testWmsPublicSingleAndSecuredOpaqueGroup} but with a nested group as the
     * testing target
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWmsNestedInPublicSingleAndSecuredOpaqueGroup() throws Exception {
        setupRequestThreadLocal("WMS");
        // setup groups
        LayerGroupInfo nested = buildLayerGroup(SecureCatalogImplGroupsTest.NESTED_GROUP_NAME, NAMED, null, statesLayer);
        LayerGroupInfo opaque = buildLayerGroup(SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME, OPAQUE_CONTAINER, null, forestsLayer, nested);
        LayerGroupInfo single = buildLayerGroup(SecureCatalogImplGroupsTest.SINGLE_GROUP_NAME, SINGLE, null, roadsLayer, nested);
        layerGroups = Arrays.asList(nested, single, opaque);
        populateCatalog();
        // setup security, single allowed to everybody, opaque to military only
        buildManager(((SecureCatalogImplGroupsTest.SINGLE_GROUP_NAME) + ".r=*"), ((SecureCatalogImplGroupsTest.OPAQUE_GROUP_NAME) + ".r=MILITARY"));
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // try the ro user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        // ... the only directly available layer should be roads, contained in single
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerGroupByName(nested.prefixedName()));
        // ... and even via groups it's the same
        Assert.assertNull(sc.getLayerGroupByName(opaque.prefixedName()));
        LayerGroupInfo securedSingleGroup = sc.getLayerGroupByName(single.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(1, securedSingleGroup.getLayers().size());
        // however switching to mil user
        SecurityContextHolder.getContext().setAuthentication(milUser);
        // ... same as above for direct access
        Assert.assertNotNull(sc.getLayerByName(roadsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(forestsLayer.prefixedName()));
        Assert.assertNull(sc.getLayerByName(statesLayer.prefixedName()));
        Assert.assertNull(sc.getLayerGroupByName(nested.prefixedName()));
        // but the opaque group is now visible along with its layers
        securedSingleGroup = sc.getLayerGroupByName(single.prefixedName());
        Assert.assertNotNull(securedSingleGroup);
        Assert.assertEquals(2, securedSingleGroup.getLayers().size());
        Assert.assertThat(securedSingleGroup.getLayers(), Matchers.contains(roadsLayer, nested));
        LayerGroupInfo nestedInSingle = ((LayerGroupInfo) (securedSingleGroup.getLayers().get(1)));
        Assert.assertEquals(1, nestedInSingle.getLayers().size());
        Assert.assertThat(nestedInSingle.getLayers(), Matchers.contains(statesLayer));
        LayerGroupInfo securedOpaqueGroup = sc.getLayerGroupByName(opaque.prefixedName());
        Assert.assertNotNull(securedOpaqueGroup);
        Assert.assertEquals(2, securedOpaqueGroup.getLayers().size());
        Assert.assertThat(securedOpaqueGroup.getLayers(), Matchers.contains(forestsLayer, nested));
        LayerGroupInfo nestedInOpaque = ((LayerGroupInfo) (securedSingleGroup.getLayers().get(1)));
        Assert.assertEquals(1, nestedInOpaque.getLayers().size());
        Assert.assertThat(nestedInOpaque.getLayers(), Matchers.contains(statesLayer));
    }
}

