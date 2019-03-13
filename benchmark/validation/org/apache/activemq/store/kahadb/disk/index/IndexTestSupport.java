/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.kahadb.disk.index;


import java.io.File;
import junit.framework.TestCase;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import org.junit.Test;


/**
 * Test a HashIndex
 */
public abstract class IndexTestSupport extends TestCase {
    private static final int COUNT = 10000;

    protected Index<String, Long> index;

    protected File directory;

    protected PageFile pf;

    protected Transaction tx;

    @Test(timeout = 60000)
    public void testIndex() throws Exception {
        createPageFileAndIndex(500);
        this.index.load(tx);
        tx.commit();
        doInsert(IndexTestSupport.COUNT);
        this.index.unload(tx);
        tx.commit();
        this.index.load(tx);
        tx.commit();
        checkRetrieve(IndexTestSupport.COUNT);
        doRemove(IndexTestSupport.COUNT);
        this.index.unload(tx);
        tx.commit();
        this.index.load(tx);
        tx.commit();
        doInsert(IndexTestSupport.COUNT);
        doRemoveHalf(IndexTestSupport.COUNT);
        doInsertHalf(IndexTestSupport.COUNT);
        this.index.unload(tx);
        tx.commit();
        this.index.load(tx);
        tx.commit();
        checkRetrieve(IndexTestSupport.COUNT);
        this.index.unload(tx);
        tx.commit();
    }
}

