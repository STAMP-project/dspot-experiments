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
package org.apache.calcite.sql.test;


import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import org.apache.calcite.config.Lex;
import org.apache.calcite.test.SqlValidatorTestCase;
import org.apache.calcite.test.WithLex;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;


/**
 * Concrete child class of {@link SqlValidatorTestCase}, containing unit tests
 * for SqlAdvisor.
 */
public class SqlAdvisorTest extends SqlValidatorTestCase {
    public static final SqlTestFactory ADVISOR_TEST_FACTORY = SqlTestFactory.INSTANCE.withValidator(SqlAdvisorValidator::new);

    @Rule
    public MethodRule configureTester = SqlValidatorTestCase.TESTER_CONFIGURATION_RULE;

    // ~ Static fields/initializers ---------------------------------------------
    private static final List<String> STAR_KEYWORD = Arrays.asList("KEYWORD(*)");

    protected static final List<String> FROM_KEYWORDS = Arrays.asList("KEYWORD(()", "KEYWORD(LATERAL)", "KEYWORD(TABLE)", "KEYWORD(UNNEST)");

    protected static final List<String> SALES_TABLES = Arrays.asList("SCHEMA(CATALOG.SALES)", "SCHEMA(CATALOG.SALES.NEST)", "TABLE(CATALOG.SALES.EMP)", "TABLE(CATALOG.SALES.EMPDEFAULTS)", "TABLE(CATALOG.SALES.EMPNULLABLES)", "TABLE(CATALOG.SALES.EMP_B)", "TABLE(CATALOG.SALES.EMP_20)", "TABLE(CATALOG.SALES.EMPNULLABLES_20)", "TABLE(CATALOG.SALES.EMP_ADDRESS)", "TABLE(CATALOG.SALES.DEPT)", "TABLE(CATALOG.SALES.DEPT_NESTED)", "TABLE(CATALOG.SALES.BONUS)", "TABLE(CATALOG.SALES.ORDERS)", "TABLE(CATALOG.SALES.SALGRADE)", "TABLE(CATALOG.SALES.SHIPMENTS)", "TABLE(CATALOG.SALES.PRODUCTS)", "TABLE(CATALOG.SALES.PRODUCTS_TEMPORAL)", "TABLE(CATALOG.SALES.SUPPLIERS)", "TABLE(CATALOG.SALES.EMP_R)", "TABLE(CATALOG.SALES.DEPT_R)");

    private static final List<String> SCHEMAS = Arrays.asList("CATALOG(CATALOG)", "SCHEMA(CATALOG.SALES)", "SCHEMA(CATALOG.STRUCT)", "SCHEMA(CATALOG.CUSTOMER)", "SCHEMA(CATALOG.SALES.NEST)");

    private static final List<String> AB_TABLES = Arrays.asList("TABLE(A)", "TABLE(B)");

    private static final List<String> EMP_TABLE = Arrays.asList("TABLE(EMP)");

    protected static final List<String> FETCH_OFFSET = Arrays.asList("KEYWORD(FETCH)", "KEYWORD(LIMIT)", "KEYWORD(OFFSET)");

    protected static final List<String> EXPR_KEYWORDS = Arrays.asList("KEYWORD(()", "KEYWORD(+)", "KEYWORD(-)", "KEYWORD(?)", "KEYWORD(ABS)", "KEYWORD(ARRAY)", "KEYWORD(AVG)", "KEYWORD(CARDINALITY)", "KEYWORD(CASE)", "KEYWORD(CAST)", "KEYWORD(CEIL)", "KEYWORD(CEILING)", "KEYWORD(CHARACTER_LENGTH)", "KEYWORD(CHAR_LENGTH)", "KEYWORD(CLASSIFIER)", "KEYWORD(COALESCE)", "KEYWORD(COLLECT)", "KEYWORD(CONVERT)", "KEYWORD(COUNT)", "KEYWORD(COVAR_POP)", "KEYWORD(COVAR_SAMP)", "KEYWORD(CUME_DIST)", "KEYWORD(CURRENT)", "KEYWORD(CURRENT_CATALOG)", "KEYWORD(CURRENT_DATE)", "KEYWORD(CURRENT_DEFAULT_TRANSFORM_GROUP)", "KEYWORD(CURRENT_PATH)", "KEYWORD(CURRENT_ROLE)", "KEYWORD(CURRENT_SCHEMA)", "KEYWORD(CURRENT_TIME)", "KEYWORD(CURRENT_TIMESTAMP)", "KEYWORD(CURRENT_USER)", "KEYWORD(CURSOR)", "KEYWORD(DATE)", "KEYWORD(DENSE_RANK)", "KEYWORD(ELEMENT)", "KEYWORD(EXISTS)", "KEYWORD(EXP)", "KEYWORD(EXTRACT)", "KEYWORD(FALSE)", "KEYWORD(FIRST_VALUE)", "KEYWORD(FLOOR)", "KEYWORD(FUSION)", "KEYWORD(GROUPING)", "KEYWORD(HOUR)", "KEYWORD(INTERVAL)", "KEYWORD(JSON_ARRAY)", "KEYWORD(JSON_ARRAYAGG)", "KEYWORD(JSON_EXISTS)", "KEYWORD(JSON_OBJECT)", "KEYWORD(JSON_OBJECTAGG)", "KEYWORD(JSON_QUERY)", "KEYWORD(JSON_VALUE)", "KEYWORD(LAG)", "KEYWORD(LAST_VALUE)", "KEYWORD(LEAD)", "KEYWORD(LN)", "KEYWORD(LOCALTIME)", "KEYWORD(LOCALTIMESTAMP)", "KEYWORD(LOWER)", "KEYWORD(MATCH_NUMBER)", "KEYWORD(MAX)", "KEYWORD(MIN)", "KEYWORD(MINUTE)", "KEYWORD(MOD)", "KEYWORD(MONTH)", "KEYWORD(MULTISET)", "KEYWORD(NEW)", "KEYWORD(NEXT)", "KEYWORD(NOT)", "KEYWORD(NTH_VALUE)", "KEYWORD(NTILE)", "KEYWORD(NULL)", "KEYWORD(NULLIF)", "KEYWORD(OCTET_LENGTH)", "KEYWORD(OVERLAY)", "KEYWORD(PERCENT_RANK)", "KEYWORD(PERIOD)", "KEYWORD(POSITION)", "KEYWORD(POWER)", "KEYWORD(PREV)", "KEYWORD(RANK)", "KEYWORD(REGR_COUNT)", "KEYWORD(REGR_SXX)", "KEYWORD(REGR_SYY)", "KEYWORD(ROW)", "KEYWORD(ROW_NUMBER)", "KEYWORD(RUNNING)", "KEYWORD(SECOND)", "KEYWORD(SESSION_USER)", "KEYWORD(SPECIFIC)", "KEYWORD(SQRT)", "KEYWORD(SUBSTRING)", "KEYWORD(STDDEV_POP)", "KEYWORD(STDDEV_SAMP)", "KEYWORD(SUM)", "KEYWORD(SYSTEM_USER)", "KEYWORD(TIME)", "KEYWORD(TIMESTAMP)", "KEYWORD(TRANSLATE)", "KEYWORD(TRIM)", "KEYWORD(TRUE)", "KEYWORD(TRUNCATE)", "KEYWORD(UNKNOWN)", "KEYWORD(UPPER)", "KEYWORD(USER)", "KEYWORD(VAR_POP)", "KEYWORD(VAR_SAMP)", "KEYWORD(YEAR)");

