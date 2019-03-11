/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.hbase;


import DeleteHBaseCells.REL_SUCCESS;
import DeleteHBaseCells.SEPARATOR;
import java.util.List;
import org.junit.Test;


public class TestDeleteHBaseCells extends DeleteTestBase {
    @Test
    public void testSimpleDelete() {
        final String SEP = "::::";
        List<String> ids = populateTable(10000);
        runner.setProperty(SEPARATOR, SEP);
        runner.assertValid();
        StringBuilder sb = new StringBuilder();
        for (String id : ids) {
            sb.append(String.format("%s%sX%sY\n", id, SEP, SEP));
        }
        runner.enqueue(sb.toString().trim());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
    }
}

