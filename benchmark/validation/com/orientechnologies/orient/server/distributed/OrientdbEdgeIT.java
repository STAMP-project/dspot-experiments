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
package com.orientechnologies.orient.server.distributed;


import ODirection.BOTH;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.server.OServer;
import org.junit.Assert;
import org.junit.Test;


public class OrientdbEdgeIT {
    private static OServer server;

    static {
        System.setProperty("ORIENTDB_ROOT_PASSWORD", "root");
    }

    public OrientdbEdgeIT() {
    }

    @Test
    public void testEdges() throws Exception {
        ODatabasePool pool = OrientdbEdgeIT.getGraphFactory();
        ODatabaseDocument g = pool.acquire();
        try {
            try {
                g.createEdgeClass("some-label");
            } catch (OSchemaException ex) {
                if (!(ex.getMessage().contains("exists")))
                    throw ex;

                g.command(new OCommandSQL("delete edge `some-label`")).execute();
            }
            try {
                g.createVertexClass("some-v-label");
            } catch (OSchemaException ex) {
                if (!(ex.getMessage().contains("exists")))
                    throw ex;

                g.command(new OCommandSQL("delete vertex `some-v-label`")).execute();
            }
        } finally {
            g.close();
        }
        ODatabaseDocument t = pool.acquire();
        t.begin();
        try {
            OVertex v1 = t.newVertex("some-v-label");
            v1.save();
            OVertex v2 = t.newVertex("some-v-label");
            v1.setProperty("_id", "v1");
            v2.setProperty("_id", "v2");
            v2.save();
            OEdge edge = v1.addEdge(v2, "some-label");
            edge.setProperty("some", "thing");
            edge.save();
            t.commit();
            t.close();
            t = pool.acquire();
            t.begin();
            Assert.assertEquals(2, t.getClass("some-v-label").count());
            Assert.assertEquals(1, t.getClass("E").count());
            Assert.assertNotNull(getVertices(t, "_id", "v1").iterator().next());
            Assert.assertNotNull(getVertices(t, "_id", "v2").iterator().next());
            t.commit();
            t.close();
            t = pool.acquire();
            t.begin();
            // works
            Assert.assertEquals(1, count(getVertices(t, "_id", "v1").iterator().next().getEdges(BOTH, "some-label")));
            // NoSuchElementException
            // assertNotNull(t.getVertices("_id", "v1").iterator().next()..labels("some-label").edges().iterator().next());//TODO what...?
            t.commit();
        } finally {
            t.close();
        }
        pool.close();
    }
}

