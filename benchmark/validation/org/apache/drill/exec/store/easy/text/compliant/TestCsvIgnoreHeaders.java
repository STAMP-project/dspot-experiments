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
package org.apache.drill.exec.store.easy.text.compliant;


import java.io.IOException;
import org.apache.drill.categories.RowSetTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;


// CSV reader now hosted on the row set framework
@Category(RowSetTests.class)
public class TestCsvIgnoreHeaders extends BaseCsvTest {
    private static String[] withHeaders = new String[]{ "a,b,c", "10,foo,bar", "20,fred,wilma" };

    private static String[] raggedRows = new String[]{ "a,b,c", "10,dino", "20,foo,bar", "30" };

    @Test
    public void testColumns() throws IOException {
        try {
            enableV3(false);
            doTestColumns();
            enableV3(true);
            doTestColumns();
        } finally {
            resetV3();
        }
    }

    @Test
    public void testRaggedRows() throws IOException {
        try {
            enableV3(false);
            doTestRaggedRows();
            enableV3(true);
            doTestRaggedRows();
        } finally {
            resetV3();
        }
    }
}

