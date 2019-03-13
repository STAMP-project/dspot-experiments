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
package org.apache.dubbo.qos.textui;


import TTable.Align;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class TKvTest {
    @Test
    public void test1() {
        TKv tKv = new TKv(new TTable.ColumnDefine(Align.RIGHT), new TTable.ColumnDefine(10, false, Align.LEFT));
        tKv.add("KEY-1", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        tKv.add("KEY-2", "1234567890");
        tKv.add("KEY-3", "1234567890");
        TTable tTable = new TTable(new TTable.ColumnDefine[]{ new TTable.ColumnDefine(), new TTable.ColumnDefine(20, false, Align.LEFT) });
        String kv = tKv.rendering();
        MatcherAssert.assertThat(kv, Matchers.containsString(("ABCDEFGHIJ" + (System.lineSeparator()))));
        MatcherAssert.assertThat(kv, Matchers.containsString(("KLMNOPQRST" + (System.lineSeparator()))));
        MatcherAssert.assertThat(kv, Matchers.containsString(("UVWXYZ" + (System.lineSeparator()))));
        tTable.addRow("OPTIONS", kv);
        String table = tTable.rendering();
        MatcherAssert.assertThat(table, Matchers.containsString("|OPTIONS|"));
        MatcherAssert.assertThat(table, Matchers.containsString("|KEY-3"));
        System.out.println(table);
    }

    @Test
    public void test2() throws Exception {
        TKv tKv = new TKv();
        tKv.add("KEY-1", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        tKv.add("KEY-2", "1234567890");
        tKv.add("KEY-3", "1234567890");
        String kv = tKv.rendering();
        MatcherAssert.assertThat(kv, Matchers.containsString("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        System.out.println(kv);
    }
}

