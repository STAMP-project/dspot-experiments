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
package org.apache.zeppelin.interpreter.remote;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class RemoteInterpreterUtilsTest {
    @Test
    public void testCreateTServerSocket() throws IOException {
        Assert.assertTrue(((RemoteInterpreterUtils.createTServerSocket(":").getServerSocket().getLocalPort()) > 0));
        String portRange = ":30000";
        Assert.assertTrue(((RemoteInterpreterUtils.createTServerSocket(portRange).getServerSocket().getLocalPort()) <= 30000));
        portRange = "30000:";
        Assert.assertTrue(((RemoteInterpreterUtils.createTServerSocket(portRange).getServerSocket().getLocalPort()) >= 30000));
        portRange = "30000:40000";
        int port = RemoteInterpreterUtils.createTServerSocket(portRange).getServerSocket().getLocalPort();
        Assert.assertTrue(((port >= 30000) && (port <= 40000)));
    }
}

