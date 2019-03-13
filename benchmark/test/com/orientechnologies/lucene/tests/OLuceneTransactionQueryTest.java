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
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Enrico Risa on 10/08/15.
 */
public class OLuceneTransactionQueryTest extends OLuceneBaseTest {
    @Test
    public void testRollback() {
        ODocument doc = new ODocument("c1");
        doc.field("p1", "abc");
        db.begin();
        db.save(doc);
        String query = "select from C1 where search_fields(['p1'], 'abc' )=true ";
        OResultSet vertices = db.command(query);
        assertThat(vertices).hasSize(1);
        db.rollback();
        vertices = db.command(query);
        assertThat(vertices).hasSize(0);
    }

    @Test
    public void txRemoveTest() {
        db.begin();
        ODocument doc = new ODocument("c1");
        doc.field("p1", "abc");
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("C1.p1");
        db.save(doc);
        String query = "select from C1 where search_fields(['p1'], 'abc' )=true ";
        OResultSet vertices = db.command(query);
        assertThat(vertices).hasSize(1);
        assertThat(index.getSize()).isEqualTo(2);
        db.commit();
        vertices = db.command(query);
        List<OResult> results = vertices.stream().collect(Collectors.toList());
        assertThat(results).hasSize(1);
        assertThat(index.getSize()).isEqualTo(2);
        db.begin();
        doc = new ODocument("c1");
        doc.field("p1", "abc");
        db.delete(results.get(0).getElement().get().getIdentity());
        vertices = db.query(query);
        Collection coll = ((Collection) (index.get("abc")));
        assertThat(coll).hasSize(0);
        assertThat(vertices).hasSize(0);
        Iterator iterator = coll.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            iterator.next();
            i++;
        } 
        Assert.assertEquals(i, 0);
        assertThat(index.getSize()).isEqualTo(1);
        vertices.close();
        db.rollback();
        query = "select from C1 where search_fields(['p1'], 'abc' )=true ";
        vertices = db.command(query);
        assertThat(vertices).hasSize(1);
        assertThat(index.getSize()).isEqualTo(2);
        vertices.close();
    }

    @Test
    public void txUpdateTest() {
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("C1.p1");
        OClass c1 = db.getMetadata().getSchema().getClass("C1");
        try {
            c1.truncate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(index.getSize(), 0);
        db.begin();
        ODocument doc = new ODocument("c1");
        doc.field("p1", "update");
        db.save(doc);
        String query = "select from C1 where search_fields([\'p1\'], \"update\")=true ";
        OResultSet vertices = db.command(query);
        assertThat(vertices).hasSize(1);
        Assert.assertEquals(1, index.getSize());
        db.commit();
        vertices = db.command(query);
        List<OResult> results = vertices.stream().collect(Collectors.toList());
        Collection coll = ((Collection) (index.get("update")));
        assertThat(results).hasSize(1);
        assertThat(coll).hasSize(1);
        assertThat(index.getSize()).isEqualTo(1);
        db.begin();
        OResult record = results.get(0);
        OElement element = record.getElement().get();
        element.setProperty("p1", "removed");
        db.save(element);
        vertices = db.command(query);
        assertThat(vertices).hasSize(0);
        Assert.assertEquals(1, index.getSize());
        query = "select from C1 where search_fields([\'p1\'], \"removed\")=true ";
        vertices = db.command(query);
        coll = ((Collection) (index.get("removed")));
        assertThat(vertices).hasSize(1);
        Assert.assertEquals(1, coll.size());
        db.rollback();
        query = "select from C1 where search_fields([\'p1\'], \"update\")=true ";
        vertices = db.command(query);
        coll = ((Collection) (index.get("update")));
        assertThat(vertices).hasSize(1);
        assertThat(coll).hasSize(1);
        assertThat(index.getSize()).isEqualTo(1);
    }

    @Test
    public void txUpdateTestComplex() {
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("C1.p1");
        OClass c1 = db.getMetadata().getSchema().getClass("C1");
        try {
            c1.truncate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(index.getSize(), 0);
        db.begin();
        ODocument doc = new ODocument("c1");
        doc.field("p1", "abc");
        ODocument doc1 = new ODocument("c1");
        doc1.field("p1", "abc");
        db.save(doc1);
        db.save(doc);
        db.commit();
        db.begin();
        doc.field("p1", "removed");
        db.save(doc);
        String query = "select from C1 where search_fields([\'p1\'], \"abc\")=true ";
        OResultSet vertices = db.command(query);
        Collection coll = ((Collection) (index.get("abc")));
        assertThat(vertices).hasSize(1);
        Assert.assertEquals(1, coll.size());
        Iterator iterator = coll.iterator();
        int i = 0;
        ORecordId rid = null;
        while (iterator.hasNext()) {
            rid = ((ORecordId) (iterator.next()));
            i++;
        } 
        Assert.assertEquals(i, 1);
        Assert.assertEquals(doc1.getIdentity().toString(), rid.getIdentity().toString());
        Assert.assertEquals(index.getSize(), 2);
        query = "select from C1 where search_fields([\'p1\'], \"removed\")=true ";
        vertices = db.command(query);
        coll = ((Collection) (index.get("removed")));
        assertThat(vertices).hasSize(1);
        Assert.assertEquals(coll.size(), 1);
        db.rollback();
        query = "select from C1 where search_fields([\'p1\'], \"abc\")=true ";
        vertices = db.command(query);
        assertThat(vertices).hasSize(2);
        Assert.assertEquals(index.getSize(), 2);
    }
}

