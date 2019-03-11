/**
 * * Copyright 2010-2016 OrientDB LTD (info(-at-)orientdb.com)
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
package com.orientechnologies.orient.etl.transformer;


import ODirection.IN;
import ODirection.OUT;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.etl.OETLBaseTest;
import com.orientechnologies.orient.etl.loader.OETLLoader;
import java.util.Iterator;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests ETL Field Transformer.
 *
 * @author Luca Garulli (l.garulli--(at)--orientdb.com)
 */
public class OETLEdgeTransformerTest extends OETLBaseTest {
    @Test
    public void testNotLightweightEdge() {
        configure(((("{source: { content: { value: \'name,surname,friend\nJay,Miner,Luca\' } }, extractor : { csv: {} }," + (" transformers: [{vertex: {class:'V1'}}, {edge:{class:'Friend',joinFieldName:'friend',lookup:'V2.name'}}," + "], loader: { orientdb: { dbURL: 'memory:")) + (name.getMethodName())) + "', dbType:'graph', useLightweightEdges:false } } }"));
        OETLLoader loader = proc.getLoader();
        ODatabasePool pool = loader.getPool();
        ODatabaseDocument db = pool.acquire();
        createClasses(db);
        db.close();
        proc.execute();
        db = pool.acquire();
        Assert.assertEquals(1, db.countClass("V1"));
        Assert.assertEquals(1, db.countClass("V2"));
        Assert.assertEquals(1, db.countClass("Friend"));
        db.close();
    }

    @Test
    public void testLookupMultipleValues() {
        configure(((("{source: { content: { value: \'name,surname,friend\nJay,Miner,Luca\' } }, extractor : { csv: {} }," + (" transformers: [{vertex: {class:'V1'}}, {edge:{class:'Friend',joinFieldName:'friend',lookup:'V2.name'}}," + "], loader: { orientdb: { dbURL: 'memory:")) + (name.getMethodName())) + "', dbType:'graph', useLightweightEdges:false } } }"));
        OETLLoader loader = proc.getLoader();
        ODatabasePool pool = loader.getPool();
        ODatabaseDocument db = pool.acquire();
        createClasses(db);
        OVertex vertex = db.newVertex("v2");
        vertex.setProperty("name", "Luca");
        db.save(vertex);
        db.commit();
        db.close();
        proc.execute();
        db = pool.acquire();
        Assert.assertEquals(1, db.countClass("V1"));
        Assert.assertEquals(2, db.countClass("V2"));
        Assert.assertEquals(2, db.countClass("Friend"));
        db.close();
        pool.close();
    }

    @Test
    public void testEdgeWithProperties() {
        configure(((("{source: { content: { value: \'id,name,surname,friendSince,friendId,friendName,friendSurname\n0,Jay,Miner,1996,1,Luca,Garulli\' } }, extractor : { csv: {} }," + (((" transformers: [ {vertex: {class:'V1'}}, " + "{edge:{unresolvedLinkAction:'CREATE',class:'Friend',joinFieldName:'friendId',lookup:'V2.fid',targetVertexFields:{name:'${input.friendName}',surname:'${input.friendSurname}'},edgeFields:{since:'${input.friendSince}'}}},") + "{field:{fieldNames:['friendSince','friendId','friendName','friendSurname'],operation:'remove'}}") + "], loader: { orientdb: { dbURL: 'memory:")) + (name.getMethodName())) + "', dbType:'graph', useLightweightEdges:false } } }"));
        OETLLoader loader = proc.getLoader();
        ODatabasePool pool = loader.getPool();
        ODatabaseDocument db = pool.acquire();
        createClasses(db);
        db.close();
        proc.execute();
        db = pool.acquire();
        Assert.assertEquals(1, db.countClass("V1"));
        Assert.assertEquals(2, db.countClass("V2"));
        Assert.assertEquals(1, db.countClass("Friend"));
        OResultSet v = db.query("SELECT from V2");
        Assert.assertTrue(v.hasNext());
        Assert.assertNotNull(v.next());
        Assert.assertTrue(v.hasNext());
        final OVertex v1 = v.next().getVertex().get();
        Assert.assertNotNull(v1);
        final Set<String> v1Props = v1.getPropertyNames();
        Assert.assertEquals(v1.getProperty("name"), "Luca");
        Assert.assertEquals(v1.getProperty("surname"), "Garulli");
        Assert.assertEquals(v1.<Integer>getProperty("fid"), Integer.valueOf(1));
        final Iterator<OEdge> edge = v1.getEdges(IN).iterator();
        Assert.assertTrue(edge.hasNext());
        final OEdge e = edge.next();
        Assert.assertNotNull(e);
        final Set<String> eProps = e.getPropertyNames();
        Assert.assertEquals(e.<Integer>getProperty("since"), Integer.valueOf(1996));
        final OVertex v0 = e.getVertex(OUT);
        Assert.assertNotNull(v0);
        final Set<String> v0Props = v0.getPropertyNames();
        Assert.assertEquals(v0.getProperty("name"), "Jay");
        Assert.assertEquals(v0.getProperty("surname"), "Miner");
        Assert.assertEquals(v0.<Integer>getProperty("id"), Integer.valueOf(0));
        v.close();
        db.close();
    }

