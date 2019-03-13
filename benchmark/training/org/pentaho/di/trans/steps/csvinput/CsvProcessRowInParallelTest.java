/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017-2019 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.csvinput;


import java.io.File;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * We take file with content
 *  and run it parallel with several steps.
 *  see docs for {@link CsvInput#prepareToRunInParallel} to understand how running file in parallel works
 *
 *  We measure the correctness of work by counting the number of lines, written on each step.
 *  As a result, we should come to this pseudo formula: numberOfLines = sum of number of lines written by each step.
 *
 *  Just a simple example:
 *  Assume, we have file with this content:
 *
 *  a,b\r\n
 *  c,d\r\n
 *
 *  If we will run it with 2 steps, we expect the first step to read 1st line, and the second step to read second line.
 *
 *  Every test is built in this pattern.
 *
 *  We actually play with 4 things:
 *  - file content
 *  - number of threads (it's actually same as number of steps)
 *  - representation of new line (it can be 2 bytes: '\r\n' (windows) or 1 byte: '\r' or '\n' (Mac, Linux) .
 *  Representation can differ. So, if we have different types of new lines in one file - it's ok.
 *  - file ends with new line or not
 */
public class CsvProcessRowInParallelTest extends CsvInputUnitTestBase {
    private StepMockHelper<CsvInputMeta, StepDataInterface> stepMockHelper;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void oneByteNewLineIndicator_NewLineAtTheEnd_2Threads() throws Exception {
        final int totalNumberOfSteps = 2;
        final String fileContent = "a;1\r" + "b;2\r";
        File sharedFile = createTestFile("UTF-8", fileContent);
        Assert.assertEquals(1, createAndRunOneStep(sharedFile, 0, totalNumberOfSteps));
        Assert.assertEquals(1, createAndRunOneStep(sharedFile, 1, totalNumberOfSteps));
    }

    @Test
    public void oneByteNewLineIndicator_NoNewLineAtTheEnd_2Threads() throws Exception {
        final int totalNumberOfSteps = 2;
        final String fileContent = "a;1\r" + ("b;2\r" + "c;3");
        File sharedFile = createTestFile("UTF-8", fileContent);
        Assert.assertEquals(2, createAndRunOneStep(sharedFile, 0, totalNumberOfSteps));
        Assert.assertEquals(1, createAndRunOneStep(sharedFile, 1, totalNumberOfSteps));
    }

    @Test
    public void PDI_15162_mixedByteNewLineIndicator_NewLineAtTheEnd_2Threads() throws Exception {
        final int totalNumberOfSteps = 2;
        final String fileContent = "ab;111\r\n" + (((((((("bc;222\r\n" + "cd;333\r\n") + "de;444\r\n") + "ef;555\r") + "fg;666\r\n") + "gh;777\r\n") + "hi;888\r\n") + "ij;999\r") + "jk;000\r");
        File sharedFile = createTestFile("UTF-8", fileContent);
        Assert.assertEquals(5, createAndRunOneStep(sharedFile, 0, totalNumberOfSteps));
        Assert.assertEquals(5, createAndRunOneStep(sharedFile, 1, totalNumberOfSteps));
    }

    @Test
    public void PDI_15162_mixedByteNewLineIndicator_NoNewLineAtTheEnd_2Threads() throws Exception {
        final int totalNumberOfSteps = 2;
        final String fileContent = "ab;111\r\n" + (((((((("bc;222\r\n" + "cd;333\r\n") + "de;444\r\n") + "ef;555\r") + "fg;666\r\n") + "gh;777\r\n") + "hi;888\r\n") + "ij;999\r") + "jk;000");
        File sharedFile = createTestFile("UTF-8", fileContent);
        Assert.assertEquals(5, createAndRunOneStep(sharedFile, 0, totalNumberOfSteps));
        Assert.assertEquals(5, createAndRunOneStep(sharedFile, 1, totalNumberOfSteps));
    }

    @Test
    public void twoByteNewLineIndicator_NewLineAtTheEnd_2Threads() throws Exception {
        final String fileContent = "a;1\r\n" + "b;2\r\n";
        final int totalNumberOfSteps = 2;
        File sharedFile = createTestFile("UTF-8", fileContent);
        Assert.assertEquals(1, createAndRunOneStep(sharedFile, 0, totalNumberOfSteps));
        Assert.assertEquals(1, createAndRunOneStep(sharedFile, 1, totalNumberOfSteps));
    }

    @Test
    public void twoByteNewLineIndicator_NoNewLineAtTheEnd_2Threads() throws Exception {
        final String fileContent = "a;1\r\n" + "b;2";
        final int totalNumberOfSteps = 2;
        File sharedFile = createTestFile("UTF-8", fileContent);
        int t1 = createAndRunOneStep(sharedFile, 0, totalNumberOfSteps);
        int t2 = createAndRunOneStep(sharedFile, 1, totalNumberOfSteps);
        Assert.assertEquals(2, (t1 + t2));
    }

    @Test
    public void twoByteNewLineIndicator_NewLineAtTheEnd_3Threads() throws Exception {
        final String fileContent = "a;1\r\n" + (((("b;2\r\n" + // thread 1 should read until this line
        "c;3\r\n") + "d;4\r\n") + // thread 2 should read until this line
        "e;5\r\n") + "f;6\r\n");
        // thread 3 should read until this line
        final int totalNumberOfSteps = 3;
        File sharedFile = createTestFile("UTF-8", fileContent);
        Assert.assertEquals(2, createAndRunOneStep(sharedFile, 0, totalNumberOfSteps));
        Assert.assertEquals(2, createAndRunOneStep(sharedFile, 1, totalNumberOfSteps));
        Assert.assertEquals(2, createAndRunOneStep(sharedFile, 2, totalNumberOfSteps));
    }

    /**
     * Here files content is 16 bytes summary, where 8 of this bytes is the first line, 5 is the second one, 3 is the
     * last.
     * <p>
     * As we are running this with 2 threads, we expect: 1st thread to read 1st line 2nd thread to read 2nd and 3d line.
     */
    @Test
    public void mixedBytesNewLineIndicator_NoNewLineAtTheEnd_2Threads() throws Exception {
        final String fileContent = "abcd;1\r\n" + ("b;2\r\n" + "d;3");
        final int totalNumberOfSteps = 2;
        File sharedFile = createTestFile("UTF-8", fileContent);
        Assert.assertEquals(1, createAndRunOneStep(sharedFile, 0, totalNumberOfSteps));
        Assert.assertEquals(2, createAndRunOneStep(sharedFile, 1, totalNumberOfSteps));
    }

    @Test
    public void mixedBytesNewLineIndicator_NewLineAtTheEnd_2Threads() throws Exception {
        final String fileContent = "abcd;1\r\n" + ("b;2\r" + "d;3\r");
        final int totalNumberOfSteps = 2;
        File sharedFile = createTestFile("UTF-8", fileContent);
        Assert.assertEquals(1, createAndRunOneStep(sharedFile, 0, totalNumberOfSteps));
        Assert.assertEquals(2, createAndRunOneStep(sharedFile, 1, totalNumberOfSteps));
    }

    @Test
    public void PDI_16589_twoByteNewLineIndicator_withHeaders_NewLineAtTheEnd_4Threads() throws Exception {
        final int totalNumberOfSteps = 4;
        final String fileContent = "Col1,Col2\r\n" + (((((((((("a,1\r\n" + "b,2\r\n") + "c,3\r\n") + "d,4\r\n") + "e,5\r\n") + "f,6\r\n") + "g,7\r\n") + "h,8\r\n") + "i,9\r\n") + "jk,10\r\n") + "lm,11\r\n");
        File sharedFile = createTestFile("UTF-8", fileContent);
        int t1 = createAndRunOneStep(sharedFile, 0, totalNumberOfSteps, true, ",");
        int t2 = createAndRunOneStep(sharedFile, 1, totalNumberOfSteps, true, ",");
        int t3 = createAndRunOneStep(sharedFile, 2, totalNumberOfSteps, true, ",");
        int t4 = createAndRunOneStep(sharedFile, 3, totalNumberOfSteps, true, ",");
        Assert.assertEquals(11, (((t1 + t2) + t3) + t4));
    }
}

