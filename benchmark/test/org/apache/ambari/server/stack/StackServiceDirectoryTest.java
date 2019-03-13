/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.stack;


import java.io.File;
import org.apache.ambari.server.AmbariException;
import org.junit.Assert;
import org.junit.Test;

import static StackDirectory.SERVICE_ADVISOR_FILE_NAME;


/**
 * Tests for StackServiceDirectory
 */
public class StackServiceDirectoryTest {
    @Test
    public void testValidServiceAdvisorClassName() throws Exception {
        String pathWithInvalidChars = "/Fake-Stack.Name/1.0/services/FAKESERVICE/";
        String serviceNameValidChars = "FakeService";
        String pathWithValidChars = "/FakeStackName/1.0/services/FAKESERVICE/";
        String serviceNameInvalidChars = "Fake-Serv.ice";
        String desiredServiceAdvisorName = "FakeStackName10FakeServiceServiceAdvisor";
        StackServiceDirectoryTest.MockStackServiceDirectory ssd1 = createStackServiceDirectory(pathWithInvalidChars);
        Assert.assertEquals(desiredServiceAdvisorName, getAdvisorName(serviceNameValidChars));
        StackServiceDirectoryTest.MockStackServiceDirectory ssd2 = createStackServiceDirectory(pathWithValidChars);
        Assert.assertEquals(desiredServiceAdvisorName, getAdvisorName(serviceNameInvalidChars));
        StackServiceDirectoryTest.MockStackServiceDirectory ssd3 = createStackServiceDirectory(pathWithInvalidChars);
        Assert.assertEquals(desiredServiceAdvisorName, getAdvisorName(serviceNameInvalidChars));
        StackServiceDirectoryTest.MockStackServiceDirectory ssd4 = createStackServiceDirectory(pathWithValidChars);
        Assert.assertEquals(desiredServiceAdvisorName, getAdvisorName(serviceNameValidChars));
    }

    private class MockStackServiceDirectory extends StackServiceDirectory {
        File advisor = null;

        MockStackServiceDirectory(String servicePath) throws AmbariException {
            super(servicePath);
            advisor = new File(servicePath, SERVICE_ADVISOR_FILE_NAME);
        }

        protected void parsePath() {
        }

        public File getAdvisorFile() {
            return advisor;
        }
    }
}

