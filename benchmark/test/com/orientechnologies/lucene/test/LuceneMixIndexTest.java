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
package com.orientechnologies.lucene.test;


import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Enrico Risa on 02/09/15.
 */
public class LuceneMixIndexTest extends BaseLuceneTest {
    @Test
    public void testMixQuery() {
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where  author = \'Hornsby\' and [title,lyrics]  LUCENE \"(title:mountain)\" "));
        Assert.assertEquals(1, docs.size());
        docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where  author = \'Hornsby\' and [title,lyrics] LUCENE \"(title:mountain)\" "));
        Assert.assertEquals(1, docs.size());
        docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where  author = \'Hornsby\' and [title,lyrics] LUCENE \"(title:ballad)\" "));
        Assert.assertEquals(0, docs.size());
        docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where  author = \'Hornsby\' and [title,lyrics] LUCENE \"(title:ballad)\" "));
        Assert.assertEquals(0, docs.size());
    }

    // @Ignore
    @Test
    public void testMixCompositeQuery() {
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where  author = \'Hornsby\' and [title,lyrics] LUCENE \"title:mountain\" "));
        Assert.assertEquals(1, docs.size());
        docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where author = \'Hornsby\' and [title,lyrics] LUCENE \"lyrics:happy\" "));
        Assert.assertEquals(1, docs.size());
        // docs = databaseDocumentTx.query(new OSQLSynchQuery<ODocument>(
        // "select * from Song where  author = 'Hornsby' and [title] LUCENE \"(title:ballad)\" "));
        // Assert.assertEquals(docs.size(), 0);
        // 
        // docs = databaseDocumentTx.query(new OSQLSynchQuery<ODocument>(
        // "select * from Song where  author = 'Hornsby' and title LUCENE \"(title:ballad)\" "));
        // Assert.assertEquals(docs.size(), 0);
    }
}

