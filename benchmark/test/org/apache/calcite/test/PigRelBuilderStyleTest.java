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


import JoinRelType.INNER;
import java.io.File;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.tools.RelBuilder;
import org.hamcrest.core.Is;
import org.junit.Assume;
import org.junit.Test;


/**
 * Tests for the {@code org.apache.calcite.adapter.pig} package that tests the
 * building of {@link PigRel} relational expressions using {@link RelBuilder} and
 * associated factories in {@link PigRelFactories}.
 */
public class PigRelBuilderStyleTest extends AbstractPigTest {
    public PigRelBuilderStyleTest() {
        Assume.assumeThat("Pigs don't like Windows", File.separatorChar, Is.is('/'));
    }

    @Test
    public void testScanAndFilter() throws Exception {
        final SchemaPlus schema = createTestSchema();
        final RelBuilder builder = createRelBuilder(schema);
        final RelNode node = builder.scan("t").filter(builder.call(GREATER_THAN, builder.field("tc0"), builder.literal("abc"))).build();
        final RelNode optimized = optimizeWithVolcano(node);
        assertScriptAndResults("t", getPigScript(optimized, schema), ("t = LOAD 'target/data.txt" + ("\' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n" + "t = FILTER t BY (tc0 > 'abc');")), new String[]{ "(b,2)", "(c,3)" });
    }

    @Test
    public void testImplWithCountWithoutGroupBy() {
        final SchemaPlus schema = createTestSchema();
        final RelBuilder builder = createRelBuilder(schema);
        final RelNode node = builder.scan("t").aggregate(builder.groupKey(), builder.count(false, "c", builder.field("tc0"))).build();
        final RelNode optimized = optimizeWithVolcano(node);
        assertScriptAndResults("t", getPigScript(optimized, schema), ("t = LOAD 'target/data.txt" + (((("\' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n" + "t = GROUP t ALL;\n") + "t = FOREACH t {\n") + "  GENERATE COUNT(t.tc0) AS c;\n") + "};")), new String[]{ "(3)" });
    }

    @Test
    public void testImplWithGroupByCountDistinct() {
        final SchemaPlus schema = createTestSchema();
        final RelBuilder builder = createRelBuilder(schema);
        final RelNode node = builder.scan("t").aggregate(builder.groupKey("tc1", "tc0"), builder.count(true, "c", builder.field("tc1"))).build();
        final RelNode optimized = optimizeWithVolcano(node);
        assertScriptAndResults("t", getPigScript(optimized, schema), ("t = LOAD 'target/data.txt" + ((((("\' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n" + "t = GROUP t BY (tc0, tc1);\n") + "t = FOREACH t {\n") + "  tc1_DISTINCT = DISTINCT t.tc1;\n") + "  GENERATE group.tc0 AS tc0, group.tc1 AS tc1, COUNT(tc1_DISTINCT) AS c;\n") + "};")), new String[]{ "(a,1,1)", "(b,2,1)", "(c,3,1)" });
    }

    @Test
    public void testImplWithJoin() throws Exception {
        final SchemaPlus schema = createTestSchema();
        final RelBuilder builder = createRelBuilder(schema);
        final RelNode node = builder.scan("t").scan("s").join(INNER, builder.equals(builder.field(2, 0, "tc1"), builder.field(2, 1, "sc0"))).filter(builder.call(GREATER_THAN, builder.field("tc0"), builder.literal("a"))).build();
        final RelNode optimized = optimizeWithVolcano(node);
        assertScriptAndResults("t", getPigScript(optimized, schema), ("t = LOAD 'target/data.txt" + (((("\' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n" + "t = FILTER t BY (tc0 > \'a\');\n") + "s = LOAD 'target/data2.txt") + "\' USING PigStorage() AS (sc0:chararray, sc1:chararray);\n") + "t = JOIN t BY tc1 , s BY sc0;")), new String[]{ "(b,2,2,label2)" });
    }
}

/**
 * End PigRelBuilderStyleTest.java
 */
