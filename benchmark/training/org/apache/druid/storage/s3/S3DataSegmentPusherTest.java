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
package org.apache.druid.storage.s3;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 */
public class S3DataSegmentPusherTest {
    private static class ValueContainer<T> {
        private T value;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testPush() throws Exception {
        testPushInternal(false, "key/foo/2015-01-01T00:00:00\\.000Z_2016-01-01T00:00:00\\.000Z/0/0/index\\.zip");
    }

    @Test
    public void testPushUseUniquePath() throws Exception {
        testPushInternal(true, "key/foo/2015-01-01T00:00:00\\.000Z_2016-01-01T00:00:00\\.000Z/0/0/[A-Za-z0-9-]{36}/index\\.zip");
    }
}