    protected static final List<String> QUANTIFIERS = Arrays.asList("KEYWORD(ALL)", "KEYWORD(ANY)", "KEYWORD(SOME)");

    protected static final List<String> SELECT_KEYWORDS = Arrays.asList("KEYWORD(ALL)", "KEYWORD(DISTINCT)", "KEYWORD(STREAM)", "KEYWORD(*)");

    private static final List<String> ORDER_KEYWORDS = Arrays.asList("KEYWORD(,)", "KEYWORD(ASC)", "KEYWORD(DESC)", "KEYWORD(NULLS)");

    private static final List<String> EMP_COLUMNS = Arrays.asList("COLUMN(EMPNO)", "COLUMN(ENAME)", "COLUMN(JOB)", "COLUMN(MGR)", "COLUMN(HIREDATE)", "COLUMN(SAL)", "COLUMN(COMM)", "COLUMN(DEPTNO)", "COLUMN(SLACKER)");

    private static final List<String> EMP_COLUMNS_E = Arrays.asList("COLUMN(EMPNO)", "COLUMN(ENAME)");

    private static final List<String> DEPT_COLUMNS = Arrays.asList("COLUMN(DEPTNO)", "COLUMN(NAME)");

    protected static final List<String> PREDICATE_KEYWORDS = Arrays.asList("KEYWORD(()", "KEYWORD(*)", "KEYWORD(+)", "KEYWORD(-)", "KEYWORD(.)", "KEYWORD(/)", "KEYWORD(%)", "KEYWORD(<)", "KEYWORD(<=)", "KEYWORD(<>)", "KEYWORD(!=)", "KEYWORD(=)", "KEYWORD(>)", "KEYWORD(>=)", "KEYWORD(AND)", "KEYWORD(BETWEEN)", "KEYWORD(CONTAINS)", "KEYWORD(EQUALS)", "KEYWORD(IMMEDIATELY)", "KEYWORD(IN)", "KEYWORD(IS)", "KEYWORD(LIKE)", "KEYWORD(MEMBER)", "KEYWORD(MULTISET)", "KEYWORD(NOT)", "KEYWORD(OR)", "KEYWORD(OVERLAPS)", "KEYWORD(PRECEDES)", "KEYWORD(SIMILAR)", "KEYWORD(SUBMULTISET)", "KEYWORD(SUCCEEDS)", "KEYWORD([)", "KEYWORD(||)");

    private static final List<String> WHERE_KEYWORDS = Arrays.asList("KEYWORD(EXCEPT)", "KEYWORD(MINUS)", "KEYWORD(FETCH)", "KEYWORD(OFFSET)", "KEYWORD(LIMIT)", "KEYWORD(GROUP)", "KEYWORD(HAVING)", "KEYWORD(INTERSECT)", "KEYWORD(ORDER)", "KEYWORD(UNION)", "KEYWORD(WINDOW)");

    private static final List<String> A_TABLE = Arrays.asList("TABLE(A)");

    protected static final List<String> JOIN_KEYWORDS = Arrays.asList("KEYWORD(FETCH)", "KEYWORD(FOR)", "KEYWORD(OFFSET)", "KEYWORD(LIMIT)", "KEYWORD(UNION)", "KEYWORD(FULL)", "KEYWORD(ORDER)", "KEYWORD(()", "KEYWORD(EXTEND)", "KEYWORD(AS)", "KEYWORD(USING)", "KEYWORD(OUTER)", "KEYWORD(RIGHT)", "KEYWORD(GROUP)", "KEYWORD(CROSS)", "KEYWORD(,)", "KEYWORD(NATURAL)", "KEYWORD(INNER)", "KEYWORD(HAVING)", "KEYWORD(LEFT)", "KEYWORD(EXCEPT)", "KEYWORD(MATCH_RECOGNIZE)", "KEYWORD(MINUS)", "KEYWORD(JOIN)", "KEYWORD(WINDOW)", "KEYWORD(.)", "KEYWORD(TABLESAMPLE)", "KEYWORD(ON)", "KEYWORD(INTERSECT)", "KEYWORD(WHERE)");

    private static final List<String> SETOPS = Arrays.asList("KEYWORD(EXCEPT)", "KEYWORD(MINUS)", "KEYWORD(INTERSECT)", "KEYWORD(ORDER)", "KEYWORD(UNION)");

