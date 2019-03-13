/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.itest.java;


import io.restassured.itest.java.support.WithJetty;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


// TODO Fix test!!
@Ignore("Temporary ignored since the remote zip file (powermock) is no longer downloadable")
public class FileDownloadITest extends WithJetty {
    @Test
    public void canDownloadLargeFiles() throws Exception {
        int expectedSize = IOUtils.toByteArray(getClass().getResourceAsStream("/powermock-easymock-junit-1.4.12.zip")).length;
        final InputStream inputStream = when().get("http://powermock.googlecode.com/files/powermock-easymock-junit-1.4.12.zip").asInputStream();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, byteArrayOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        IOUtils.closeQuietly(inputStream);
        Assert.assertThat(byteArrayOutputStream.size(), equalTo(expectedSize));
    }

    @Test
    public void canDownloadLargeFilesWhenLoggingIfValidationFailsIsEnabled() throws Exception {
        int expectedSize = IOUtils.toByteArray(getClass().getResourceAsStream("/powermock-easymock-junit-1.4.12.zip")).length;
        final InputStream inputStream = when().get("http://powermock.googlecode.com/files/powermock-easymock-junit-1.4.12.zip").then().log().ifValidationFails().extract().asInputStream();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, byteArrayOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        IOUtils.closeQuietly(inputStream);
        Assert.assertThat(byteArrayOutputStream.size(), equalTo(expectedSize));
    }

    @Test
    public void loggingWithBinaryCharsetWorks() throws Exception {
        int expectedSize = IOUtils.toByteArray(getClass().getResourceAsStream("/powermock-easymock-junit-1.4.12.zip")).length;
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        final InputStream inputStream = when().get("http://powermock.googlecode.com/files/powermock-easymock-junit-1.4.12.zip").asInputStream();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, byteArrayOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        IOUtils.closeQuietly(inputStream);
        Assert.assertThat(writer.toString(), not(isEmptyOrNullString()));
        Assert.assertThat(byteArrayOutputStream.size(), equalTo(expectedSize));
    }
}

