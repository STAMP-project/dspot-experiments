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
package org.apache.drill.exec.expr.fn.impl;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.apache.drill.categories.SqlFunctionTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.util.Text;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SqlFunctionTest.class, UnlikelyTest.class })
public class TestStringFunctions extends BaseTestQuery {
    @Test
    public void testStrPosMultiByte() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select `position`('a', 'abc') res1 from (values(1))").ordered().baselineColumns("res1").baselineValues(1L).go();
        BaseTestQuery.testBuilder().sqlQuery("select `position`(\'\\u11E9\', \'\\u11E9\\u0031\') res1 from (values(1))").ordered().baselineColumns("res1").baselineValues(1L).go();
    }

    @Test
    public void testSplitPart() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select split_part('abc~@~def~@~ghi', '~@~', 1) res1 from (values(1))").ordered().baselineColumns("res1").baselineValues("abc").go();
        BaseTestQuery.testBuilder().sqlQuery("select split_part('abc~@~def~@~ghi', '~@~', 2) res1 from (values(1))").ordered().baselineColumns("res1").baselineValues("def").go();
        // invalid index
        boolean expectedErrorEncountered;
        try {
            BaseTestQuery.testBuilder().sqlQuery("select split_part('abc~@~def~@~ghi', '~@~', 0) res1 from (values(1))").ordered().baselineColumns("res1").baselineValues("abc").go();
            expectedErrorEncountered = false;
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Index in split_part must be positive, value provided was 0"));
            expectedErrorEncountered = true;
        }
        if (!expectedErrorEncountered) {
            throw new RuntimeException("Missing expected error on invalid index for split_part function");
        }
        // with a multi-byte splitter
        BaseTestQuery.testBuilder().sqlQuery("select split_part(\'abc\\u1111drill\\u1111ghi\', \'\\u1111\', 2) res1 from (values(1))").ordered().baselineColumns("res1").baselineValues("drill").go();
        // going beyond the last available index, returns empty string
        BaseTestQuery.testBuilder().sqlQuery("select split_part('a,b,c', ',', 4) res1 from (values(1))").ordered().baselineColumns("res1").baselineValues("").go();
        // if the delimiter does not appear in the string, 1 returns the whole string
        BaseTestQuery.testBuilder().sqlQuery("select split_part('a,b,c', ' ', 1) res1 from (values(1))").ordered().baselineColumns("res1").baselineValues("a,b,c").go();
    }

    @Test
    public void testRegexpMatches() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(("select regexp_matches(a, '^a.*') res1, regexp_matches(b, '^a.*') res2 " + "from (values('abc', 'bcd'), ('bcd', 'abc')) as t(a,b)")).unOrdered().baselineColumns("res1", "res2").baselineValues(true, false).baselineValues(false, true).build().run();
    }

    @Test
    public void testRegexpMatchesNonAscii() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(("select regexp_matches(a, 'M?nchen') res1, regexp_matches(b, 'AM?nchenA') res2 " + "from (values('M?nchen', 'M?nchenA'), ('M?nchenA', 'AM?nchenA')) as t(a,b)")).unOrdered().baselineColumns("res1", "res2").baselineValues(true, false).baselineValues(false, true).build().run();
    }

    @Test
    public void testRegexpReplace() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(("select regexp_replace(a, 'a|c', 'x') res1, regexp_replace(b, 'd', 'zzz') res2 " + "from (values('abc', 'bcd'), ('bcd', 'abc')) as t(a,b)")).unOrdered().baselineColumns("res1", "res2").baselineValues("xbx", "bczzz").baselineValues("bxd", "abc").build().run();
    }

    @Test
    public void testLikeStartsWith() throws Exception {
        // all ASCII.
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD'), ('ABCD'),('ABCDE')," + "('AABCD'),('ABABCD'),('ABC$XYZ'), (''),('abcd'),") + "('x'), ('xyz'), ('%')) tbl(id) ") + "where id like 'ABC%'"))).unOrdered().baselineColumns("id").baselineValues("ABC").baselineValues("ABCD").baselineValues("ABCDE").baselineValues("ABC$XYZ").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD')," + "('ABCD'),('ABCDE'),('AABCD'),('ABAB CD'),('ABC$XYZ'),") + "(''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like 'AB%'"))).unOrdered().baselineColumns("id").baselineValues("AB").baselineValues("ABC").baselineValues("ABD").baselineValues("ABCD").baselineValues("ABCDE").baselineValues("ABAB CD").baselineValues("ABC$XYZ").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'), ('ABC'), ('ABD'), ('ABCD')," + "('ABCDE'),('AABCD'),('ABAB CD'),('ABC$XYZ'), (''),") + "('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like 'A%'"))).unOrdered().baselineColumns("id").baselineValues("A").baselineValues("AB").baselineValues("ABC").baselineValues("ABD").baselineValues("ABCD").baselineValues("ABCDE").baselineValues("AABCD").baselineValues("ABAB CD").baselineValues("ABC$XYZ").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE')," + "('AABCD'), ('ABABCD'),('ABC$XYZ'), (''),('abcd'),") + "('x'), ('xyz'), ('%')) tbl(id)") + " where id like 'z%'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // patternLength > txtLength
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD')," + "('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like 'ABCDEXYZRST%'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // non ASCII
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('?E?s?W?????i?T????3???i?T?U2~~')," + " ('xyz'), ('%')) tbl(id)") + " where id like '?%'"))).unOrdered().baselineColumns("id").baselineValues("?E?s?W?????i?T????3???i?T?U2~~").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('ABC?E?s?W?????i?T????3???i?T?U2~~'), " + "('xyz'), ('%')) tbl(id)") + " where id like 'ABC?%'"))).unOrdered().baselineColumns("id").baselineValues("ABC?E?s?W?????i?T????3???i?T?U2~~").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('ABC'), ('ABC?E?s?W?????i?T????3???i?T?U2~~'), ('xyz'), ('%')) tbl(id)" + " where id like 'A%'"))).unOrdered().baselineColumns("id").baselineValues("ABC").baselineValues("ABC?E?s?W?????i?T????3???i?T?U2~~").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('ABC'), ('?E?s?W?????i?T????3???i?T?U2~~'), ('xyz'), ('%')) tbl(id) " + "where id like 'Z%'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
    }

    @Test
    public void testLikeEndsWith() throws Exception {
        // all ASCII. End with multiple characters
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD'), ('ABCD'),('ABCDE')," + "('AABCD'),('ABABCD'),('ABC$XYZ'), (''),('abcd'), ") + "('x'), ('xyz'), ('%')) tbl(id) ") + "where id like '%BCD'"))).unOrdered().baselineColumns("id").baselineValues("ABCD").baselineValues("AABCD").baselineValues("ABABCD").build().run();
        // all ASCII. End with single character.
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD'), ('ABCD'),('ABCDE')," + "('AABCD'),('ABABCD'),('ABC$XYZ'), (''),('abcd'), ") + "('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%D'"))).unOrdered().baselineColumns("id").baselineValues("ABD").baselineValues("ABCD").baselineValues("AABCD").baselineValues("ABABCD").build().run();
        // all ASCII. End with nothing. Should match all.
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD'), ('ABCD'),('ABCDE')," + "('AABCD'),('ABABCD'),('ABC$XYZ'), (''),('abcd'), ") + "('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%'"))).unOrdered().baselineColumns("id").baselineValues("A").baselineValues("AB").baselineValues("ABC").baselineValues("ABD").baselineValues("ABCD").baselineValues("ABCDE").baselineValues("AABCD").baselineValues("ABABCD").baselineValues("ABC$XYZ").baselineValues("").baselineValues("abcd").baselineValues("x").baselineValues("xyz").baselineValues("%").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD')," + "('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%F'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // patternLength > txtLength
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD'),('ABABCD')," + "('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id) ") + "where id like '%ABCDEXYZRST'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // patternLength == txtLength
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD'),('ABABCD')," + "('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id) ") + "where id like '%ABC'"))).unOrdered().baselineColumns("id").baselineValues("ABC").build().run();
        // non ASCII. End with single character
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('?E?s?W?????i?T????3???i?T?U2~~'), ('')," + "('?T?U2~~'), ('xyz'), ('%')) tbl(id) ") + "where id like '%~~'"))).unOrdered().baselineColumns("id").baselineValues("?E?s?W?????i?T????3???i?T?U2~~").baselineValues("?T?U2~~").build().run();
        // non ASCII. End with multiple characters
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('ABC?E?s?W?????i?T????3???i?T?U2~~'), " + "(''), ('?T?U2~~'), ('xyz'), ('%')) tbl(id)") + "where id like '%?T?U2~~'"))).unOrdered().baselineColumns("id").baselineValues("ABC?E?s?W?????i?T????3???i?T?U2~~").baselineValues("?T?U2~~").build().run();
        // non ASCII, no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('ABC?E?s?W?????i?T????3???i?T?U2~~'), ('')," + "('xyz'), ('%')) tbl(id)") + "where id like '%E'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
    }

    @Test
    public void testLikeContains() throws Exception {
        // all ASCII. match at the beginning, middle and end.
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD'), ('ABCD'),('DEABC')," + "('AABCD'), ('ABABCDEF'),('AABC$XYZ'), (''),('abcd'), ('x'), ") + "('xyz'), ('%')) tbl(id) ") + "where id like '%ABC%'"))).unOrdered().baselineColumns("id").baselineValues("ABC").baselineValues("ABCD").baselineValues("DEABC").baselineValues("AABCD").baselineValues("ABABCDEF").baselineValues("AABC$XYZ").build().run();
        // all ASCII. match at the beginning, middle and end, single character.
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD'), ('ABCD'),('ABCDE')," + "('AABCD'),('ABABCD'),('CAB$XYZ'), (''),('abcd'), ('x'), ") + "('xyz'), ('%')) tbl(id)") + "where id like '%C%'"))).unOrdered().baselineColumns("id").baselineValues("ABC").baselineValues("ABCD").baselineValues("ABCDE").baselineValues("AABCD").baselineValues("ABABCD").baselineValues("CAB$XYZ").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD'), ('ABCD'),('ABCDE')," + "('AABCD'),('ABABCD'),('CAB$XYZ'), (''),('abcd'), ('x'),") + "('xyz'), ('%')) tbl(id)") + "where id like '%FGH%'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // patternLength > txtLength
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD'),('ABABCD')," + "('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%ABCDEXYZRST%'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // all match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD'),('ABABCD')," + "('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%%'"))).unOrdered().baselineColumns("id").baselineValues("A").baselineValues("AB").baselineValues("ABC").baselineValues("ABCD").baselineValues("ABCDE").baselineValues("AABCD").baselineValues("ABABCD").baselineValues("ABC$XYZ").baselineValues("").baselineValues("abcd").baselineValues("x").baselineValues("xyz").baselineValues("%").build().run();
        // non ASCII
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('?E?s?W?????i?T????3???i?T?U2~~'), " + "(''), ('?T?U2~~'), ('xyz'), ('%')) tbl(id)") + "where id like '%?U2%'"))).unOrdered().baselineColumns("id").baselineValues("?E?s?W?????i?T????3???i?T?U2~~").baselineValues("?T?U2~~").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('ABC?E?s?W?????i?T????3???i?T?U2~~'), " + "(''), ('?T?U2~~'), ('xyz'), ('%')) tbl(id)") + "where id like '%E?s?W%'"))).unOrdered().baselineColumns("id").baselineValues("ABC?E?s?W?????i?T????3???i?T?U2~~").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('ABC'), ('ABC?E?s?W?????i?T????3???i?T?U2~~'), ('')," + "('xyz'), ('%')) tbl(id) where id like '%?T?T%'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
    }

    @Test
    public void testLikeConstant() throws Exception {
        // all ASCII
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD'), " + "('ABCD'),('ABCDE'),('AABCD'),('ABABCD'),('ABC$XYZ'), (''),") + "('abcd'), ('x'), ('xyz'), ('%')) tbl(id) ") + "where id like 'ABC'"))).unOrdered().baselineColumns("id").baselineValues("ABC").build().run();
        // Multiple same values
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABC')," + "('ABD'), ('ABCD'),('ABCDE'),('AABCD'),('ABABCD'),('ABC$XYZ'),") + "(''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like 'ABC'"))).unOrdered().baselineColumns("id").baselineValues("ABC").baselineValues("ABC").build().run();
        // match empty string
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC'), ('ABD'), ('ABCD'),('ABCDE')," + "('AABCD'),('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'),") + " ('xyz'), ('%')) tbl(id)") + "where id like ''"))).unOrdered().baselineColumns("id").baselineValues("").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD')," + "('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'),") + "('%')) tbl(id) where id like 'EFGH'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // patternLength > txtLength
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD'),('ABABCD')," + "('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + " where id like 'ABCDEXYZRST'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // non ASCII
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('?E?s?W?????i?T????3???i?T?U2~~'), (''), " + "('?T?U2~~'), ('xyz'), ('%')) tbl(id)") + " where id like '?T?U2~~'"))).unOrdered().baselineColumns("id").baselineValues("?T?U2~~").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('ABC?E?s?W?????i?T????3???i?T?U2~~'), ('')," + "('?T?U2~~'), ('xyz'), ('%')) tbl(id)") + "where id like 'ABC?E?s?W?????i?T????3???i?T?U2~~'"))).unOrdered().baselineColumns("id").baselineValues("ABC?E?s?W?????i?T????3???i?T?U2~~").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('ABC'), ('ABC?E?s?W?????i?T????3???i?T?U2~~'), (''), " + "('xyz'), ('%')) tbl(id) where id like '?T?T'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
    }

    @Test
    public void testLikeWithEscapeStartsWith() throws Exception {
        // all ASCII
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB'),('ABC%'), ('ABD'), ('ABCD'),('ABCDE')," + "('AABCD'),('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'),") + "('xyz'), ('%')) tbl(id) ") + "where id like 'ABC#%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("ABC%").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('A%B'),('A%B%C%'), ('ABD'), ('ABCD')," + "('ABCDE'),('AABCD'),('A%BABCD'),('ABC$XYZ'), (''),") + "('abcd'), ('x'), ('xyz'), ('%')) tbl (id)") + "where id like 'A#%B%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A%B").baselineValues("A%B%C%").baselineValues("A%BABCD").build().run();
        // Multiple escape characters
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A_'),('AB'),('ABC'), ('ABD'), ('A_BC%D_')," + "('ABCDE'), ('A_BC%D_XYZ'),('ABABCD'),('A_BC%D_$%XYZ'),") + " (''),('abcd'), ('x'), ('%')) tbl(id)") + "where id like 'A#_BC#%D#_%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A_BC%D_").baselineValues("A_BC%D_XYZ").baselineValues("A_BC%D_$%XYZ").build().run();
        // Escape character followed by escape character
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A_'),('AB'),('ABC'), ('ABD'), ('ABC%D_')," + "('A#BC%D_E'),('A_BC%D_XYZ'),('ABABCD'),('A#BC%D_$%XYZ'),") + " (''),('abcd'), ('x'), ('%')) tbl(id)") + "where id like 'A##BC#%D#_%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A#BC%D_E").baselineValues("A#BC%D_$%XYZ").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD')," + "('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + " where id like 'z#%' escape '#'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // patternLength > txtLength
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('ABCD'),('ABCDE'),('AABCD')," + "('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + " where id like 'ABCDEXYZRST#_%' escape '#'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        // non ASCII
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('?E_?s?W?????i?T????3???i?T?U2~~')," + " ('xyz'), ('%')) tbl(id)") + " where id like '?E#_%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("?E_?s?W?????i?T????3???i?T?U2~~").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('ABC?E?%s?W?????i?T????3???i?T?U2~~'), " + "('xyz'), ('%')) tbl(id)") + " where id like 'ABC?E?#%s?W%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("ABC?E?%s?W?????i?T????3???i?T?U2~~").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('AB%C'), " + "('AB%C?E?s?W?????i?T????3???i?T?U2~~'), ('xyz'), ('%')) tbl(id)") + " where id like 'AB#%C%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("AB%C").baselineValues("AB%C?E?s?W?????i?T????3???i?T?U2~~").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('?E?s?W?????i?T????3???i?T?U2~~')," + " ('xyz'), ('%')) tbl(id)") + "where id like 'Z$%%' escape '$'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
    }

    @Test
    public void testLikeWithEscapeEndsWith() throws Exception {
        // all ASCII
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('AB%C'),('ABCDE'),('AABCD')," + "('ABAB%C'),('ABC$XYZAB%C'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id) ") + "where id like '%AB$%C' escape '$'"))).unOrdered().baselineColumns("id").baselineValues("AB%C").baselineValues("ABAB%C").baselineValues("ABC$XYZAB%C").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB_'),('AB%C%AB_'), ('ABD'), ('ABCD')," + "('ABCDE'), ('AABCD'),('AB%ABCD'),('ABC$XYZAB_'), (''),") + "('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%AB#_' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("AB_").baselineValues("AB%C%AB_").baselineValues("ABC$XYZAB_").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A_'),('AB'),('ABC'), ('ABD'), ('A_BCD'),('ABCDEA_')," + "('A_ABCD'),('ABABCDA_'),('A_BC$XYZA_'), (''),('abcd'),") + " ('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%A#_' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A_").baselineValues("ABCDEA_").baselineValues("ABABCDA_").baselineValues("A_BC$XYZA_").build().run();
        // Multiple escape characters
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A_'),('AB'),('ABC'), ('ABD'), ('A_BC%D_'),('ABCDE')," + "('XYZA_BC%D_'),('ABABCD'),('$%XYZA_BC%D_'), (''),") + "('abcd'), ('x'), ('%')) tbl(id)") + " where id like '%A#_BC#%D#_' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A_BC%D_").baselineValues("XYZA_BC%D_").baselineValues("$%XYZA_BC%D_").build().run();
        // Escape character followed by escape character
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A_'),('AB'),('ABC'), ('ABD'), ('A#BC%D_'),('A#BC%D_E')," + "('A_BC%D_XYZ'),('ABABCD'),('$%XYZA#BC%D_'), (''),") + "('abcd'), ('x'), ('%')) tbl(id)") + " where id like '%A##BC#%D#_' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A#BC%D_").baselineValues("$%XYZA#BC%D_").build().run();
        // non ASCII
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('?E_?s?W?????i?T????3???i?T?U2_~~')," + " ('xyz'), ('%')) tbl(id)") + " where id like '%2#_~~' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("?E_?s?W?????i?T????3???i?T?U2_~~").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('ABC?E?%s?W?????i?T????3???i?T?U2~~ABC?E?%s?W'), " + "('xyz'), ('%')) tbl(id)") + " where id like '%ABC?E?#%s?W' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("ABC?E?%s?W?????i?T????3???i?T?U2~~ABC?E?%s?W").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('AB%C'), " + "('AB%C?E?s?W?????i?T????3???i?T?U2~~AB%C'), ('xyz'), ('%')) tbl(id)") + " where id like '%AB#%C' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("AB%C").baselineValues("AB%C?E?s?W?????i?T????3???i?T?U2~~AB%C").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('ABC'), ('?E?s?W?????i?T????3???i?T?U2~~')," + " ('xyz'), ('%')) tbl(id)") + "where id like '%$%' escape '$'"))).unOrdered().baselineColumns("id").baselineValues("%").build().run();
    }

    @Test
    public void testLikeWithEscapeContains() throws Exception {
        // test EndsWith
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC'),('AB%C'),('ABCDE'),('AB%AB%CDED')," + "('ABAB%CDE'),('ABC$XYZAB%C'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%AB$%C%' escape '$'"))).unOrdered().baselineColumns("id").baselineValues("ABAB%CDE").baselineValues("AB%AB%CDED").baselineValues("AB%C").baselineValues("ABC$XYZAB%C").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB_'),('%AB%C%AB_'), ('%AB%D'), ('ABCD')," + "('AB%AC%AB%DE'), ('AABCD'),('AB%AB%CD'),('ABC$XYZAB_'),") + "(''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%#%AB#%%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("%AB%C%AB_").baselineValues("%AB%D").baselineValues("AB%AB%CD").baselineValues("AB%AC%AB%DE").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A'),('AB_'),('%AB%C%AB_'), ('%AB%D'), ('ABCD')," + "('AB%AC%AB%DE'), ('AABCD'),('AB%AB%CD'),('ABC$XYZAB_'),") + "(''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%#%A#_B#%%' escape '#'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A_'),('AB'),('ABC'), ('ABD'), ('A_BCD'),('ABA_CDEA_')," + "('A_ABCD'),('ABABCDA_'),('A_BC$XYZA_'), (''),") + "('abcd'), ('x'), ('xyz'), ('%')) tbl(id)") + "where id like '%A#_%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A_").baselineValues("A_BCD").baselineValues("ABA_CDEA_").baselineValues("A_ABCD").baselineValues("A_BC$XYZA_").baselineValues("ABABCDA_").build().run();
        // Multiple escape characters
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A_'),('AB'),('ABC'), ('ABD'), ('A_BC%D_'),('ABCDE')," + "('XYZA_BC%D_'),('ABABCD'),('$%XYZA_BC%D_'), (''),") + "('abcd'), ('x'), ('%')) tbl") + "(id) where id like '%A#_BC#%D#_' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A_BC%D_").baselineValues("XYZA_BC%D_").baselineValues("$%XYZA_BC%D_").build().run();
        // Escape character followed by escape character
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ((("VALUES('A_'),('AB'),('ABC'), ('ABDA#BC%D_'), ('A#BC%D_')," + "('A#BC%BC%D_E'),('A_BC%D_XYZ'),('ABABCD'),('$%XYZA#BC%D_'),") + " (''),('abcd'), ('x'), ('%')) tbl(id)") + " where id like '%A##BC#%D#_%' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A#BC%D_").baselineValues("$%XYZA#BC%D_").baselineValues("ABDA#BC%D_").build().run();
    }

    @Test
    public void testLikeWithEscapeConstant() throws Exception {
        // test startsWith
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC%'),('ABCD'),('ABCDE'),('AABCD')," + "('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id) ") + "where id like 'ABC%%' escape '%' "))).unOrdered().baselineColumns("id").baselineValues("ABC%").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('%ABC'),('ABCD'),('ABCDE'),('AABCD')," + "('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id) ") + "where id like '%%ABC' escape '%' "))).unOrdered().baselineColumns("id").baselineValues("%ABC").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('AB%C'),('ABCD'),('ABCDE'),('AABCD'),('ABABCD')," + "('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id) ") + "where id like 'AB%%C' escape '%' "))).unOrdered().baselineColumns("id").baselineValues("AB%C").build().run();
        // Multiple escape characters
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A_'),('AB'),('ABC'), ('ABD'), ('%_BC%D_'),('ABCDE')," + "('XYZA_BC%D_'),('ABABCD'),('$%XYZA_BC%D_'), (''),('abcd'), ('x'), ('%')) tbl(id)") + " where id like '%%_BC%%D%_' escape '%'"))).unOrdered().baselineColumns("id").baselineValues("%_BC%D_").build().run();
        // Escape character followed by escape character
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A_'),('AB'),('ABC'), ('ABDA#BC%D_'), ('A#BC%D_'),('A#BA#BC%D_E')," + "('A_BC%D_XYZ'),('ABABCD'),('$%XYZA#BC%D_'), (''),('abcd'), ('x'), ('%')) tbl(id)") + " where id like 'A##BC#%D#_' escape '#'"))).unOrdered().baselineColumns("id").baselineValues("A#BC%D_").build().run();
        // no match
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + (("VALUES('A'),('AB'),('ABC%'),('ABCD'),('ABCDE'),('AABCD')," + "('ABABCD'),('ABC$XYZ'), (''),('abcd'), ('x'), ('xyz'), ('%')) tbl(id) ") + "where id like '%_ABC%%' escape '%' "))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
    }

    @Test
    public void testLikeRandom() throws Exception {
        // test Random queries with like
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('aeiou'),('abcdef'),('afdrgt'),('abcdt'),('aaaa'),('a'),('aeiou'),(''),('a aa')) tbl(id)" + "where id not like 'a %'"))).unOrdered().baselineColumns("id").baselineValues("aeiou").baselineValues("abcdef").baselineValues("afdrgt").baselineValues("abcdt").baselineValues("aaaa").baselineValues("a").baselineValues("aeiou").baselineValues("").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('aeiou'),('abcdefizu'),('afdrgt'),('abcdt'),('aaaa'),('a'),('aeiou'),(''),('a aa')) tbl(id)" + "where id like 'a%i_u'"))).unOrdered().baselineColumns("id").baselineValues("aeiou").baselineValues("aeiou").baselineValues("abcdefizu").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('xyzaeioughbcd'),('abcdefizu'),('afdrgt'),('abcdt'),('aaaa'),('a'),('aeiou'),(''),('a aa')) tbl(id)" + "where id like '%a_i_u%bcd%'"))).unOrdered().baselineColumns("id").baselineValues("xyzaeioughbcd").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like 'ab'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like '%ab'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like 'ab%'"))).unOrdered().baselineColumns("id").baselineValues("abc").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like '%ab%'"))).unOrdered().baselineColumns("id").baselineValues("abc").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like 'abc'"))).unOrdered().baselineColumns("id").baselineValues("abc").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like 'abc%'"))).unOrdered().baselineColumns("id").baselineValues("abc").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like '%abc'"))).unOrdered().baselineColumns("id").baselineValues("abc").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like '%abc%'"))).unOrdered().baselineColumns("id").baselineValues("abc").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like 'abcd'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like 'abcd%'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like '%abcd'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like '%abcd%'"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like ''"))).unOrdered().baselineColumns("id").expectsEmptyResultSet().build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like '%'"))).unOrdered().baselineColumns("id").baselineValues("abc").build().run();
        BaseTestQuery.testBuilder().sqlQuery((" SELECT  id FROM (" + ("VALUES('abc')) tbl(id)" + "where id like '%%'"))).unOrdered().baselineColumns("id").baselineValues("abc").build().run();
    }

    @Test
    public void testILike() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select n_name from cp.`tpch/nation.parquet` where ilike(n_name, '%united%') = true").unOrdered().baselineColumns("n_name").baselineValues("UNITED STATES").baselineValues("UNITED KINGDOM").build().run();
    }

    @Test
    public void testILikeEscape() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select a from (select concat(r_name , '_region') a from cp.`tpch/region.parquet`) where ilike(a, 'asia#_region', '#') = true").unOrdered().baselineColumns("a").baselineValues("ASIA_region").build().run();
    }

    @Test
    public void testSubstr() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select substr(n_name, 'UN.TE.') a from cp.`tpch/nation.parquet` where ilike(n_name, 'united%') = true").unOrdered().baselineColumns("a").baselineValues("UNITED").baselineValues("UNITED").build().run();
    }

    @Test
    public void testLpadTwoArgConvergeToLpad() throws Exception {
        final String query_1 = "SELECT lpad(r_name, 25) \n" + "FROM cp.`tpch/region.parquet`";
        final String query_2 = "SELECT lpad(r_name, 25, \' \') \n" + "FROM cp.`tpch/region.parquet`";
        BaseTestQuery.testBuilder().sqlQuery(query_1).unOrdered().sqlBaselineQuery(query_2).build().run();
    }

    @Test
    public void testRpadTwoArgConvergeToRpad() throws Exception {
        final String query_1 = "SELECT rpad(r_name, 25) \n" + "FROM cp.`tpch/region.parquet`";
        final String query_2 = "SELECT rpad(r_name, 25, \' \') \n" + "FROM cp.`tpch/region.parquet`";
        BaseTestQuery.testBuilder().sqlQuery(query_1).unOrdered().sqlBaselineQuery(query_2).build().run();
    }

    @Test
    public void testLtrimOneArgConvergeToLtrim() throws Exception {
        final String query_1 = "SELECT ltrim(concat(\' \', r_name, \' \')) \n" + "FROM cp.`tpch/region.parquet`";
        final String query_2 = "SELECT ltrim(concat(\' \', r_name, \' \'), \' \') \n" + "FROM cp.`tpch/region.parquet`";
        BaseTestQuery.testBuilder().sqlQuery(query_1).unOrdered().sqlBaselineQuery(query_2).build().run();
    }

    @Test
    public void testRtrimOneArgConvergeToRtrim() throws Exception {
        final String query_1 = "SELECT rtrim(concat(\' \', r_name, \' \')) \n" + "FROM cp.`tpch/region.parquet`";
        final String query_2 = "SELECT rtrim(concat(\' \', r_name, \' \'), \' \') \n" + "FROM cp.`tpch/region.parquet`";
        BaseTestQuery.testBuilder().sqlQuery(query_1).unOrdered().sqlBaselineQuery(query_2).build().run();
    }

    @Test
    public void testBtrimOneArgConvergeToBtrim() throws Exception {
        final String query_1 = "SELECT btrim(concat(\' \', r_name, \' \')) \n" + "FROM cp.`tpch/region.parquet`";
        final String query_2 = "SELECT btrim(concat(\' \', r_name, \' \'), \' \') \n" + "FROM cp.`tpch/region.parquet`";
        BaseTestQuery.testBuilder().sqlQuery(query_1).unOrdered().sqlBaselineQuery(query_2).build().run();
    }

    @Test
    public void testSplit() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select split(n_name, ' ') words from cp.`tpch/nation.parquet` where n_nationkey = 24").unOrdered().baselineColumns("words").baselineValues(ImmutableList.of(new Text("UNITED"), new Text("STATES"))).build().run();
    }

    @Test
    public void testSplitWithNullInput() throws Exception {
        // Contents of the generated file:
        /* {"a": "aaaaaa.bbb.cc.ddddd"}
        {"a": null}
        {"a": "aa"}
         */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), "nullable_strings.json")))) {
            String[] fieldValue = new String[]{ "\"aaaaaa.bbb.cc.ddddd\"", null, "\"aa\"" };
            for (String value : fieldValue) {
                String entry = String.format("{ \"a\": %s}\n", value);
                writer.write(entry);
            }
        }
        BaseTestQuery.testBuilder().sqlQuery("select split(a, '.') wordsCount from dfs.`nullable_strings.json` t").unOrdered().baselineColumns("wordsCount").baselineValues(ImmutableList.of(new Text("aaaaaa"), new Text("bbb"), new Text("cc"), new Text("ddddd"))).baselineValues(ImmutableList.of()).baselineValues(ImmutableList.of(new Text("aa"))).go();
    }

    @Test
    public void testReverse() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select reverse('qwerty') words from (values(1))").unOrdered().baselineColumns("words").baselineValues("ytrewq").build().run();
    }

    // DRILL-5424
    @Test
    public void testReverseLongVarChars() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), "table_with_long_varchars.json")))) {
            for (int i = 0; i < 10; i++) {
                writer.write("{ \"a\": \"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz\"}");
            }
        }
        BaseTestQuery.test("select reverse(a) from dfs.`table_with_long_varchars.json` t");
    }

    @Test
    public void testLower() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(("select\n" + (((((("lower(\'ABC\') col_upper,\n" + "lower(\'abc\') col_lower,\n") + "lower(\'AbC aBc\') col_space,\n") + "lower(\'123ABC$!abc123.\') as col_special,\n") + "lower(\'\') as col_empty,\n") + "lower(cast(null as varchar(10))) as col_null\n") + "from (values(1))"))).unOrdered().baselineColumns("col_upper", "col_lower", "col_space", "col_special", "col_empty", "col_null").baselineValues("abc", "abc", "abc abc", "123abc$!abc123.", "", null).build().run();
    }

    @Test
    public void testUpper() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(("select\n" + (((((("upper(\'ABC\')as col_upper,\n" + "upper(\'abc\') as col_lower,\n") + "upper(\'AbC aBc\') as col_space,\n") + "upper(\'123ABC$!abc123.\') as col_special,\n") + "upper(\'\') as col_empty,\n") + "upper(cast(null as varchar(10))) as col_null\n") + "from (values(1))"))).unOrdered().baselineColumns("col_upper", "col_lower", "col_space", "col_special", "col_empty", "col_null").baselineValues("ABC", "ABC", "ABC ABC", "123ABC$!ABC123.", "", null).build().run();
    }

    @Test
    public void testInitcap() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(("select\n" + (((((("initcap(\'ABC\')as col_upper,\n" + "initcap(\'abc\') as col_lower,\n") + "initcap(\'AbC aBc\') as col_space,\n") + "initcap(\'123ABC$!abc123.\') as col_special,\n") + "initcap(\'\') as col_empty,\n") + "initcap(cast(null as varchar(10))) as col_null\n") + "from (values(1))"))).unOrdered().baselineColumns("col_upper", "col_lower", "col_space", "col_special", "col_empty", "col_null").baselineValues("Abc", "Abc", "Abc Abc", "123abc$!Abc123.", "", null).build().run();
    }

    @Test
    public void testMultiByteEncoding() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(("select\n" + ((("upper(\'\u043f\u0440\u0438\u0432\u0435\u0442\')as col_upper,\n" + "lower(\'\u041f\u0420\u0418\u0412\u0415\u0422\') as col_lower,\n") + "initcap(\'\u043f\u0440\u0438\u0412\u0415\u0422\') as col_initcap\n") + "from (values(1))"))).unOrdered().baselineColumns("col_upper", "col_lower", "col_initcap").baselineValues("??????", "??????", "??????").build().run();
    }
}