    private static final String EMPNO_EMP = "COLUMN(EMPNO)\n" + "TABLE(EMP)\n";

    // ~ Constructors -----------------------------------------------------------
    public SqlAdvisorTest() {
        super();
    }

    @Test
    public void testFrom() throws Exception {
        String sql;
        sql = "select a.empno, b.deptno from ^dummy a, sales.dummy b";
        assertHint(sql, SqlAdvisorTest.SCHEMAS, getSalesTables(), getFromKeywords());// join

        sql = "select a.empno, b.deptno from ^";
        assertComplete(sql, SqlAdvisorTest.SCHEMAS, getSalesTables(), getFromKeywords());
        sql = "select a.empno, b.deptno from ^, sales.dummy b";
        assertComplete(sql, SqlAdvisorTest.SCHEMAS, getSalesTables(), getFromKeywords());
        sql = "select a.empno, b.deptno from ^a";
        assertComplete(sql, SqlAdvisorTest.SCHEMAS, getSalesTables(), getFromKeywords());
        sql = "select a.empno, b.deptno from dummy a, ^sales.dummy b";
        assertHint(sql, SqlAdvisorTest.SCHEMAS, getSalesTables(), getFromKeywords());// join

    }

    @Test
    public void testFromComplete() {
        String sql = "select a.empno, b.deptno from dummy a, sales.^";
        assertComplete(sql, getSalesTables());
    }

    @Test
    public void testGroup() {
        // This test is hard because the statement is not valid if you replace
        // '^' with a dummy identifier.
        String sql = "select a.empno, b.deptno from emp group ^";
        assertComplete(sql, Arrays.asList("KEYWORD(BY)"));
    }

    @Test
    public void testJoin() throws Exception {
        String sql;
        // from
        sql = "select a.empno, b.deptno from ^dummy a join sales.dummy b " + "on a.deptno=b.deptno where empno=1";
        assertHint(sql, getFromKeywords(), SqlAdvisorTest.SCHEMAS, getSalesTables());
        // from
        sql = "select a.empno, b.deptno from ^ a join sales.dummy b";
        assertComplete(sql, getFromKeywords(), SqlAdvisorTest.SCHEMAS, getSalesTables());
        // REVIEW: because caret is before 'sales', should it ignore schema
        // name and present all schemas and all tables in the default schema?
        // join
        sql = "select a.empno, b.deptno from dummy a join ^sales.dummy b " + "on a.deptno=b.deptno where empno=1";
        assertHint(sql, getFromKeywords(), SqlAdvisorTest.SCHEMAS, getSalesTables());
        sql = "select a.empno, b.deptno from dummy a join sales.^";
        assertComplete(sql, getSalesTables());// join

        sql = "select a.empno, b.deptno from dummy a join sales.^ on";
        assertComplete(sql, getSalesTables());// join

        // unfortunately cannot complete this case: syntax is too broken
        sql = "select a.empno, b.deptno from dummy a join sales.^ on a.deptno=";
        assertComplete(sql, SqlAdvisorTest.QUANTIFIERS, SqlAdvisorTest.EXPR_KEYWORDS);// join

    }

    @Test
    public void testJoinKeywords() {
        // variety of keywords possible
        List<String> list = getJoinKeywords();
        String sql = "select * from dummy join sales.emp ^";
        assertSimplify(sql, "SELECT * FROM dummy JOIN sales.emp _suggest_");
        assertComplete(sql, list);
    }

    @Test
    public void testSimplifyStarAlias() {
        String sql;
        sql = "select ax^ from (select * from dummy a)";
        assertSimplify(sql, "SELECT ax _suggest_ FROM ( SELECT * FROM dummy a )");
    }

    @Test
    public void testSimlifySubQueryStar() {
        String sql;
        sql = "select ax^ from (select (select * from dummy) axc from dummy a)";
        assertSimplify(sql, "SELECT ax _suggest_ FROM ( SELECT ( SELECT * FROM dummy ) axc FROM dummy a )");
        assertComplete(sql, "COLUMN(AXC)\n", "ax");
        sql = "select ax^ from (select a.x+0 axa, b.x axb, (select * from dummy) axbc from dummy a, dummy b)";
        assertSimplify(sql, "SELECT ax _suggest_ FROM ( SELECT a.x+0 axa , b.x axb , ( SELECT * FROM dummy ) axbc FROM dummy a , dummy b )");
        assertComplete(sql, "COLUMN(AXA)\nCOLUMN(AXB)\nCOLUMN(AXBC)\n", "ax");
        sql = "select ^ from (select * from dummy)";
        assertSimplify(sql, "SELECT _suggest_ FROM ( SELECT * FROM dummy )");
        sql = "select ^ from (select x.* from dummy x)";
        assertSimplify(sql, "SELECT _suggest_ FROM ( SELECT x.* FROM dummy x )");
        sql = "select ^ from (select a.x + b.y from dummy a, dummy b)";
        assertSimplify(sql, "SELECT _suggest_ FROM ( SELECT a.x + b.y FROM dummy a , dummy b )");
    }

    @Test
    public void testSimlifySubQueryMultipleFrom() {
        String sql;
        // "dummy b" should be removed
        sql = "select axc from (select (select ^ from dummy) axc from dummy a), dummy b";
        assertSimplify(sql, "SELECT * FROM ( SELECT ( SELECT _suggest_ FROM dummy ) axc FROM dummy a )");
        // "dummy b" should be removed
        sql = "select axc from dummy b, (select (select ^ from dummy) axc from dummy a)";
        assertSimplify(sql, "SELECT * FROM ( SELECT ( SELECT _suggest_ FROM dummy ) axc FROM dummy a )");
    }

    @Test
    public void testSimlifyMinus() {
        String sql;
        sql = "select ^ from dummy a minus select * from dummy b";
        assertSimplify(sql, "SELECT _suggest_ FROM dummy a");
        sql = "select * from dummy a minus select ^ from dummy b";
        assertSimplify(sql, "SELECT _suggest_ FROM dummy b");
    }

