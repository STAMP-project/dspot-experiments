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
package org.apache.calcite.tools;


import AnsiSqlDialect.DEFAULT;
import CalciteSystemProperty.DEBUG;
import ConventionTraitDef.INSTANCE;
import Prepare.CatalogReader;
import Programs.RULE_SET;
import SqlExplainFormat.TEXT;
import SqlExplainLevel.DIGEST_ATTRIBUTES;
import SqlTypeName.INTEGER;
import TableModify.Operation;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.enumerable.EnumerableTableScan;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.plan.RelOptAbstractTable;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableModify;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalTableModify;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.ModifiableTable;
import org.apache.calcite.schema.Path;
import org.apache.calcite.schema.ProjectableFilterableTable;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Schemas;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.test.CalciteAssert;
import org.apache.calcite.test.CalciteConnection;
import org.apache.calcite.util.ImmutableBitSet;
import org.apache.calcite.util.TestUtil;
import org.apache.calcite.util.Util;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.calcite.test.CalciteAssert.SchemaSpec.HR;
import static org.apache.calcite.test.CalciteAssert.SchemaSpec.JDBC_SCOTT;


/**
 * Unit tests for methods in {@link Frameworks}.
 */
public class FrameworksTest {
    @Test
    public void testOptimize() {
        RelNode x = Frameworks.withPlanner(( cluster, relOptSchema, rootSchema) -> {
            final RelDataTypeFactory typeFactory = cluster.getTypeFactory();
            final Table table = new AbstractTable() {
                public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                    final RelDataType stringType = typeFactory.createJavaType(.class);
                    final RelDataType integerType = typeFactory.createJavaType(.class);
                    return typeFactory.builder().add("s", stringType).add("i", integerType).build();
                }
            };
            // "SELECT * FROM myTable"
            final RelOptAbstractTable relOptTable = new RelOptAbstractTable(relOptSchema, "myTable", table.getRowType(typeFactory)) {};
            final EnumerableTableScan tableRel = EnumerableTableScan.create(cluster, relOptTable);
            // "WHERE i > 1"
            final RexBuilder rexBuilder = cluster.getRexBuilder();
            final RexNode condition = rexBuilder.makeCall(SqlStdOperatorTable.GREATER_THAN, rexBuilder.makeFieldAccess(rexBuilder.makeRangeReference(tableRel), "i", true), rexBuilder.makeExactLiteral(BigDecimal.ONE));
            final LogicalFilter filter = LogicalFilter.create(tableRel, condition);
            // Specify that the result should be in Enumerable convention.
            final RelNode rootRel = filter;
            final RelOptPlanner planner = cluster.getPlanner();
            RelTraitSet desiredTraits = cluster.traitSet().replace(EnumerableConvention.INSTANCE);
            final RelNode rootRel2 = planner.changeTraits(rootRel, desiredTraits);
            planner.setRoot(rootRel2);
            // Now, plan.
            return planner.findBestExp();
        });
        String s = RelOptUtil.dumpPlan("", x, TEXT, DIGEST_ATTRIBUTES);
        Assert.assertThat(Util.toLinux(s), CoreMatchers.equalTo(("EnumerableFilter(condition=[>($1, 1)])\n" + "  EnumerableTableScan(table=[[myTable]])\n")));
    }

    /**
     * Unit test to test create root schema which has no "metadata" schema.
     */
    @Test
    public void testCreateRootSchemaWithNoMetadataSchema() {
        SchemaPlus rootSchema = Frameworks.createRootSchema(false);
        Assert.assertThat(rootSchema.getSubSchemaNames().size(), CoreMatchers.equalTo(0));
    }

    /**
     * Tests that validation (specifically, inferring the result of adding
     * two DECIMAL(19, 0) values together) happens differently with a type system
     * that allows a larger maximum precision for decimals.
     *
     * <p>Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-413">[CALCITE-413]
     * Add RelDataTypeSystem plugin, allowing different max precision of a
     * DECIMAL</a>.
     *
     * <p>Also tests the plugin system, by specifying implementations of a
     * plugin interface with public and private constructors.
     */
    @Test
    public void testTypeSystem() {
        checkTypeSystem(19, Frameworks.newConfigBuilder().build());
        checkTypeSystem(25, Frameworks.newConfigBuilder().typeSystem(FrameworksTest.HiveLikeTypeSystem.INSTANCE).build());
        checkTypeSystem(31, Frameworks.newConfigBuilder().typeSystem(new FrameworksTest.HiveLikeTypeSystem2()).build());
    }

    /**
     * Tests that the validator expands identifiers by default.
     *
     * <p>Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-593">[CALCITE-593]
     * Validator in Frameworks should expand identifiers</a>.
     */
    @Test
    public void testFrameworksValidatorWithIdentifierExpansion() throws Exception {
        final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        final FrameworkConfig config = Frameworks.newConfigBuilder().defaultSchema(CalciteAssert.addSchema(rootSchema, HR)).build();
        final Planner planner = Frameworks.getPlanner(config);
        SqlNode parse = planner.parse("select * from \"emps\" ");
        SqlNode val = planner.validate(parse);
        String valStr = val.toSqlString(DEFAULT, false).getSql();
        String expandedStr = "SELECT `emps`.`empid`, `emps`.`deptno`, `emps`.`name`, `emps`.`salary`, `emps`.`commission`\n" + "FROM `hr`.`emps` AS `emps`";
        Assert.assertThat(Util.toLinux(valStr), CoreMatchers.equalTo(expandedStr));
    }

    /**
     * Test for {@link Path}.
     */
    @Test
    public void testSchemaPath() {
        final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        final FrameworkConfig config = Frameworks.newConfigBuilder().defaultSchema(CalciteAssert.addSchema(rootSchema, HR)).build();
        final Path path = Schemas.path(config.getDefaultSchema());
        Assert.assertThat(path.size(), CoreMatchers.is(2));
        Assert.assertThat(path.get(0).left, CoreMatchers.is(""));
        Assert.assertThat(path.get(1).left, CoreMatchers.is("hr"));
        Assert.assertThat(path.names().size(), CoreMatchers.is(1));
        Assert.assertThat(path.names().get(0), CoreMatchers.is("hr"));
        Assert.assertThat(path.schemas().size(), CoreMatchers.is(2));
        final Path parent = path.parent();
        Assert.assertThat(parent.size(), CoreMatchers.is(1));
        Assert.assertThat(parent.names().size(), CoreMatchers.is(0));
        final Path grandparent = parent.parent();
        Assert.assertThat(grandparent.size(), CoreMatchers.is(0));
        try {
            Object o = grandparent.parent();
            Assert.fail(("expected exception, got " + o));
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1996">[CALCITE-1996]
     * VALUES syntax</a>.
     *
     * <p>With that bug, running a VALUES query would succeed before running a
     * query that reads from a JDBC table, but fail after it. Before, the plan
     * would use {@link org.apache.calcite.adapter.enumerable.EnumerableValues},
     * but after, it would use
     * {@link org.apache.calcite.adapter.jdbc.JdbcRules.JdbcValues}, and would
     * generate invalid SQL syntax.
     *
     * <p>Even though the SQL generator has been fixed, we are still interested in
     * how JDBC convention gets lodged in the planner's state.
     */
    @Test
    public void testJdbcValues() throws Exception {
        CalciteAssert.that().with(JDBC_SCOTT).doWithConnection(( connection) -> {
            try {
                final FrameworkConfig config = Frameworks.newConfigBuilder().defaultSchema(connection.getRootSchema()).build();
                final RelBuilder builder = RelBuilder.create(config);
                final RelRunner runner = connection.unwrap(RelRunner.class);
                final RelNode values = builder.values(new String[]{ "a", "b" }, "X", 1, "Y", 2).project(builder.field("a")).build();
                // If you run the "values" query before the "scan" query,
                // everything works fine. JdbcValues is never instantiated in any
                // of the 3 queries.
                if (false) {
                    runner.prepare(values).executeQuery();
                }
                final RelNode scan = builder.scan("JDBC_SCOTT", "EMP").build();
                runner.prepare(scan).executeQuery();
                builder.clear();
                // running this after the scott query causes the exception
                RelRunner runner2 = connection.unwrap(RelRunner.class);
                runner2.prepare(values).executeQuery();
            } catch (Exception e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2039">[CALCITE-2039]
     * AssertionError when pushing project to ProjectableFilterableTable</a>
     * using UPDATE via {@link Frameworks}.
     */
    @Test
    public void testUpdate() throws Exception {
        Table table = new FrameworksTest.TableImpl();
        final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        SchemaPlus schema = rootSchema.add("x", new AbstractSchema());
        schema.add("MYTABLE", table);
        List<RelTraitDef> traitDefs = new ArrayList<>();
        traitDefs.add(INSTANCE);
        traitDefs.add(RelDistributionTraitDef.INSTANCE);
        SqlParser.Config parserConfig = SqlParser.configBuilder(SqlParser.Config.DEFAULT).setCaseSensitive(false).build();
        final FrameworkConfig config = // define the rules you want to apply
        Frameworks.newConfigBuilder().parserConfig(parserConfig).defaultSchema(schema).traitDefs(traitDefs).ruleSets(RuleSets.ofList(AbstractConverter.ExpandConversionRule.INSTANCE)).programs(Programs.ofRules(RULE_SET)).build();
        executeQuery(config, " UPDATE MYTABLE set id=7 where id=1", DEBUG.value());
    }

    /**
     * Modifiable, filterable table.
     */
    private static class TableImpl extends AbstractTable implements ModifiableTable , ProjectableFilterableTable {
        TableImpl() {
        }

        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            return typeFactory.builder().add("id", typeFactory.createSqlType(INTEGER)).add("name", typeFactory.createSqlType(INTEGER)).build();
        }

        public Statistic getStatistic() {
            return Statistics.of(15.0, ImmutableList.of(ImmutableBitSet.of(0)), ImmutableList.of());
        }

        public Enumerable<Object[]> scan(DataContext root, List<RexNode> filters, int[] projects) {
            throw new UnsupportedOperationException();
        }

        public Collection getModifiableCollection() {
            throw new UnsupportedOperationException();
        }

        public TableModify toModificationRel(RelOptCluster cluster, RelOptTable table, Prepare.CatalogReader catalogReader, RelNode child, TableModify.Operation operation, List<String> updateColumnList, List<RexNode> sourceExpressionList, boolean flattened) {
            return LogicalTableModify.create(table, catalogReader, child, operation, updateColumnList, sourceExpressionList, flattened);
        }

        public <T> Queryable<T> asQueryable(QueryProvider queryProvider, SchemaPlus schema, String tableName) {
            throw new UnsupportedOperationException();
        }

        public Type getElementType() {
            return Object.class;
        }

        public Expression getExpression(SchemaPlus schema, String tableName, Class clazz) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Dummy type system, similar to Hive's, accessed via an INSTANCE member.
     */
    public static class HiveLikeTypeSystem extends RelDataTypeSystemImpl {
        public static final RelDataTypeSystem INSTANCE = new FrameworksTest.HiveLikeTypeSystem();

        private HiveLikeTypeSystem() {
        }

        @Override
        public int getMaxNumericPrecision() {
            assert (super.getMaxNumericPrecision()) == 19;
            return 25;
        }
    }

    /**
     * Dummy type system, similar to Hive's, accessed via a public default
     * constructor.
     */
    public static class HiveLikeTypeSystem2 extends RelDataTypeSystemImpl {
        public HiveLikeTypeSystem2() {
        }

        @Override
        public int getMaxNumericPrecision() {
            assert (super.getMaxNumericPrecision()) == 19;
            return 38;
        }
    }
}

/**
 * End FrameworksTest.java
 */
