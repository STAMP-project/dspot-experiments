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
package org.apache.ignite.internal.processors.cache;


import java.util.concurrent.Callable;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test reveals issue of null values in SQL query resultset columns that correspond to compound key.
 * That happens when QueryEntity.keyFields has wrong register compared to QueryEntity.fields.
 * Issue only manifests for BinaryMarshaller case. Otherwise the keyFields aren't taken into account.
 */
public class QueryEntityCaseMismatchTest extends GridCommonAbstractTest {
    /**
     * The cache must not initialize if QueryEntity.keyFields isn't subset of QueryEntity.fields
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCacheInitializationFailure() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                startGrid(1);
                return null;
            }
        }, IgniteCheckedException.class, null);
    }
}

