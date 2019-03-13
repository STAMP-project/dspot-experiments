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
package org.apache.hadoop.yarn.api.records;


import YarnConfiguration.IPC_RECORD_FACTORY_CLASS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the URL class.
 */
public class TestURL {
    @Test
    public void testConversion() throws Exception {
        Configuration conf = new Configuration();
        conf.set(IPC_RECORD_FACTORY_CLASS, TestURL.RecordFactoryForTest.class.getName());
        String[] pathStrs = new String[]{ "/", ".", "foo/bar", "foo", "/foo/bar/baz", "moo://bar/baz", "moo://bar:123/baz", "moo:///foo", "moo://foo@bar:123/baz/foo", "moo://foo@bar/baz/foo", "moo://foo@bar", "moo://foo:123" };
        for (String s : pathStrs) {
            Path path = new Path(s);
            Assert.assertEquals(path, URL.fromPath(path, conf).toPath());
        }
        Path p = new Path("/foo/bar#baz");
        Assert.assertEquals(p, URL.fromPath(p, conf).toPath());
    }

    /**
     * Record factory that instantiates URLs for this test.
     */
    public static class RecordFactoryForTest implements RecordFactory {
        private static final TestURL.RecordFactoryForTest SELF = new TestURL.RecordFactoryForTest();

        @SuppressWarnings("unchecked")
        @Override
        public <T> T newRecordInstance(Class<T> clazz) {
            return ((T) (new TestURL.URLForTest()));
        }

        public static RecordFactory get() {
            return TestURL.RecordFactoryForTest.SELF;
        }
    }

    /**
     * URL fake for this test; sidesteps proto-URL dependency.
     */
    public static class URLForTest extends URL {
        private String scheme;

        private String userInfo;

        private String host;

        private String file;

        private int port;

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(String userInfo) {
            this.userInfo = userInfo;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}

