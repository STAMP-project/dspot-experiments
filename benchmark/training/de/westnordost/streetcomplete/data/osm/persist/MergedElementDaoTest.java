package de.westnordost.streetcomplete.data.osm.persist;


import Element.Type.NODE;
import Element.Type.RELATION;
import Element.Type.WAY;
import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import java.util.ArrayList;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MergedElementDaoTest {
    private NodeDao nodeDao;

    private WayDao wayDao;

    private RelationDao relationDao;

    private MergedElementDao dao;

    @Test
    public void putNode() {
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.getType()).thenReturn(NODE);
        dao.put(node);
        Mockito.verify(nodeDao).put(node);
    }

    @Test
    public void getNode() {
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.getId()).thenReturn(1L);
        dao.get(NODE, 1L);
        Mockito.verify(nodeDao).get(1L);
    }

    @Test
    public void deleteNode() {
        dao.delete(NODE, 1L);
        Mockito.verify(nodeDao).delete(1L);
    }

    @Test
    public void putWay() {
        Way way = Mockito.mock(Way.class);
        Mockito.when(way.getType()).thenReturn(WAY);
        dao.put(way);
        Mockito.verify(wayDao).put(way);
    }

    @Test
    public void getWay() {
        Way way = Mockito.mock(Way.class);
        Mockito.when(way.getId()).thenReturn(1L);
        dao.get(WAY, 1L);
        Mockito.verify(wayDao).get(1L);
    }

    @Test
    public void deleteWay() {
        dao.delete(WAY, 1L);
        Mockito.verify(wayDao).delete(1L);
    }

    @Test
    public void putRelation() {
        Relation relation = Mockito.mock(Relation.class);
        Mockito.when(relation.getType()).thenReturn(RELATION);
        dao.put(relation);
        Mockito.verify(relationDao).put(relation);
    }

    @Test
    public void getRelation() {
        Relation relation = Mockito.mock(Relation.class);
        Mockito.when(relation.getId()).thenReturn(1L);
        dao.get(RELATION, 1L);
        Mockito.verify(relationDao).get(1L);
    }

    @Test
    public void deleteRelation() {
        dao.delete(RELATION, 1L);
        Mockito.verify(relationDao).delete(1L);
    }

    @Test
    public void putAllRelations() {
        ArrayList<Element> elements = new ArrayList<>();
        elements.add(createARelation());
        dao.putAll(elements);
        Mockito.verify(relationDao).putAll(ArgumentMatchers.anyCollection());
    }

    @Test
    public void putAllWays() {
        ArrayList<Element> elements = new ArrayList<>();
        elements.add(createAWay());
        dao.putAll(elements);
        Mockito.verify(wayDao).putAll(ArgumentMatchers.anyCollection());
    }

    @Test
    public void putAllNodes() {
        ArrayList<Element> elements = new ArrayList<>();
        elements.add(createANode());
        dao.putAll(elements);
        Mockito.verify(nodeDao).putAll(ArgumentMatchers.anyCollection());
    }

    @Test
    public void putAllElements() {
        ArrayList<Element> elements = new ArrayList<>();
        elements.add(createANode());
        elements.add(createAWay());
        elements.add(createARelation());
        dao.putAll(elements);
        Mockito.verify(nodeDao).putAll(ArgumentMatchers.anyCollection());
        Mockito.verify(wayDao).putAll(ArgumentMatchers.anyCollection());
        Mockito.verify(relationDao).putAll(ArgumentMatchers.anyCollection());
    }

    @Test
    public void deleteUnreferenced() {
        dao.deleteUnreferenced();
        Mockito.verify(nodeDao).deleteUnreferenced();
        Mockito.verify(wayDao).deleteUnreferenced();
        Mockito.verify(relationDao).deleteUnreferenced();
    }
}

