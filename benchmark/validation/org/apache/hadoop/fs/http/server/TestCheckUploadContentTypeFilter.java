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
package org.apache.hadoop.fs.http.server;


import HttpFSFileSystem.Operation.APPEND;
import HttpFSFileSystem.Operation.CREATE;
import HttpFSFileSystem.Operation.GETHOMEDIRECTORY;
import HttpFSFileSystem.Operation.MKDIRS;
import org.junit.Test;


public class TestCheckUploadContentTypeFilter {
    @Test
    public void putUpload() throws Exception {
        test("PUT", CREATE.toString(), "application/octet-stream", true, false);
    }

    @Test
    public void postUpload() throws Exception {
        test("POST", APPEND.toString(), "APPLICATION/OCTET-STREAM", true, false);
    }

    @Test
    public void putUploadWrong() throws Exception {
        test("PUT", CREATE.toString(), "plain/text", false, false);
        test("PUT", CREATE.toString(), "plain/text", true, true);
    }

    @Test
    public void postUploadWrong() throws Exception {
        test("POST", APPEND.toString(), "plain/text", false, false);
        test("POST", APPEND.toString(), "plain/text", true, true);
    }

    @Test
    public void getOther() throws Exception {
        test("GET", GETHOMEDIRECTORY.toString(), "plain/text", false, false);
    }

    @Test
    public void putOther() throws Exception {
        test("PUT", MKDIRS.toString(), "plain/text", false, false);
    }
}

