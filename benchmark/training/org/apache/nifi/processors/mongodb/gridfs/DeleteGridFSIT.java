/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nifi.processors.mongodb.gridfs;


import CoreAttributes.FILENAME;
import DeleteGridFS.FILE_NAME;
import DeleteGridFS.QUERY;
import DeleteGridFS.QUERY_ATTRIBUTE;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;


public class DeleteGridFSIT extends GridFSITTestBase {
    private TestRunner runner;

    private static final String BUCKET = "delete_test_bucket";

    @Test
    public void testFileAndQueryAtSameTime() {
        runner.setProperty(FILE_NAME, "${test_var}");
        runner.setProperty(QUERY, "{}");
        runner.assertNotValid();
    }

    @Test
    public void testNeitherFileNorQuery() {
        runner.assertNotValid();
    }

    @Test
    public void testDeleteByFileName() {
        testDeleteByProperty(FILE_NAME, String.format("${%s}", FILENAME.key()), setupTestFile());
    }

    @Test
    public void testDeleteByQuery() {
        testDeleteByProperty(QUERY, "{}", setupTestFile());
    }

    @Test
    public void testQueryAttribute() {
        String attrName = "gridfs.query.used";
        String fileName = setupTestFile();
        runner.setProperty(QUERY_ATTRIBUTE, attrName);
        testDeleteByProperty(FILE_NAME, String.format("${%s}", FILENAME.key()), fileName);
        testForQueryAttribute(fileName, attrName);
    }
}