    @Test
    public void testOnCondition() throws Exception {
        String sql;
        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on ^a.deptno=b.dummy where empno=1";
        assertHint(sql, SqlAdvisorTest.AB_TABLES, SqlAdvisorTest.EXPR_KEYWORDS);// on left

        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.^";
        assertComplete(sql, SqlAdvisorTest.EMP_COLUMNS);// on left

        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.deptno=^b.dummy where empno=1";
        assertHint(sql, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.QUANTIFIERS, SqlAdvisorTest.AB_TABLES);// on right

        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.deptno=b.^ where empno=1";
        assertComplete(sql, SqlAdvisorTest.DEPT_COLUMNS);// on right

        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.deptno=b.^";
        assertComplete(sql, SqlAdvisorTest.DEPT_COLUMNS);// on right

    }

    @Test
    public void testFromWhere() throws Exception {
        String sql;
        sql = "select a.empno, b.deptno from sales.emp a, sales.dept b " + "where b.deptno=^a.dummy";
        assertHint(sql, SqlAdvisorTest.AB_TABLES, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.QUANTIFIERS);// where list

        sql = "select a.empno, b.deptno from sales.emp a, sales.dept b " + "where b.deptno=a.^";
        assertComplete(sql, ImmutableMap.of("COLUMN(COMM)", "COMM"), SqlAdvisorTest.EMP_COLUMNS);// where list

        sql = "select a.empno, b.deptno from sales.emp a, sales.dept b " + "where b.deptno=a.e^";
        assertComplete(sql, ImmutableMap.of("COLUMN(ENAME)", "ename"), SqlAdvisorTest.EMP_COLUMNS_E);// where list

        // hints contain no columns, only table aliases, because there are >1
        // aliases
        sql = "select a.empno, b.deptno from sales.emp a, sales.dept b " + "where ^dummy=1";
        assertComplete(sql, ImmutableMap.of("KEYWORD(CURRENT_TIMESTAMP)", "CURRENT_TIMESTAMP"), SqlAdvisorTest.AB_TABLES, SqlAdvisorTest.EXPR_KEYWORDS);// where list

        sql = "select a.empno, b.deptno from sales.emp a, sales.dept b " + "where ^";
        assertComplete(sql, SqlAdvisorTest.AB_TABLES, SqlAdvisorTest.EXPR_KEYWORDS);// where list

        // If there's only one table alias, we allow both the alias and the
        // unqualified columns
        assertComplete(("select a.empno, a.deptno from sales.emp a " + "where ^"), SqlAdvisorTest.A_TABLE, SqlAdvisorTest.EMP_COLUMNS, SqlAdvisorTest.EXPR_KEYWORDS);
    }

    @Test
    public void testWhereList() throws Exception {
        String sql;
        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.deptno=b.deptno where ^dummy=1";
        assertHint(sql, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.AB_TABLES);// where list

        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.deptno=b.deptno where ^";
        assertComplete(sql, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.AB_TABLES);// where list

        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.deptno=b.deptno where ^a.dummy=1";
        assertHint(sql, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.AB_TABLES);// where list

        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.deptno=b.deptno where a.^";
        assertComplete(sql, SqlAdvisorTest.EMP_COLUMNS);
        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.deptno=b.deptno where a.empno ^ ";
        assertComplete(sql, SqlAdvisorTest.PREDICATE_KEYWORDS, SqlAdvisorTest.WHERE_KEYWORDS);
    }

    @Test
    public void testSelectList() throws Exception {
        String sql;
        sql = "select ^dummy, b.dummy from sales.emp a join sales.dept b " + "on a.deptno=b.deptno where empno=1";
        assertHint(sql, getSelectKeywords(), SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.AB_TABLES, SqlAdvisorTest.SETOPS, SqlAdvisorTest.FETCH_OFFSET);
        sql = "select ^ from (values (1))";
        assertComplete(sql, getSelectKeywords(), SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.SETOPS, SqlAdvisorTest.FETCH_OFFSET, Arrays.asList("TABLE(EXPR$0)", "COLUMN(EXPR$0)"));
        sql = "select ^ from (values (1)) as t(c)";
        assertComplete(sql, getSelectKeywords(), SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.SETOPS, SqlAdvisorTest.FETCH_OFFSET, Arrays.asList("TABLE(T)", "COLUMN(C)"));
        sql = "select ^, b.dummy from sales.emp a join sales.dept b ";
        assertComplete(sql, getSelectKeywords(), SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.SETOPS, SqlAdvisorTest.AB_TABLES, SqlAdvisorTest.FETCH_OFFSET);
        sql = "select dummy, ^b.dummy from sales.emp a join sales.dept b " + "on a.deptno=b.deptno where empno=1";
        assertHint(sql, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.STAR_KEYWORD, SqlAdvisorTest.AB_TABLES);
        sql = "select dummy, b.^ from sales.emp a join sales.dept b on true";
        assertComplete(sql, SqlAdvisorTest.STAR_KEYWORD, SqlAdvisorTest.DEPT_COLUMNS);
        // REVIEW: Since 'b' is not a valid alias, should it suggest anything?
        // We don't get through validation, so the only suggestion, '*', comes
        // from the parser.
        sql = "select dummy, b.^ from sales.emp a";
        assertComplete(sql, SqlAdvisorTest.STAR_KEYWORD);
        sql = "select ^emp.dummy from sales.emp";
        assertHint(sql, getSelectKeywords(), SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.EMP_COLUMNS, SqlAdvisorTest.SETOPS, SqlAdvisorTest.FETCH_OFFSET, Arrays.asList("TABLE(EMP)"));
        sql = "select emp.^ from sales.emp";
        assertComplete(sql, SqlAdvisorTest.EMP_COLUMNS, SqlAdvisorTest.STAR_KEYWORD);
    }

