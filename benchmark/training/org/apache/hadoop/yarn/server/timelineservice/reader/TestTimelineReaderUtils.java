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
package org.apache.hadoop.yarn.server.timelineservice.reader;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestTimelineReaderUtils {
    @Test
    public void testSplitUsingEscapeAndDelimChar() throws Exception {
        List<String> list = TimelineReaderUtils.split("*!cluster!*!b**o***!xer!oozie**", '!', '*');
        String[] arr = new String[list.size()];
        arr = list.toArray(arr);
        Assert.assertArrayEquals(new String[]{ "!cluster", "!b*o*!xer", "oozie*" }, arr);
        list = TimelineReaderUtils.split("*!cluster!*!b**o***!xer!!", '!', '*');
        arr = new String[list.size()];
        arr = list.toArray(arr);
        Assert.assertArrayEquals(new String[]{ "!cluster", "!b*o*!xer", "", "" }, arr);
    }

    @Test
    public void testJoinAndEscapeStrings() throws Exception {
        Assert.assertEquals("*!cluster!*!b**o***!xer!oozie**", TimelineReaderUtils.joinAndEscapeStrings(new String[]{ "!cluster", "!b*o*!xer", "oozie*" }, '!', '*'));
        Assert.assertEquals("*!cluster!*!b**o***!xer!!", TimelineReaderUtils.joinAndEscapeStrings(new String[]{ "!cluster", "!b*o*!xer", "", "" }, '!', '*'));
        Assert.assertNull(TimelineReaderUtils.joinAndEscapeStrings(new String[]{ "!cluster", "!b*o*!xer", null, "" }, '!', '*'));
    }
}

