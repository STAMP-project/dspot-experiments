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
package org.apache.flink.yarn;


import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An extension of the {@link YARNSessionFIFOITCase} that runs the tests in a secured YARN cluster.
 */
public class YARNSessionFIFOSecuredITCase extends YARNSessionFIFOITCase {
    protected static final Logger LOG = LoggerFactory.getLogger(YARNSessionFIFOSecuredITCase.class);

    // timeout after a minute.
    @Test(timeout = 60000)
    @Override
    public void testDetachedMode() throws IOException, InterruptedException {
        super.testDetachedMode();
        final String[] mustHave = new String[]{ "Login successful for user", "using keytab file" };
        final boolean jobManagerRunsWithKerberos = YarnTestBase.verifyStringsInNamedLogFiles(mustHave, "jobmanager.log");
        final boolean taskManagerRunsWithKerberos = YarnTestBase.verifyStringsInNamedLogFiles(mustHave, "taskmanager.log");
        Assert.assertThat("The JobManager and the TaskManager should both run with Kerberos.", (jobManagerRunsWithKerberos && taskManagerRunsWithKerberos), Matchers.is(true));
    }
}

