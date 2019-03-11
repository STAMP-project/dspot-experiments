/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.hadoop.inotify;


import java.util.regex.Pattern;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.junit.Assert;
import org.junit.Test;


public class TestNotificationEventPathFilter {
    @Test
    public void acceptShouldProperlyReturnFalseWithNullPath() throws Exception {
        Assert.assertFalse(new NotificationEventPathFilter(Pattern.compile(""), true).accept(null));
    }

    @Test
    public void acceptPathShouldProperlyIgnorePathsWhereTheLastComponentStartsWithADot() throws Exception {
        PathFilter filter = new NotificationEventPathFilter(Pattern.compile(".*"), true);
        Assert.assertFalse(filter.accept(new Path("/.some_hidden_file")));
        Assert.assertFalse(filter.accept(new Path("/some/long/path/.some_hidden_file/")));
    }

    @Test
    public void acceptPathShouldProperlyAcceptPathsWhereTheNonLastComponentStartsWithADot() throws Exception {
        PathFilter filter = new NotificationEventPathFilter(Pattern.compile(".*"), true);
        Assert.assertTrue(filter.accept(new Path("/some/long/path/.some_hidden_file/should/work")));
        Assert.assertTrue(filter.accept(new Path("/.some_hidden_file/should/still/accept")));
    }

    @Test
    public void acceptPathShouldProperlyMatchAllSubdirectoriesThatMatchWatchDirectoryAndFileFilter() throws Exception {
        PathFilter filter = new NotificationEventPathFilter(Pattern.compile("/root(/.*)?"), true);
        Assert.assertTrue(filter.accept(new Path("/root/sometest.txt")));
    }

    @Test
    public void acceptPathShouldProperlyMatchWhenWatchDirectoryMatchesPath() throws Exception {
        PathFilter filter = new NotificationEventPathFilter(Pattern.compile("/root(/.*)?"), false);
        Assert.assertTrue(filter.accept(new Path("/root")));
    }
}

