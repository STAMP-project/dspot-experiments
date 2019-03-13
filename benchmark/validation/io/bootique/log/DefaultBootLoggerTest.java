/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.log;


import java.io.PrintStream;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultBootLoggerTest {
    private PrintStream mockStdout;

    private PrintStream mockStderr;

    @Test
    public void testStdout() {
        DefaultBootLogger logger = new DefaultBootLogger(true, mockStdout, mockStderr);
        logger.stdout("outmsg");
        Mockito.verify(mockStdout).println("outmsg");
        Mockito.verifyZeroInteractions(mockStderr);
    }

    @Test
    public void testStderr() {
        DefaultBootLogger logger = new DefaultBootLogger(true, mockStdout, mockStderr);
        logger.stderr("errmsg");
        Mockito.verify(mockStderr).println("errmsg");
        Mockito.verifyZeroInteractions(mockStdout);
    }

    @Test
    public void testTrace() {
        DefaultBootLogger logger = new DefaultBootLogger(true, mockStdout, mockStderr);
        logger.trace(() -> "mytrace");
        Mockito.verify(mockStderr).println("mytrace");
        Mockito.verifyZeroInteractions(mockStdout);
    }

    @Test
    public void testNoTrace() {
        DefaultBootLogger logger = new DefaultBootLogger(false, mockStdout, mockStderr);
        logger.trace(() -> "mytrace");
        Mockito.verifyZeroInteractions(mockStderr);
        Mockito.verifyZeroInteractions(mockStdout);
    }
}

