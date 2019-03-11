package com.orientechnologies.orient.core.sql.parser;


import com.orientechnologies.orient.core.command.OBasicCommandContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class OSelectStatementTest {
    @Test
    public void testParserSimpleSelect1() {
        SimpleNode stm = checkRightSyntax("select from Foo");
        Assert.assertTrue((stm instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (stm));
        Assert.assertTrue(((select.getProjection()) == null));
        Assert.assertTrue(((select.getTarget()) != null));
        Assert.assertTrue((!(Boolean.TRUE.equals(select.getLockRecord()))));
        Assert.assertTrue(((select.getWhereClause()) == null));
    }

    @Test
    public void testParserSimpleSelect2() {
        SimpleNode stm = checkRightSyntax("select bar from Foo");
        Assert.assertTrue((stm instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (stm));
        Assert.assertTrue(((select.getProjection()) != null));
        Assert.assertTrue(((select.getProjection().getItems()) != null));
        Assert.assertEquals(select.getProjection().getItems().size(), 1);
        Assert.assertTrue(((select.getTarget()) != null));
        Assert.assertTrue((!(Boolean.TRUE.equals(select.getLockRecord()))));
        Assert.assertTrue(((select.getWhereClause()) == null));
    }

    @Test
    public void testComments() {
        checkRightSyntax("select from Foo");
        checkRightSyntax("select /* aaa bbb ccc*/from Foo");
        checkRightSyntax("select /* aaa bbb \nccc*/from Foo");
        checkRightSyntax("select /** aaa bbb ccc**/from Foo");
        checkRightSyntax("select /** aaa bbb ccc*/from Foo");
        checkRightSyntax("/* aaa bbb ccc*/select from Foo");
        checkRightSyntax("select from Foo/* aaa bbb ccc*/");
        checkRightSyntax("/* aaa bbb ccc*/select from Foo/* aaa bbb ccc*/");
        checkWrongSyntax("select /** aaa bbb */ccc*/from Foo");
        checkWrongSyntax("select /**  /*aaa bbb */ccc*/from Foo");
        checkWrongSyntax("*/ select from Foo");
    }

    @Test
    public void testSimpleSelect() {
        checkRightSyntax("select from Foo");
        checkRightSyntax("select * from Foo");
        checkWrongSyntax("select from Foo bar");
        checkWrongSyntax("select * from Foo bar");
        checkWrongSyntax("select * Foo");
    }

    @Test
    public void testUnwind() {
        checkRightSyntax("select from Foo unwind foo");
        checkRightSyntax("select from Foo unwind foo, bar");
        checkRightSyntax("select from Foo where foo = 1 unwind foo, bar");
        checkRightSyntax("select from Foo where foo = 1 order by foo unwind foo, bar");
        checkRightSyntax("select from Foo where foo = 1 group by bar order by foo unwind foo, bar");
    }

    @Test
    public void testSubSelect() {
        checkRightSyntax("select from (select from Foo)");
        checkWrongSyntax("select from select from foo");
    }

    @Test
    public void testSimpleSelectWhere() {
        checkRightSyntax("select from Foo where name = 'foo'");
        checkRightSyntax("select * from Foo where name = 'foo'");
        checkRightSyntax("select from Foo where name = \'foo\' and surname = \"bar\"");
        checkRightSyntax("select * from Foo where name = \'foo\' and surname = \"bar\"");
        checkWrongSyntax("select * from Foo name = 'foo'");
        checkWrongSyntax("select from Foo bar where name = 'foo'");
        checkWrongSyntax("select * from Foo bar where name = 'foo'");
        checkWrongSyntax("select Foo where name = 'foo'");
        checkWrongSyntax("select * Foo where name = 'foo'");
        // issue #5221
        checkRightSyntax("select from $1");
    }

    @Test
    public void testIn() {
        SimpleNode result = checkRightSyntax("select count(*) from OFunction where name in [\"a\"]");
        // result.dump("    ");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testNotIn() {
        SimpleNode result = checkRightSyntax("select count(*) from OFunction where name not in [\"a\"]");
        // result.dump("    ");
        Assert.assertTrue((result instanceof OStatement));
        OStatement stm = ((OStatement) (result));
    }

    @Test
    public void testMath1() {
        SimpleNode result = checkRightSyntax(("" + "select * from sqlSelectIndexReuseTestClass where prop1 = 1 + 1"));
        // result.dump("    ");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testIndex1() {
        SimpleNode result = checkRightSyntax("select from index:collateCompositeIndexCS where key = ['VAL', 'VaL']");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testIndex2() {
        SimpleNode result = checkRightSyntax("select from index:collateCompositeIndexCS where key between ['VAL', 'VaL'] and ['zz', 'zz']");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testFetchPlan1() {
        SimpleNode result = checkRightSyntax(("" + "select 'Ay' as a , 'bEE' as b from Foo fetchplan *:1"));
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testFetchPlan2() {
        SimpleNode result = checkRightSyntax(("" + "select 'Ay' as a , 'bEE' as b fetchplan *:1"));
        Assert.assertTrue((result instanceof OSelectWithoutTargetStatement));
        OSelectWithoutTargetStatement select = ((OSelectWithoutTargetStatement) (result));
    }

    @Test
    public void testContainsWithCondition() {
        SimpleNode result = checkRightSyntax("select from Profile where customReferences.values() CONTAINS 'a'");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testNamedParam() {
        SimpleNode result = checkRightSyntax("select from JavaComplexTestClass where enumField = :enumItem");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testBoolean() {
        SimpleNode result = checkRightSyntax("select from Foo where bar = true");
        // result.dump("    ");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testDottedAtField() {
        SimpleNode result = checkRightSyntax("select from City where country.@class = 'Country'");
        // result.dump("    ");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testQuotedFieldNameFrom() {
        SimpleNode result = checkRightSyntax("select `from` from City where country.@class = 'Country'");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testQuotedTargetName() {
        checkRightSyntax("select from `edge`");
        checkRightSyntax("select from `from`");
        checkRightSyntax("select from `vertex`");
        checkRightSyntax("select from `select`");
    }

    @Test
    public void testQuotedFieldName() {
        checkRightSyntax("select `foo` from City where country.@class = 'Country'");
    }

    @Test
    public void testLongDotted() {
        SimpleNode result = checkRightSyntax("select from Profile where location.city.country.name = 'Spain'");
        // result.dump("    ");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testInIsNotAReservedWord() {
        SimpleNode result = checkRightSyntax("select count(*) from TRVertex where in.type() not in [\"LINKSET\"] ");
        // result.dump("    ");
        Assert.assertTrue((result instanceof OSelectStatement));
        OSelectStatement select = ((OSelectStatement) (result));
    }

    @Test
    public void testSelectFunction() {
        SimpleNode result = checkRightSyntax("select max(1,2,7,0,-2,3), 'pluto'");
        // result.dump("    ");
        Assert.assertTrue((result instanceof OSelectWithoutTargetStatement));
        OSelectWithoutTargetStatement select = ((OSelectWithoutTargetStatement) (result));
    }

    @Test
    public void testEscape1() {
        SimpleNode result = checkRightSyntax("select from cluster:internal where \"\\u005C\\u005C\" = \"\\u005C\\u005C\" ");
        Assert.assertTrue((result instanceof OSelectStatement));
    }

    @Test
    public void testWildcardSuffix() {
        checkRightSyntax("select foo.* from bar ");
    }

    @Test
    public void testEmptyCollection() {
        String query = "select from bar where name not in :param1";
        OrientSql osql = getParserFor(query);
        try {
            SimpleNode result = osql.parse();
            OSelectStatement stm = ((OSelectStatement) (result));
            Map<Object, Object> params = new HashMap<Object, Object>();
            params.put("param1", new HashSet<Object>());
            StringBuilder parsed = new StringBuilder();
            stm.toString(params, parsed);
            Assert.assertEquals(parsed.toString(), "SELECT FROM bar WHERE name NOT IN []");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testEscape2() {
        try {
            SimpleNode result = checkWrongSyntax("select from cluster:internal where \"\\u005C\" = \"\\u005C\" ");
            Assert.fail();
        } catch (Error e) {
        }
    }

    @Test
    public void testSpatial() {
        checkRightSyntax("select *,$distance from Place where [latitude,longitude,$spatial] NEAR [41.893056,12.482778,{\"maxDistance\": 0.5}]");
        checkRightSyntax("select * from Place where [latitude,longitude] WITHIN [[51.507222,-0.1275],[55.507222,-0.1275]]");
    }

    @Test
    public void testSubConditions() {
        checkRightSyntax("SELECT @rid as rid, localName FROM Person WHERE ( 'milano' IN out('lives').localName OR 'roma' IN out('lives').localName ) ORDER BY age ASC");
    }

    @Test
    public void testRecordAttributes() {
        // issue #4430
        checkRightSyntax("SELECT @this, @rid, @rid_id, @rid_pos, @version, @class, @type, @size, @fields, @raw from V");
        checkRightSyntax("SELECT @THIS, @RID, @RID_ID, @RID_POS, @VERSION, @CLASS, @TYPE, @SIZE, @FIELDS, @RAW from V");
    }

    @Test
    public void testDoubleEquals() {
        // issue #4413
        checkRightSyntax("SELECT from V where name = 'foo'");
        checkRightSyntax("SELECT from V where name == 'foo'");
    }

    @Test
    public void testMatches() {
        checkRightSyntax("select from Person where name matches 'a'");
        checkRightSyntax("select from Person where name matches \'(?i)(^\\\\Qname1\\\\E$)|(^\\\\Qname2\\\\E$)|(^\\\\Qname3\\\\E$)\' and age=30");
    }

    // issue #3718
    @Test
    public void testComplexTarget1() {
        checkRightSyntax("SELECT $e FROM [#1:1,#1:2] LET $e = (SELECT FROM $current.prop1)");
        checkRightSyntax("SELECT $e FROM [#1:1,#1:2] let $e = (SELECT FROM (SELECT FROM $parent.$current))");
    }

    @Test
    public void testEval() {
        checkRightSyntax(("  select  sum(weight) , f.name as name from (\n" + ((("      select weight, if(eval(\"out.name = \'one\'\"),out,in) as f  from (\n" + "      select expand(bothE(\'E\')) from V\n") + "  )\n") + "      ) group by name\n")));
    }

    @Test
    public void testNewLine() {
        checkRightSyntax("INSERT INTO Country SET name=\"one\\ntwo\" RETURN @rid");
    }

    @Test
    public void testJsonWithUrl() {
        checkRightSyntax("insert into V content { \"url\": \"http://www.google.com\" } ");
    }

    @Test
    public void testGroupBy() {
        // issue #4245
        checkRightSyntax(("select in.name from (  \n" + (("select expand(outE()) from V\n" + ")\n") + "group by in.name")));
    }

    @Test
    public void testInputParams() {
        checkRightSyntax("select from foo where name like  '%'+ :param1 + '%'");
        checkRightSyntax("select from foo where name like  'aaa'+ :param1 + 'a'");
    }

    @Test
    public void testClusterList() {
        checkRightSyntax("select from cluster:[foo,bar]");
    }

    @Test
    public void checkOrderBySyntax() {
        checkRightSyntax("select from test order by something ");
        checkRightSyntax("select from test order by something, somethingElse ");
        checkRightSyntax("select from test order by something asc, somethingElse desc");
        checkRightSyntax("select from test order by something asc, somethingElse ");
        checkRightSyntax("select from test order by something, somethingElse asc");
        checkRightSyntax("select from test order by something asc");
        checkRightSyntax("select from test order by something desc");
        checkRightSyntax("select from test order by (something desc)");
        checkRightSyntax("select from test order by (something asc)");
        checkRightSyntax("select from test order by (something asc),somethingElse");
        checkRightSyntax("select from test order by (something),(somethingElse)");
        checkRightSyntax("select from test order by something,(somethingElse)");
        checkRightSyntax("select from test order by (something asc),(somethingElse desc)");
    }

    @Test
    public void testReturn() {
        checkRightSyntax("select from ouser timeout 1 exception");
        checkRightSyntax("select from ouser timeout 1 return");
    }

    @Test
    public void testFlatten() {
        OSelectStatement stm = ((OSelectStatement) (checkRightSyntax("select from ouser where name = 'foo'")));
        List<OAndBlock> flattended = stm.whereClause.flatten();
        Assert.assertTrue(((OBinaryCondition) (flattended.get(0).subBlocks.get(0))).left.isBaseIdentifier());
        Assert.assertFalse(((OBinaryCondition) (flattended.get(0).subBlocks.get(0))).right.isBaseIdentifier());
        Assert.assertFalse(((OBinaryCondition) (flattended.get(0).subBlocks.get(0))).left.isEarlyCalculated(new OBasicCommandContext()));
        Assert.assertTrue(((OBinaryCondition) (flattended.get(0).subBlocks.get(0))).right.isEarlyCalculated(new OBasicCommandContext()));
    }

    @Test
    public void testParamWithMatches() {
        // issue #5229
        checkRightSyntax("select from Person where name matches :param1");
    }

    @Test
    public void testInstanceOfE() {
        // issue #5212
        checkRightSyntax("select from Friend where @class instanceof 'E'");
    }

    @Test
    public void testSelectFromClusterNumber() {
        checkRightSyntax("select from cluster:12");
    }

    @Test
    public void testReservedWordsAsNamedParams() {
        // issue #5493
        checkRightSyntax("select from V limit :limit");
    }

    @Test
    public void testFetchPlanBracketStar() {
        // issue #5689
        checkRightSyntax("SELECT FROM Def fetchplan *:2 [*]in_*:-2");
        checkRightSyntax("SELECT FROM Def fetchplan *:2 [1]in_*:-2");
    }

    @Test
    public void testJsonQuoting() {
        // issue #5911
        checkRightSyntax("SELECT \'\\/\\/\'");
        checkRightSyntax("SELECT \"\\/\\/\"");
    }

    @Test
    public void testSkipLimitInQueryWithNoTarget() {
        // issue #5589
        checkRightSyntax(("SELECT eval(\'$TotalListsQuery[0].Count\') AS TotalLists\n" + ("   LET $TotalListsQuery = ( SELECT Count(1) AS Count FROM ContactList WHERE Account=#20:1 AND EntityInfo.State=0)\n" + " LIMIT 1")));
        checkRightSyntax(("SELECT eval(\'$TotalListsQuery[0].Count\') AS TotalLists\n" + ("   LET $TotalListsQuery = ( SELECT Count(1) AS Count FROM ContactList WHERE Account=#20:1 AND EntityInfo.State=0)\n" + " SKIP 10 LIMIT 1")));
    }

    @Test
    public void testQuotedBacktick() {
        checkRightSyntax("SELECT \"\" as `bla\\`bla` from foo");
    }

    @Test
    public void testParamConcat() {
        // issue #6049
        checkRightSyntax("Select * From ACNodeAuthentication where acNodeID like ? ");
        checkRightSyntax("Select * From ACNodeAuthentication where acNodeID like ? + '%'");
        checkRightSyntax("Select * From ACNodeAuthentication where acNodeID like \"%\" + ? + \'%\'");
    }

    @Test
    public void testAppendParams() {
        checkRightSyntax("select from User where Account.Name like :name + '%'");
    }

    @Test
    public void testLetMatch() {
        checkRightSyntax("select $a let $a = (MATCH {class:Foo, as:bar, where:(name = 'foo')} return $elements)");
    }

    @Test
    public void testDistinct() {
        checkRightSyntax("select distinct(foo) from V");
        checkRightSyntax("select distinct foo, bar, baz from V");
    }

    @Test
    public void testRange() {
        checkRightSyntax("select foo[1..5] from V");
        checkRightSyntax("select foo[1...5] from V");
        checkRightSyntax("select foo[?..?] from V");
        checkRightSyntax("select foo[?...?] from V");
        checkRightSyntax("select foo[:a..:b] from V");
        checkRightSyntax("select foo[:a...:b] from V");
        checkWrongSyntax("select foo[1....5] from V");
    }

    @Test
    public void testRidString() {
        checkRightSyntax("select \"@rid\" as v from V");
        SimpleNode stm2 = checkRightSyntax("select {\"@rid\": \"#12:0\"} as v from V");
        System.out.println(stm2);
    }

    @Test
    public void testTranslateLucene() {
        OSelectStatement stm = ((OSelectStatement) (checkRightSyntax("select from V where name LUCENE 'foo'")));
        stm.whereClause.getBaseExpression().translateLuceneOperator();
        Assert.assertTrue(stm.whereClause.toString().contains("search_fields([\"name\"], \'foo\') = true"));
        Assert.assertFalse(stm.whereClause.toString().contains("LUCENE"));
    }

    @Test
    public void testFromAsNamedParam() {
        checkRightSyntax("select from V where fromDate = :from");
    }

    @Test
    public void testNestedProjections() {
        checkRightSyntax("select foo:{*} from V");
        checkRightSyntax("select foo:{name, surname, address:{*}} from V");
        checkRightSyntax("select foo:{!name} from V");
        checkRightSyntax("select foo:{!out_*} from V");
        checkRightSyntax("select foo:{!out_*, !in_*} from V");
        checkRightSyntax("select foo:{*, !out_*, !in_*} from V");
    }

    @Test
    public void testCollectionFilteringByValue() {
        checkRightSyntax("select foo[= 'foo'] from V");
        checkRightSyntax("select foo[like '%foo'] from V");
        checkRightSyntax("select foo[> 2] from V");
        checkRightSyntax("select foo[> 2][< 5] from V");
        checkRightSyntax("select foo[ IN ['a', 'b']] from V");
        checkRightSyntax("select bar[IN (select from foo) ] from V");
        checkRightSyntax("select bar[IN $a ] from V LET $a = (SELECT FROM V)");
        checkRightSyntax("select foo[not IN ['a', 'b']] from V");
        checkRightSyntax("select bar[not IN (select from foo) ] from V");
        checkRightSyntax("select bar[not IN $a ] from V LET $a = (SELECT FROM V)");
    }

    @Test
    public void testLockRecord() {
        checkRightSyntax("select from V LOCK RECORD");
        checkRightSyntax("select from V LOCK NONE");
        checkRightSyntax("select from V LOCK DEFAULT");
        checkRightSyntax("select from V LOCK SHARED");
        checkWrongSyntax("select from V LOCK RECORD FOO");
        checkWrongSyntax("select from V LOCK FOO");
    }

    @Test
    public void testContainsAny() {
        checkRightSyntax("select from V WHERE foo containsany ['foo', 'bar']");
        checkRightSyntax("select from V WHERE foo CONTAINSANY ['foo', 'bar']");
        checkWrongSyntax("select from V WHERE foo CONTAINSANY ");
    }

    @Test
    public void testOrderByCollate() {
        checkRightSyntax("select from V order by foo asc collate ci");
        checkRightSyntax("select from V order by foo asc collate ci, bar desc collate ci");
        checkRightSyntax("select from V order by foo collate ci, bar collate ci");
        checkWrongSyntax("select from V order by foo collate ");
        checkWrongSyntax("select from V order by foo asc collate ");
    }
}

