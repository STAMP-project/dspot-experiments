/**
 * *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.graph.sql;


import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.graph.GraphNoTxAbstractTest;
import com.tinkerpop.blueprints.Vertex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class GraphCommandsTest extends GraphNoTxAbstractTest {
    @Test
    public void testEmptyParams() {
        String sql = "SELECT FROM V WHERE tags NOT IN :tags";
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("tags", new HashSet<String>());
        Iterable<Vertex> results = ((Iterable<Vertex>) (GraphNoTxAbstractTest.graph.command(new OSQLSynchQuery(sql)).execute(queryParams)));
        Assert.assertTrue(results.iterator().hasNext());
    }

    @Test
    public void testAddValueSQL() {
        GraphNoTxAbstractTest.graph.command(new OCommandSQL("update V add testprop = 'first' return after @this limit 1")).execute();
        Iterable<Vertex> results = ((Iterable<Vertex>) (GraphNoTxAbstractTest.graph.command(new OSQLSynchQuery("select from V where 'first' in testprop")).execute()));
        Assert.assertTrue(results.iterator().hasNext());
        GraphNoTxAbstractTest.graph.command(new OCommandSQL("update V add testprop = 'second' return after @this limit 1")).execute();
        results = ((Iterable<Vertex>) (GraphNoTxAbstractTest.graph.command(new OSQLSynchQuery("select from V where 'first' in testprop")).execute()));
        Assert.assertTrue(results.iterator().hasNext());
        results = ((Iterable<Vertex>) (GraphNoTxAbstractTest.graph.command(new OSQLSynchQuery("select from V where 'second' in testprop")).execute()));
        Assert.assertTrue(results.iterator().hasNext());
    }
}

