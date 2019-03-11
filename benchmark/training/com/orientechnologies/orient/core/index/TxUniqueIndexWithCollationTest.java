/**
 * *  Copyright 2016 OrientDB LTD (info(at)orientdb.com)
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
 *  * For more information: http://www.orientdb.com
 */
package com.orientechnologies.orient.core.index;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OLegacyResultSet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 */
public class TxUniqueIndexWithCollationTest {
    private ODatabaseDocumentTx db;

    @Test
    public void testSubstrings() {
        db.begin();
        db.command(new OCommandSQL("update user set name='abd' where name='Aby'")).execute();
        final OLegacyResultSet<ODocument> r = db.command(new OCommandSQL("select * from user where name like '%B%' order by name")).execute();
        Assert.assertEquals(3, r.size());
        Assert.assertEquals("abc", r.get(0).field("name"));
        Assert.assertEquals("abd", r.get(1).field("name"));
        Assert.assertEquals("abz", r.get(2).field("name"));
        db.commit();
    }

    @Test
    public void testRange() {
        db.begin();
        db.command(new OCommandSQL("update user set name='Abd' where name='Aby'")).execute();
        final OLegacyResultSet<ODocument> r = db.command(new OCommandSQL("select * from user where name >= 'abd' order by name")).execute();
        Assert.assertEquals(2, r.size());
        Assert.assertEquals("Abd", r.get(0).field("name"));
        Assert.assertEquals("abz", r.get(1).field("name"));
        db.commit();
    }

    @Test
    public void testIn() {
        db.begin();
        db.command(new OCommandSQL("update user set name='abd' where name='Aby'")).execute();
        final OLegacyResultSet<ODocument> r = db.command(new OCommandSQL("select * from user where name in ['Abc', 'Abd', 'Abz'] order by name")).execute();
        Assert.assertEquals(3, r.size());
        Assert.assertEquals("abc", r.get(0).field("name"));
        Assert.assertEquals("abd", r.get(1).field("name"));
        Assert.assertEquals("abz", r.get(2).field("name"));
        db.commit();
    }
}

