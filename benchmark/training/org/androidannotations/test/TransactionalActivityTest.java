/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test;


import android.database.sqlite.SQLiteDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class TransactionalActivityTest {
    private SQLiteDatabase mockDb;

    private TransactionalActivity_ activity;

    @Test
    public void successfulTransaction() {
        activity.successfulTransaction(mockDb);
        InOrder inOrder = Mockito.inOrder(mockDb);
        inOrder.verify(mockDb).beginTransaction();
        inOrder.verify(mockDb).execSQL(ArgumentMatchers.anyString());
        inOrder.verify(mockDb).setTransactionSuccessful();
        inOrder.verify(mockDb).endTransaction();
    }

    @Test
    public void rollbackedTransaction() {
        try {
            activity.rollbackedTransaction(mockDb);
            Assert.fail("This method should throw an exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
        Mockito.verify(mockDb, Mockito.never()).setTransactionSuccessful();
        InOrder inOrder = Mockito.inOrder(mockDb);
        inOrder.verify(mockDb).beginTransaction();
        inOrder.verify(mockDb).endTransaction();
    }
}

