/**
 * * Copyright 2010-2014 OrientDB LTD (info(-at-)orientdb.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package com.orientechnologies.orient.graph.blueprints;


import Direction.IN;
import Direction.OUT;
import OClass.INDEX_TYPE.UNIQUE;
import OType.EMBEDDEDLIST;
import OType.EMBEDDEDSET;
import OType.STRING;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndexException;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.tinkerpop.blueprints.impls.orient.Vertex;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class GraphTest {
    public static final String URL = "memory:" + (GraphTest.class.getSimpleName());

    @Test
    public void indexes() {
        OrientGraph g = new OrientGraph(GraphTest.URL, "admin", "admin");
        try {
            if ((g.getVertexType("VC1")) == null) {
                g.createVertexType("VC1");
            }
        } finally {
            g.shutdown();
        }
        g = new OrientGraph(GraphTest.URL, "admin", "admin");
        try {
            // System.out.println(g.getIndexedKeys(Vertex.class,true)); this will print VC1.p1
            if ((g.getIndex("VC1.p1", Vertex.class)) == null) {
                // this will return null. I do not know why
                g.createKeyIndex("p1", Vertex.class, new Parameter<String, String>("class", "VC1"), new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, OType>("keytype", OType.STRING));
            }
        } catch (OIndexException e) {
            // ignore because the index may exist
        } finally {
            g.shutdown();
        }
        g = new OrientGraph(GraphTest.URL, "admin", "admin");
        String val1 = (System.currentTimeMillis()) + "";
        try {
            Vertex v = g.addVertex("class:VC1");
            v.setProperty("p1", val1);
        } finally {
            g.shutdown();
        }
        g = new OrientGraph(GraphTest.URL, "admin", "admin");
        try {
            Vertex v = g.addVertex("class:VC1");
            v.setProperty("p1", val1);
        } finally {
            try {
                g.shutdown();
                Assert.fail("must throw duplicate key here!");
            } catch (ORecordDuplicatedException e) {
                // ok
            }
        }
    }

    @Test
    public void testIndexCollate() {
        OrientGraph g = new OrientGraph(GraphTest.URL, "admin", "admin");
        OrientVertexType vCollate = g.createVertexType("VCollate");
        vCollate.createProperty("name", STRING);
        g.createKeyIndex("name", Vertex.class, new Parameter<String, String>("class", "VCollate"), new Parameter<String, String>("type", "UNIQUE"), new Parameter<String, OType>("keytype", OType.STRING), new Parameter<String, String>("collate", "ci"));
        OrientVertex vertex = g.addVertex("class:VCollate", new Object[]{ "name", "Enrico" });
        g.commit();
        Iterable<Vertex> enrico = g.getVertices("VCollate.name", "ENRICO");
        Assert.assertEquals(true, enrico.iterator().hasNext());
    }

    @Test
    public void testGetCompositeKeyBySingleValue() {
        OrientGraph g = new OrientGraph(GraphTest.URL, "admin", "admin");
        OrientVertexType vComposite = g.createVertexType("VComposite");
        vComposite.createProperty("login", STRING);
        vComposite.createProperty("permissions", EMBEDDEDSET, STRING);
        vComposite.createIndex("VComposite_Login_Perm", UNIQUE, "login", "permissions");
        String loginOne = "admin";
        Set<String> permissionsOne = new HashSet<String>();
        permissionsOne.add("perm1");
        permissionsOne.add("perm2");
        String loginTwo = "user";
        Set<String> permissionsTwo = new HashSet<String>();
        permissionsTwo.add("perm3");
        permissionsTwo.add("perm4");
        g.addVertex("class:VComposite", "login", loginOne, "permissions", permissionsOne);
        g.commit();
        g.addVertex("class:VComposite", "login", loginTwo, "permissions", permissionsTwo);
        g.commit();
        Iterable<Vertex> vertices = g.getVertices("VComposite", new String[]{ "login" }, new String[]{ "admin" });
        Iterator<Vertex> verticesIterator = vertices.iterator();
        Assert.assertTrue(verticesIterator.hasNext());
        Vertex vertex = verticesIterator.next();
        Assert.assertEquals(vertex.getProperty("login"), "admin");
        Assert.assertEquals(vertex.getProperty("permissions"), permissionsOne);
    }

    @Test
    public void testEmbeddedListAsVertexProperty() {
        OrientGraph g = new OrientGraph(GraphTest.URL, "admin", "admin");
        try {
            OrientVertexType vertexType = g.createVertexType("EmbeddedClass");
            vertexType.createProperty("embeddedList", EMBEDDEDLIST);
            OrientVertex vertex = g.addVertex("class:EmbeddedClass");
            List<ODocument> embeddedList = new ArrayList<ODocument>();
            ODocument docOne = new ODocument();
            docOne.field("prop", "docOne");
            ODocument docTwo = new ODocument();
            docTwo.field("prop", "docTwo");
            embeddedList.add(docOne);
            embeddedList.add(docTwo);
            vertex.setProperty("embeddedList", embeddedList);
            final Object id = vertex.getId();
            g.shutdown();
            g = new OrientGraph(GraphTest.URL, "admin", "admin");
            vertex = g.getVertex(id);
            embeddedList = vertex.getProperty("embeddedList");
            docOne = embeddedList.get(0);
            Assert.assertEquals(docOne.field("prop"), "docOne");
            docTwo = embeddedList.get(1);
            Assert.assertEquals(docTwo.field("prop"), "docTwo");
        } finally {
            g.shutdown();
        }
    }

    @Test
    public void testGetEdgesUpdate() {
        OrientGraph g = new OrientGraph(GraphTest.URL, "admin", "admin");
        try {
            g.createVertexType("GetEdgesUpdate");
            g.createEdgeType("getEdgesUpdateEdge");
            OrientVertex vertexOne = g.addVertex("class:GetEdgesUpdate");
            OrientVertex vertexTwo = g.addVertex("class:GetEdgesUpdate");
            OrientVertex vertexThree = g.addVertex("class:GetEdgesUpdate");
            OrientVertex vertexFour = g.addVertex("class:GetEdgesUpdate");
            vertexOne.addEdge("getEdgesUpdateEdge", vertexTwo);
            vertexOne.addEdge("getEdgesUpdateEdge", vertexThree);
            vertexOne.addEdge("getEdgesUpdateEdge", vertexFour);
            g.commit();
            Iterable<Edge> iterable = vertexOne.getEdges(OUT, "getEdgesUpdateEdge");
            Iterator<Edge> iterator = iterable.iterator();
            int counter = 0;
            while (iterator.hasNext()) {
                iterator.next();
                counter++;
            } 
            Assert.assertEquals(3, counter);
            iterable = vertexOne.getEdges(OUT, "getEdgesUpdateEdge");
            iterator = iterable.iterator();
            Edge deleteEdge = ((Edge) (iterator.next()));
            Vertex deleteVertex = deleteEdge.getVertex(IN);
            deleteVertex.remove();
            g.commit();
            iterable = vertexOne.getEdges(OUT, "getEdgesUpdateEdge");
            iterator = iterable.iterator();
            counter = 0;
            while (iterator.hasNext()) {
                iterator.next();
                counter++;
            } 
            Assert.assertEquals(2, counter);
        } finally {
            g.shutdown();
        }
    }

    @Test
    public void testBrokenVertex1() {
        OrientGraph g = new OrientGraph(GraphTest.URL, "admin", "admin");
        try {
            g.createVertexType("BrokenVertex1V");
            g.createEdgeType("BrokenVertex1E");
            OrientVertex vertexOne = g.addVertex("class:BrokenVertex1V");
            OrientVertex vertexTwo = g.addVertex("class:BrokenVertex1V");
            vertexOne.addEdge("BrokenVertex1E", vertexTwo);
            g.commit();
            g.command(new OCommandSQL((("delete from " + (vertexTwo.getRecord().getIdentity())) + " unsafe"))).execute();
            // g.command(new OCommandSQL("update BrokenVertex1E set out = null")).execute();
            g.shutdown();
            g = new OrientGraph(GraphTest.URL, "admin", "admin");
            Iterable<Vertex> iterable = g.command(new OCommandSQL("select from BrokenVertex1V")).execute();
            Iterator<Vertex> iterator = iterable.iterator();
            int counter = 0;
            while (iterator.hasNext()) {
                OrientVertex v = ((OrientVertex) (iterator.next()));
                for (Vertex v1 : v.getVertices(OUT, "BrokenVertex1E")) {
                    Assert.assertNotNull(getRecord());
                }
            } 
        } finally {
            g.shutdown();
        }
    }

    @Test
    public void shouldAddVertexAndEdgeInTheSameCluster() {
        OrientGraphFactory orientGraphFactory = new OrientGraphFactory("memory:shouldAddVertexAndEdgeInTheSameCluster");
        final OrientGraphNoTx graphDbNoTx = orientGraphFactory.getNoTx();
        try {
            OrientVertexType deviceVertex = graphDbNoTx.createVertexType("Device");
            OrientEdgeType edgeType = graphDbNoTx.createEdgeType("Link");
            edgeType.addCluster("Links");
            OrientVertex dev1 = graphDbNoTx.addVertex("Device");
            OrientVertex dev2 = graphDbNoTx.addVertex("Device");
            final OrientEdge e = graphDbNoTx.addEdge("class:Link,cluster:Links", dev1, dev2, null);
            Assert.assertEquals(e.getIdentity().getClusterId(), graphDbNoTx.getRawGraph().getClusterIdByName("Links"));
        } finally {
            graphDbNoTx.shutdown();
            orientGraphFactory.close();
        }
    }

    @Test
    public void testCustomPredicate() {
        OrientGraphFactory orientGraphFactory = new OrientGraphFactory("memory:testCustomPredicate");
        final OrientGraphNoTx g = orientGraphFactory.getNoTx();
        try {
            g.addVertex(null).setProperty("test", true);
            g.addVertex(null).setProperty("test", false);
            g.addVertex(null).setProperty("no", true);
            g.commit();
            GraphQuery query = g.query();
            query.has("test", new Predicate() {
                @Override
                public boolean evaluate(Object first, Object second) {
                    return (first != null) && (first.equals(second));
                }
            }, true);
            Iterable<Vertex> vertices = query.vertices();
            final Iterator<Vertex> it = vertices.iterator();
            Assert.assertTrue(it.hasNext());
            Assert.assertTrue(((Boolean) (it.next().getProperty("test"))));
            Assert.assertFalse(it.hasNext());
        } finally {
            g.shutdown();
            orientGraphFactory.close();
        }
    }

    @Test
    public void testKebabCaseQuery() {
        OrientGraphFactory orientGraphFactory = new OrientGraphFactory("memory:testKebabCase");
        final OrientGraphNoTx g = orientGraphFactory.getNoTx();
        try {
            g.addVertex(null).setProperty("test-one", true);
            g.addVertex(null).setProperty("test-one", false);
            g.commit();
            GraphQuery query = g.query();
            query.has("test-one", true);
            Iterable<Vertex> vertices = query.vertices();
            final Iterator<Vertex> it = vertices.iterator();
            Assert.assertTrue(it.hasNext());
            Assert.assertTrue(((Boolean) (it.next().getProperty("test-one"))));
            Assert.assertFalse(it.hasNext());
        } finally {
            g.shutdown();
            orientGraphFactory.close();
        }
    }

    @Test
    public void testIndexCreateDropCreate() {
        OrientGraph g = new OrientGraph(GraphTest.URL, "admin", "admin");
        try {
            g.createIndex("IndexCreateDropCreate", Vertex.class);
            g.dropIndex("IndexCreateDropCreate");
            g.createIndex("IndexCreateDropCreate", Vertex.class);
        } finally {
            g.shutdown();
        }
    }

    @Test
    public void testCompositeKey() {
        OrientGraphNoTx graph = new OrientGraphNoTx("memory:testComposite");
        try {
            graph.createVertexType("Account");
            graph.command(new OCommandSQL("create property account.description STRING")).execute();
            graph.command(new OCommandSQL("create property account.namespace STRING")).execute();
            graph.command(new OCommandSQL("create property account.name STRING")).execute();
            graph.command(new OCommandSQL("create index account.composite on account (name, namespace) unique")).execute();
            graph.addVertex("class:account", new Object[]{ "name", "foo", "namespace", "bar", "description", "foobar" });
            graph.addVertex("class:account", new Object[]{ "name", "foo", "namespace", "baz", "description", "foobaz" });
            Iterable<Vertex> vertices = graph.command(new OCommandSQL("select from index:account.composite where key = [ 'foo', 'baz' ]")).execute();
            List<Vertex> list = new ArrayList<>();
            vertices.forEach(list::add);
            Assert.assertEquals(1, list.size());
            vertices = graph.getVertices("account.composite", new Object[]{ "foo", "baz" });
            list = new ArrayList();
            vertices.forEach(list::add);
            Assert.assertEquals(1, list.size());
            vertices = graph.getVertices("account.composite", new OCompositeKey("foo", "baz"));
            list = new ArrayList();
            vertices.forEach(list::add);
            Assert.assertEquals(1, list.size());
            graph.getVertices("account.composite", new OCompositeKey("foo", "baz"));
        } finally {
            graph.drop();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompositeExceptionKey() {
        OrientGraphNoTx graph = new OrientGraphNoTx("memory:testComposite");
        try {
            graph.createVertexType("Account");
            graph.command(new OCommandSQL("create property account.description STRING")).execute();
            graph.command(new OCommandSQL("create property account.namespace STRING")).execute();
            graph.command(new OCommandSQL("create property account.name STRING")).execute();
            graph.command(new OCommandSQL("create index account.composite on account (name, namespace) unique")).execute();
            graph.addVertex("class:account", new Object[]{ "name", "foo", "namespace", "bar", "description", "foobar" });
            graph.addVertex("class:account", new Object[]{ "name", "foo", "namespace", "baz", "description", "foobaz" });
            Iterable<Vertex> vertices = graph.command(new OCommandSQL("select from index:account.composite where key = [ 'foo', 'baz' ]")).execute();
            List<Vertex> list = new ArrayList<>();
            vertices.forEach(list::add);
            Assert.assertEquals(1, list.size());
            graph.getVertices("account.composite", new Object[]{ "foo", "baz", "bar" });
        } finally {
            graph.drop();
        }
    }
}