    @Test
    public void testOrderByList() throws Exception {
        String sql;
        sql = "select emp.empno from sales.emp where empno=1 order by ^dummy";
        assertHint(sql, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.EMP_COLUMNS, SqlAdvisorTest.EMP_TABLE);
        sql = "select emp.empno from sales.emp where empno=1 order by ^";
        assertComplete(sql, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.EMP_COLUMNS, SqlAdvisorTest.EMP_TABLE);
        sql = "select emp.empno\n" + (("from sales.emp as e(\n" + "  mpno,name,ob,gr,iredate,al,omm,eptno,lacker)\n") + "where e.mpno=1 order by ^");
        assertComplete(sql, SqlAdvisorTest.EXPR_KEYWORDS, Arrays.asList("COLUMN(MPNO)", "COLUMN(NAME)", "COLUMN(OB)", "COLUMN(GR)", "COLUMN(IREDATE)", "COLUMN(AL)", "COLUMN(OMM)", "COLUMN(EPTNO)", "COLUMN(LACKER)"), Arrays.asList("TABLE(E)"));
        sql = "select emp.empno from sales.emp where empno=1 order by empno ^, deptno";
        assertComplete(sql, SqlAdvisorTest.PREDICATE_KEYWORDS, SqlAdvisorTest.ORDER_KEYWORDS, SqlAdvisorTest.FETCH_OFFSET);
    }

    @Test
    public void testSubQuery() throws Exception {
        String sql;
        final List<String> xyColumns = Arrays.asList("COLUMN(X)", "COLUMN(Y)");
        final List<String> tTable = Arrays.asList("TABLE(T)");
        sql = "select ^t.dummy from (select 1 as x, 2 as y from sales.emp) as t where t.dummy=1";
        assertHint(sql, SqlAdvisorTest.EXPR_KEYWORDS, getSelectKeywords(), xyColumns, tTable, SqlAdvisorTest.SETOPS, SqlAdvisorTest.FETCH_OFFSET);
        sql = "select t.^ from (select 1 as x, 2 as y from sales.emp) as t";
        assertComplete(sql, xyColumns, SqlAdvisorTest.STAR_KEYWORD);
        sql = "select t.x from (select 1 as x, 2 as y from sales.emp) as t where ^t.dummy=1";
        assertHint(sql, SqlAdvisorTest.EXPR_KEYWORDS, tTable, xyColumns);
        sql = "select t.x from (select 1 as x, 2 as y from sales.emp) as t where t.^";
        assertComplete(sql, xyColumns);
        sql = "select t.x from (select 1 as x, 2 as y from sales.emp) as t where ^";
        assertComplete(sql, SqlAdvisorTest.EXPR_KEYWORDS, tTable, xyColumns);
        // with extra from item, aliases are ambiguous, so columns are not
        // offered
        sql = "select a.x from (select 1 as x, 2 as y from sales.emp) as a, dept as b where ^";
        assertComplete(sql, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.AB_TABLES);
        // note that we get hints even though there's a syntax error in
        // select clause ('t.')
        sql = "select t. from (select 1 as x, 2 as y from (select x from sales.emp)) as t where ^";
        String simplified = "SELECT * FROM ( SELECT 1 as x , 2 as y FROM ( SELECT x FROM sales.emp ) ) as t WHERE _suggest_";
        assertSimplify(sql, simplified);
        assertComplete(sql, SqlAdvisorTest.EXPR_KEYWORDS, tTable, xyColumns);
        sql = "select t.x from (select 1 as x, 2 as y from sales.^) as t";
        assertComplete(sql, getSalesTables());
    }

    @Test
    public void testSubQueryInWhere() {
        String sql;
        // Aliases from enclosing sub-queries are inherited: hence A from
        // enclosing, B from same scope.
        // The raw columns from dept are suggested (because they can
        // be used unqualified in the inner scope) but the raw
        // columns from emp are not (because they would need to be qualified
        // with A).
        sql = "select * from sales.emp a where deptno in (" + "select * from sales.dept b where ^)";
        String simplifiedSql = "SELECT * FROM sales.emp a WHERE deptno in (" + " SELECT * FROM sales.dept b WHERE _suggest_ )";
        assertSimplify(sql, simplifiedSql);
        assertComplete(sql, SqlAdvisorTest.AB_TABLES, SqlAdvisorTest.DEPT_COLUMNS, SqlAdvisorTest.EXPR_KEYWORDS);
    }

    @Test
    public void testSimpleParserTokenizer() {
        String sql = "select" + (((((((((((((((((((((((((((((((((((" 12" + " ") + "*") + " 1.23e45") + " ") + "(") + "\"an id\"") + ",") + " ") + "\"an id with \"\"quotes\' inside\"") + ",") + " ") + "/* a comment, with \'quotes\', over\nmultiple lines\nand select keyword */") + "\n ") + "(") + " ") + "a") + " ") + "different") + " ") + "// comment\n\r") + "//and a comment /* containing comment */ and then some more\r") + ")") + " ") + "from") + " ") + "t") + ")") + ")") + "/* a comment after close paren */") + " ") + "(") + "'quoted'") + " ") + "\'string with \'\'single and \"double\"\" quote\'") + ")");
        String expected = "SELECT\n" + ((((((((((((((((((((((("ID(12)\n" + "ID(*)\n") + "ID(1.23e45)\n") + "LPAREN\n") + "DQID(\"an id\")\n") + "COMMA\n") + "DQID(\"an id with \"\"quotes\' inside\")\n") + "COMMA\n") + "COMMENT\n") + "LPAREN\n") + "ID(a)\n") + "ID(different)\n") + "COMMENT\n") + "COMMENT\n") + "RPAREN\n") + "FROM\n") + "ID(t)\n") + "RPAREN\n") + "RPAREN\n") + "COMMENT\n") + "LPAREN\n") + "SQID(\'quoted\')\n") + "SQID(\'string with \'\'single and \"double\"\" quote\')\n") + "RPAREN\n");
        assertTokenizesTo(sql, expected);
        // Tokenizer should be lenient if input ends mid-token
        assertTokenizesTo("select /* unfinished comment", "SELECT\nCOMMENT\n");
        assertTokenizesTo("select // unfinished comment", "SELECT\nCOMMENT\n");
        assertTokenizesTo("'starts with string'", "SQID(\'starts with string\')\n");
        assertTokenizesTo("'unfinished string", "SQID(\'unfinished string)\n");
        assertTokenizesTo("\"unfinished double-quoted id", "DQID(\"unfinished double-quoted id)\n");
        assertTokenizesTo("123", "ID(123)\n");
    }

