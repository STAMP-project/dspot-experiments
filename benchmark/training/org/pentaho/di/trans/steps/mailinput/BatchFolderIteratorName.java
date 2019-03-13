/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.mailinput;


import javax.mail.Folder;
import junit.framework.Assert;
import org.junit.Test;


public class BatchFolderIteratorName {
    static Folder folder = null;

    @Test
    public void testBatchSize2() {
        BatchFolderIterator bfi = new BatchFolderIterator(BatchFolderIteratorName.folder, 2, 1, 2);
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertFalse(bfi.hasNext());
    }

    @Test
    public void testBatchSize1x2() {
        BatchFolderIterator bfi = new BatchFolderIterator(BatchFolderIteratorName.folder, 1, 1, 2);
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertFalse(bfi.hasNext());
    }

    @Test
    public void testBatchSize1() {
        BatchFolderIterator bfi = new BatchFolderIterator(BatchFolderIteratorName.folder, 1, 1, 1);
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertFalse(bfi.hasNext());
    }

    @Test
    public void testBatchSize2x2() {
        BatchFolderIterator bfi = new BatchFolderIterator(BatchFolderIteratorName.folder, 2, 1, 4);
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertFalse(bfi.hasNext());
    }

    @Test
    public void testBatchSize2x3() {
        BatchFolderIterator bfi = new BatchFolderIterator(BatchFolderIteratorName.folder, 2, 1, 5);
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertTrue(bfi.hasNext());
        bfi.next();
        Assert.assertFalse(bfi.hasNext());
    }
}

