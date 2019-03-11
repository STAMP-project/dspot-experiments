/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.operator;


import Order.ASCENDING;
import Order.DESCENDING;
import java.io.Serializable;
import org.apache.flink.api.common.InvalidProgramException;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Test;


/**
 * Tests for partitioning.
 */
public class PartitionOperatorTest {
    /**
     * Custom data type, for testing purposes.
     */
    public static class CustomPojo implements Serializable , Comparable<PartitionOperatorTest.CustomPojo> {
        private Integer number;

        private String name;

        public CustomPojo() {
        }

        public CustomPojo(Integer number, String name) {
            this.number = number;
            this.name = name;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int compareTo(PartitionOperatorTest.CustomPojo o) {
            return Integer.compare(this.number, o.number);
        }
    }

    /**
     * Custom data type with nested type, for testing purposes.
     */
    public static class NestedPojo implements Serializable {
        private PartitionOperatorTest.CustomPojo nested;

        private Long outer;

        public NestedPojo() {
        }

        public NestedPojo(PartitionOperatorTest.CustomPojo nested, Long outer) {
            this.nested = nested;
            this.outer = outer;
        }

        public PartitionOperatorTest.CustomPojo getNested() {
            return nested;
        }

        public void setNested(PartitionOperatorTest.CustomPojo nested) {
            this.nested = nested;
        }

        public Long getOuter() {
            return outer;
        }

        public void setOuter(Long outer) {
            this.outer = outer;
        }
    }

    @Test
    public void testRebalance() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.rebalance();
    }

