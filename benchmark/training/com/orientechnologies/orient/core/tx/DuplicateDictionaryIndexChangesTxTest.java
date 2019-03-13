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
package com.orientechnologies.orient.core.tx;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 */
public class DuplicateDictionaryIndexChangesTxTest {
    private static ODatabaseDocumentTx db;

    private OIndex index;

    @Test
    public void testDuplicateNullsOnCreate() {
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        // saved persons will have null name
        final ODocument person1 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").save();
        final ODocument person2 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").save();
        final ODocument person3 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").save();
        // change some names to not null
        person1.field("name", "Name1").save();
        person2.field("name", "Name1").save();
        // should never throw
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person2, index.get("Name1"));
        Assert.assertEquals(person3, index.get(null));
    }

    @Test
    public void testDuplicateNullsOnUpdate() {
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", ((Object) (null))).save();
        final ODocument person2 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", ((Object) (null))).save();
        final ODocument person3 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", ((Object) (null))).save();
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person3, index.get(null));
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        // change some names
        person1.field("name", "Name2").save();
        person2.field("name", "Name2").save();
        person3.field("name", "Name3").save();
        // and again
        person1.field("name", "Name1").save();
        person2.field("name", "Name1").save();
        // should never throw
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person2, index.get("Name1"));
        Assert.assertEquals(person3, index.get("Name3"));
    }

    @Test
    public void testDuplicateValuesOnCreate() {
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        // saved persons will have same name
        final ODocument person1 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person2 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person3 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        // change names to unique
        person1.field("name", "Name1").save();
        person2.field("name", "Name2").save();
        person3.field("name", "Name1").save();
        // should never throw
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get("same"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name1"));
    }

    @Test
    public void testDuplicateValuesOnUpdate() {
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        final ODocument person2 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "Name2").save();
        final ODocument person3 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "Name3").save();
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        // saved persons will have same name
        person1.field("name", "same").save();
        person2.field("name", "same").save();
        person3.field("name", "same").save();
        // change names back to unique in reverse order
        person3.field("name", "Name3").save();
        person2.field("name", "Name2").save();
        person1.field("name", "Name1").save();
        // should never throw
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get("same"));
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
    }

    @Test
    public void testDuplicateValuesOnCreateDelete() {
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        // saved persons will have same name
        final ODocument person1 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person2 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person3 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person4 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        person1.delete();
        person2.field("name", "Name2").save();
        person3.delete();
        person4.field("name", "Name2").save();
        person4.delete();
        // should never throw
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get("same"));
        Assert.assertEquals(person2, index.get("Name2"));
    }

    @Test
    public void testDuplicateValuesOnUpdateDelete() {
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        final ODocument person2 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "Name2").save();
        final ODocument person3 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "Name3").save();
        final ODocument person4 = DuplicateDictionaryIndexChangesTxTest.db.newInstance("Person").field("name", "Name4").save();
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
        Assert.assertEquals(person4, index.get("Name4"));
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        person1.delete();
        person2.field("name", "same").save();
        person3.delete();
        person4.field("name", "same").save();
        person2.field("name", "Name2").save();
        person4.field("name", "Name2").save();
        // should never throw
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person4, index.get("Name2"));
        Assert.assertNull(index.get("same"));
        DuplicateDictionaryIndexChangesTxTest.db.begin();
        person2.delete();
        person4.delete();
        DuplicateDictionaryIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get("Name2"));
        Assert.assertNull(index.get("same"));
    }
}

