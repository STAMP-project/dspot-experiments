/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.python;


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import Type.TABLE;
import java.io.IOException;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterGroup;
import org.apache.zeppelin.interpreter.InterpreterOutput;
import org.apache.zeppelin.interpreter.InterpreterOutputListener;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;


/**
 * In order for this test to work, test env must have installed:
 * <ol>
 * - <li>Python</li>
 * - <li>NumPy</li>
 * - <li>Pandas</li>
 * - <li>PandaSql</li>
 * <ol>
 * <p>
 * To run manually on such environment, use:
 * <code>
 * mvn -Dpython.test.exclude='' test -pl python -am
 * </code>
 */
public class PythonInterpreterPandasSqlTest implements InterpreterOutputListener {
    private InterpreterGroup intpGroup;

    private PythonInterpreterPandasSql sql;

    private PythonInterpreter python;

    private InterpreterContext context;

    InterpreterOutput out;

    @Test
    public void dependenciesAreInstalled() throws InterpreterException {
        InterpreterResult ret = python.interpret("import pandas\nimport pandasql\nimport numpy\n", context);
        Assert.assertEquals(ret.message().toString(), SUCCESS, ret.code());
    }

    @Test
    public void errorMessageIfDependenciesNotInstalled() throws InterpreterException {
        InterpreterResult ret;
        ret = sql.interpret("SELECT * from something", context);
        Assert.assertNotNull(ret);
        Assert.assertEquals(ret.message().get(0).getData(), ERROR, ret.code());
        Assert.assertTrue(ret.message().get(0).getData().contains("no such table: something"));
    }

    @Test
    public void sqlOverTestDataPrintsTable() throws IOException, InterpreterException {
        InterpreterResult ret;
        // given
        // String expectedTable = "name\tage\n\nmoon\t33\n\npark\t34";
        ret = python.interpret("import pandas as pd", context);
        ret = python.interpret("import numpy as np", context);
        // DataFrame df2 \w test data
        ret = python.interpret(("df2 = pd.DataFrame({ 'age'  : np.array([33, 51, 51, 34]), " + "'name' : pd.Categorical(['moon','jobs','gates','park'])})"), context);
        Assert.assertEquals(ret.message().toString(), SUCCESS, ret.code());
        // when
        ret = sql.interpret("select name, age from df2 where age < 40", context);
        // then
        Assert.assertEquals(new String(out.getOutputAt(1).toByteArray()), SUCCESS, ret.code());
        Assert.assertEquals(new String(out.getOutputAt(1).toByteArray()), TABLE, out.getOutputAt(1).getType());
        Assert.assertTrue(((new String(out.getOutputAt(1).toByteArray()).indexOf("moon\t33")) > 0));
        Assert.assertTrue(((new String(out.getOutputAt(1).toByteArray()).indexOf("park\t34")) > 0));
        Assert.assertEquals(SUCCESS, sql.interpret("select case when name==\"aa\" then name else name end from df2", context).code());
    }

    @Test
    public void badSqlSyntaxFails() throws IOException, InterpreterException {
        // when
        InterpreterResult ret = sql.interpret("select wrong syntax", context);
        // then
        Assert.assertNotNull("Interpreter returned 'null'", ret);
        Assert.assertEquals(ret.toString(), ERROR, ret.code());
    }

    @Test
    public void showDataFrame() throws IOException, InterpreterException {
        InterpreterResult ret;
        ret = python.interpret("import pandas as pd", context);
        ret = python.interpret("import numpy as np", context);
        // given a Pandas DataFrame with an index and non-text data
        ret = python.interpret("index = pd.Index([10, 11, 12, 13], name='index_name')", context);
        ret = python.interpret("d1 = {1 : [np.nan, 1, 2, 3], 'two' : [3., 4., 5., 6.7]}", context);
        ret = python.interpret("df1 = pd.DataFrame(d1, index=index)", context);
        Assert.assertEquals(ret.message().toString(), SUCCESS, ret.code());
        // when
        ret = python.interpret("z.show(df1, show_index=True)", context);
        // then
        Assert.assertEquals(new String(out.getOutputAt(0).toByteArray()), SUCCESS, ret.code());
        Assert.assertEquals(new String(out.getOutputAt(1).toByteArray()), TABLE, out.getOutputAt(1).getType());
        Assert.assertTrue(new String(out.getOutputAt(1).toByteArray()).contains("index_name"));
        Assert.assertTrue(new String(out.getOutputAt(1).toByteArray()).contains("nan"));
        Assert.assertTrue(new String(out.getOutputAt(1).toByteArray()).contains("6.7"));
    }
}

