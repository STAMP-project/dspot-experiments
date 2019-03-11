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
package org.apache.zeppelin.kylin;


import InterpreterResult.Type.TABLE;
import java.util.Properties;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;


public class KylinInterpreterTest {
    static final Properties KYLIN_PROPERTIES = new Properties();

    @Test
    public void testWithDefault() {
        KylinInterpreter t = new MockKylinInterpreter(getDefaultProperties());
        InterpreterResult result = t.interpret(("select a.date,sum(b.measure) as measure from kylin_fact_table a " + "inner join kylin_lookup_table b on a.date=b.date group by a.date"), null);
        Assert.assertEquals("default", t.getProject(("select a.date,sum(b.measure) as measure " + ("from kylin_fact_table a inner join kylin_lookup_table b on a.date=b.date " + "group by a.date"))));
        Assert.assertEquals(TABLE, result.message().get(0).getType());
    }

    @Test
    public void testWithProject() {
        KylinInterpreter t = new MockKylinInterpreter(getDefaultProperties());
        Assert.assertEquals("project2", t.getProject(("(project2)\n select a.date,sum(b.measure) " + ("as measure from kylin_fact_table a inner join kylin_lookup_table b on " + "a.date=b.date group by a.date"))));
        Assert.assertEquals("", t.getProject(("()\n select a.date,sum(b.measure) as measure " + ("from kylin_fact_table a inner join kylin_lookup_table b on a.date=b.date " + "group by a.date"))));
        Assert.assertEquals(("\n select a.date,sum(b.measure) as measure from kylin_fact_table a " + "inner join kylin_lookup_table b on a.date=b.date group by a.date"), t.getSQL(("(project2)\n select a.date,sum(b.measure) as measure " + ("from kylin_fact_table a inner join kylin_lookup_table b on a.date=b.date " + "group by a.date"))));
        Assert.assertEquals(("\n select a.date,sum(b.measure) as measure from kylin_fact_table a " + "inner join kylin_lookup_table b on a.date=b.date group by a.date"), t.getSQL(("()\n select a.date,sum(b.measure) as measure from kylin_fact_table a " + "inner join kylin_lookup_table b on a.date=b.date group by a.date")));
    }

    @Test
    public void testParseResult() {
        String msg = "{\"columnMetas\":[{\"isNullable\":1,\"displaySize\":256,\"label\":\"COUNTRY\"," + (((((((((((((((((((("\"name\":\"COUNTRY\",\"schemaName\":\"DEFAULT\",\"catelogName\":null," + "\"tableName\":\"SALES_TABLE\",\"precision\":256,\"scale\":0,\"columnType\":12,") + "\"columnTypeName\":\"VARCHAR\",\"writable\":false,\"readOnly\":true,") + "\"definitelyWritable\":false,\"autoIncrement\":false,\"caseSensitive\":true,") + "\"searchable\":false,\"currency\":false,\"signed\":true},{\"isNullable\":1,") + "\"displaySize\":256,\"label\":\"CURRENCY\",\"name\":\"CURRENCY\",") + "\"schemaName\":\"DEFAULT\",\"catelogName\":null,\"tableName\":\"SALES_TABLE\",") + "\"precision\":256,\"scale\":0,\"columnType\":12,\"columnTypeName\":\"VARCHAR\",") + "\"writable\":false,\"readOnly\":true,\"definitelyWritable\":false,") + "\"autoIncrement\":false,\"caseSensitive\":true,\"searchable\":false,") + "\"currency\":false,\"signed\":true},{\"isNullable\":0,\"displaySize\":19,") + "\"label\":\"COUNT__\",\"name\":\"COUNT__\",\"schemaName\":\"DEFAULT\",") + "\"catelogName\":null,\"tableName\":\"SALES_TABLE\",\"precision\":19,\"scale\":0,") + "\"columnType\":-5,\"columnTypeName\":\"BIGINT\",\"writable\":false,") + "\"readOnly\":true,\"definitelyWritable\":false,\"autoIncrement\":false,") + "\"caseSensitive\":true,\"searchable\":false,\"currency\":false,\"signed\":true}],") + "\"results\":[[\"AMERICA\",\"USD\",null],[null,\"RMB\",0],[\"KOR\",null,100],") + "[\"\\\"abc\\\"\",\"a,b,c\",-1]],\"cube\":\"Sample_Cube\",\"affectedRowCount\":0,") + "\"isException\":false,\"exceptionMessage\":null,\"duration\":134,") + "\"totalScanCount\":1,\"hitExceptionCache\":false,\"storageCacheUsed\":false,") + "\"partial\":false}");
        String expected = "%table COUNTRY \tCURRENCY \tCOUNT__ \t \n" + ((("AMERICA \tUSD \tnull \t \n" + "null \tRMB \t0 \t \n") + "KOR \tnull \t100 \t \n") + "\\\"abc\\\" \ta,b,c \t-1 \t \n");
        KylinInterpreter t = new MockKylinInterpreter(getDefaultProperties());
        String actual = t.formatResult(msg);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseEmptyResult() {
        String msg = "{\"columnMetas\":[{\"isNullable\":1,\"displaySize\":256,\"label\":\"COUNTRY\"," + (((((((((((((((((((("\"name\":\"COUNTRY\",\"schemaName\":\"DEFAULT\",\"catelogName\":null," + "\"tableName\":\"SALES_TABLE\",\"precision\":256,\"scale\":0,\"columnType\":12,") + "\"columnTypeName\":\"VARCHAR\",\"writable\":false,\"readOnly\":true,") + "\"definitelyWritable\":false,\"autoIncrement\":false,\"caseSensitive\":true,") + "\"searchable\":false,\"currency\":false,\"signed\":true},{\"isNullable\":1,") + "\"displaySize\":256,\"label\":\"CURRENCY\",\"name\":\"CURRENCY\",") + "\"schemaName\":\"DEFAULT\",\"catelogName\":null,\"tableName\":\"SALES_TABLE\",") + "\"precision\":256,\"scale\":0,\"columnType\":12,\"columnTypeName\":\"VARCHAR\",") + "\"writable\":false,\"readOnly\":true,\"definitelyWritable\":false,") + "\"autoIncrement\":false,\"caseSensitive\":true,\"searchable\":false,") + "\"currency\":false,\"signed\":true},{\"isNullable\":0,\"displaySize\":19,") + "\"label\":\"COUNT__\",\"name\":\"COUNT__\",\"schemaName\":\"DEFAULT\",") + "\"catelogName\":null,\"tableName\":\"SALES_TABLE\",\"precision\":19,\"scale\":0,") + "\"columnType\":-5,\"columnTypeName\":\"BIGINT\",\"writable\":false,") + "\"readOnly\":true,\"definitelyWritable\":false,\"autoIncrement\":false,") + "\"caseSensitive\":true,\"searchable\":false,\"currency\":false,\"signed\":true}],") + "\"results\":[],") + "\"cube\":\"Sample_Cube\",\"affectedRowCount\":0,") + "\"isException\":false,\"exceptionMessage\":null,\"duration\":134,") + "\"totalScanCount\":1,\"hitExceptionCache\":false,\"storageCacheUsed\":false,") + "\"partial\":false}");
        String expected = "%table COUNTRY \tCURRENCY \tCOUNT__ \t \n";
        KylinInterpreter t = new MockKylinInterpreter(getDefaultProperties());
        String actual = t.formatResult(msg);
        Assert.assertEquals(expected, actual);
    }
}

