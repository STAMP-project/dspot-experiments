/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.state.internals;


import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;


/**
 * The purpose of this test is, to catch interface changes if we upgrade {@link RocksDB}.
 * Using reflections, we make sure the {@link RocksDBGenericOptionsToDbOptionsColumnFamilyOptionsAdapter} maps all
 * methods from {@link DBOptions} and {@link ColumnFamilyOptions} to/from {@link Options} correctly.
 */
@RunWith(EasyMockRunner.class)
public class RocksDBGenericOptionsToDbOptionsColumnFamilyOptionsAdapterTest {
    private final List<String> ignoreMethods = new LinkedList<String>() {
        {
            add("isOwningHandle");
            add("dispose");
            add("wait");
            add("equals");
            add("getClass");
            add("hashCode");
            add("notify");
            add("notifyAll");
            add("toString");
        }
    };

    @Mock
    private DBOptions dbOptions;

    @Mock
    private ColumnFamilyOptions columnFamilyOptions;

    @Test
    public void shouldOverwriteAllOptionsMethods() throws Exception {
        for (final Method method : Options.class.getMethods()) {
            if (!(ignoreMethods.contains(method.getName()))) {
                RocksDBGenericOptionsToDbOptionsColumnFamilyOptionsAdapter.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            }
        }
    }

    @Test
    public void shouldForwardAllDbOptionsCalls() throws Exception {
        for (final Method method : Options.class.getMethods()) {
            if (!(ignoreMethods.contains(method.getName()))) {
                try {
                    DBOptions.class.getMethod(method.getName(), method.getParameterTypes());
                    verifyDBOptionsMethodCall(method);
                } catch (final NoSuchMethodException expectedAndSwallow) {
                }
            }
        }
    }

    @Test
    public void shouldForwardAllColumnFamilyCalls() throws Exception {
        for (final Method method : Options.class.getMethods()) {
            if (!(ignoreMethods.contains(method.getName()))) {
                try {
                    ColumnFamilyOptions.class.getMethod(method.getName(), method.getParameterTypes());
                    verifyColumnFamilyOptionsMethodCall(method);
                } catch (final NoSuchMethodException expectedAndSwallow) {
                }
            }
        }
    }
}

