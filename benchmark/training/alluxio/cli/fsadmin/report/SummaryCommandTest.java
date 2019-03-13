/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.cli.fsadmin.report;


import PropertyKey.USER_DATE_FORMAT_PATTERN;
import alluxio.client.MetaMasterClient;
import alluxio.client.block.BlockMasterClient;
import alluxio.conf.AlluxioConfiguration;
import alluxio.util.ConfigurationUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Test;


public class SummaryCommandTest {
    private static AlluxioConfiguration sConf = new alluxio.conf.InstancedConfiguration(ConfigurationUtils.defaults());

    private MetaMasterClient mMetaMasterClient;

    private BlockMasterClient mBlockMasterClient;

    private ByteArrayOutputStream mOutputStream;

    private PrintStream mPrintStream;

    @Test
    public void summary() throws IOException {
        SummaryCommand summaryCommand = new SummaryCommand(mMetaMasterClient, mBlockMasterClient, SummaryCommandTest.sConf.get(USER_DATE_FORMAT_PATTERN), mPrintStream);
        summaryCommand.run();
        checkIfOutputValid(SummaryCommandTest.sConf.get(USER_DATE_FORMAT_PATTERN));
    }
}

