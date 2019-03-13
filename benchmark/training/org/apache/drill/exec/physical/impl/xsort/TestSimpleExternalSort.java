/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.impl.xsort;


import org.apache.drill.categories.SlowTest;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.DrillTest;
import org.apache.drill.test.TestTools;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;


@Category({ SlowTest.class })
public class TestSimpleExternalSort extends DrillTest {
    @Rule
    public final TestRule TIMEOUT = TestTools.getTimeoutRule(160000);

    @Rule
    public final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    @Test
    public void mergeSortWithSv2Managed() throws Exception {
        mergeSortWithSv2(false);
    }

    @Test
    public void mergeSortWithSv2Legacy() throws Exception {
        mergeSortWithSv2(true);
    }

    @Test
    public void sortOneKeyDescendingMergeSortManaged() throws Throwable {
        sortOneKeyDescendingMergeSort(false);
    }

    @Test
    public void sortOneKeyDescendingMergeSortLegacy() throws Throwable {
        sortOneKeyDescendingMergeSort(true);
    }

    @Test
    public void sortOneKeyDescendingExternalSortManaged() throws Throwable {
        sortOneKeyDescendingExternalSort(false);
    }

    @Test
    public void sortOneKeyDescendingExternalSortLegacy() throws Throwable {
        sortOneKeyDescendingExternalSort(true);
    }

    @Test
    public void outOfMemoryExternalSortManaged() throws Throwable {
        outOfMemoryExternalSort(false);
    }

    @Test
    public void outOfMemoryExternalSortLegacy() throws Throwable {
        outOfMemoryExternalSort(true);
    }
}

