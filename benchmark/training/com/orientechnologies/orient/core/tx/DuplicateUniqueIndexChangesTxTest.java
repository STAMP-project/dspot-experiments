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
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 */
public class DuplicateUniqueIndexChangesTxTest {
    private static ODatabaseDocumentTx db;

    private OIndex index;

    @Test
    public void testDuplicateNullsOnCreate() {
        DuplicateUniqueIndexChangesTxTest.db.begin();
        // saved persons will have null name
        final ODocument person1 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").save();
        final ODocument person2 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").save();
        final ODocument person3 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").save();
        // change names to unique
        person1.field("name", "Name1").save();
        person2.field("name", "Name2").save();
        person3.field("name", "Name3").save();
        // should not throw ORecordDuplicatedException exception
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get(null));
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
    }

    @Test
    public void testDuplicateNullsOnUpdate() {
        DuplicateUniqueIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        final ODocument person2 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name2").save();
        final ODocument person3 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name3").save();
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get(null));
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
        DuplicateUniqueIndexChangesTxTest.db.begin();
        // saved persons will have null name
        person1.field("name", ((Object) (null))).save();
        person2.field("name", ((Object) (null))).save();
        person3.field("name", ((Object) (null))).save();
        // change names back to unique swapped
        person1.field("name", "Name2").save();
        person2.field("name", "Name1").save();
        person3.field("name", "Name3").save();
        // and again
        person1.field("name", "Name1").save();
        person2.field("name", "Name2").save();
        // should not throw ORecordDuplicatedException exception
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get(null));
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
    }

    @Test
    public void testDuplicateValuesOnCreate() {
        DuplicateUniqueIndexChangesTxTest.db.begin();
        // saved persons will have same name
        final ODocument person1 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person2 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person3 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        // change names to unique
        person1.field("name", "Name1").save();
        person2.field("name", "Name2").save();
        person3.field("name", "Name3").save();
        // should not throw ORecordDuplicatedException exception
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get("same"));
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
    }

    @Test
    public void testDuplicateValuesOnUpdate() {
        DuplicateUniqueIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        final ODocument person2 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name2").save();
        final ODocument person3 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name3").save();
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
        DuplicateUniqueIndexChangesTxTest.db.begin();
        // saved persons will have same name
        person1.field("name", "same").save();
        person2.field("name", "same").save();
        person3.field("name", "same").save();
        // change names back to unique in reverse order
        person3.field("name", "Name3").save();
        person2.field("name", "Name2").save();
        person1.field("name", "Name1").save();
        // should not throw ORecordDuplicatedException exception
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get("same"));
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
    }

    @Test
    public void testDuplicateValuesOnCreateDelete() {
        DuplicateUniqueIndexChangesTxTest.db.begin();
        // saved persons will have same name
        final ODocument person1 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person2 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person3 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person4 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        person1.delete();
        person2.field("name", "Name2").save();
        person3.delete();
        // should not throw ORecordDuplicatedException exception
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person4, index.get("same"));
    }

    @Test
    public void testDuplicateValuesOnUpdateDelete() {
        DuplicateUniqueIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        final ODocument person2 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name2").save();
        final ODocument person3 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name3").save();
        final ODocument person4 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name4").save();
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
        Assert.assertEquals(person4, index.get("Name4"));
        DuplicateUniqueIndexChangesTxTest.db.begin();
        person1.delete();
        person2.field("name", "same").save();
        person3.delete();
        person4.field("name", "same").save();
        person2.field("name", "Name2").save();
        // should not throw ORecordDuplicatedException exception
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person4, index.get("same"));
        DuplicateUniqueIndexChangesTxTest.db.begin();
        person2.delete();
        person4.delete();
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertNull(index.get("Name2"));
        Assert.assertNull(index.get("same"));
    }

    @Test(expected = ORecordDuplicatedException.class)
    public void testDuplicateCreateThrows() {
        DuplicateUniqueIndexChangesTxTest.db.begin();
        DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").save();
        DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").save();
        DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        // Assert.assertThrows(ORecordDuplicatedException.class, new Assert.ThrowingRunnable() {
        // @Override
        // public void run() throws Throwable {
        // db.commit();
        // }
        // });
        DuplicateUniqueIndexChangesTxTest.db.commit();
    }

    @Test(expected = ORecordDuplicatedException.class)
    public void testDuplicateUpdateThrows() {
        DuplicateUniqueIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        final ODocument person2 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name2").save();
        final ODocument person3 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name3").save();
        final ODocument person4 = DuplicateUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name4").save();
        DuplicateUniqueIndexChangesTxTest.db.commit();
        // verify index state
        Assert.assertEquals(person1, index.get("Name1"));
        Assert.assertEquals(person2, index.get("Name2"));
        Assert.assertEquals(person3, index.get("Name3"));
        Assert.assertEquals(person4, index.get("Name4"));
        DuplicateUniqueIndexChangesTxTest.db.begin();
        person1.field("name", "Name1").save();
        person2.field("name", ((Object) (null))).save();
        person3.field("name", "Name1").save();
        person4.field("name", ((Object) (null))).save();
        // Assert.assertThrows(ORecordDuplicatedException.class, new Assert.ThrowingRunnable() {
        // @Override
        // public void run() throws Throwable {
        // db.commit();
        // }
        // });
        DuplicateUniqueIndexChangesTxTest.db.commit();
    }
}

