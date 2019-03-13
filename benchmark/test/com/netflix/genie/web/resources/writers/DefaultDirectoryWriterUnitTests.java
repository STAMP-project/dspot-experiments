/**
 * Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.web.resources.writers;


import DefaultDirectoryWriter.Directory;
import DefaultDirectoryWriter.Entry;
import com.netflix.genie.common.util.GenieObjectMapper;
import com.netflix.genie.test.categories.UnitTest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.w3c.tidy.Tidy;


/**
 * Unit tests for the DefaultDirectoryWriter.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class DefaultDirectoryWriterUnitTests {
    private static final String REQUEST_URL_BASE = ("http://genie.netflix.com:8080/api/v3/jobs/" + (UUID.randomUUID().toString())) + "/output";

    private static final String REQUEST_URL_WITH_PARENT = ((DefaultDirectoryWriterUnitTests.REQUEST_URL_BASE) + "/") + (UUID.randomUUID().toString());

    private static final long PARENT_SIZE = 0L;

    private static final Instant PARENT_LAST_MODIFIED = Instant.now();

    private static final String PARENT_NAME = "../";

    private static final String PARENT_URL = DefaultDirectoryWriterUnitTests.REQUEST_URL_BASE;

    private static final long DIR_1_SIZE = 0L;

    private static final Instant DIR_1_LAST_MODIFIED = Instant.now().plus(13142, ChronoUnit.MILLIS);

    private static final String DIR_1_NAME = UUID.randomUUID().toString();

    private static final String DIR_1_URL = ((DefaultDirectoryWriterUnitTests.REQUEST_URL_WITH_PARENT) + "/") + (DefaultDirectoryWriterUnitTests.DIR_1_NAME);

    private static final long DIR_2_SIZE = 0L;

    private static final Instant DIR_2_LAST_MODIFIED = Instant.now().minus(1830, ChronoUnit.MILLIS);

    private static final String DIR_2_NAME = UUID.randomUUID().toString();

    private static final String DIR_2_URL = ((DefaultDirectoryWriterUnitTests.REQUEST_URL_WITH_PARENT) + "/") + (DefaultDirectoryWriterUnitTests.DIR_2_NAME);

    private static final long FILE_1_SIZE = 73522431;

    private static final Instant FILE_1_LAST_MODIFIED = Instant.now().plus(1832430, ChronoUnit.MILLIS);

    private static final String FILE_1_NAME = UUID.randomUUID().toString();

    private static final String FILE_1_URL = ((DefaultDirectoryWriterUnitTests.REQUEST_URL_WITH_PARENT) + "/") + (DefaultDirectoryWriterUnitTests.FILE_1_NAME);

    private static final long FILE_2_SIZE = 735231;

    private static final Instant FILE_2_LAST_MODIFIED = Instant.now().plus(1832443, ChronoUnit.MILLIS);

    private static final String FILE_2_NAME = UUID.randomUUID().toString();

    private static final String FILE_2_URL = ((DefaultDirectoryWriterUnitTests.REQUEST_URL_WITH_PARENT) + "/") + (DefaultDirectoryWriterUnitTests.FILE_2_NAME);

    private DefaultDirectoryWriter writer;

    private File directory;

    private Entry directoryEntry1;

    private Entry directoryEntry2;

    private Entry fileEntry1;

    private Entry fileEntry2;

    /**
     * Make sure if the argument passed in isn't a directory an exception is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantGetDirectoryWithoutValidDirectory() {
        Mockito.when(this.directory.isDirectory()).thenReturn(false);
        this.writer.getDirectory(this.directory, UUID.randomUUID().toString(), false);
    }

    /**
     * Make sure an exception is thrown when no request URL is passed in to the method.
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantGetDirectoryWithoutRequestUrl() {
        this.writer.getDirectory(this.directory, null, false);
    }

    /**
     * Make sure can get a directory with a parent.
     */
    @Test
    public void canGetDirectoryWithParent() {
        this.setupWithParent();
        final DefaultDirectoryWriter.Directory dir = this.writer.getDirectory(this.directory, DefaultDirectoryWriterUnitTests.REQUEST_URL_WITH_PARENT, true);
        Assert.assertThat(dir.getParent(), Matchers.notNullValue());
        Assert.assertThat(dir.getParent().getName(), Matchers.is(DefaultDirectoryWriterUnitTests.PARENT_NAME));
        Assert.assertThat(dir.getParent().getUrl(), Matchers.is(DefaultDirectoryWriterUnitTests.PARENT_URL));
        Assert.assertThat(dir.getParent().getSize(), Matchers.is(DefaultDirectoryWriterUnitTests.PARENT_SIZE));
        Assert.assertThat(dir.getParent().getLastModified(), Matchers.is(DefaultDirectoryWriterUnitTests.PARENT_LAST_MODIFIED));
        Assert.assertThat(dir.getDirectories(), Matchers.notNullValue());
        Assert.assertThat(dir.getDirectories().size(), Matchers.is(2));
        Assert.assertThat(dir.getDirectories(), Matchers.containsInAnyOrder(this.directoryEntry1, this.directoryEntry2));
        Assert.assertThat(dir.getFiles(), Matchers.notNullValue());
        Assert.assertThat(dir.getFiles().size(), Matchers.is(2));
        Assert.assertThat(dir.getFiles(), Matchers.containsInAnyOrder(this.fileEntry1, this.fileEntry2));
    }

    /**
     * Make sure can get a directory without a parent.
     */
    @Test
    public void canGetDirectoryWithoutParent() {
        this.setupWithoutParent();
        final DefaultDirectoryWriter.Directory dir = this.writer.getDirectory(this.directory, DefaultDirectoryWriterUnitTests.REQUEST_URL_BASE, false);
        Assert.assertThat(dir.getParent(), Matchers.nullValue());
        Assert.assertThat(dir.getDirectories(), Matchers.notNullValue());
        Assert.assertTrue(dir.getDirectories().isEmpty());
        Assert.assertThat(dir.getFiles(), Matchers.notNullValue());
        Assert.assertTrue(dir.getFiles().isEmpty());
    }

    /**
     * Make sure can get html representation of the directory.
     *
     * @throws Exception
     * 		on any problem
     */
    @Test
    public void canConvertToHtml() throws Exception {
        this.setupWithParent();
        final String html = this.writer.toHtml(this.directory, DefaultDirectoryWriterUnitTests.REQUEST_URL_WITH_PARENT, true);
        Assert.assertThat(html, Matchers.notNullValue());
        // Not going to parse the whole HTML to validate contents, too much work.
        // So just make sure HTML is valid so at least it doesn't cause error in browser
        final Tidy tidy = new Tidy();
        final Writer stringWriter = new StringWriter();
        tidy.parse(new ByteArrayInputStream(html.getBytes(Charset.forName("UTF-8"))), stringWriter);
        Assert.assertThat(tidy.getParseErrors(), Matchers.is(0));
        Assert.assertThat(tidy.getParseWarnings(), Matchers.is(0));
    }

    /**
     * Make sure can get a json representation of the directory.
     *
     * @throws Exception
     * 		on any problem
     */
    @Test
    public void canConvertToJson() throws Exception {
        this.setupWithParent();
        final String json = this.writer.toJson(this.directory, DefaultDirectoryWriterUnitTests.REQUEST_URL_WITH_PARENT, true);
        Assert.assertThat(json, Matchers.notNullValue());
        final DefaultDirectoryWriter.Directory dir = GenieObjectMapper.getMapper().readValue(json, Directory.class);
        Assert.assertThat(dir.getParent(), Matchers.notNullValue());
        Assert.assertThat(dir.getParent().getName(), Matchers.is(DefaultDirectoryWriterUnitTests.PARENT_NAME));
        Assert.assertThat(dir.getParent().getUrl(), Matchers.is(DefaultDirectoryWriterUnitTests.PARENT_URL));
        Assert.assertThat(dir.getParent().getSize(), Matchers.is(DefaultDirectoryWriterUnitTests.PARENT_SIZE));
        Assert.assertThat(dir.getParent().getLastModified(), Matchers.is(DefaultDirectoryWriterUnitTests.PARENT_LAST_MODIFIED));
        Assert.assertThat(dir.getDirectories(), Matchers.notNullValue());
        Assert.assertThat(dir.getDirectories().size(), Matchers.is(2));
        Assert.assertThat(dir.getDirectories(), Matchers.containsInAnyOrder(this.directoryEntry1, this.directoryEntry2));
        Assert.assertThat(dir.getFiles(), Matchers.notNullValue());
        Assert.assertThat(dir.getFiles().size(), Matchers.is(2));
        Assert.assertThat(dir.getFiles(), Matchers.containsInAnyOrder(this.fileEntry1, this.fileEntry2));
    }
}

