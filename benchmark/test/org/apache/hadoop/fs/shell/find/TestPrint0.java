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
package org.apache.hadoop.fs.shell.find;


import Print.Print0;
import Result.PASS;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.shell.PathData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;


public class TestPrint0 {
    private FileSystem mockFs;

    @Rule
    public Timeout globalTimeout = new Timeout(10000);

    // test the full path is printed to stdout with a '\0'
    @Test
    public void testPrint() throws IOException {
        Print.Print0 print = new Print.Print0();
        PrintStream out = Mockito.mock(PrintStream.class);
        FindOptions options = new FindOptions();
        options.setOut(out);
        print.setOptions(options);
        String filename = "/one/two/test";
        PathData item = new PathData(filename, mockFs.getConf());
        Assert.assertEquals(PASS, print.apply(item, (-1)));
        Mockito.verify(out).print((filename + '\u0000'));
        Mockito.verifyNoMoreInteractions(out);
    }
}

