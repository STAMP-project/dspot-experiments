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
package org.pentaho.di.core.row;


import ValueMetaInterface.TYPE_BIGNUMBER;
import ValueMetaInterface.TYPE_DATE;
import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_NUMBER;
import ValueMetaInterface.TYPE_STRING;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class RowMetaAddRemoveValueTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    @Test
    public void testAddRemoveValue() throws Exception {
        RowMetaInterface rowMeta = new RowMeta();
        // Add values
        ValueMetaInterface a = ValueMetaFactory.createValueMeta("a", TYPE_STRING);
        rowMeta.addValueMeta(a);
        Assert.assertEquals(1, rowMeta.size());
        ValueMetaInterface b = ValueMetaFactory.createValueMeta("b", TYPE_INTEGER);
        rowMeta.addValueMeta(b);
        Assert.assertEquals(2, rowMeta.size());
        ValueMetaInterface c = ValueMetaFactory.createValueMeta("c", TYPE_DATE);
        rowMeta.addValueMeta(c);
        Assert.assertEquals(3, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        Assert.assertEquals(1, rowMeta.indexOfValue("b"));
        Assert.assertEquals(2, rowMeta.indexOfValue("c"));
        ValueMetaInterface d = ValueMetaFactory.createValueMeta("d", TYPE_NUMBER);
        rowMeta.addValueMeta(0, d);
        Assert.assertEquals(4, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("d"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a"));
        Assert.assertEquals(2, rowMeta.indexOfValue("b"));
        Assert.assertEquals(3, rowMeta.indexOfValue("c"));
        ValueMetaInterface e = ValueMetaFactory.createValueMeta("e", TYPE_BIGNUMBER);
        rowMeta.addValueMeta(2, e);
        Assert.assertEquals(5, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("d"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a"));
        Assert.assertEquals(2, rowMeta.indexOfValue("e"));
        Assert.assertEquals(3, rowMeta.indexOfValue("b"));
        Assert.assertEquals(4, rowMeta.indexOfValue("c"));
        // Remove values in reverse order
        rowMeta.removeValueMeta("e");
        Assert.assertEquals(4, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("d"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a"));
        Assert.assertEquals(2, rowMeta.indexOfValue("b"));
        Assert.assertEquals(3, rowMeta.indexOfValue("c"));
        rowMeta.removeValueMeta("d");
        Assert.assertEquals(3, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        Assert.assertEquals(1, rowMeta.indexOfValue("b"));
        Assert.assertEquals(2, rowMeta.indexOfValue("c"));
        rowMeta.removeValueMeta("c");
        Assert.assertEquals(2, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        Assert.assertEquals(1, rowMeta.indexOfValue("b"));
        rowMeta.removeValueMeta("b");
        Assert.assertEquals(1, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        rowMeta.removeValueMeta("a");
        Assert.assertEquals(0, rowMeta.size());
    }

    @Test
    public void testAddRemoveRenameValue() throws Exception {
        RowMetaInterface rowMeta = new RowMeta();
        // Add values
        ValueMetaInterface a = ValueMetaFactory.createValueMeta("a", TYPE_STRING);
        rowMeta.addValueMeta(a);
        Assert.assertEquals(1, rowMeta.size());
        ValueMetaInterface b = ValueMetaFactory.createValueMeta("a", TYPE_INTEGER);
        rowMeta.addValueMeta(b);
        Assert.assertEquals(2, rowMeta.size());
        ValueMetaInterface c = ValueMetaFactory.createValueMeta("a", TYPE_DATE);
        rowMeta.addValueMeta(c);
        Assert.assertEquals(3, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a_1"));
        Assert.assertEquals(2, rowMeta.indexOfValue("a_2"));
        ValueMetaInterface d = ValueMetaFactory.createValueMeta("a", TYPE_NUMBER);
        rowMeta.addValueMeta(0, d);
        Assert.assertEquals(4, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a_3"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a"));
        Assert.assertEquals(2, rowMeta.indexOfValue("a_1"));
        Assert.assertEquals(3, rowMeta.indexOfValue("a_2"));
        ValueMetaInterface e = ValueMetaFactory.createValueMeta("a", TYPE_BIGNUMBER);
        rowMeta.addValueMeta(2, e);
        Assert.assertEquals(5, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a_3"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a"));
        Assert.assertEquals(2, rowMeta.indexOfValue("a_4"));
        Assert.assertEquals(3, rowMeta.indexOfValue("a_1"));
        Assert.assertEquals(4, rowMeta.indexOfValue("a_2"));
        // Remove values in reverse order
        rowMeta.removeValueMeta("a_4");
        Assert.assertEquals(4, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a_3"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a"));
        Assert.assertEquals(2, rowMeta.indexOfValue("a_1"));
        Assert.assertEquals(3, rowMeta.indexOfValue("a_2"));
        rowMeta.removeValueMeta("a_3");
        Assert.assertEquals(3, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a_1"));
        Assert.assertEquals(2, rowMeta.indexOfValue("a_2"));
        rowMeta.removeValueMeta("a_2");
        Assert.assertEquals(2, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a_1"));
        rowMeta.removeValueMeta("a_1");
        Assert.assertEquals(1, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        rowMeta.removeValueMeta("a");
        Assert.assertEquals(0, rowMeta.size());
    }

    @Test
    public void testAddRemoveValueCaseInsensitive() throws Exception {
        RowMetaInterface rowMeta = new RowMeta();
        // Add values
        ValueMetaInterface a = ValueMetaFactory.createValueMeta("A", TYPE_STRING);
        rowMeta.addValueMeta(a);
        Assert.assertEquals(1, rowMeta.size());
        ValueMetaInterface b = ValueMetaFactory.createValueMeta("b", TYPE_INTEGER);
        rowMeta.addValueMeta(b);
        Assert.assertEquals(2, rowMeta.size());
        ValueMetaInterface c = ValueMetaFactory.createValueMeta("C", TYPE_DATE);
        rowMeta.addValueMeta(c);
        Assert.assertEquals(3, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        Assert.assertEquals(1, rowMeta.indexOfValue("B"));
        Assert.assertEquals(2, rowMeta.indexOfValue("c"));
        ValueMetaInterface d = ValueMetaFactory.createValueMeta("d", TYPE_NUMBER);
        rowMeta.addValueMeta(0, d);
        Assert.assertEquals(4, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("D"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a"));
        Assert.assertEquals(2, rowMeta.indexOfValue("B"));
        Assert.assertEquals(3, rowMeta.indexOfValue("c"));
        ValueMetaInterface e = ValueMetaFactory.createValueMeta("E", TYPE_BIGNUMBER);
        rowMeta.addValueMeta(2, e);
        Assert.assertEquals(5, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("D"));
        Assert.assertEquals(1, rowMeta.indexOfValue("a"));
        Assert.assertEquals(2, rowMeta.indexOfValue("e"));
        Assert.assertEquals(3, rowMeta.indexOfValue("b"));
        Assert.assertEquals(4, rowMeta.indexOfValue("c"));
        // Remove values in reverse order
        rowMeta.removeValueMeta("e");
        Assert.assertEquals(4, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("d"));
        Assert.assertEquals(1, rowMeta.indexOfValue("A"));
        Assert.assertEquals(2, rowMeta.indexOfValue("b"));
        Assert.assertEquals(3, rowMeta.indexOfValue("C"));
        rowMeta.removeValueMeta("D");
        Assert.assertEquals(3, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        Assert.assertEquals(1, rowMeta.indexOfValue("B"));
        Assert.assertEquals(2, rowMeta.indexOfValue("c"));
        rowMeta.removeValueMeta("c");
        Assert.assertEquals(2, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        Assert.assertEquals(1, rowMeta.indexOfValue("B"));
        rowMeta.removeValueMeta("b");
        Assert.assertEquals(1, rowMeta.size());
        Assert.assertEquals(0, rowMeta.indexOfValue("a"));
        rowMeta.removeValueMeta("a");
        Assert.assertEquals(0, rowMeta.size());
    }
}

