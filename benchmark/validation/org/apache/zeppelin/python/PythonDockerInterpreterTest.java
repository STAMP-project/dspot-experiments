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


import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterOutput;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PythonDockerInterpreterTest {
    private PythonDockerInterpreter docker;

    private PythonInterpreter python;

    @Test
    public void testActivateEnv() throws InterpreterException {
        InterpreterContext context = getInterpreterContext();
        docker.interpret("activate env", context);
        Mockito.verify(python, Mockito.times(1)).open();
        Mockito.verify(python, Mockito.times(1)).close();
        Mockito.verify(docker, Mockito.times(1)).pull(ArgumentMatchers.any(InterpreterOutput.class), ArgumentMatchers.anyString());
        Mockito.verify(python).setPythonExec(Mockito.matches("docker run -i --rm -v.*"));
    }

    @Test
    public void testDeactivate() throws InterpreterException {
        InterpreterContext context = getInterpreterContext();
        docker.interpret("deactivate", context);
        Mockito.verify(python, Mockito.times(1)).open();
        Mockito.verify(python, Mockito.times(1)).close();
        Mockito.verify(python).setPythonExec(null);
    }
}

