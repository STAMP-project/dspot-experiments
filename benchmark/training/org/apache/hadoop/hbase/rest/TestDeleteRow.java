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
package org.apache.hadoop.hbase.rest;


import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.rest.client.Response;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RestTests.class, MediumTests.class })
public class TestDeleteRow extends RowResourceBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDeleteRow.class);

    @Test
    public void testDeleteNonExistentColumn() throws Exception {
        Response response = RowResourceBase.putValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.checkAndDeleteJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(304, response.getCode());
        Assert.assertEquals(200, RowResourceBase.getValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1).getCode());
        response = RowResourceBase.checkAndDeleteJson(RowResourceBase.TABLE, RowResourceBase.ROW_2, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_2);
        Assert.assertEquals(304, response.getCode());
        Assert.assertEquals(200, RowResourceBase.getValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1).getCode());
        response = RowResourceBase.checkAndDeleteJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, "dummy", RowResourceBase.VALUE_1);
        Assert.assertEquals(400, response.getCode());
        Assert.assertEquals(200, RowResourceBase.getValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1).getCode());
        response = RowResourceBase.checkAndDeleteJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, "dummy:test", RowResourceBase.VALUE_1);
        Assert.assertEquals(404, response.getCode());
        Assert.assertEquals(200, RowResourceBase.getValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1).getCode());
        response = RowResourceBase.checkAndDeleteJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, "a:test", RowResourceBase.VALUE_1);
        Assert.assertEquals(304, response.getCode());
        Assert.assertEquals(200, RowResourceBase.getValueJson(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1).getCode());
    }

    @Test
    public void testDeleteXML() throws IOException, JAXBException {
        Response response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        Assert.assertEquals(200, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        response = RowResourceBase.deleteValue(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.getValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        RowResourceBase.checkValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2, RowResourceBase.VALUE_2);
        response = RowResourceBase.putValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.checkAndDeletePB(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1, RowResourceBase.VALUE_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.getValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        response = RowResourceBase.deleteRow(RowResourceBase.TABLE, RowResourceBase.ROW_1);
        Assert.assertEquals(200, response.getCode());
        response = RowResourceBase.getValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        response = RowResourceBase.getValueXML(RowResourceBase.TABLE, RowResourceBase.ROW_1, RowResourceBase.COLUMN_2);
        Assert.assertEquals(404, response.getCode());
        // Delete a row in non existent table
        response = RowResourceBase.deleteValue("dummy", RowResourceBase.ROW_1, RowResourceBase.COLUMN_1);
        Assert.assertEquals(404, response.getCode());
        // Delete non existent column
        response = RowResourceBase.deleteValue(RowResourceBase.TABLE, RowResourceBase.ROW_1, "dummy");
        Assert.assertEquals(404, response.getCode());
    }
}

