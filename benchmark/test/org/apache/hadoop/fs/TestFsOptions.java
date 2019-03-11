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
package org.apache.hadoop.fs;


import DataChecksum.Type;
import DataChecksum.Type.CRC32;
import DataChecksum.Type.CRC32C;
import org.apache.hadoop.fs.Options.ChecksumOpt;
import org.junit.Test;


public class TestFsOptions {
    @Test
    public void testProcessChecksumOpt() {
        ChecksumOpt defaultOpt = new ChecksumOpt(Type.CRC32, 512);
        ChecksumOpt finalOpt;
        // Give a null
        finalOpt = ChecksumOpt.processChecksumOpt(defaultOpt, null);
        checkParams(defaultOpt, finalOpt);
        // null with bpc
        finalOpt = ChecksumOpt.processChecksumOpt(defaultOpt, null, 1024);
        checkParams(CRC32, 1024, finalOpt);
        ChecksumOpt myOpt = new ChecksumOpt();
        // custom with unspecified parameters
        finalOpt = ChecksumOpt.processChecksumOpt(defaultOpt, myOpt);
        checkParams(defaultOpt, finalOpt);
        myOpt = new ChecksumOpt(Type.CRC32C, 2048);
        // custom config
        finalOpt = ChecksumOpt.processChecksumOpt(defaultOpt, myOpt);
        checkParams(CRC32C, 2048, finalOpt);
        // custom config + bpc
        finalOpt = ChecksumOpt.processChecksumOpt(defaultOpt, myOpt, 4096);
        checkParams(CRC32C, 4096, finalOpt);
    }
}

