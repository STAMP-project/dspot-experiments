package de.westnordost.streetcomplete.data.osm.download;


import Element.Type;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.OsmLatLon;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.RelationMember;
import de.westnordost.osmapi.map.data.Way;
import de.westnordost.streetcomplete.data.osm.ElementGeometry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ElementCreatorTestGeometry implements WayGeometrySource {
    private static final LatLon P0 = new OsmLatLon(0.0, 2.0);

    private static final LatLon P1 = new OsmLatLon(0.0, 0.0);

    private static final LatLon P2 = new OsmLatLon(2.0, 0.0);

    private static final LatLon P3 = new OsmLatLon(2.0, 2.0);

    private static final Node N0 = new de.westnordost.osmapi.map.data.OsmNode(0L, 0, ElementCreatorTestGeometry.P0, null);

    private static final Node N1 = new de.westnordost.osmapi.map.data.OsmNode(1L, 0, ElementCreatorTestGeometry.P1, null);

    private static final Node N2 = new de.westnordost.osmapi.map.data.OsmNode(2L, 0, ElementCreatorTestGeometry.P2, null);

    private static final Node N3 = new de.westnordost.osmapi.map.data.OsmNode(3L, 0, ElementCreatorTestGeometry.P3, null);

    private static final Map<String, String> wayArea = new HashMap<>();

    private static final Map<String, String> relationArea = new HashMap<>();

    static {
        ElementCreatorTestGeometry.wayArea.put("area", "yes");
        ElementCreatorTestGeometry.relationArea.put("type", "multipolygon");
    }

    private static final Way W0 = new OsmWay(0L, 0, Arrays.asList(0L, 1L), null);

    private static final Way W1 = new OsmWay(1L, 0, Arrays.asList(1L, 2L, 0L), null);

    private static final Way W2 = new OsmWay(2L, 0, Arrays.asList(0L, 1L, 2L, 0L), ElementCreatorTestGeometry.wayArea);

    private static final Way W3 = new OsmWay(3L, 0, Arrays.asList(3L, 2L), null);

    private static final Way W4 = new OsmWay(4L, 0, Arrays.asList(0L, 1L, 1L, 2L), null);

    private static final Way W5 = new OsmWay(5L, 0, Collections.emptyList(), null);

    private static final RelationMember RM0 = new de.westnordost.osmapi.map.data.OsmRelationMember(0L, "outer", Type.WAY);

    private static final RelationMember RM1 = new de.westnordost.osmapi.map.data.OsmRelationMember(1L, "outer", Type.WAY);

    private static final RelationMember RM2 = new de.westnordost.osmapi.map.data.OsmRelationMember(2L, "inner", Type.WAY);

    private static final RelationMember RM3 = new de.westnordost.osmapi.map.data.OsmRelationMember(3L, "", Type.WAY);

    private static final Relation R0 = new de.westnordost.osmapi.map.data.OsmRelation(0L, 0, Arrays.asList(ElementCreatorTestGeometry.RM0, ElementCreatorTestGeometry.RM1, ElementCreatorTestGeometry.RM3), ElementCreatorTestGeometry.relationArea);

    private static final Relation R1 = new de.westnordost.osmapi.map.data.OsmRelation(1L, 0, Arrays.asList(ElementCreatorTestGeometry.RM0, ElementCreatorTestGeometry.RM1, ElementCreatorTestGeometry.RM2, ElementCreatorTestGeometry.RM3), null);

    private static final List<Node> nodes = Arrays.asList(ElementCreatorTestGeometry.N0, ElementCreatorTestGeometry.N1, ElementCreatorTestGeometry.N2, ElementCreatorTestGeometry.N3);

    private static final List<Way> ways = Arrays.asList(ElementCreatorTestGeometry.W0, ElementCreatorTestGeometry.W1, ElementCreatorTestGeometry.W2, ElementCreatorTestGeometry.W3, ElementCreatorTestGeometry.W4, ElementCreatorTestGeometry.W5);

    @Test
    public void createForNode() {
        ElementGeometry geom = createCreator().create(ElementCreatorTestGeometry.N0);
        Assert.assertEquals(ElementCreatorTestGeometry.P0, geom.center);
    }

    @Test
    public void createForEmptyWay() {
        ElementGeometry geom = createCreator().create(ElementCreatorTestGeometry.W5);
        Assert.assertNull(geom);
    }

    @Test
    public void createForWayWithDuplicateNodes() {
        ElementGeometry geom = createCreator().create(ElementCreatorTestGeometry.W4);
        Assert.assertNotNull(geom.polylines);
        Assert.assertNotNull(geom.center);
    }

    @Test
    public void createForSimpleAreaWay() {
        ElementGeometry geom = createCreator().create(ElementCreatorTestGeometry.W2);
        Assert.assertNotNull(geom.polygons);
        Assert.assertEquals(1, geom.polygons.size());
        List<LatLon> polygon = geom.polygons.get(0);
        for (int i = 0; i < (ElementCreatorTestGeometry.W2.getNodeIds().size()); ++i) {
            LatLon shouldBe = ElementCreatorTestGeometry.nodes.get(ElementCreatorTestGeometry.W2.getNodeIds().get(i).intValue()).getPosition();
            Assert.assertEquals(shouldBe, polygon.get(i));
        }
    }

    @Test
    public void createForSimpleNonAreaWay() {
        ElementGeometry geom = createCreator().create(ElementCreatorTestGeometry.W0);
        Assert.assertNotNull(geom.polylines);
        Assert.assertEquals(1, geom.polylines.size());
        Assert.assertEquals(ElementCreatorTestGeometry.W0.getNodeIds().size(), geom.polylines.get(0).size());
        List<LatLon> polyline = geom.polylines.get(0);
        for (int i = 0; i < (ElementCreatorTestGeometry.W0.getNodeIds().size()); ++i) {
            LatLon shouldBe = ElementCreatorTestGeometry.nodes.get(ElementCreatorTestGeometry.W0.getNodeIds().get(i).intValue()).getPosition();
            Assert.assertEquals(shouldBe, polyline.get(i));
        }
    }

    @Test
    public void createForMultipolygonRelation() {
        ElementGeometry geom = createCreator().create(ElementCreatorTestGeometry.R0);
        Assert.assertNotNull(geom.polygons);
        Assert.assertEquals(1, geom.polygons.size());
    }

    @Test
    public void createForPolylineRelation() {
        ElementGeometry geom = createCreator().create(ElementCreatorTestGeometry.R1);
        Assert.assertNotNull(geom.polylines);
        Assert.assertEquals(3, geom.polylines.size());
    }
}

