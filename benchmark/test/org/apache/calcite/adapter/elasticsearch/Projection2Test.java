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
package org.apache.calcite.adapter.elasticsearch;


import CalciteAssert.AssertThat;
import java.util.Locale;
import org.apache.calcite.test.CalciteAssert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Checks renaming of fields (also upper, lower cases) during projections
 */
public class Projection2Test {
    @ClassRule
    public static final EmbeddedElasticsearchPolicy NODE = EmbeddedElasticsearchPolicy.create();

    private static final String NAME = "nested";

    @Test
    public void projection() {
        CalciteAssert.that().with(newConnectionFactory()).query("select \"a\", \"b.a\", \"b.b\", \"b.c.a\" from view").returns("a=1; b.a=2; b.b=3; b.c.a=foo\n");
    }

    @Test
    public void projection2() {
        String sql = String.format(Locale.ROOT, ("select _MAP['a'], _MAP['b.a'], _MAP['b.b'], " + "_MAP[\'b.c.a\'], _MAP[\'missing\'], _MAP[\'b.missing\'] from \"elastic\".\"%s\""), Projection2Test.NAME);
        CalciteAssert.that().with(newConnectionFactory()).query(sql).returns("EXPR$0=1; EXPR$1=2; EXPR$2=3; EXPR$3=foo; EXPR$4=null; EXPR$5=null\n");
    }

    @Test
    public void projection3() {
        CalciteAssert.that().with(newConnectionFactory()).query(String.format(Locale.ROOT, "select * from \"elastic\".\"%s\"", Projection2Test.NAME)).returns("_MAP={a=1, b={a=2, b=3, c={a=foo}}}\n");
        CalciteAssert.that().with(newConnectionFactory()).query(String.format(Locale.ROOT, "select *, _MAP[\'a\'] from \"elastic\".\"%s\"", Projection2Test.NAME)).returns("_MAP={a=1, b={a=2, b=3, c={a=foo}}}; EXPR$1=1\n");
    }

    /**
     * Test that {@code _id} field is available when queried explicitly.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-id-field.html">ID Field</a>
     */
    @Test
    public void projectionWithIdField() {
        final CalciteAssert.AssertThat factory = CalciteAssert.that().with(newConnectionFactory());
        factory.query("select \"id\" from view").returns(Projection2Test.regexMatch("id=\\p{Graph}+"));
        factory.query("select \"id\", \"id\" from view").returns(Projection2Test.regexMatch("id=\\p{Graph}+; id=\\p{Graph}+"));
        factory.query("select \"id\", \"a\" from view").returns(Projection2Test.regexMatch("id=\\p{Graph}+; a=1"));
        factory.query("select \"a\", \"id\" from view").returns(Projection2Test.regexMatch("a=1; id=\\p{Graph}+"));
        // single _id column
        final String sql1 = String.format(Locale.ROOT, ("select _MAP['_id'] " + " from \"elastic\".\"%s\""), Projection2Test.NAME);
        factory.query(sql1).returns(Projection2Test.regexMatch("EXPR$0=\\p{Graph}+"));
        // multiple columns: _id and a
        final String sql2 = String.format(Locale.ROOT, ("select _MAP['_id'], _MAP['a'] " + " from \"elastic\".\"%s\""), Projection2Test.NAME);
        factory.query(sql2).returns(Projection2Test.regexMatch("EXPR$0=\\p{Graph}+; EXPR$1=1"));
        // multiple _id columns
        final String sql3 = String.format(Locale.ROOT, ("select _MAP['_id'], _MAP['_id'] " + " from \"elastic\".\"%s\""), Projection2Test.NAME);
        factory.query(sql3).returns(Projection2Test.regexMatch("EXPR$0=\\p{Graph}+; EXPR$1=\\p{Graph}+"));
        // _id column with same alias
        final String sql4 = String.format(Locale.ROOT, ("select _MAP[\'_id\'] as \"_id\" " + " from \"elastic\".\"%s\""), Projection2Test.NAME);
        factory.query(sql4).returns(Projection2Test.regexMatch("_id=\\p{Graph}+"));
        // _id field not available implicitly
        factory.query(String.format(Locale.ROOT, "select * from \"elastic\".\"%s\"", Projection2Test.NAME)).returns(Projection2Test.regexMatch("_MAP={a=1, b={a=2, b=3, c={a=foo}}}"));
        factory.query(String.format(Locale.ROOT, "select *, _MAP[\'_id\'] from \"elastic\".\"%s\"", Projection2Test.NAME)).returns(Projection2Test.regexMatch("_MAP={a=1, b={a=2, b=3, c={a=foo}}}; EXPR$1=\\p{Graph}+"));
    }
}

/**
 * End Projection2Test.java
 */
