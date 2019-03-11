/**
 * * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
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
package com.orientechnologies.lucene.tests;


import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Enrico Risa on 10/08/15.
 */
public class OLuceneTransactionEmbeddedQueryTest extends OLuceneBaseTest {
    @Test
    public void testRollback() {
        ODocument doc = new ODocument("c1");
        doc.field("p1", new String[]{ "abc" });
        db.begin();
        db.save(doc);
        String query = "select from C1 where search_class( \"abc\")=true ";
        OResultSet vertices = db.command(query);
        assertThat(vertices).hasSize(1);
        db.rollback();
        query = "select from C1 where search_class( \"abc\")=true  ";
        vertices = db.command(query);
        assertThat(vertices).hasSize(0);
    }

    @Test
    public void txRemoveTest() {
        db.begin();
        ODocument doc = new ODocument("c1");
        doc.field("p1", new String[]{ "abc" });
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("C1.p1");
        db.save(doc);
        String query = "select from C1 where search_class( \"abc\")=true";
        OResultSet vertices = db.command(query);
        assertThat(vertices).hasSize(1);
        Assert.assertEquals(index.getSize(), 2);
        db.commit();
        vertices = db.command(query);
        assertThat(vertices).hasSize(1);
        Assert.assertEquals(index.getSize(), 2);
        db.begin();
        db.delete(doc);
        vertices = db.command(query);
        Collection coll = ((Collection) (index.get("abc")));
        assertThat(vertices).hasSize(0);
        Assert.assertEquals(coll.size(), 0);
        Iterator iterator = coll.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            iterator.next();
            i++;
        } 
        Assert.assertEquals(i, 0);
        Assert.assertEquals(index.getSize(), 1);
        db.rollback();
        vertices = db.command(query);
        assertThat(vertices).hasSize(1);
        Assert.assertEquals(index.getSize(), 2);
    }

    @Test
    public void txUpdateTestComplex() {
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("C1.p1");
        Assert.assertEquals(index.getSize(), 1);
        db.begin();
        ODocument doc = new ODocument("c1");
        doc.field("p1", new String[]{ "abc" });
        ODocument doc1 = new ODocument("c1");
        doc1.field("p1", new String[]{ "abc" });
        db.save(doc1);
        db.save(doc);
        db.commit();
        db.begin();
        doc.field("p1", new String[]{ "removed" });
        db.save(doc);
        String query = "select from C1 where p1 lucene \"abc\"";
        List<ODocument> vertices = db.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(query)).execute();
        Collection coll = ((Collection) (index.get("abc")));
        Assert.assertEquals(vertices.size(), 1);
        Assert.assertEquals(coll.size(), 1);
        Iterator iterator = coll.iterator();
        int i = 0;
        ORecordId rid = null;
        while (iterator.hasNext()) {
            rid = ((ORecordId) (iterator.next()));
            i++;
        } 
        Assert.assertEquals(i, 1);
        Assert.assertEquals(doc1.getIdentity().toString(), rid.getIdentity().toString());
        Assert.assertEquals(index.getSize(), 3);
        query = "select from C1 where p1 lucene \"removed\" ";
        vertices = db.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(query)).execute();
        coll = ((Collection) (index.get("removed")));
        Assert.assertEquals(vertices.size(), 1);
        Assert.assertEquals(coll.size(), 1);
        db.rollback();
        query = "select from C1 where p1 lucene \"abc\" ";
        vertices = db.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(query)).execute();
        Assert.assertEquals(vertices.size(), 2);
        Assert.assertEquals(index.getSize(), 3);
    }
}

