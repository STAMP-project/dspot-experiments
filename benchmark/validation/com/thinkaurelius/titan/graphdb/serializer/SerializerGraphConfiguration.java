package com.thinkaurelius.titan.graphdb.serializer;


import Cardinality.LIST;
import com.google.common.collect.Iterators;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass1;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass2;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TEnum;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class SerializerGraphConfiguration {
    StandardTitanGraph graph;

    @Test
    public void testOnlyRegisteredSerialization() {
        TitanManagement mgmt = graph.openManagement();
        PropertyKey time = mgmt.makePropertyKey("time").dataType(Integer.class).make();
        PropertyKey any = mgmt.makePropertyKey("any").cardinality(LIST).dataType(Object.class).make();
        mgmt.buildIndex("byTime", Vertex.class).addKey(time).buildCompositeIndex();
        EdgeLabel knows = mgmt.makeEdgeLabel("knows").make();
        VertexLabel person = mgmt.makeVertexLabel("person").make();
        mgmt.commit();
        TitanTransaction tx = graph.newTransaction();
        TitanVertex v = tx.addVertex("person");
        v.property("time", 5);
        v.property("any", new Double(5.0));
        v.property("any", new TClass1(5, 1.5F));
        v.property("any", TEnum.THREE);
        tx.commit();
        tx = graph.newTransaction();
        v = tx.query().has("time", 5).vertices().iterator().next();
        Assert.assertEquals(5, ((int) (v.value("time"))));
        Assert.assertEquals(3, Iterators.size(v.properties("any")));
        tx.rollback();
        // Verify that non-registered objects aren't allowed
        for (Object o : new Object[]{ new TClass2("abc", 5) }) {
            tx = graph.newTransaction();
            v = tx.addVertex("person");
            try {
                v.property("any", o);// Should not be allowed

                tx.commit();
                Assert.fail();
            } catch (IllegalArgumentException e) {
            } finally {
                if (tx.isOpen())
                    tx.rollback();

            }
        }
    }
}

