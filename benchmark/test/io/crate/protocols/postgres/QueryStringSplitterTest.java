/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.protocols.postgres;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class QueryStringSplitterTest {
    @Test
    public void testEmptyQuery() {
        Assert.assertThat(QueryStringSplitter.splitQuery("  "), Matchers.contains(""));
        Assert.assertThat(QueryStringSplitter.splitQuery("      "), Matchers.contains(""));
        Assert.assertThat(QueryStringSplitter.splitQuery(";"), Matchers.contains(";"));
        Assert.assertThat(QueryStringSplitter.splitQuery(";; ;"), Matchers.contains(";", ";", " ;"));
        Assert.assertThat(QueryStringSplitter.splitQuery(";  "), Matchers.contains(";"));
        Assert.assertThat(QueryStringSplitter.splitQuery(";;  "), Matchers.contains(";", ";"));
        Assert.assertThat(QueryStringSplitter.splitQuery("; ;   ;"), Matchers.contains(";", " ;", "   ;"));
    }

    @Test
    public void testSimpleQuery() {
        Assert.assertThat(QueryStringSplitter.splitQuery("select id from users"), Matchers.contains("select id from users"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select id from users;"), Matchers.contains("select id from users;"));
    }

    @Test
    public void testMultiQuery() {
        Assert.assertThat(QueryStringSplitter.splitQuery("select 1; select 2"), Matchers.contains("select 1;", " select 2"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select 1;select 2;"), Matchers.contains("select 1;", "select 2;"));
    }

    @Test
    public void testSingleQuoteEscaping() {
        Assert.assertThat(QueryStringSplitter.splitQuery("select 'Hello ''Joe''';select 2"), Matchers.contains("select 'Hello ''Joe''';", "select 2"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select 'Hello Semicolon;';select 2"), Matchers.contains("select 'Hello Semicolon;';", "select 2"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select \'Hello \"Semicolon\";\';select 2"), Matchers.contains("select \'Hello \"Semicolon\";\';", "select 2"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select 'Hello comment -- test;';select 2"), Matchers.contains("select 'Hello comment -- test;';", "select 2"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select 'Hello comment /* bla */;';select 2"), Matchers.contains("select 'Hello comment /* bla */;';", "select 2"));
        Assert.assertThat(QueryStringSplitter.splitQuery("insert into doc.test (col) values ('aaa'';')"), Matchers.contains("insert into doc.test (col) values ('aaa'';')"));
        Assert.assertThat(QueryStringSplitter.splitQuery("insert into doc.test (col) values ('aaa' '';')"), Matchers.contains("insert into doc.test (col) values ('aaa' '';", "')"));
    }

    @Test
    public void testDoubleQuoteEscaping() {
        Assert.assertThat(QueryStringSplitter.splitQuery("select \"USER\";select \"crazy\'Column\""), Matchers.contains("select \"USER\";", "select \"crazy\'Column\""));
        Assert.assertThat(QueryStringSplitter.splitQuery("select \"SemiColon;\";select \"crazy\'Column\""), Matchers.contains("select \"SemiColon;\";", "select \"crazy\'Column\""));
        Assert.assertThat(QueryStringSplitter.splitQuery("select \"\'SemiColon\';\";select \"crazy\'Column\""), Matchers.contains("select \"\'SemiColon\';\";", "select \"crazy\'Column\""));
        Assert.assertThat(QueryStringSplitter.splitQuery("select \"Hello comment -- test;\";select 2"), Matchers.contains("select \"Hello comment -- test;\";", "select 2"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select \"Hello comment /* bla */;\"; select 2"), Matchers.contains("select \"Hello comment /* bla */;\";", " select 2"));
    }

    @Test
    public void testLineComment() {
        Assert.assertThat(QueryStringSplitter.splitQuery("select 1;--select 2"), Matchers.contains("select 1;"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select 1;--select 2\nselect 3"), Matchers.contains("select 1;", "select 3"));
    }

    @Test
    public void testBlockComment() {
        Assert.assertThat(QueryStringSplitter.splitQuery("select 1; /* just a comment */"), Matchers.contains("select 1;"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select 1; /* just \n a comment \n */"), Matchers.contains("select 1;"));
        Assert.assertThat(QueryStringSplitter.splitQuery("select 1; /* just \n a comment \n */select 2"), Matchers.contains("select 1;", "select 2"));
    }

    @Test
    public void testComplexMultiQuery() {
        Assert.assertThat(QueryStringSplitter.splitQuery(("select id, 'text' from users;" + (((("select count(*) from users group by id;" + " -- comment ") + "\n select * from users;") + "/* block - commment \n */") + "select \"USER\""))), Matchers.contains("select id, 'text' from users;", "select count(*) from users group by id;", " select * from users;", "select \"USER\""));
    }
}

