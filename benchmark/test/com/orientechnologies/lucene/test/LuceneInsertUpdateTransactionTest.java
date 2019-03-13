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


import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by enricorisa on 28/06/14.
 */
public class LuceneInsertUpdateTransactionTest extends BaseLuceneTest {
    public LuceneInsertUpdateTransactionTest() {
        super();
    }

    @Test
    public void testInsertUpdateTransactionWithIndex() throws Exception {
        OSchema schema = db.getMetadata().getSchema();
        schema.reload();
        db.begin();
        ODocument doc = new ODocument("City");
        doc.field("name", "Rome");
        db.save(doc);
        OIndex idx = schema.getClass("City").getClassIndex("City.name");
        Assert.assertNotNull(idx);
        Collection<?> coll = ((Collection<?>) (idx.get("Rome")));
        Assert.assertEquals(coll.size(), 1);
        db.rollback();
        coll = ((Collection<?>) (idx.get("Rome")));
        Assert.assertEquals(coll.size(), 0);
        db.begin();
        doc = new ODocument("City");
        doc.field("name", "Rome");
        db.save(doc);
        OUser user = new OUser("test", "test");
        db.save(user.getDocument());
        db.commit();
        coll = ((Collection<?>) (idx.get("Rome")));
        Assert.assertEquals(coll.size(), 1);
    }
}

