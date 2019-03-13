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
package org.apache.hadoop.hbase.rest.model;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RestTests.class, SmallTests.class })
public class TestRowModel extends TestModelBase<RowModel> {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRowModel.class);

    private static final byte[] ROW1 = Bytes.toBytes("testrow1");

    private static final byte[] COLUMN1 = Bytes.toBytes("testcolumn1");

    private static final byte[] VALUE1 = Bytes.toBytes("testvalue1");

    private static final long TIMESTAMP1 = 1245219839331L;

    public TestRowModel() throws Exception {
        super(RowModel.class);
        AS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Row key=\"dGVzdHJvdzE=\">" + "<Cell column=\"dGVzdGNvbHVtbjE=\" timestamp=\"1245219839331\">dGVzdHZhbHVlMQ==</Cell></Row>";
        AS_JSON = "{\"key\":\"dGVzdHJvdzE=\",\"Cell\":[{\"column\":\"dGVzdGNvbHVtbjE=\"," + "\"timestamp\":1245219839331,\"$\":\"dGVzdHZhbHVlMQ==\"}]}";
    }

    @Test
    public void testEquals() throws Exception {
        RowModel rowModel1 = buildTestModel();
        RowModel rowModel2 = buildTestModel();
        Assert.assertEquals(rowModel1, rowModel2);
        RowModel rowModel3 = new RowModel();
        Assert.assertFalse(rowModel1.equals(rowModel3));
    }

    @Test
    public void testToString() throws Exception {
        String expectedRowKey = ToStringBuilder.reflectionToString(TestRowModel.ROW1, ToStringStyle.SIMPLE_STYLE);
        RowModel rowModel = buildTestModel();
        System.out.println(rowModel);
        Assert.assertTrue(StringUtils.contains(rowModel.toString(), expectedRowKey));
    }
}

