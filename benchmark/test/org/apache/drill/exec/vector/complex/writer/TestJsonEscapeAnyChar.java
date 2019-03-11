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
package org.apache.drill.exec.vector.complex.writer;


import java.io.File;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.test.ClusterTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestJsonEscapeAnyChar extends ClusterTest {
    private File testFile;

    private static final String TABLE = "escape.json";

    private static final String JSON_DATA = "{\"name\": \"ABC\\S\"}";

    private static final String QUERY = String.format("select * from dfs.`%s`", TestJsonEscapeAnyChar.TABLE);

    @Test
    public void testwithOptionEnabled() throws Exception {
        try {
            enableJsonReaderEscapeAnyChar();
            testBuilder().sqlQuery(TestJsonEscapeAnyChar.QUERY).unOrdered().baselineColumns("name").baselineValues("ABCS").build().run();
        } finally {
            resetJsonReaderEscapeAnyChar();
        }
    }

    @Test
    public void testwithOptionDisabled() throws Exception {
        try {
            queryBuilder().sql(TestJsonEscapeAnyChar.QUERY).run();
        } catch (UserRemoteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("DATA_READ ERROR: Error parsing JSON - Unrecognized character escape"));
        }
    }
}

