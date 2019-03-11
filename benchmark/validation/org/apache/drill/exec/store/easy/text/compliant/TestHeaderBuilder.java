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


import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.test.DrillTest;
import org.junit.Assert;
import org.junit.Test;


public class TestHeaderBuilder extends DrillTest {
    @Test
    public void testEmptyHeader() {
        HeaderBuilder hb = new HeaderBuilder();
        hb.startBatch();
        try {
            hb.finishRecord();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains("must define at least one header"));
        }
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "");
        try {
            hb.finishRecord();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains("must define at least one header"));
        }
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "   ");
        validateHeader(hb, new String[]{ "column_1" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, ",");
        validateHeader(hb, new String[]{ "column_1", "column_2" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, " , ");
        validateHeader(hb, new String[]{ "column_1", "column_2" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "a,   ");
        validateHeader(hb, new String[]{ "a", "column_2" });
    }

    @Test
    public void testWhiteSpace() {
        HeaderBuilder hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "a");
        validateHeader(hb, new String[]{ "a" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, " a ");
        validateHeader(hb, new String[]{ "a" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "    a    ");
        validateHeader(hb, new String[]{ "a" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "a,b,c");
        validateHeader(hb, new String[]{ "a", "b", "c" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, " a , b ,  c ");
        validateHeader(hb, new String[]{ "a", "b", "c" });
    }

    @Test
    public void testSyntax() {
        HeaderBuilder hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "a_123");
        validateHeader(hb, new String[]{ "a_123" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "a_123_");
        validateHeader(hb, new String[]{ "a_123_" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "az09_");
        validateHeader(hb, new String[]{ "az09_" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "+");
        validateHeader(hb, new String[]{ "column_1" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "+,-");
        validateHeader(hb, new String[]{ "column_1", "column_2" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "+9a");
        validateHeader(hb, new String[]{ "col_9a" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "9a");
        validateHeader(hb, new String[]{ "col_9a" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "a+b");
        validateHeader(hb, new String[]{ "a_b" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "a_b");
        validateHeader(hb, new String[]{ "a_b" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "EXPR$0");
        validateHeader(hb, new String[]{ "EXPR_0" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "(_-^-_)");
        validateHeader(hb, new String[]{ "col_______" });
    }

    @Test
    public void testUnicode() {
        HeaderBuilder hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "?????");
        validateHeader(hb, new String[]{ "?????" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "??????");
        validateHeader(hb, new String[]{ "??????" });
        hb = new HeaderBuilder();
        hb.startBatch();
        parse(hb, "Paris,?????,??????");
        validateHeader(hb, new String[]{ "Paris", "?????", "??????" });
    }

    @Test
    public void testDuplicateNames() {
        testParser("a,a", new String[]{ "a", "a_2" });
        testParser("a,A", new String[]{ "a", "A_2" });
        // It ain't pretty, but it is unique...
        testParser("a,A,A_2", new String[]{ "a", "A_2", "A_2_2" });
        // Verify with non-ASCII characters
        testParser("?????,?????", new String[]{ "?????", "?????_2" });
    }
}

