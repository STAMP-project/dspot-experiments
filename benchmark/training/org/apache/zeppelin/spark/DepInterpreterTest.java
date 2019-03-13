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
package org.apache.zeppelin.spark;


import Code.SUCCESS;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DepInterpreterTest {
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    private DepInterpreter dep;

    private InterpreterContext context;

    @Test
    public void testDefault() throws InterpreterException {
        dep.getDependencyContext().reset();
        InterpreterResult ret = dep.interpret("z.load(\"org.apache.commons:commons-csv:1.1\")", context);
        Assert.assertEquals(SUCCESS, ret.code());
        Assert.assertEquals(1, dep.getDependencyContext().getFiles().size());
        Assert.assertEquals(1, dep.getDependencyContext().getFilesDist().size());
    }
}