    @Test
    public void testSimpleParser() {
        String sql;
        String expected;
        // from
        sql = "select * from ^where";
        expected = "SELECT * FROM _suggest_";
        assertSimplify(sql, expected);
        // from
        sql = "select a.empno, b.deptno from ^";
        expected = "SELECT * FROM _suggest_";
        assertSimplify(sql, expected);
        // select list
        sql = "select ^ from (values (1))";
        expected = "SELECT _suggest_ FROM ( values ( 1 ) )";
        assertSimplify(sql, expected);
        sql = "select emp.^ from sales.emp";
        expected = "SELECT emp. _suggest_ FROM sales.emp";
        assertSimplify(sql, expected);
        sql = "select ^from sales.emp";
        expected = "SELECT _suggest_ FROM sales.emp";
        assertSimplify(sql, expected);
        // remove other expressions in select clause
        sql = "select a.empno ,^  from sales.emp a , sales.dept b";
        expected = "SELECT _suggest_ FROM sales.emp a , sales.dept b";
        assertSimplify(sql, expected);
        sql = "select ^, a.empno from sales.emp a , sales.dept b";
        expected = "SELECT _suggest_ FROM sales.emp a , sales.dept b";
        assertSimplify(sql, expected);
        sql = "select dummy, b.^ from sales.emp a , sales.dept b";
        expected = "SELECT b. _suggest_ FROM sales.emp a , sales.dept b";
        assertSimplify(sql, expected);
        // join
        sql = "select a.empno, b.deptno from dummy a join ^on where empno=1";
        expected = "SELECT * FROM dummy a JOIN _suggest_ ON TRUE";
        assertSimplify(sql, expected);
        // join
        sql = "select a.empno, b.deptno from dummy a join sales.^ where empno=1";
        expected = "SELECT * FROM dummy a JOIN sales. _suggest_";
        assertSimplify(sql, expected);
        // on
        sql = "select a.empno, b.deptno from sales.emp a join sales.dept b " + "on a.deptno=^";
        expected = "SELECT * FROM sales.emp a JOIN sales.dept b " + "ON a.deptno= _suggest_";
        assertSimplify(sql, expected);
        // where
        sql = "select a.empno, b.deptno from sales.emp a, sales.dept b " + "where ^";
        expected = "SELECT * FROM sales.emp a , sales.dept b WHERE _suggest_";
        assertSimplify(sql, expected);
        // order by
        sql = "select emp.empno from sales.emp where empno=1 order by ^";
        expected = "SELECT emp.empno FROM sales.emp ORDER BY _suggest_";
        assertSimplify(sql, expected);
        // sub-query in from
        sql = "select t.^ from (select 1 as x, 2 as y from sales.emp) as t " + "where t.dummy=1";
        expected = "SELECT t. _suggest_ " + "FROM ( SELECT 1 as x , 2 as y FROM sales.emp ) as t";
        assertSimplify(sql, expected);
        sql = "select t. from (select 1 as x, 2 as y from " + "(select x from sales.emp)) as t where ^";
        expected = "SELECT * FROM ( SELECT 1 as x , 2 as y FROM " + "( SELECT x FROM sales.emp ) ) as t WHERE _suggest_";
        assertSimplify(sql, expected);
        sql = "select ^from (select 1 as x, 2 as y from sales.emp), " + ("(select 2 as y from (select m from n where)) as t " + "where t.dummy=1");
        expected = "SELECT _suggest_ FROM ( SELECT 1 as x , 2 as y FROM sales.emp ) " + ", ( SELECT 2 as y FROM ( SELECT m FROM n ) ) as t";
        assertSimplify(sql, expected);
        // Note: completes the missing close paren; wipes out select clause of
        // both outer and inner queries since not relevant.
        sql = "select t.x from ( select 1 as x, 2 as y from sales.^";
        expected = "SELECT * FROM ( SELECT * FROM sales. _suggest_ )";
        assertSimplify(sql, expected);
        sql = "select t.^ from (select 1 as x, 2 as y from sales)";
        expected = "SELECT t. _suggest_ FROM ( SELECT 1 as x , 2 as y FROM sales )";
        assertSimplify(sql, expected);
        // sub-query in where; note that:
        // 1. removes the SELECT clause of sub-query in WHERE clause;
        // 2. keeps SELECT clause of sub-query in FROM clause;
        // 3. removes GROUP BY clause of sub-query in FROM clause;
        // 4. removes SELECT clause of outer query.
        sql = "select x + y + 32 from " + ("(select 1 as x, 2 as y from sales group by invalid stuff) as t " + "where x in (select deptno from emp where foo + t.^ < 10)");
        expected = "SELECT * FROM ( SELECT 1 as x , 2 as y FROM sales ) as t " + "WHERE x in ( SELECT * FROM emp WHERE foo + t. _suggest_ < 10 )";
        assertSimplify(sql, expected);
        // if hint is in FROM, can remove other members of FROM clause
        sql = "select a.empno, b.deptno from dummy a, sales.^";
        expected = "SELECT * FROM sales. _suggest_";
        assertSimplify(sql, expected);
        // function
        sql = "select count(1) from sales.emp a where ^";
        expected = "SELECT * FROM sales.emp a WHERE _suggest_";
        assertSimplify(sql, expected);
        sql = "select count(1) from sales.emp a " + "where substring(a.^ FROM 3 for 6) = '1234'";
        expected = "SELECT * FROM sales.emp a " + "WHERE substring ( a. _suggest_ FROM 3 for 6 ) = '1234'";
        assertSimplify(sql, expected);
        // missing ')' following sub-query
        sql = "select * from sales.emp a where deptno in (" + "select * from sales.dept b where ^";
        expected = "SELECT * FROM sales.emp a WHERE deptno in (" + " SELECT * FROM sales.dept b WHERE _suggest_ )";
        assertSimplify(sql, expected);
        // keyword embedded in single and double quoted string should be
        // ignored
        sql = "select \'a cat from a king\' as foobar, 1 / 2 \"where\" from t " + "group by t.^ order by 123";
        expected = "SELECT * FROM t GROUP BY t. _suggest_";
        assertSimplify(sql, expected);
        // skip comments
        sql = "select /* here is from */ 'cat' as foobar, 1 as x from t group by t.^ order by 123";
        expected = "SELECT * FROM t GROUP BY t. _suggest_";
        assertSimplify(sql, expected);
        // skip comments
        sql = "select // here is from clause\n \'cat\' as foobar, 1 as x from t group by t.^ order by 123";
        expected = "SELECT * FROM t GROUP BY t. _suggest_";
        assertSimplify(sql, expected);
        // skip comments
        sql = "select -- here is from clause\n \'cat\' as foobar, 1 as x from t group by t.^ order by 123";
        expected = "SELECT * FROM t GROUP BY t. _suggest_";
        assertSimplify(sql, expected);
        // skip comments
        sql = "-- test test \nselect -- here is from \n \'cat\' as foobar, 1 as x from t group by t.^ order by 123";
        expected = "SELECT * FROM t GROUP BY t. _suggest_";
        assertSimplify(sql, expected);
    }

