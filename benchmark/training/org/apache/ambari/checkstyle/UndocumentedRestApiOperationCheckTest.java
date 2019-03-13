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
package org.apache.ambari.checkstyle;


import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;


public class UndocumentedRestApiOperationCheckTest extends AbstractModuleTestSupport {
    @Test
    public void test() throws Exception {
        final DefaultConfiguration config = createModuleConfig(UndocumentedRestApiOperationCheck.class);
        final String[] expected = new String[]{ "36: " + (UndocumentedRestApiOperationCheck.MESSAGE), "53: " + (UndocumentedRestApiOperationCheck.MESSAGE), "70: " + (UndocumentedRestApiOperationCheck.MESSAGE), "87: " + (UndocumentedRestApiOperationCheck.MESSAGE), "104: " + (UndocumentedRestApiOperationCheck.MESSAGE), "121: " + (UndocumentedRestApiOperationCheck.MESSAGE) };
        verify(config, getPath("InputRestApiOperation.java"), expected);
    }
}

