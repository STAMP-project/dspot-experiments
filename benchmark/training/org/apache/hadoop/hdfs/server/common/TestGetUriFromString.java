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
package org.apache.hadoop.hdfs.server.common;


import java.io.IOException;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is a unit test, which tests {@link Util#stringAsURI(String)}
 * for Windows and Unix style file paths.
 */
public class TestGetUriFromString {
    private static final Logger LOG = LoggerFactory.getLogger(TestGetUriFromString.class);

    private static final String RELATIVE_FILE_PATH = "relativeFilePath";

    private static final String ABSOLUTE_PATH_UNIX = "/tmp/file1";

    private static final String ABSOLUTE_PATH_WINDOWS = "C:\\Documents and Settings\\All Users";

    private static final String URI_FILE_SCHEMA = "file";

    private static final String URI_PATH_UNIX = "/var/www";

    private static final String URI_PATH_WINDOWS = "/C:/Documents%20and%20Settings/All%20Users";

    private static final String URI_UNIX = ((TestGetUriFromString.URI_FILE_SCHEMA) + "://") + (TestGetUriFromString.URI_PATH_UNIX);

    private static final String URI_WINDOWS = ((TestGetUriFromString.URI_FILE_SCHEMA) + "://") + (TestGetUriFromString.URI_PATH_WINDOWS);

    /**
     * Test for a relative path, os independent
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testRelativePathAsURI() throws IOException {
        URI u = Util.stringAsURI(TestGetUriFromString.RELATIVE_FILE_PATH);
        TestGetUriFromString.LOG.info(("Uri: " + u));
        Assert.assertNotNull(u);
    }

    /**
     * Test for an OS dependent absolute paths.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testAbsolutePathAsURI() throws IOException {
        URI u = null;
        u = Util.stringAsURI(TestGetUriFromString.ABSOLUTE_PATH_WINDOWS);
        Assert.assertNotNull(("Uri should not be null for Windows path" + (TestGetUriFromString.ABSOLUTE_PATH_WINDOWS)), u);
        Assert.assertEquals(TestGetUriFromString.URI_FILE_SCHEMA, u.getScheme());
        u = Util.stringAsURI(TestGetUriFromString.ABSOLUTE_PATH_UNIX);
        Assert.assertNotNull(("Uri should not be null for Unix path" + (TestGetUriFromString.ABSOLUTE_PATH_UNIX)), u);
        Assert.assertEquals(TestGetUriFromString.URI_FILE_SCHEMA, u.getScheme());
    }

    /**
     * Test for a URI
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testURI() throws IOException {
        TestGetUriFromString.LOG.info(("Testing correct Unix URI: " + (TestGetUriFromString.URI_UNIX)));
        URI u = Util.stringAsURI(TestGetUriFromString.URI_UNIX);
        TestGetUriFromString.LOG.info(("Uri: " + u));
        Assert.assertNotNull("Uri should not be null at this point", u);
        Assert.assertEquals(TestGetUriFromString.URI_FILE_SCHEMA, u.getScheme());
        Assert.assertEquals(TestGetUriFromString.URI_PATH_UNIX, u.getPath());
        TestGetUriFromString.LOG.info(("Testing correct windows URI: " + (TestGetUriFromString.URI_WINDOWS)));
        u = Util.stringAsURI(TestGetUriFromString.URI_WINDOWS);
        TestGetUriFromString.LOG.info(("Uri: " + u));
        Assert.assertNotNull("Uri should not be null at this point", u);
        Assert.assertEquals(TestGetUriFromString.URI_FILE_SCHEMA, u.getScheme());
        Assert.assertEquals(TestGetUriFromString.URI_PATH_WINDOWS.replace("%20", " "), u.getPath());
    }
}

