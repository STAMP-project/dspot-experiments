/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import org.apache.calcite.materialize.MaterializationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit tests for server and DDL.
 */
@RunWith(Parameterized.class)
public class ServerQuidemTest extends QuidemTest {
    /**
     * Creates a ServerQuidemTest. Public per {@link Parameterized}.
     */
    @SuppressWarnings("WeakerAccess")
    public ServerQuidemTest(String path) {
        super(path);
    }

    @Override
    @Test
    public void test() throws Exception {
        MaterializationService.setThreadLocal();
        super.test();
    }
}

/**
 * End ServerQuidemTest.java
 */