    @WithLex(Lex.SQL_SERVER)
    @Test
    public void testSimpleParserQuotedIdSqlServer() {
        testSimpleParserQuotedIdImpl();
    }

    @WithLex(Lex.MYSQL)
    @Test
    public void testSimpleParserQuotedIdMySql() {
        testSimpleParserQuotedIdImpl();
    }

    @WithLex(Lex.JAVA)
    @Test
    public void testSimpleParserQuotedIdJava() {
        testSimpleParserQuotedIdImpl();
    }

    @Test
    public void testSimpleParserQuotedIdDefault() {
        testSimpleParserQuotedIdImpl();
    }

    @Test
    public void testPartialIdentifier() {
        String sql = "select * from emp where e^ and emp.deptno = 10";
        String expected = "COLUMN(EMPNO)\n" + ((((("COLUMN(ENAME)\n" + "KEYWORD(ELEMENT)\n") + "KEYWORD(EXISTS)\n") + "KEYWORD(EXP)\n") + "KEYWORD(EXTRACT)\n") + "TABLE(EMP)\n");
        assertComplete(sql, expected, "e", ImmutableMap.of("KEYWORD(EXISTS)", "exists", "TABLE(EMP)", "emp"));
        sql = "select * from emp where \"e^ and emp.deptno = 10";
        expected = "COLUMN(EMPNO)\n" + ((((("COLUMN(ENAME)\n" + "KEYWORD(ELEMENT)\n") + "KEYWORD(EXISTS)\n") + "KEYWORD(EXP)\n") + "KEYWORD(EXTRACT)\n") + "TABLE(EMP)\n");
        assertComplete(sql, expected, "\"e", ImmutableMap.of("KEYWORD(EXISTS)", "exists", "TABLE(EMP)", "\"EMP\""));
        sql = "select * from emp where E^ and emp.deptno = 10";
        expected = "COLUMN(EMPNO)\n" + ((((("COLUMN(ENAME)\n" + "KEYWORD(ELEMENT)\n") + "KEYWORD(EXISTS)\n") + "KEYWORD(EXP)\n") + "KEYWORD(EXTRACT)\n") + "TABLE(EMP)\n");
        assertComplete(sql, expected, "E", ImmutableMap.of("KEYWORD(EXISTS)", "EXISTS", "TABLE(EMP)", "EMP"));
        // cursor in middle of word and at end
        sql = "select * from emp where e^";
        assertComplete(sql, expected, null);
        // longer completion
        sql = "select * from emp where em^";
        assertComplete(sql, SqlAdvisorTest.EMPNO_EMP, null, ImmutableMap.of("COLUMN(EMPNO)", "empno"));
        // word after punctuation
        sql = "select deptno,em^ from emp where 1+2<3+4";
        assertComplete(sql, SqlAdvisorTest.EMPNO_EMP, null, ImmutableMap.of("COLUMN(EMPNO)", "empno"));
        // inside double-quotes, no terminating double-quote.
        // Only identifiers should be suggested (no keywords),
        // and suggestion should include double-quotes
        sql = "select deptno,\"EM^ from emp where 1+2<3+4";
        assertComplete(sql, SqlAdvisorTest.EMPNO_EMP, "\"EM", ImmutableMap.of("COLUMN(EMPNO)", "\"EMPNO\""));
        // inside double-quotes, match is case-insensitive as well
        sql = "select deptno,\"em^ from emp where 1+2<3+4";
        assertComplete(sql, SqlAdvisorTest.EMPNO_EMP, "\"em", ImmutableMap.of("COLUMN(EMPNO)", "\"EMPNO\""));
        // when input strings has mixed casing, match should be case-sensitive
        sql = "select deptno,eM^ from emp where 1+2<3+4";
        assertComplete(sql, "", "eM");
        // when input strings has mixed casing, match should be case-sensitive
        sql = "select deptno,\"eM^ from emp where 1+2<3+4";
        assertComplete(sql, "", "\"eM");
        // eat up following double-quote
        sql = "select deptno,\"EM^ps\" from emp where 1+2<3+4";
        assertComplete(sql, SqlAdvisorTest.EMPNO_EMP, "\"EM", ImmutableMap.of("COLUMN(EMPNO)", "\"EMPNO\""));
        // closing double-quote is at very end of string
        sql = "select * from emp where 5 = \"EM^xxx\"";
        assertComplete(sql, SqlAdvisorTest.EMPNO_EMP, "\"EM", ImmutableMap.of("COLUMN(EMPNO)", "\"EMPNO\""));
        // just before dot
        sql = "select emp.^name from emp";
        assertComplete(sql, SqlAdvisorTest.EMP_COLUMNS, SqlAdvisorTest.STAR_KEYWORD);
    }

