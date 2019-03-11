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
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static OTransactionIndexChangesPerKey.SET_ADD_THRESHOLD;


/**
 *
 *
 * @author Sergey Sitnikov
 */
public class DuplicateNonUniqueIndexChangesTxTest {
    private static ODatabaseDocumentTx db;

    private OIndex index;

    @Test
    public void testDuplicateNullsOnCreate() {
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        // saved persons will have null name
        final ODocument person1 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").save();
        final ODocument person2 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").save();
        final ODocument person3 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").save();
        // change some names
        person3.field("name", "Name3").save();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids(null, person1, person2);
        assertRids("Name3", person3);
    }

    @Test
    public void testDuplicateNullsOnUpdate() {
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        final ODocument person2 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name2").save();
        final ODocument person3 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name3").save();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids(null);
        assertRids("Name1", person1);
        assertRids("Name2", person2);
        assertRids("Name3", person3);
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        // saved persons will have null name
        person1.field("name", ((Object) (null))).save();
        person2.field("name", ((Object) (null))).save();
        person3.field("name", ((Object) (null))).save();
        // change names
        person1.field("name", "Name2").save();
        person2.field("name", "Name1").save();
        person3.field("name", "Name2").save();
        // and again
        person1.field("name", "Name1").save();
        person2.field("name", "Name2").save();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids(null);
        assertRids("Name1", person1);
        assertRids("Name2", person2, person3);
        assertRids("Name3");
    }

    @Test
    public void testDuplicateValuesOnCreate() {
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        // saved persons will have same name
        final ODocument person1 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person2 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person3 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        // change some names
        person2.field("name", "Name1").save();
        person2.field("name", "Name2").save();
        person3.field("name", "Name2").save();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids("same", person1);
        assertRids("Name1");
        assertRids("Name2", person2, person3);
    }

    @Test
    public void testDuplicateValuesOnUpdate() {
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        final ODocument person2 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name2").save();
        final ODocument person3 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name3").save();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids(null);
        assertRids("Name1", person1);
        assertRids("Name2", person2);
        assertRids("Name3", person3);
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        // saved persons will have same name
        person1.field("name", "same").save();
        person2.field("name", "same").save();
        person3.field("name", "same").save();
        // change names back to unique in reverse order
        person3.field("name", "Name3").save();
        person2.field("name", "Name2").save();
        person1.field("name", "Name1").save();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids("same");
        assertRids("Name1", person1);
        assertRids("Name2", person2);
        assertRids("Name3", person3);
    }

    @Test
    public void testDuplicateValuesOnCreateDelete() {
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        // saved persons will have same name
        final ODocument person1 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person2 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person3 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        final ODocument person4 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "same").save();
        person1.delete();
        person2.field("name", "Name2").save();
        person3.delete();
        person4.field("name", "Name2").save();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids("Name1");
        assertRids("Name2", person2, person4);
        assertRids("Name3");
        assertRids("Name4");
    }

    @Test
    public void testDuplicateValuesOnUpdateDelete() {
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        final ODocument person1 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name1").save();
        final ODocument person2 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name2").save();
        final ODocument person3 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name3").save();
        final ODocument person4 = DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name4").save();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids("Name1", person1);
        assertRids("Name2", person2);
        assertRids("Name3", person3);
        assertRids("Name4", person4);
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        person1.delete();
        person2.field("name", "same").save();
        person3.delete();
        person4.field("name", "same").save();
        person2.field("name", "Name2").save();
        person4.field("name", "Name2").save();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids("same");
        assertRids("Name1");
        assertRids("Name2", person2, person4);
        assertRids("Name3");
        assertRids("Name4");
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        person2.delete();
        person4.delete();
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        assertRids("Name2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testManyManyUpdatesToTheSameKey() {
        final Set<Integer> unseen = new HashSet<Integer>();
        DuplicateNonUniqueIndexChangesTxTest.db.begin();
        for (int i = 0; i < ((SET_ADD_THRESHOLD) * 2); ++i) {
            DuplicateNonUniqueIndexChangesTxTest.db.newInstance("Person").field("name", "Name").field("serial", i).save();
            unseen.add(i);
        }
        DuplicateNonUniqueIndexChangesTxTest.db.commit();
        // verify index state
        final Iterable<OIdentifiable> rids = ((Iterable<OIdentifiable>) (index.get("Name")));
        for (OIdentifiable rid : rids) {
            final ODocument document = DuplicateNonUniqueIndexChangesTxTest.db.load(rid.getIdentity());
            unseen.remove(document.<Integer>field("serial"));
        }
        Assert.assertTrue(unseen.isEmpty());
    }
}

