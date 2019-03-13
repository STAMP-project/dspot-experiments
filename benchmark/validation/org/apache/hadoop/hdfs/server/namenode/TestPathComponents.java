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
package org.apache.hadoop.hdfs.server.namenode;


import org.apache.hadoop.hdfs.DFSUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class TestPathComponents {
    @Test
    public void testBytes2ByteArrayFQ() throws Exception {
        testString("/", new String[]{ null });
        testString("//", new String[]{ null });
        testString("/file", new String[]{ "", "file" });
        testString("/dir/", new String[]{ "", "dir" });
        testString("//file", new String[]{ "", "file" });
        testString("/dir//file", new String[]{ "", "dir", "file" });
        testString("//dir/dir1//", new String[]{ "", "dir", "dir1" });
        testString("//dir//dir1//", new String[]{ "", "dir", "dir1" });
        testString("//dir//dir1//file", new String[]{ "", "dir", "dir1", "file" });
    }

    @Test
    public void testBytes2ByteArrayRelative() throws Exception {
        testString("file", new String[]{ "file" });
        testString("dir/", new String[]{ "dir" });
        testString("dir//", new String[]{ "dir" });
        testString("dir//file", new String[]{ "dir", "file" });
        testString("dir/dir1//", new String[]{ "dir", "dir1" });
        testString("dir//dir1//", new String[]{ "dir", "dir1" });
        testString("dir//dir1//file", new String[]{ "dir", "dir1", "file" });
    }

    @Test
    public void testByteArray2PathStringRoot() {
        byte[][] components = DFSUtil.getPathComponents("/");
        Assert.assertEquals("", DFSUtil.byteArray2PathString(components, 0, 0));
        Assert.assertEquals("/", DFSUtil.byteArray2PathString(components, 0, 1));
    }

    @Test
    public void testByteArray2PathStringFQ() {
        byte[][] components = DFSUtil.getPathComponents("/1/2/3");
        Assert.assertEquals("/1/2/3", DFSUtil.byteArray2PathString(components));
        Assert.assertEquals("", DFSUtil.byteArray2PathString(components, 0, 0));
        Assert.assertEquals("/", DFSUtil.byteArray2PathString(components, 0, 1));
        Assert.assertEquals("/1", DFSUtil.byteArray2PathString(components, 0, 2));
        Assert.assertEquals("/1/2", DFSUtil.byteArray2PathString(components, 0, 3));
        Assert.assertEquals("/1/2/3", DFSUtil.byteArray2PathString(components, 0, 4));
        Assert.assertEquals("", DFSUtil.byteArray2PathString(components, 1, 0));
        Assert.assertEquals("1", DFSUtil.byteArray2PathString(components, 1, 1));
        Assert.assertEquals("1/2", DFSUtil.byteArray2PathString(components, 1, 2));
        Assert.assertEquals("1/2/3", DFSUtil.byteArray2PathString(components, 1, 3));
    }

    @Test
    public void testByteArray2PathStringRelative() {
        byte[][] components = DFSUtil.getPathComponents("1/2/3");
        Assert.assertEquals("1/2/3", DFSUtil.byteArray2PathString(components));
        Assert.assertEquals("", DFSUtil.byteArray2PathString(components, 0, 0));
        Assert.assertEquals("1", DFSUtil.byteArray2PathString(components, 0, 1));
        Assert.assertEquals("1/2", DFSUtil.byteArray2PathString(components, 0, 2));
        Assert.assertEquals("1/2/3", DFSUtil.byteArray2PathString(components, 0, 3));
        Assert.assertEquals("", DFSUtil.byteArray2PathString(components, 1, 0));
        Assert.assertEquals("2", DFSUtil.byteArray2PathString(components, 1, 1));
        Assert.assertEquals("2/3", DFSUtil.byteArray2PathString(components, 1, 2));
    }
}

