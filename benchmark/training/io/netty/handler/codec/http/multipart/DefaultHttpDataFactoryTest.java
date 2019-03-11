/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http.multipart;


import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Assert;
import org.junit.Test;


public class DefaultHttpDataFactoryTest {
    // req1 equals req2
    private static final HttpRequest req1 = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/form");

    private static final HttpRequest req2 = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/form");

    private DefaultHttpDataFactory factory;

    @Test
    public void cleanRequestHttpDataShouldIdentifiesRequestsByTheirIdentities() throws Exception {
        // Create some data belonging to req1 and req2
        Attribute attribute1 = factory.createAttribute(DefaultHttpDataFactoryTest.req1, "attribute1", "value1");
        Attribute attribute2 = factory.createAttribute(DefaultHttpDataFactoryTest.req2, "attribute2", "value2");
        FileUpload file1 = factory.createFileUpload(DefaultHttpDataFactoryTest.req1, "file1", "file1.txt", HttpPostBodyUtil.DEFAULT_TEXT_CONTENT_TYPE, HttpHeaderValues.IDENTITY.toString(), UTF_8, 123);
        FileUpload file2 = factory.createFileUpload(DefaultHttpDataFactoryTest.req2, "file2", "file2.txt", HttpPostBodyUtil.DEFAULT_TEXT_CONTENT_TYPE, HttpHeaderValues.IDENTITY.toString(), UTF_8, 123);
        file1.setContent(Unpooled.copiedBuffer("file1 content", UTF_8));
        file2.setContent(Unpooled.copiedBuffer("file2 content", UTF_8));
        // Assert that they are not deleted
        Assert.assertNotNull(attribute1.getByteBuf());
        Assert.assertNotNull(attribute2.getByteBuf());
        Assert.assertNotNull(file1.getByteBuf());
        Assert.assertNotNull(file2.getByteBuf());
        Assert.assertEquals(1, attribute1.refCnt());
        Assert.assertEquals(1, attribute2.refCnt());
        Assert.assertEquals(1, file1.refCnt());
        Assert.assertEquals(1, file2.refCnt());
        // Clean up by req1
        factory.cleanRequestHttpData(DefaultHttpDataFactoryTest.req1);
        // Assert that data belonging to req1 has been cleaned up
        Assert.assertNull(attribute1.getByteBuf());
        Assert.assertNull(file1.getByteBuf());
        Assert.assertEquals(0, attribute1.refCnt());
        Assert.assertEquals(0, file1.refCnt());
        // But not req2
        Assert.assertNotNull(attribute2.getByteBuf());
        Assert.assertNotNull(file2.getByteBuf());
        Assert.assertEquals(1, attribute2.refCnt());
        Assert.assertEquals(1, file2.refCnt());
    }

    @Test
    public void removeHttpDataFromCleanShouldIdentifiesDataByTheirIdentities() throws Exception {
        // Create some equal data items belonging to the same request
        Attribute attribute1 = factory.createAttribute(DefaultHttpDataFactoryTest.req1, "attribute", "value");
        Attribute attribute2 = factory.createAttribute(DefaultHttpDataFactoryTest.req1, "attribute", "value");
        FileUpload file1 = factory.createFileUpload(DefaultHttpDataFactoryTest.req1, "file", "file.txt", HttpPostBodyUtil.DEFAULT_TEXT_CONTENT_TYPE, HttpHeaderValues.IDENTITY.toString(), UTF_8, 123);
        FileUpload file2 = factory.createFileUpload(DefaultHttpDataFactoryTest.req1, "file", "file.txt", HttpPostBodyUtil.DEFAULT_TEXT_CONTENT_TYPE, HttpHeaderValues.IDENTITY.toString(), UTF_8, 123);
        file1.setContent(Unpooled.copiedBuffer("file content", UTF_8));
        file2.setContent(Unpooled.copiedBuffer("file content", UTF_8));
        // Before doing anything, assert that the data items are equal
        Assert.assertEquals(attribute1.hashCode(), attribute2.hashCode());
        Assert.assertTrue(attribute1.equals(attribute2));
        Assert.assertEquals(file1.hashCode(), file2.hashCode());
        Assert.assertTrue(file1.equals(file2));
        // Remove attribute2 and file2 from being cleaned up by factory
        factory.removeHttpDataFromClean(DefaultHttpDataFactoryTest.req1, attribute2);
        factory.removeHttpDataFromClean(DefaultHttpDataFactoryTest.req1, file2);
        // Clean up by req1
        factory.cleanRequestHttpData(DefaultHttpDataFactoryTest.req1);
        // Assert that attribute1 and file1 have been cleaned up
        Assert.assertNull(attribute1.getByteBuf());
        Assert.assertNull(file1.getByteBuf());
        Assert.assertEquals(0, attribute1.refCnt());
        Assert.assertEquals(0, file1.refCnt());
        // But not attribute2 and file2
        Assert.assertNotNull(attribute2.getByteBuf());
        Assert.assertNotNull(file2.getByteBuf());
        Assert.assertEquals(1, attribute2.refCnt());
        Assert.assertEquals(1, file2.refCnt());
        // Cleanup attribute2 and file2 manually to avoid memory leak, not via factory
        attribute2.release();
        file2.release();
        Assert.assertEquals(0, attribute2.refCnt());
        Assert.assertEquals(0, file2.refCnt());
    }
}

