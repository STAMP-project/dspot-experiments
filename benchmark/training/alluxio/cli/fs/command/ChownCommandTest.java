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
package alluxio.cli.fs.command;


import alluxio.exception.AlluxioException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;


public class ChownCommandTest {
    private ByteArrayOutputStream mOutput = new ByteArrayOutputStream();

    private ByteArrayOutputStream mError = new ByteArrayOutputStream();

    @Test
    public void chownPanicIllegalOwnerName() throws AlluxioException, IOException {
        ChownCommand command = new ChownCommand(null);
        String expectedOutput = String.format("Failed to parse user.1:group1 as user or user:group%n");
        verifyChownCommandReturnValueAndOutput(command, (-1), expectedOutput, "user.1:group1", "/testFile");
        String[] args2 = new String[]{ "user#1:group1", "/testFile" };
        expectedOutput = String.format("Failed to parse user#1:group1 as user or user:group%n");
        verifyChownCommandReturnValueAndOutput(command, (-1), expectedOutput, "user#1:group1", "/testFile");
        String[] args3 = new String[]{ "6user^$group$", "/testFile" };
        expectedOutput = String.format("Failed to parse 6user^$group$ as user or user:group%n");
        verifyChownCommandReturnValueAndOutput(command, (-1), expectedOutput, "6user^$group$", "/testFile");
    }

    @Test
    public void chownPanicIllegalGroupName() throws AlluxioException, IOException {
        ChownCommand command = new ChownCommand(null);
        String expectedOutput = String.format("Failed to parse user1:group.1 as user or user:group%n");
        verifyChownCommandReturnValueAndOutput(command, (-1), expectedOutput, "user1:group.1", "/testFile");
        expectedOutput = String.format("Failed to parse user1:^6group$ as user or user:group%n");
        verifyChownCommandReturnValueAndOutput(command, (-1), expectedOutput, "user1:^6group$", "/testFile");
    }
}