    @Test
    public void testHashPartitionByField1() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionByHash(0);
    }

    @Test
    public void testHashPartitionByField2() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionByHash(0, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testHashPartitionByFieldOutOfRange() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionByHash(0, 1, 2);
    }

    @Test
    public void testHashPartitionByFieldName1() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.CustomPojo> ds = getPojoDataSet(env);
        ds.partitionByHash("number");
    }

    @Test
    public void testHashPartitionByFieldName2() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.CustomPojo> ds = getPojoDataSet(env);
        ds.partitionByHash("number", "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashPartitionByInvalidFieldName() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.CustomPojo> ds = getPojoDataSet(env);
        ds.partitionByHash("number", "name", "invalidField");
    }

    @Test
    public void testRangePartitionByFieldName1() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.CustomPojo> ds = getPojoDataSet(env);
        ds.partitionByRange("number");
    }

    @Test
    public void testRangePartitionByFieldName2() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.CustomPojo> ds = getPojoDataSet(env);
        ds.partitionByRange("number", "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangePartitionByInvalidFieldName() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.CustomPojo> ds = getPojoDataSet(env);
        ds.partitionByRange("number", "name", "invalidField");
    }

    @Test
    public void testRangePartitionByField1() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionByRange(0);
    }

    @Test
    public void testRangePartitionByField2() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionByRange(0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangePartitionWithEmptyIndicesKey() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSource<Tuple2<Tuple2<Integer, Integer>, Integer>> ds = env.fromElements(new Tuple2(new Tuple2(1, 1), 1), new Tuple2(new Tuple2(2, 2), 2), new Tuple2(new Tuple2(2, 2), 2));
        ds.partitionByRange(new int[]{  });
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRangePartitionByFieldOutOfRange() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionByRange(0, 1, 2);
    }

    @Test(expected = IllegalStateException.class)
    public void testHashPartitionWithOrders() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionByHash(1).withOrders(ASCENDING);
    }

    @Test(expected = IllegalStateException.class)
    public void testRebalanceWithOrders() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.rebalance().withOrders(ASCENDING);
    }

    @Test
    public void testRangePartitionWithOrders() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionByRange(0).withOrders(ASCENDING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangePartitionWithTooManyOrders() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionByRange(0).withOrders(ASCENDING, DESCENDING);
    }

    @Test
    public void testRangePartitionByComplexKeyWithOrders() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSource<Tuple2<Tuple2<Integer, Integer>, Integer>> ds = env.fromElements(new Tuple2(new Tuple2(1, 1), 1), new Tuple2(new Tuple2(2, 2), 2), new Tuple2(new Tuple2(2, 2), 2));
        ds.partitionByRange(0, 1).withOrders(ASCENDING, DESCENDING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangePartitionByComplexKeyWithTooManyOrders() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSource<Tuple2<Tuple2<Integer, Integer>, Integer>> ds = env.fromElements(new Tuple2(new Tuple2(1, 1), 1), new Tuple2(new Tuple2(2, 2), 2), new Tuple2(new Tuple2(2, 2), 2));
        ds.partitionByRange(0).withOrders(ASCENDING, DESCENDING);
    }

    @Test
    public void testRangePartitionBySelectorComplexKeyWithOrders() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.NestedPojo> ds = getNestedPojoDataSet(env);
        ds.partitionByRange(new org.apache.flink.api.java.functions.KeySelector<PartitionOperatorTest.NestedPojo, PartitionOperatorTest.CustomPojo>() {
            @Override
            public PartitionOperatorTest.CustomPojo getKey(PartitionOperatorTest.NestedPojo value) throws Exception {
                return value.getNested();
            }
        }).withOrders(ASCENDING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangePartitionBySelectorComplexKeyWithTooManyOrders() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.NestedPojo> ds = getNestedPojoDataSet(env);
        ds.partitionByRange(new org.apache.flink.api.java.functions.KeySelector<PartitionOperatorTest.NestedPojo, PartitionOperatorTest.CustomPojo>() {
            @Override
            public PartitionOperatorTest.CustomPojo getKey(PartitionOperatorTest.NestedPojo value) throws Exception {
                return value.getNested();
            }
        }).withOrders(ASCENDING, DESCENDING);
    }

    @Test
    public void testRangePartitionCustomPartitionerByFieldId() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionCustom(new org.apache.flink.api.common.functions.Partitioner<Integer>() {
            @Override
            public int partition(Integer key, int numPartitions) {
                return 1;
            }
        }, 0);
    }

    @Test(expected = InvalidProgramException.class)
    public void testRangePartitionInvalidCustomPartitionerByFieldId() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<Tuple2<Integer, String>> ds = getTupleDataSet(env);
        ds.partitionCustom(new org.apache.flink.api.common.functions.Partitioner<Integer>() {
            @Override
            public int partition(Integer key, int numPartitions) {
                return 1;
            }
        }, 1);
    }

    @Test
    public void testRangePartitionCustomPartitionerByFieldName() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.CustomPojo> ds = getPojoDataSet(env);
        ds.partitionCustom(new org.apache.flink.api.common.functions.Partitioner<Integer>() {
            @Override
            public int partition(Integer key, int numPartitions) {
                return 1;
            }
        }, "number");
    }

    @Test(expected = InvalidProgramException.class)
    public void testRangePartitionInvalidCustomPartitionerByFieldName() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.CustomPojo> ds = getPojoDataSet(env);
        ds.partitionCustom(new org.apache.flink.api.common.functions.Partitioner<Integer>() {
            @Override
            public int partition(Integer key, int numPartitions) {
                return 1;
            }
        }, "name");
    }

    @Test
    public void testRangePartitionCustomPartitionerByKeySelector() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final DataSet<PartitionOperatorTest.CustomPojo> ds = getPojoDataSet(env);
        ds.partitionCustom(new org.apache.flink.api.common.functions.Partitioner<Integer>() {
            @Override
            public int partition(Integer key, int numPartitions) {
                return 1;
            }
        }, new org.apache.flink.api.java.functions.KeySelector<PartitionOperatorTest.CustomPojo, Integer>() {
            @Override
            public Integer getKey(PartitionOperatorTest.CustomPojo value) throws Exception {
                return value.getNumber();
            }
        });
    }
}

