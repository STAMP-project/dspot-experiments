/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.submarine.client.cli;


import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.submarine.client.cli.param.ShowJobParameters;
import org.apache.hadoop.yarn.submarine.common.MockClientContext;
import org.apache.hadoop.yarn.submarine.common.exception.SubmarineException;
import org.apache.hadoop.yarn.submarine.runtimes.RuntimeFactory;
import org.apache.hadoop.yarn.submarine.runtimes.common.MemorySubmarineStorage;
import org.apache.hadoop.yarn.submarine.runtimes.common.SubmarineStorage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestShowJobCliParsing {
    @Test
    public void testPrintHelp() {
        MockClientContext mockClientContext = new MockClientContext();
        ShowJobCli showJobCli = new ShowJobCli(mockClientContext);
        showJobCli.printUsages();
    }

    @Test
    public void testShowJob() throws IOException, InterruptedException, ParseException, YarnException, SubmarineException {
        MockClientContext mockClientContext = new MockClientContext();
        ShowJobCli showJobCli = new ShowJobCli(mockClientContext) {
            @Override
            protected void getAndPrintJobInfo() {
                // do nothing
            }
        };
        showJobCli.run(new String[]{ "--name", "my-job" });
        ShowJobParameters parameters = showJobCli.getParameters();
        Assert.assertEquals(parameters.getName(), "my-job");
    }

    @Test
    public void testSimpleShowJob() throws IOException, InterruptedException, ParseException, YarnException, SubmarineException {
        SubmarineStorage storage = new MemorySubmarineStorage();
        MockClientContext mockClientContext = new MockClientContext();
        RuntimeFactory runtimeFactory = Mockito.mock(RuntimeFactory.class);
        Mockito.when(runtimeFactory.getSubmarineStorage()).thenReturn(storage);
        mockClientContext.setRuntimeFactory(runtimeFactory);
        ShowJobCli showJobCli = new ShowJobCli(mockClientContext);
        try {
            showJobCli.run(new String[]{ "--name", "my-job" });
        } catch (IOException e) {
            // expected
        }
        storage.addNewJob("my-job", getMockJobInfo("my-job"));
        showJobCli.run(new String[]{ "--name", "my-job" });
    }
}

