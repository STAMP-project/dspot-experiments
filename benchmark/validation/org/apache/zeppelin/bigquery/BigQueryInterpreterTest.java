/**
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.bigquery;


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import InterpreterResult.Type.TABLE;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterGroup;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;


public class BigQueryInterpreterTest {
    protected static class Constants {
        private String projectId;

        private String oneQuery;

        private String wrongQuery;

        public String getProjectId() {
            return projectId;
        }

        public String getOne() {
            return oneQuery;
        }

        public String getWrong() {
            return wrongQuery;
        }
    }

    protected static BigQueryInterpreterTest.Constants constants = null;

    public BigQueryInterpreterTest() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        if ((BigQueryInterpreterTest.constants) == null) {
            InputStream is = this.getClass().getResourceAsStream("/constants.json");
            BigQueryInterpreterTest.constants = new Gson().<BigQueryInterpreterTest.Constants>fromJson(new InputStreamReader(is), BigQueryInterpreterTest.Constants.class);
        }
    }

    private InterpreterGroup intpGroup;

    private BigQueryInterpreter bqInterpreter;

    private InterpreterContext context;

    @Test
    public void sqlSuccess() {
        InterpreterResult ret = bqInterpreter.interpret(BigQueryInterpreterTest.constants.getOne(), context);
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertEquals(ret.message().get(0).getType(), TABLE);
    }

    @Test
    public void badSqlSyntaxFails() {
        InterpreterResult ret = bqInterpreter.interpret(BigQueryInterpreterTest.constants.getWrong(), context);
        Assert.assertEquals(ERROR, ret.code());
    }

    @Test
    public void testWithQueryPrefix() {
        InterpreterResult ret = bqInterpreter.interpret("#standardSQL\n WITH t AS (select 1) SELECT * FROM t", context);
        Assert.assertEquals(SUCCESS, ret.code());
    }

    @Test
    public void testInterpreterOutputData() {
        InterpreterResult ret = bqInterpreter.interpret("SELECT 1 AS col1, 2 AS col2", context);
        String[] lines = ret.message().get(0).getData().split("\\n");
        Assert.assertEquals(2, lines.length);
        Assert.assertEquals("col1\tcol2", lines[0]);
        Assert.assertEquals("1\t2", lines[1]);
    }
}

