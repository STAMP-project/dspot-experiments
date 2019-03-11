/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.aws.lambda;


import PutLambda.AWS_LAMBDA_FUNCTION_NAME;
import PutLambda.CREDENTIALS_FILE;
import PutLambda.REL_FAILURE;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


/**
 * This test contains both unit and integration test (integration tests are ignored by default)
 */
public class ITPutLambda {
    private TestRunner runner;

    protected static final String CREDENTIALS_FILE = (System.getProperty("user.home")) + "/aws-credentials.properties";

    @Test
    public void testSizeGreaterThan6MB() throws Exception {
        runner = TestRunners.newTestRunner(PutLambda.class);
        runner.setProperty(PutLambda.CREDENTIALS_FILE, ITPutLambda.CREDENTIALS_FILE);
        runner.setProperty(AWS_LAMBDA_FUNCTION_NAME, "hello");
        runner.assertValid();
        byte[] largeInput = new byte[6000001];
        for (int i = 0; i < 6000001; i++) {
            largeInput[i] = 'a';
        }
        runner.enqueue(largeInput);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }
}

