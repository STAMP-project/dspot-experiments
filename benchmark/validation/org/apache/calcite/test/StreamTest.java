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


import Schema.TableType;
import com.google.common.collect.ImmutableList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.calcite.DataContext;
import org.apache.calcite.avatica.util.DateTimeUtils;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.RelCollations;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.schema.StreamableTable;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.TableFactory;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for streaming queries.
 */
public class StreamTest {
    public static final String STREAM_SCHEMA_NAME = "STREAMS";

    public static final String INFINITE_STREAM_SCHEMA_NAME = "INFINITE_STREAMS";

    public static final String STREAM_JOINS_SCHEMA_NAME = "STREAM_JOINS";

    private static final String STREAM_JOINS_MODEL = ((((((((((("{\n" + ((((((((((("  version: \'1.0\',\n" + "  defaultSchema: \'STREAM_JOINS\',\n") + "   schemas: [\n") + "     {\n") + "       name: \'STREAM_JOINS\',\n") + "       tables: [ {\n") + "         type: \'custom\',\n") + "         name: \'ORDERS\',\n") + "         stream: {\n") + "           stream: true\n") + "         },\n") + "         factory: '")) + (StreamTest.OrdersStreamTableFactory.class.getName())) + "\'\n") + "       }, \n") + "       {\n") + "         type: \'custom\',\n") + "         name: \'PRODUCTS\',\n") + "         factory: '") + (StreamTest.ProductsTableFactory.class.getName())) + "\'\n") + "       }]\n") + "     }]}";

    public static final String STREAM_MODEL = (((((("{\n" + (("  version: \'1.0\',\n" + "  defaultSchema: \'foodmart\',\n") + "   schemas: [\n")) + (StreamTest.schemaFor(StreamTest.STREAM_SCHEMA_NAME, StreamTest.OrdersStreamTableFactory.class))) + ",\n") + (StreamTest.schemaFor(StreamTest.INFINITE_STREAM_SCHEMA_NAME, StreamTest.InfiniteOrdersStreamTableFactory.class))) + "\n") + "   ]\n") + "}";

    @Test
    public void testStream() {
        CalciteAssert.model(StreamTest.STREAM_MODEL).withDefaultSchema("STREAMS").query("select stream * from orders").convertContains(("LogicalDelta\n" + ("  LogicalProject(ROWTIME=[$0], ID=[$1], PRODUCT=[$2], UNITS=[$3])\n" + "    LogicalTableScan(table=[[STREAMS, ORDERS]])\n"))).explainContains(("EnumerableInterpreter\n" + "  BindableTableScan(table=[[STREAMS, ORDERS, (STREAM)]])")).returns(startsWith("ROWTIME=2015-02-15 10:15:00; ID=1; PRODUCT=paint; UNITS=10", "ROWTIME=2015-02-15 10:24:15; ID=2; PRODUCT=paper; UNITS=5"));
    }

    @Test
    public void testStreamFilterProject() {
        CalciteAssert.model(StreamTest.STREAM_MODEL).withDefaultSchema("STREAMS").query("select stream product from orders where units > 6").convertContains(("LogicalDelta\n" + (("  LogicalProject(PRODUCT=[$2])\n" + "    LogicalFilter(condition=[>($3, 6)])\n") + "      LogicalTableScan(table=[[STREAMS, ORDERS]])\n"))).explainContains(("EnumerableCalc(expr#0..3=[{inputs}], expr#4=[6], expr#5=[>($t3, $t4)], PRODUCT=[$t2], $condition=[$t5])\n" + ("  EnumerableInterpreter\n" + "    BindableTableScan(table=[[STREAMS, ORDERS, (STREAM)]])"))).returns(startsWith("PRODUCT=paint", "PRODUCT=brush"));
    }

