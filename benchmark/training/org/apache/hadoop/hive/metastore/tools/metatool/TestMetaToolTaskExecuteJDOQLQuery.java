/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore.tools.metatool;


import ObjectStore.QueryWrapper;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.hadoop.hive.metastore.ObjectStore;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for MetaToolTaskExecuteJDOQLQuery.
 */
@Category(MetastoreUnitTest.class)
public class TestMetaToolTaskExecuteJDOQLQuery {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private OutputStream os;

    @Test
    public void testSelectQuery() throws Exception {
        String selectQuery = "select a from b";
        String entry1 = "abc";
        String entry2 = "def";
        ObjectStore mockObjectStore = Mockito.mock(ObjectStore.class);
        Mockito.doReturn(Arrays.asList(entry1, entry2)).when(mockObjectStore).executeJDOQLSelect(ArgumentMatchers.eq(selectQuery), ArgumentMatchers.any(QueryWrapper.class));
        MetaToolTaskExecuteJDOQLQuery t = new MetaToolTaskExecuteJDOQLQuery();
        t.setCommandLine(new HiveMetaToolCommandLine(new String[]{ "-executeJDOQL", selectQuery }));
        t.setObjectStore(mockObjectStore);
        t.execute();
        Assert.assertTrue((((os.toString()) + " doesn't contain ") + entry1), os.toString().contains(entry1));
        Assert.assertTrue((((os.toString()) + " doesn't contain ") + entry2), os.toString().contains(entry2));
    }

    @Test
    public void testUpdateQuerySuccessful() throws Exception {
        testUpdateQuery(1L, "Number of records updated: 1");
    }

    @Test
    public void testUpdateQueryNotSuccessful() throws Exception {
        testUpdateQuery((-1L), "Encountered error during executeJDOQL - commit of JDO transaction failed.");
    }

    @Test
    public void testIllegalQuery() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("HiveMetaTool:Unsupported statement type, only select and update supported");
        String illegalQuery = "abcde";
        MetaToolTaskExecuteJDOQLQuery t = new MetaToolTaskExecuteJDOQLQuery();
        t.setCommandLine(new HiveMetaToolCommandLine(new String[]{ "-executeJDOQL", illegalQuery }));
        t.execute();
    }
}

