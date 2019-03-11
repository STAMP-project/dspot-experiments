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
package org.apache.zeppelin.helium;


import ZeppelinConfiguration.ConfVars.ZEPPELIN_NOTEBOOK_S3_ENDPOINT;
import ZeppelinConfiguration.ConfVars.ZEPPELIN_NOTEBOOK_S3_TIMEOUT;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class HeliumOnlineRegistryTest {
    // ip 192.168.65.17 belongs to private network
    // request will be ended with connection time out error
    private static final String IP = "192.168.65.17";

    private static final String TIMEOUT = "2000";

    private File tmpDir;

    @Test
    public void zeppelinNotebookS3TimeoutPropertyTest() throws IOException {
        System.setProperty(ZEPPELIN_NOTEBOOK_S3_TIMEOUT.getVarName(), HeliumOnlineRegistryTest.TIMEOUT);
        System.setProperty(ZEPPELIN_NOTEBOOK_S3_ENDPOINT.getVarName(), HeliumOnlineRegistryTest.IP);
        HeliumOnlineRegistry heliumOnlineRegistry = new HeliumOnlineRegistry(("https://" + (HeliumOnlineRegistryTest.IP)), ("https://" + (HeliumOnlineRegistryTest.IP)), tmpDir);
        long start = System.currentTimeMillis();
        heliumOnlineRegistry.getAll();
        long processTime = (System.currentTimeMillis()) - start;
        long basicTimeout = Long.valueOf(ZEPPELIN_NOTEBOOK_S3_TIMEOUT.getStringValue());
        Assert.assertTrue(String.format("Wrong timeout during connection: expected %s, actual is about %d", HeliumOnlineRegistryTest.TIMEOUT, processTime), (basicTimeout > processTime));
    }
}

