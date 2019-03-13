/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.contract;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test concat -if supported
 */
public abstract class AbstractContractConcatTest extends AbstractFSContractTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractContractConcatTest.class);

    private Path testPath;

    private Path srcFile;

    private Path zeroByteFile;

    private Path target;

    @Test
    public void testConcatEmptyFiles() throws Throwable {
        ContractTestUtils.touch(getFileSystem(), target);
        handleExpectedException(LambdaTestUtils.intercept(Exception.class, () -> getFileSystem().concat(target, new Path[0])));
    }

    @Test
    public void testConcatMissingTarget() throws Throwable {
        handleExpectedException(LambdaTestUtils.intercept(Exception.class, () -> getFileSystem().concat(target, new Path[]{ zeroByteFile })));
    }

    @Test
    public void testConcatFileOnFile() throws Throwable {
        byte[] block = ContractTestUtils.dataset(AbstractFSContractTestBase.TEST_FILE_LEN, 0, 255);
        ContractTestUtils.createFile(getFileSystem(), target, false, block);
        getFileSystem().concat(target, new Path[]{ srcFile });
        ContractTestUtils.assertFileHasLength(getFileSystem(), target, ((AbstractFSContractTestBase.TEST_FILE_LEN) * 2));
        ContractTestUtils.validateFileContent(ContractTestUtils.readDataset(getFileSystem(), target, ((AbstractFSContractTestBase.TEST_FILE_LEN) * 2)), new byte[][]{ block, block });
    }

    @Test
    public void testConcatOnSelf() throws Throwable {
        byte[] block = ContractTestUtils.dataset(AbstractFSContractTestBase.TEST_FILE_LEN, 0, 255);
        ContractTestUtils.createFile(getFileSystem(), target, false, block);
        handleExpectedException(LambdaTestUtils.intercept(Exception.class, () -> getFileSystem().concat(target, new Path[]{ target })));
    }
}