    @Test
    public void testStreamGroupByHaving() {
        CalciteAssert.model(StreamTest.STREAM_MODEL).withDefaultSchema("STREAMS").query(("select stream floor(rowtime to hour) as rowtime,\n" + ((("  product, count(*) as c\n" + "from orders\n") + "group by floor(rowtime to hour), product\n") + "having count(*) > 1"))).convertContains(("LogicalDelta\n" + ((("  LogicalFilter(condition=[>($2, 1)])\n" + "    LogicalAggregate(group=[{0, 1}], C=[COUNT()])\n") + "      LogicalProject(ROWTIME=[FLOOR($0, FLAG(HOUR))], PRODUCT=[$2])\n") + "        LogicalTableScan(table=[[STREAMS, ORDERS]])\n"))).explainContains(("EnumerableCalc(expr#0..2=[{inputs}], expr#3=[1], expr#4=[>($t2, $t3)], proj#0..2=[{exprs}], $condition=[$t4])\n" + ((("  EnumerableAggregate(group=[{0, 1}], C=[COUNT()])\n" + "    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[FLAG(HOUR)], expr#5=[FLOOR($t0, $t4)], ROWTIME=[$t5], PRODUCT=[$t2])\n") + "      EnumerableInterpreter\n") + "        BindableTableScan(table=[[STREAMS, ORDERS, (STREAM)]])"))).returns(startsWith("ROWTIME=2015-02-15 10:00:00; PRODUCT=paint; C=2"));
    }

