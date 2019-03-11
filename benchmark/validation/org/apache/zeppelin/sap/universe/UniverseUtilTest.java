/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.sap.universe;


import org.junit.Assert;
import org.junit.Test;


public class UniverseUtilTest {
    private UniverseClient universeClient;

    private UniverseUtil universeUtil;

    @Test
    public void testForConvert() throws UniverseException {
        String request = "universe [testUniverse];\n" + ("select [Measure].[name5]\n" + "where [Filter].[name3] and [Dimension].[Test].[name2] > 1;");
        UniverseQuery universeQuery = universeUtil.convertQuery(request, universeClient, null);
        Assert.assertNotNull(universeQuery);
        Assert.assertNotNull(universeQuery.getUniverseInfo());
        Assert.assertEquals(("<resultObjects>\n" + ("<resultObject path=\"Measure|folder\\name5|measure\" id=\"name5id\"/>\n" + "</resultObjects>")), universeQuery.getSelect());
        Assert.assertEquals(("<and>\n" + ((((((((("<predefinedFilter path=\"Filter|folder\\name3|filter\" id=\"name3id\"/>\n" + "\n<comparisonFilter path=\"Dimension|folder\\Test|folder\\name2|dimension\"") + " operator=\"GreaterThan\" id=\"name2id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"Numeric\">1</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</and>\n")), universeQuery.getWhere());
        Assert.assertEquals("testUniverse", universeQuery.getUniverseInfo().getName());
    }

    @Test
    public void testConvertConditions() throws UniverseException {
        String request = "universe [testUniverse];\n" + (((((((((("select [Measure].[name5]\n" + "where [Filter].[name3] ") + "and [Dimension].[Test].[name2] >= 1 ") + "and [Dimension].[Test].[name2] < 20 ") + "and [Dimension].[Test].[name1] <> 'test' ") + "and [Dimension].[Test].[name1] is not null ") + "and [Measure].[name5] is null") + "and [Dimension].[Test].[name1] in ('var1', 'v a r 2') ") + "and [Dimension].[Test].[name1] in ('var1','withoutspaces')") + "and [Dimension].[Test].[name1] in ('one value')") + "and [Dimension].[Test].[name2] in (1,3,4);");
        UniverseQuery universeQuery = universeUtil.convertQuery(request, universeClient, null);
        Assert.assertNotNull(universeQuery);
        Assert.assertEquals(("<and>\n" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("<and>\n" + "<and>\n") + "<and>\n") + "<and>\n") + "<and>\n") + "<and>\n") + "<and>\n") + "<and>\n") + "<predefinedFilter path=\"Filter|folder\\name3|filter\" id=\"name3id\"/>\n\n") + "<comparisonFilter path=\"Dimension|folder\\Test|folder\\name2|dimension\"") + " operator=\"GreaterThanOrEqualTo\" id=\"name2id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"Numeric\">1</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</and>\n\n") + "<comparisonFilter path=\"Dimension|folder\\Test|folder\\name2|dimension\"") + " operator=\"LessThan\" id=\"name2id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"Numeric\">20</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</and>\n\n") + "<comparisonFilter path=\"Dimension|folder\\Test|folder\\name1|dimension\"") + " operator=\"NotEqualTo\" id=\"name1id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"String\">test</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</and>\n\n") + "<comparisonFilter id=\"name1id\" path=\"Dimension|folder\\Test|folder\\name1|dimension\"") + " operator=\"IsNotNull\"/>\n\n") + "</and>\n\n") + "<comparisonFilter id=\"name5id\" path=\"Measure|folder\\name5|measure\" operator=\"IsNull\"/>\n\n") + "</and>\n\n") + "<comparisonFilter path=\"Dimension|folder\\Test|folder\\name1|dimension\"") + " operator=\"InList\" id=\"name1id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"String\">var1</caption>\n") + "</value>\n") + "<value>\n") + "<caption type=\"String\">v a r 2</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</and>\n\n") + "<comparisonFilter path=\"Dimension|folder\\Test|folder\\name1|dimension\"") + " operator=\"InList\" id=\"name1id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"String\">var1</caption>\n") + "</value>\n") + "<value>\n") + "<caption type=\"String\">withoutspaces</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</and>\n\n") + "<comparisonFilter path=\"Dimension|folder\\Test|folder\\name1|dimension\"") + " operator=\"InList\" id=\"name1id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"String\">one value</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</and>\n\n") + "<comparisonFilter path=\"Dimension|folder\\Test|folder\\name2|dimension\"") + " operator=\"InList\" id=\"name2id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"Numeric\">1</caption>\n") + "</value>\n") + "<value>\n") + "<caption type=\"Numeric\">3</caption>\n") + "</value>\n") + "<value>\n") + "<caption type=\"Numeric\">4</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</and>\n")), universeQuery.getWhere());
    }

    @Test(expected = UniverseException.class)
    public void testFailConvertWithoutUniverse() throws UniverseException {
        String request = "universe ;\n" + ("select [Measure].[name5]\n" + "where [Filter].[name3] and [Dimension].[Test].[name2] > 1;");
        universeUtil.convertQuery(request, universeClient, null);
    }

    @Test(expected = UniverseException.class)
    public void testFailConvertWithIncorrectSelect() throws UniverseException {
        String request = "universe [testUniverse];\n" + "select [not].[exist];";
        universeUtil.convertQuery(request, universeClient, null);
    }

    @Test(expected = UniverseException.class)
    public void testFailConvertWithIncorrectCondition() throws UniverseException {
        String request = "universe [testUniverse];\n" + ("select [Measure].[name5]\n" + "where [Filter].[name;");
        universeUtil.convertQuery(request, universeClient, null);
    }

    @Test
    public void testFiltersConditions() throws UniverseException {
        String request1 = "universe [testUniverse];\n" + ("select [Measure].[name5]\n" + "where [Filter].[name3];");
        String request2 = "universe [testUniverse];\n" + ("select [Measure].[name5]\n" + "where [Measure].[name5] > 2 and [Filter].[name3];");
        String request3 = "universe [testUniverse];\n" + ("select [Measure].[name5]\n" + "where [Filter].[name3] or [Measure].[name5];");
        String request4 = "universe [testUniverse];\n" + ("select [Measure].[name5]\n" + "where [Filter].[name3] and [Measure].[name5] is null;");
        UniverseQuery universeQuery = universeUtil.convertQuery(request1, universeClient, null);
        Assert.assertEquals("<predefinedFilter path=\"Filter|folder\\name3|filter\" id=\"name3id\"/>\n", universeQuery.getWhere());
        universeQuery = universeUtil.convertQuery(request2, universeClient, null);
        Assert.assertEquals(("<and>\n" + (((((((("<comparisonFilter path=\"Measure|folder\\name5|measure\" operator=\"GreaterThan\" id=\"name5id\">\n" + "<constantOperand>\n") + "<value>\n") + "<caption type=\"Numeric\">2</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "<predefinedFilter path=\"Filter|folder\\name3|filter\" id=\"name3id\"/>\n\n") + "</and>\n")), universeQuery.getWhere());
        universeQuery = universeUtil.convertQuery(request3, universeClient, null);
        Assert.assertEquals(("<or>\n" + (("<predefinedFilter path=\"Filter|folder\\name3|filter\" id=\"name3id\"/>\n\n" + "<predefinedFilter path=\"Measure|folder\\name5|measure\" id=\"name5id\"/>\n\n") + "</or>\n")), universeQuery.getWhere());
        universeQuery = universeUtil.convertQuery(request4, universeClient, null);
        Assert.assertEquals(("<and>\n" + (("<predefinedFilter path=\"Filter|folder\\name3|filter\" id=\"name3id\"/>\n\n" + "<comparisonFilter id=\"name5id\" path=\"Measure|folder\\name5|measure\" operator=\"IsNull\"/>\n\n") + "</and>\n")), universeQuery.getWhere());
    }

    @Test
    public void testNestedConditions() throws UniverseException {
        String request = "universe [testUniverse];\n" + ((("select [Dimension].[Test].[name2]\n" + "where ([Measure].[name5] = \'text\' or ([Dimension].[Test].[name1] in (\'1\',\'2\', \'3\') and\n") + "[Dimension].[Test].[name2] is not null)) and ([Filter].[name4] or [Measure].[name5] >=12)\n") + "or [Dimension].[Test].[name2] not in (31, 65, 77);");
        UniverseQuery universeQuery = universeUtil.convertQuery(request, universeClient, null);
        Assert.assertEquals(("<or>\n" + (((((((((((((((((((((((((((((((((((((((((((((((((("<and>\n" + "<or>\n") + "<comparisonFilter path=\"Measure|folder\\name5|measure\" operator=\"EqualTo\" id=\"name5id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"String\">text</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "<and>\n") + "<comparisonFilter path=\"Dimension|folder\\Test|folder\\name1|dimension\" operator=\"InList\" id=\"name1id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"String\">1</caption>\n") + "</value>\n") + "<value>\n") + "<caption type=\"String\">2</caption>\n") + "</value>\n") + "<value>\n") + "<caption type=\"String\">3</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "<comparisonFilter id=\"name2id\" path=\"Dimension|folder\\Test|folder\\name2|dimension\" operator=\"IsNotNull\"/>\n\n") + "</and>\n\n") + "</or>\n\n") + "<or>\n") + "<predefinedFilter path=\"Filter|folder\\name4|filter\" id=\"name4id\"/>\n\n") + "<comparisonFilter path=\"Measure|folder\\name5|measure\" operator=\"GreaterThanOrEqualTo\" id=\"name5id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"Numeric\">12</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</or>\n\n") + "</and>\n\n") + "<comparisonFilter path=\"Dimension|folder\\Test|folder\\name2|dimension\" operator=\"NotInList\" id=\"name2id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"Numeric\">31</caption>\n") + "</value>\n") + "<value>\n") + "<caption type=\"Numeric\">65</caption>\n") + "</value>\n") + "<value>\n") + "<caption type=\"Numeric\">77</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "</or>\n")), universeQuery.getWhere());
    }

    @Test
    public void testWithoutConditions() throws UniverseException {
        String request = "universe [testUniverse];\n" + ("select [Dimension].[Test].[name2], [Measure].[name5],\n" + "[Dimension].[Test].[name1] ;");
        UniverseQuery universeQuery = universeUtil.convertQuery(request, universeClient, null);
        Assert.assertNull(universeQuery.getWhere());
        Assert.assertEquals(("<resultObjects>\n" + ((("<resultObject path=\"Dimension|folder\\Test|folder\\name2|dimension\" id=\"name2id\"/>\n" + "<resultObject path=\"Measure|folder\\name5|measure\" id=\"name5id\"/>\n") + "<resultObject path=\"Dimension|folder\\Test|folder\\name1|dimension\" id=\"name1id\"/>\n") + "</resultObjects>")), universeQuery.getSelect());
    }

    @Test
    public void testCaseSensitive() throws UniverseException {
        String request = "uniVersE [testUniverse];\n" + ("seLEct [Dimension].[Test].[name2], [Measure].[name5]\n" + "whERE [Dimension].[Test].[name2] Is NULl Or [Measure].[name5] IN (1,2) aNd [Measure].[name5] is NOT nUll;");
        UniverseQuery universeQuery = universeUtil.convertQuery(request, universeClient, null);
        Assert.assertEquals(("<resultObjects>\n" + (("<resultObject path=\"Dimension|folder\\Test|folder\\name2|dimension\" id=\"name2id\"/>\n" + "<resultObject path=\"Measure|folder\\name5|measure\" id=\"name5id\"/>\n") + "</resultObjects>")), universeQuery.getSelect());
        Assert.assertEquals(("<or>\n" + (((((((((((((("<comparisonFilter id=\"name2id\" path=\"Dimension|folder\\Test|folder\\name2|dimension\" operator=\"IsNull\"/>\n\n" + "<and>\n") + "<comparisonFilter path=\"Measure|folder\\name5|measure\" operator=\"InList\" id=\"name5id\">\n") + "<constantOperand>\n") + "<value>\n") + "<caption type=\"Numeric\">1</caption>\n") + "</value>\n") + "<value>\n") + "<caption type=\"Numeric\">2</caption>\n") + "</value>\n") + "</constantOperand>\n") + "</comparisonFilter>\n\n") + "<comparisonFilter id=\"name5id\" path=\"Measure|folder\\name5|measure\" operator=\"IsNotNull\"/>\n\n") + "</and>\n\n") + "</or>\n")), universeQuery.getWhere());
    }
}