    @Test
    public void testErrorOnDuplicateVertex() {
        configure(((("{source: { content: { value: \'name,surname,friend\nJay,Miner,Luca\nJay,Miner,Luca\' } }, extractor : { csv: {} }," + (" transformers: [{merge: {joinFieldName:'name',lookup:'V1.name'}}, {vertex: {class:'V1'}}, {edge:{class:'Friend',joinFieldName:'friend',lookup:'V2.name'}}," + "], loader: { orientdb: { dbURL: 'memory:")) + (name.getMethodName())) + "', dbType:'graph', useLightweightEdges:false } } }"));
        OETLLoader loader = proc.getLoader();
        ODatabasePool pool = loader.getPool();
        ODatabaseDocument db = pool.acquire();
        createClasses(db);
        db.close();
        proc.execute();
        db = pool.acquire();
        Assert.assertEquals(1, db.countClass("V1"));
        db.close();
    }

    @Test
    public void testSkipDuplicateVertex() {
        configure(((("{source: { content: { value: \'name,surname,friend\nJay,Miner,Luca\nJay,Miner,Luca\' } }, extractor : { csv: {} }," + ((" transformers: [ {merge: {joinFieldName:'name',lookup:'V1.name'}}, " + "{vertex: {class:'V1'}}, {edge:{class:'Friend',skipDuplicates:true, joinFieldName:'friend',lookup:'V2.name'}},") + "], loader: { orientdb: { dbURL: 'memory:")) + (name.getMethodName())) + "', dbType:'graph', useLightweightEdges:false } } }"));
        OETLLoader loader = proc.getLoader();
        ODatabasePool pool = loader.getPool();
        ODatabaseDocument db = pool.acquire();
        createClasses(db);
        db.close();
        proc.execute();
        db = pool.acquire();
        Assert.assertEquals(1, db.countClass("V1"));
        Assert.assertEquals(1, db.countClass("V2"));
        Assert.assertEquals(1, db.countClass("Friend"));
    }

    @Test
    public void testVertexAndEdgesOnSeparatedFiles() {
        // IMPORT PERSON (VERTICES)
        configure(((("{source: { content: { value: \'id,name\n1,Luigi\n2,Luca\n3,Enrico\n4,Franco\n5,Gianni\' } }, extractor : { csv: {} }," + (" transformers: [ {merge: {joinFieldName:'id',lookup:'PersonMF.id'}}, {vertex: {class:'PersonMF'}}" + "], loader: { orientdb: { dbURL: 'plocal:./target/databases/")) + (name.getMethodName())) + "', dbType:'graph', classes: [{name:'PersonMF',extends:'V'}] } } }"));
        OETLLoader loader = proc.getLoader();
        ODatabasePool pool = loader.getPool();
        ODatabaseDocument db = pool.acquire();
        // createClasses(db);
        db.close();
        proc.execute();
        db = pool.acquire();
        Assert.assertEquals(5, db.countClass("PersonMF"));
        db.close();
        pool.close();
        proc.getLoader().close();
        // IMPORT FRIEND (EDGES)
        configure(((("{source: { content: { value: \'friend_from,friend_to,since\n" + (((((((((((("1,2,2005\n" + "1,3,2008\n") + "2,3,2008\n") + "1,4,2015\n") + "2,5,2008\n") + "3,5,2015\n") + "4,5,2015' } }, extractor : { csv: {} },") + " transformers: [") + "{merge: {joinFieldName:'friend_from',lookup:'PersonMF.id'}},") + "{vertex: {class:'PersonMF'}},") + "{edge:{class:'FriendMF',joinFieldName:'friend_to',lookup:'PersonMF.id',edgeFields:{since:'${input.since}'} }},") + "{field: {operation:'remove', fieldNames:['friend_from','friend_to','since']}}") + "], loader: { orientdb: { dbURL: 'plocal:./target/databases/")) + (name.getMethodName())) + "', dbType:'graph', classes: [{name:'FriendMF',extends:'E'}] } } }"));
        loader = proc.getLoader();
        pool = loader.getPool();
        db = pool.acquire();
        // createClasses(db);
        db.close();
        proc.execute();
        db = pool.acquire();
        Assert.assertEquals(5, db.countClass("PersonMF"));
        Assert.assertEquals(7, db.countClass("FriendMF"));
        db.close();
        pool.close();
        proc.getLoader().close();
    }
}