    @Test
    public void testStreamOrderBy() {
        CalciteAssert.model(StreamTest.STREAM_MODEL).withDefaultSchema("STREAMS").query(("select stream floor(rowtime to hour) as rowtime,\n" + (("  product, units\n" + "from orders\n") + "order by floor(orders.rowtime to hour), product desc"))).convertContains(("LogicalDelta\n" + (("  LogicalSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n" + "    LogicalProject(ROWTIME=[FLOOR($0, FLAG(HOUR))], PRODUCT=[$2], UNITS=[$3])\n") + "      LogicalTableScan(table=[[STREAMS, ORDERS]])\n"))).explainContains(("EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n" + (("  EnumerableCalc(expr#0..3=[{inputs}], expr#4=[FLAG(HOUR)], expr#5=[FLOOR($t0, $t4)], ROWTIME=[$t5], PRODUCT=[$t2], UNITS=[$t3])\n" + "    EnumerableInterpreter\n") + "      BindableTableScan(table=[[STREAMS, ORDERS, (STREAM)]])"))).returns(startsWith("ROWTIME=2015-02-15 10:00:00; PRODUCT=paper; UNITS=5", "ROWTIME=2015-02-15 10:00:00; PRODUCT=paint; UNITS=10", "ROWTIME=2015-02-15 10:00:00; PRODUCT=paint; UNITS=3"));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-809">[CALCITE-809]
     * TableScan does not support large/infinite scans</a>.
     */
    @Test
    public void testInfiniteStreamsDoNotBufferInMemory() {
        CalciteAssert.model(StreamTest.STREAM_MODEL).withDefaultSchema(StreamTest.INFINITE_STREAM_SCHEMA_NAME).query("select stream * from orders").limit(100).explainContains(("EnumerableInterpreter\n" + "  BindableTableScan(table=[[INFINITE_STREAMS, ORDERS, (STREAM)]])")).returnsCount(100);
    }

    @Test(timeout = 10000)
    public void testStreamCancel() {
        final String explain = "EnumerableInterpreter\n" + "  BindableTableScan(table=[[INFINITE_STREAMS, ORDERS, (STREAM)]])";
        CalciteAssert.model(StreamTest.STREAM_MODEL).withDefaultSchema(StreamTest.INFINITE_STREAM_SCHEMA_NAME).query("select stream * from orders").explainContains(explain).returns(( resultSet) -> {
            int n = 0;
            try {
                while (resultSet.next()) {
                    if ((++n) == 5) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(3);
                                resultSet.getStatement().cancel();
                            } catch (InterruptedException | SQLException e) {
                                // ignore
                            }
                        }).start();
                    }
                } 
                Assert.fail("expected cancel, got end-of-data");
            } catch (SQLException e) {
                Assert.assertThat(e.getMessage(), CoreMatchers.is("Statement canceled"));
            }
            // With a 3 millisecond delay, typically n is between 200 - 400
            // before cancel takes effect.
            Assert.assertTrue(("n is " + n), (n > 5));
        });
    }

    @Test
    public void testStreamToRelationJoin() {
        CalciteAssert.model(StreamTest.STREAM_JOINS_MODEL).withDefaultSchema(StreamTest.STREAM_JOINS_SCHEMA_NAME).query(("select stream " + ("orders.rowtime as rowtime, orders.id as orderId, products.supplier as supplierId " + "from orders join products on orders.product = products.id"))).convertContains(("LogicalDelta\n" + (((("  LogicalProject(ROWTIME=[$0], ORDERID=[$1], SUPPLIERID=[$6])\n" + "    LogicalJoin(condition=[=($4, $5)], joinType=[inner])\n") + "      LogicalProject(ROWTIME=[$0], ID=[$1], PRODUCT=[$2], UNITS=[$3], PRODUCT0=[CAST($2):VARCHAR(32) NOT NULL])\n") + "        LogicalTableScan(table=[[STREAM_JOINS, ORDERS]])\n") + "      LogicalTableScan(table=[[STREAM_JOINS, PRODUCTS]])\n"))).explainContains(("" + (((((("EnumerableCalc(expr#0..6=[{inputs}], proj#0..1=[{exprs}], SUPPLIERID=[$t6])\n" + "  EnumerableJoin(condition=[=($4, $5)], joinType=[inner])\n") + "    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[CAST($t2):VARCHAR(32) NOT NULL], proj#0..4=[{exprs}])\n") + "      EnumerableInterpreter\n") + "        BindableTableScan(table=[[STREAM_JOINS, ORDERS, (STREAM)]])\n") + "    EnumerableInterpreter\n") + "      BindableTableScan(table=[[STREAM_JOINS, PRODUCTS]])"))).returns(startsWith("ROWTIME=2015-02-15 10:15:00; ORDERID=1; SUPPLIERID=1", "ROWTIME=2015-02-15 10:24:15; ORDERID=2; SUPPLIERID=0", "ROWTIME=2015-02-15 10:24:45; ORDERID=3; SUPPLIERID=1"));
    }

    /**
     * Base table for the Orders table. Manages the base schema used for the test tables and common
     * functions.
     */
    private abstract static class BaseOrderStreamTable implements ScannableTable {
        protected final RelProtoDataType protoRowType = ( a0) -> a0.builder().add("ROWTIME", SqlTypeName.TIMESTAMP).add("ID", SqlTypeName.INTEGER).add("PRODUCT", SqlTypeName.VARCHAR, 10).add("UNITS", SqlTypeName.INTEGER).build();

        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            return protoRowType.apply(typeFactory);
        }

        public Statistic getStatistic() {
            return Statistics.of(100.0, ImmutableList.of(), RelCollations.createSingleton(0));
        }

        public TableType getJdbcTableType() {
            return TableType.TABLE;
        }

        @Override
        public boolean isRolledUp(String column) {
            return false;
        }

        @Override
        public boolean rolledUpColumnValidInsideAgg(String column, SqlCall call, SqlNode parent, CalciteConnectionConfig config) {
            return false;
        }
    }

    /**
     * Mock table that returns a stream of orders from a fixed array.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class OrdersStreamTableFactory implements TableFactory<Table> {
        // public constructor, per factory contract
        public OrdersStreamTableFactory() {
        }

        public Table create(SchemaPlus schema, String name, Map<String, Object> operand, RelDataType rowType) {
            return new StreamTest.OrdersTable(StreamTest.OrdersStreamTableFactory.getRowList());
        }

        public static ImmutableList<Object[]> getRowList() {
            final Object[][] rows = new Object[][]{ new Object[]{ StreamTest.OrdersStreamTableFactory.ts(10, 15, 0), 1, "paint", 10 }, new Object[]{ StreamTest.OrdersStreamTableFactory.ts(10, 24, 15), 2, "paper", 5 }, new Object[]{ StreamTest.OrdersStreamTableFactory.ts(10, 24, 45), 3, "brush", 12 }, new Object[]{ StreamTest.OrdersStreamTableFactory.ts(10, 58, 0), 4, "paint", 3 }, new Object[]{ StreamTest.OrdersStreamTableFactory.ts(11, 10, 0), 5, "paint", 3 } };
            return ImmutableList.copyOf(rows);
        }

        private static Object ts(int h, int m, int s) {
            return DateTimeUtils.unixTimestamp(2015, 2, 15, h, m, s);
        }
    }

    /**
     * Table representing the ORDERS stream.
     */
    public static class OrdersTable extends StreamTest.BaseOrderStreamTable implements StreamableTable {
        private final ImmutableList<Object[]> rows;

        public OrdersTable(ImmutableList<Object[]> rows) {
            this.rows = rows;
        }

        public Enumerable<Object[]> scan(DataContext root) {
            return Linq4j.asEnumerable(rows);
        }

        @Override
        public Table stream() {
            return new StreamTest.OrdersTable(rows);
        }

        @Override
        public boolean isRolledUp(String column) {
            return false;
        }

        @Override
        public boolean rolledUpColumnValidInsideAgg(String column, SqlCall call, SqlNode parent, CalciteConnectionConfig config) {
            return false;
        }
    }

    /**
     * Mock table that returns a stream of orders from a fixed array.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class InfiniteOrdersStreamTableFactory implements TableFactory<Table> {
        // public constructor, per factory contract
        public InfiniteOrdersStreamTableFactory() {
        }

        public Table create(SchemaPlus schema, String name, Map<String, Object> operand, RelDataType rowType) {
            return new StreamTest.InfiniteOrdersTable();
        }
    }

    /**
     * Table representing an infinitely larger ORDERS stream.
     */
    public static class InfiniteOrdersTable extends StreamTest.BaseOrderStreamTable implements StreamableTable {
        public Enumerable<Object[]> scan(DataContext root) {
            return Linq4j.asEnumerable(() -> new Iterator<Object[]>() {
                private final String[] items = { "paint", "paper", "brush" };

                private int counter = 0;

                public boolean hasNext() {
                    return true;
                }

                public Object[] next() {
                    final int index = (counter)++;
                    return new Object[]{ System.currentTimeMillis(), index, items[(index % items.length)], 10 };
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            });
        }

        public Table stream() {
            return this;
        }
    }

    /**
     * Table representing the history of the ORDERS stream.
     */
    public static class OrdersHistoryTable extends StreamTest.BaseOrderStreamTable {
        private final ImmutableList<Object[]> rows;

        public OrdersHistoryTable(ImmutableList<Object[]> rows) {
            this.rows = rows;
        }

        public Enumerable<Object[]> scan(DataContext root) {
            return Linq4j.asEnumerable(rows);
        }
    }

    /**
     * Mocks a simple relation to use for stream joining test.
     */
    public static class ProductsTableFactory implements TableFactory<Table> {
        public Table create(SchemaPlus schema, String name, Map<String, Object> operand, RelDataType rowType) {
            final Object[][] rows = new Object[][]{ new Object[]{ "paint", 1 }, new Object[]{ "paper", 0 }, new Object[]{ "brush", 1 } };
            return new StreamTest.ProductsTable(ImmutableList.copyOf(rows));
        }
    }

    /**
     * Table representing the PRODUCTS relation.
     */
    public static class ProductsTable implements ScannableTable {
        private final ImmutableList<Object[]> rows;

        public ProductsTable(ImmutableList<Object[]> rows) {
            this.rows = rows;
        }

        private final RelProtoDataType protoRowType = ( a0) -> a0.builder().add("ID", SqlTypeName.VARCHAR, 32).add("SUPPLIER", SqlTypeName.INTEGER).build();

        public Enumerable<Object[]> scan(DataContext root) {
            return Linq4j.asEnumerable(rows);
        }

        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            return protoRowType.apply(typeFactory);
        }

        public Statistic getStatistic() {
            return Statistics.of(200.0, ImmutableList.of());
        }

        public TableType getJdbcTableType() {
            return TableType.TABLE;
        }

        @Override
        public boolean isRolledUp(String column) {
            return false;
        }

        @Override
        public boolean rolledUpColumnValidInsideAgg(String column, SqlCall call, SqlNode parent, CalciteConnectionConfig config) {
            return false;
        }
    }
}

/**
 * End StreamTest.java
 */
