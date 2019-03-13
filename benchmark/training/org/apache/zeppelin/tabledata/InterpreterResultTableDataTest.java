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
package org.apache.zeppelin.tabledata;


import InterpreterResult.Type;
import java.util.Iterator;
import junit.framework.TestCase;
import org.apache.zeppelin.interpreter.InterpreterResultMessage;
import org.junit.Assert;
import org.junit.Test;


public class InterpreterResultTableDataTest {
    @Test
    public void test() {
        InterpreterResultMessage msg = new InterpreterResultMessage(Type.TABLE, "key\tvalue\nsun\t100\nmoon\t200\n");
        InterpreterResultTableData table = new InterpreterResultTableData(msg);
        ColumnDef[] cols = table.columns();
        Assert.assertEquals(2, cols.length);
        Assert.assertEquals("key", cols[0].name());
        Assert.assertEquals("value", cols[1].name());
        Iterator<Row> it = table.rows();
        Row row = it.next();
        Assert.assertEquals(2, row.get().length);
        Assert.assertEquals("sun", row.get()[0]);
        Assert.assertEquals("100", row.get()[1]);
        row = it.next();
        Assert.assertEquals("moon", row.get()[0]);
        Assert.assertEquals("200", row.get()[1]);
        TestCase.assertFalse(it.hasNext());
    }
}

