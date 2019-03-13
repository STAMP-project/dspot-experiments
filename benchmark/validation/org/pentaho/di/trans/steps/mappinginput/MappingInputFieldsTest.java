/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.mappinginput;


import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_STRING;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.BlockingRowSet;
import org.pentaho.di.core.plugins.Plugin;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mapping.MappingValueRename;


public class MappingInputFieldsTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static Plugin p1;

    private static Plugin p2;

    MappingInput step;

    MappingInputMeta meta;

    /**
     * verifies: If SelectingAndSortingUnspecifiedFields checkbox is checked, then
     * <ol>
     * <li>all fields throw to the next step;
     * <li>fields are resorted: mapped fields, then alphabetical sorted not mapped fields.
     * </ol>
     */
    @Test
    public void testSelectingAndSortingUnspecifiedFields() throws Exception {
        meta.setSelectingAndSortingUnspecifiedFields(true);
        MappingInputData sdi = new MappingInputData();
        sdi.linked = true;
        sdi.valueRenames = new ArrayList<MappingValueRename>();
        sdi.valueRenames.add(new MappingValueRename("number2", "n2"));
        sdi.valueRenames.add(new MappingValueRename("number4", "n4"));
        BlockingRowSet in = new BlockingRowSet(10);
        BlockingRowSet out = new BlockingRowSet(10);
        RowMeta rm = new RowMeta();
        rm.addValueMeta(new ValueMetaString("string"));
        rm.addValueMeta(new ValueMetaInteger("number1"));
        rm.addValueMeta(new ValueMetaInteger("number2"));
        rm.addValueMeta(new ValueMetaInteger("number3"));
        rm.addValueMeta(new ValueMetaInteger("number"));
        rm.addValueMeta(new ValueMetaInteger("number4"));
        rm.addValueMeta(new ValueMetaInteger("number5"));
        in.putRow(rm, new Object[]{ "str", new Integer(100501), new Integer(100502), new Integer(100503), new Integer(100500), new Integer(100504), new Integer(100505) });
        in.putRow(rm, new Object[]{ "str_1", new Integer(200501), new Integer(200502), new Integer(200503), new Integer(200500), new Integer(200504), new Integer(200505) });
        step.addRowSetToInputRowSets(in);
        step.addRowSetToOutputRowSets(out);
        Assert.assertTrue(step.init(meta, sdi));
        Assert.assertTrue(step.processRow(meta, sdi));
        Object[] outRowData = out.getRow();
        RowMetaInterface outMeta = out.getRowMeta();
        Assert.assertEquals("All fields are expected.", 7, outMeta.size());
        int i = 0;
        // Check if row-meta is formed according to the step specification
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "n2", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "n4", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number1", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number3", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number5", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_STRING, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "string", outMeta.getValueMeta((i++)).getName());
        // Check if row-data corresponds to the row-meta
        Assert.assertEquals("the field value mismatch.", new Integer(100502), outRowData[0]);
        Assert.assertEquals("the field value mismatch.", new Integer(100504), outRowData[1]);
        Assert.assertEquals("the field value mismatch.", new Integer(100500), outRowData[2]);
        Assert.assertEquals("the field value mismatch.", new Integer(100501), outRowData[3]);
        Assert.assertEquals("the field value mismatch.", new Integer(100503), outRowData[4]);
        Assert.assertEquals("the field value mismatch.", new Integer(100505), outRowData[5]);
        Assert.assertEquals("the field value mismatch.", "str", outRowData[6]);
        Assert.assertTrue(step.processRow(meta, sdi));
        outRowData = out.getRow();
        outMeta = out.getRowMeta();
        Assert.assertEquals("All fields are expected.", 7, outMeta.size());
        i = 0;
        // Check if row-meta is formed according to the step specification
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "n2", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "n4", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number1", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number3", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number5", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_STRING, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "string", outMeta.getValueMeta((i++)).getName());
        // Check if row-data corresponds to the row-meta
        Assert.assertEquals("the field value mismatch.", new Integer(200502), outRowData[0]);
        Assert.assertEquals("the field value mismatch.", new Integer(200504), outRowData[1]);
        Assert.assertEquals("the field value mismatch.", new Integer(200500), outRowData[2]);
        Assert.assertEquals("the field value mismatch.", new Integer(200501), outRowData[3]);
        Assert.assertEquals("the field value mismatch.", new Integer(200503), outRowData[4]);
        Assert.assertEquals("the field value mismatch.", new Integer(200505), outRowData[5]);
        Assert.assertEquals("the field value mismatch.", "str_1", outRowData[6]);
    }

    /* verifies: If SelectingAndSortingUnspecifiedFields checkbox is not checked, then 1)all fields throw to the next step;
    2)fields are not resorted;
     */
    @Test
    public void testOnlySpecifiedFields() throws Exception {
        meta.setSelectingAndSortingUnspecifiedFields(false);
        MappingInputData sdi = new MappingInputData();
        sdi.linked = true;
        sdi.valueRenames = new ArrayList<MappingValueRename>();
        sdi.valueRenames.add(new MappingValueRename("number2", "n2"));
        sdi.valueRenames.add(new MappingValueRename("number4", "n4"));
        BlockingRowSet in = new BlockingRowSet(10);
        BlockingRowSet out = new BlockingRowSet(10);
        RowMeta rm = new RowMeta();
        rm.addValueMeta(new ValueMetaString("string"));
        rm.addValueMeta(new ValueMetaInteger("number1"));
        rm.addValueMeta(new ValueMetaInteger("number2"));
        rm.addValueMeta(new ValueMetaInteger("number3"));
        rm.addValueMeta(new ValueMetaInteger("number"));
        rm.addValueMeta(new ValueMetaInteger("number4"));
        rm.addValueMeta(new ValueMetaInteger("number5"));
        in.putRow(rm, new Object[]{ "str", new Integer(100501), new Integer(100502), new Integer(100503), new Integer(100500), new Integer(100504), new Integer(100505) });
        in.putRow(rm, new Object[]{ "str_1", new Integer(200501), new Integer(200502), new Integer(200503), new Integer(200500), new Integer(200504), new Integer(200505) });
        step.addRowSetToInputRowSets(in);
        step.addRowSetToOutputRowSets(out);
        Assert.assertTrue(step.init(meta, sdi));
        Assert.assertTrue(step.processRow(meta, sdi));
        Object[] outRowData = out.getRow();
        RowMetaInterface outMeta = out.getRowMeta();
        Assert.assertEquals("All fields are expected.", 7, outMeta.size());
        int i = 0;
        // Check if row-meta is formed according to the step specification
        Assert.assertEquals("the field type-meta mismatch.", TYPE_STRING, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "string", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number1", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "n2", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number3", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "n4", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number5", outMeta.getValueMeta((i++)).getName());
        // Check if row-data corresponds to the row-meta
        Assert.assertEquals("the field value mismatch.", "str", outRowData[0]);
        Assert.assertEquals("the field value mismatch.", new Integer(100501), outRowData[1]);
        Assert.assertEquals("the field value mismatch.", new Integer(100502), outRowData[2]);
        Assert.assertEquals("the field value mismatch.", new Integer(100503), outRowData[3]);
        Assert.assertEquals("the field value mismatch.", new Integer(100500), outRowData[4]);
        Assert.assertEquals("the field value mismatch.", new Integer(100504), outRowData[5]);
        Assert.assertEquals("the field value mismatch.", new Integer(100505), outRowData[6]);
        Assert.assertTrue(step.processRow(meta, sdi));
        outRowData = out.getRow();
        outMeta = out.getRowMeta();
        Assert.assertEquals("All fields are expected.", 7, outMeta.size());
        i = 0;
        // Check if row-meta is formed according to the step specification
        Assert.assertEquals("the field type-meta mismatch.", TYPE_STRING, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "string", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number1", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "n2", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number3", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "n4", outMeta.getValueMeta((i++)).getName());
        Assert.assertEquals("the field type-meta mismatch.", TYPE_INTEGER, outMeta.getValueMeta(i).getType());
        Assert.assertEquals("the field name-meta mismatch.", "number5", outMeta.getValueMeta((i++)).getName());
        // Check if row-data corresponds to the row-meta
        Assert.assertEquals("the field value mismatch.", "str_1", outRowData[0]);
        Assert.assertEquals("the field value mismatch.", new Integer(200501), outRowData[1]);
        Assert.assertEquals("the field value mismatch.", new Integer(200502), outRowData[2]);
        Assert.assertEquals("the field value mismatch.", new Integer(200503), outRowData[3]);
        Assert.assertEquals("the field value mismatch.", new Integer(200500), outRowData[4]);
        Assert.assertEquals("the field value mismatch.", new Integer(200504), outRowData[5]);
        Assert.assertEquals("the field value mismatch.", new Integer(200505), outRowData[6]);
    }
}

