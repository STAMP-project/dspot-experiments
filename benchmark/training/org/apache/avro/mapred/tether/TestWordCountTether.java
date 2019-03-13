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
package org.apache.avro.mapred.tether;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * See also TestTetherTool for an example of how to submit jobs using the thether tool.
 */
public class TestWordCountTether {
    @Rule
    public TemporaryFolder INPUT_DIR = new TemporaryFolder();

    @Rule
    public TemporaryFolder OUTPUT_DIR = new TemporaryFolder();

    /**
     * Test the job using the sasl protocol
     *
     * @throws Exception
     * 		
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testJob() throws Exception {
        _runjob("sasl");
    }

    /**
     * Test the job using the http protocol
     *
     * @throws Exception
     * 		
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testhtp() throws Exception {
        _runjob("http");
    }
}

