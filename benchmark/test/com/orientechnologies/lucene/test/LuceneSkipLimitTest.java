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
 * Created by enricorisa on 08/10/14.
 */
public class LuceneSkipLimitTest extends BaseLuceneTest {
    @Test
    public void testContext() {
        List<ODocument> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [title] LUCENE \"(title:man)\""));
        Assert.assertEquals(docs.size(), 14);
        ODocument doc = docs.get(9);
        docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [title] LUCENE \"(title:man)\" skip 10 limit 10"));
        Assert.assertEquals(docs.size(), 4);
        Assert.assertEquals(docs.contains(doc), false);
        docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select * from Song where [title] LUCENE \"(title:man)\" skip 14 limit 10"));
        Assert.assertEquals(docs.size(), 0);
    }
}

