/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.graph.sql;


import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.graph.gremlin.OGremlinHelper;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SQLGraphFunctionsTest {
    private static OrientGraph graph;

    public SQLGraphFunctionsTest() {
    }

    @Test
    public void checkDijkstra() {
        String subquery = "select $current, $target, Dijkstra($current, $target , \'weight\') as path from V let $target = ( select from V where name = \'C\' ) where 1 > 0";
        Iterable<OrientVertex> result = SQLGraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<OrientVertex>(subquery)).execute();
        Assert.assertTrue(result.iterator().hasNext());
        for (OrientVertex d : result) {
            OrientVertex $current = d.getProperty("$current");
            Object name = $current.getProperty("name");
            Iterable<OrientVertex> $target = ((Iterable<OrientVertex>) (d.getProperty("$target")));
            Object name1 = $target.iterator().next().getProperty("name");
            System.out.println(((((("Shortest path from " + name) + " and ") + name1) + " is: ") + (d.getProperty("path"))));
        }
    }

    @Test
    public void checkMinusInString() {
        Iterable<OrientVertex> result = SQLGraphFunctionsTest.graph.command(new OCommandSQL("select expand( out()[name='D-D'] ) from V")).execute();
        Assert.assertTrue(result.iterator().hasNext());
    }

    @Test
    public void testGremlinTraversal() {
        OGremlinHelper.global().create();
        SQLGraphFunctionsTest.graph.setAutoStartTx(false);
        SQLGraphFunctionsTest.graph.commit();
        SQLGraphFunctionsTest.graph.command(new OCommandSQL("create class tc1 extends V clusters 1")).execute();
        SQLGraphFunctionsTest.graph.command(new OCommandSQL("create class edge1 extends E clusters 1")).execute();
        SQLGraphFunctionsTest.graph.setAutoStartTx(true);
        OrientVertex v1 = SQLGraphFunctionsTest.graph.command(new OCommandSQL("create vertex tc1 SET id='1', name='name1'")).execute();
        OrientVertex v2 = SQLGraphFunctionsTest.graph.command(new OCommandSQL("create vertex tc1 SET id='2', name='name2'")).execute();
        SQLGraphFunctionsTest.graph.commit();
        int tc1Id = SQLGraphFunctionsTest.graph.getRawGraph().getClusterIdByName("tc1");
        int edge1Id = SQLGraphFunctionsTest.graph.getRawGraph().getClusterIdByName("edge1");
        Iterable<OrientEdge> e = SQLGraphFunctionsTest.graph.command(new OCommandSQL((((("create edge edge1 from #" + tc1Id) + ":0 to #") + tc1Id) + ":1 set f='fieldValue';"))).execute();
        SQLGraphFunctionsTest.graph.commit();
        List<ODocument> result = SQLGraphFunctionsTest.graph.getRawGraph().query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select gremlin('current.outE') from tc1"));
        Assert.assertEquals(2, result.size());
        ODocument firstItem = result.get(0);
        List<OrientEdge> firstResult = firstItem.field("gremlin");
        Assert.assertEquals(1, firstResult.size());
        OrientEdge edge = firstResult.get(0);
        Assert.assertEquals(new ORecordId(edge1Id, 0), ((ORID) (edge.getId())));
        ODocument secondItem = result.get(1);
        List<OrientEdge> secondResult = secondItem.field("gremlin");
        Assert.assertTrue(secondResult.isEmpty());
        OGremlinHelper.global().destroy();
    }
}