    @Test
    @WithLex(Lex.JAVA)
    public void testAdviceKeywordsJava() {
        String sql;
        sql = "select deptno, exi^ from emp where 1+2<3+4";
        assertComplete(sql, "KEYWORD(EXISTS)\n", "exi", ImmutableMap.of("KEYWORD(EXISTS)", "exists"));
    }

    @Test
    @WithLex(Lex.JAVA)
    public void testAdviceMixedCase() {
        String sql;
        sql = "select is^ from (select 1 isOne from emp)";
        assertComplete(sql, "COLUMN(isOne)\n", "is", ImmutableMap.of("COLUMN(isOne)", "isOne"));
    }

    @Test
    @WithLex(Lex.JAVA)
    public void testAdviceExpression() {
        String sql;
        sql = "select s.`count`+s.co^ from (select 1 `count` from emp) s";
        assertComplete(sql, "COLUMN(count)\n", "co", ImmutableMap.of("COLUMN(count)", "`count`"));
    }

    @Test
    @WithLex(Lex.JAVA)
    public void testAdviceEmptyFrom() {
        String sql;
        sql = "select * from^";
        assertComplete(sql, "KEYWORD(FROM)\n", "from", ImmutableMap.of("KEYWORD(FROM)", "from"));
    }

    @Test
    public void testNestSchema() throws Exception {
        String sql;
        sql = "select * from sales.n^";
        assertComplete(sql, "SCHEMA(CATALOG.SALES.NEST)\n", "n", ImmutableMap.of("SCHEMA(CATALOG.SALES.NEST)", "nest"));
        sql = "select * from sales.\"n^asfasdf";
        assertComplete(sql, "SCHEMA(CATALOG.SALES.NEST)\n", "\"n", ImmutableMap.of("SCHEMA(CATALOG.SALES.NEST)", "\"NEST\""));
        sql = "select * from sales.n^est";
        assertComplete(sql, "SCHEMA(CATALOG.SALES.NEST)\n", "n", ImmutableMap.of("SCHEMA(CATALOG.SALES.NEST)", "nest"));
        sql = "select * from sales.nu^";
        assertComplete(sql, "", "nu");
    }

    @Test
    public void testNestTable2() throws Exception {
        String sql;
        // select scott.emp.deptno from scott.emp as e; # not valid
        sql = "select catalog.sales.emp.em^ from catalog.sales.emp as e";
        assertComplete(sql, "", "em");
    }

    @Test
    public void testNestTable4() throws Exception {
        String sql;
        // select scott.emp.deptno from emp as emp; # not valid
        sql = "select catalog.sales.emp.em^ from catalog.sales.emp as emp";
        assertComplete(sql, "", "em");
    }

    @Test
    public void testNestTableSchemaMustMatch() throws Exception {
        String sql;
        // select foo.emp.deptno from emp; # not valid
        sql = "select sales.nest.em^ from catalog.sales.emp_r";
        assertComplete(sql, "", "em");
    }

    @WithLex(Lex.SQL_SERVER)
    @Test
    public void testNestSchemaSqlServer() throws Exception {
        String sql;
        sql = "select * from SALES.N^";
        assertComplete(sql, "SCHEMA(CATALOG.SALES.NEST)\n", "N", ImmutableMap.of("SCHEMA(CATALOG.SALES.NEST)", "NEST"));
        sql = "select * from SALES.[n^asfasdf";
        assertComplete(sql, "SCHEMA(CATALOG.SALES.NEST)\n", "[n", ImmutableMap.of("SCHEMA(CATALOG.SALES.NEST)", "[NEST]"));
        sql = "select * from SALES.[N^est";
        assertComplete(sql, "SCHEMA(CATALOG.SALES.NEST)\n", "[N", ImmutableMap.of("SCHEMA(CATALOG.SALES.NEST)", "[NEST]"));
        sql = "select * from SALES.NU^";
        assertComplete(sql, "", "NU");
    }

    @Test
    public void testUnion() throws Exception {
        // we simplify set ops such as UNION by removing other queries -
        // thereby avoiding validation errors due to mismatched select lists
        String sql = "select 1 from emp union select 2 from dept a where ^ and deptno < 5";
        String simplified = "SELECT * FROM dept a WHERE _suggest_ and deptno < 5";
        assertSimplify(sql, simplified);
        assertComplete(sql, SqlAdvisorTest.EXPR_KEYWORDS, SqlAdvisorTest.A_TABLE, SqlAdvisorTest.DEPT_COLUMNS);
        // UNION ALL
        sql = "select 1 from emp union all select 2 from dept a where ^ and deptno < 5";
        assertSimplify(sql, simplified);
        // hint is in first query
        sql = "select 1 from emp group by ^ except select 2 from dept a";
        simplified = "SELECT * FROM emp GROUP BY _suggest_";
        assertSimplify(sql, simplified);
    }

    @WithLex(Lex.SQL_SERVER)
    @Test
    public void testMssql() {
        String sql = "select 1 from [emp] union select 2 from [DEPT] a where ^ and deptno < 5";
        String simplified = "SELECT * FROM [DEPT] a WHERE _suggest_ and deptno < 5";
        assertSimplify(sql, simplified);
        assertComplete(sql, SqlAdvisorTest.EXPR_KEYWORDS, Arrays.asList("TABLE(a)"), SqlAdvisorTest.DEPT_COLUMNS);
    }
}

/**
 * End SqlAdvisorTest.java
 */
