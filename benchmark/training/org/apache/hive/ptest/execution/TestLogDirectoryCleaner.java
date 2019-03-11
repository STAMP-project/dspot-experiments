/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.ptest.execution;


import com.google.common.collect.Lists;
import java.io.File;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestLogDirectoryCleaner {
    @Rule
    public TemporaryFolder baseDir = new TemporaryFolder();

    @Test
    public void testClean() throws Exception {
        File dir = create("a-0", "a-1", "a-2", "malformed", "b-0", "c-0", "c-5");
        LogDirectoryCleaner cleaner = new LogDirectoryCleaner(dir, 1);
        cleaner.run();
        List<String> remaining = Lists.newArrayList(dir.list());
        Collections.sort(remaining);
        Assert.assertEquals(Lists.newArrayList("a-1", "a-2", "b-0", "c-5", "malformed"), remaining);
    }
}

