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


import alluxio.client.block.BlockMasterClient;
import alluxio.client.block.options.GetWorkerReportOptions;
import alluxio.wire.WorkerInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CapacityCommandTest {
    private BlockMasterClient mBlockMasterClient;

    @Test
    public void longCapacity() throws IOException {
        List<WorkerInfo> longInfoList = prepareLongInfoList();
        Mockito.when(mBlockMasterClient.getWorkerReport(Mockito.any())).thenReturn(longInfoList);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();PrintStream printStream = new PrintStream(outputStream, true, "utf-8")) {
            CapacityCommand capacityCommand = new CapacityCommand(mBlockMasterClient, printStream);
            capacityCommand.generateCapacityReport(GetWorkerReportOptions.defaults());
            String output = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            // CHECKSTYLE.OFF: LineLengthExceed - Much more readable
            List<String> expectedOutput = Arrays.asList("Capacity information for all workers: ", "    Total Capacity: 29.80GB", "        Tier: MEM  Size: 8.38GB", "        Tier: SSD  Size: 4768.37MB", "        Tier: HDD  Size: 1907.35MB", "        Tier: DOM  Size: 9.31GB", "        Tier: RAM  Size: 5.59GB", "    Used Capacity: 10.24GB", "        Tier: MEM  Size: 3051.76MB", "        Tier: SSD  Size: 286.10MB", "        Tier: HDD  Size: 1907.35MB", "        Tier: DOM  Size: 476.84MB", "        Tier: RAM  Size: 4768.37MB", "    Used Percentage: 34%", "    Free Percentage: 66%", "", "Worker Name      Last Heartbeat   Storage       Total            MEM           SSD           HDD           DOM           RAM           ", "216.239.33.96    542              capacity      18.63GB          4768.37MB     4768.37MB     -             9.31GB        -             ", "                                  used          953.67MB (5%)    190.73MB      286.10MB      -             476.84MB      -             ", "64.68.90.1       3123             capacity      11.18GB          3814.70MB     -             1907.35MB     -             5.59GB        ", "                                  used          9.31GB (83%)     2861.02MB     -             1907.35MB     -             4768.37MB     ");
            // CHECKSTYLE.ON: LineLengthExceed
            List<String> testOutput = Arrays.asList(output.split("\n"));
            Assert.assertThat(testOutput, IsIterableContainingInOrder.contains(expectedOutput.toArray()));
        }
    }

    @Test
    public void shortCapacity() throws IOException {
        List<WorkerInfo> shortInfoList = prepareShortInfoList();
        Mockito.when(mBlockMasterClient.getWorkerReport(Mockito.any())).thenReturn(shortInfoList);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();PrintStream printStream = new PrintStream(outputStream, true, "utf-8")) {
            CapacityCommand capacityCommand = new CapacityCommand(mBlockMasterClient, printStream);
            capacityCommand.generateCapacityReport(GetWorkerReportOptions.defaults());
            String output = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            List<String> expectedOutput = Arrays.asList("Capacity information for all workers: ", "    Total Capacity: 14.90GB", "        Tier: RAM  Size: 14.90GB", "    Used Capacity: 5.12GB", "        Tier: RAM  Size: 5.12GB", "    Used Percentage: 34%", "    Free Percentage: 66%", "", "Worker Name      Last Heartbeat   Storage       RAM", "215.42.95.24     953              capacity      9.31GB", "                                  used          476.84MB (5%)", "29.53.5.124      6424122          capacity      5.59GB", "                                  used          4768.37MB (83%)");
            List<String> testOutput = Arrays.asList(output.split("\n"));
            Assert.assertThat(testOutput, IsIterableContainingInOrder.contains(expectedOutput.toArray()));
        }
    }

    @Test
    public void longWorkerNameCapacity() throws IOException {
        List<WorkerInfo> longWorkerNameInfoList = prepareLongWorkerNameInfoList();
        Mockito.when(mBlockMasterClient.getWorkerReport(Mockito.any())).thenReturn(longWorkerNameInfoList);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();PrintStream printStream = new PrintStream(outputStream, true, "utf-8")) {
            CapacityCommand capacityCommand = new CapacityCommand(mBlockMasterClient, printStream);
            capacityCommand.generateCapacityReport(GetWorkerReportOptions.defaults());
            String output = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            List<String> testRst = Arrays.asList(output.split("\n"));
            // CHECKSTYLE.OFF: LineLengthExceed - Much more readable
            List<String> expectedOutput = Arrays.asList("Capacity information for all workers: ", "    Total Capacity: 3051.76MB", "        Tier: MEM  Size: 1144.41MB", "        Tier: SSD  Size: 572.20MB", "        Tier: HDD  Size: 190.73MB", "    Used Capacity: 1049.04MB", "        Tier: MEM  Size: 305.18MB", "        Tier: SSD  Size: 28.61MB", "        Tier: HDD  Size: 190.73MB", "    Used Percentage: 34%", "    Free Percentage: 66%", "", "Worker Name                 Last Heartbeat   Storage       Total            MEM           SSD           HDD           ", "org.apache.hdp1             681              capacity      1907.35MB        572.20MB      572.20MB      -             ", "                                             used          95.37MB (5%)     19.07MB       28.61MB       -             ", "org.alluxio.long.host1      6211             capacity      1144.41MB        572.20MB      -             190.73MB      ", "                                             used          953.67MB (83%)   286.10MB      -             190.73MB      ");
            // CHECKSTYLE.ON: LineLengthExceed
            List<String> testOutput = Arrays.asList(output.split("\n"));
            Assert.assertThat(testOutput, IsIterableContainingInOrder.contains(expectedOutput.toArray()));
        }
    }
}

