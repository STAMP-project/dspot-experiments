/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.lucene.internal.directory;


import java.io.File;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.lucene.internal.filesystem.FileSystem;
import org.apache.geode.internal.cache.BucketNotFoundException;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class DumpDirectoryFilesJUnitTest {
    private RegionFunctionContext context;

    private String indexName = "index";

    private String directoryName = "directory";

    private String bucketName = "bucket";

    private FileSystem fileSystem;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldInvokeExportOnBuckets() throws BucketNotFoundException {
        DumpDirectoryFiles dump = new DumpDirectoryFiles();
        dump.execute(context);
        File expectedDir = new File(directoryName, (((indexName) + "_") + (bucketName)));
        Mockito.verify(fileSystem).export(ArgumentMatchers.eq(expectedDir));
    }

    @Test
    public void shouldThrowIllegalStateWhenMissingIndex() throws BucketNotFoundException {
        DumpDirectoryFiles dump = new DumpDirectoryFiles();
        Mockito.when(context.getArguments()).thenReturn(new String[]{ "badDirectory", "badIndex" });
        expectedException.expect(IllegalStateException.class);
        dump.execute(context);
    }

    @Test
    public void shouldThrowIllegalArgumentWhenGivenBadArguments() throws BucketNotFoundException {
        DumpDirectoryFiles dump = new DumpDirectoryFiles();
        Mockito.when(context.getArguments()).thenReturn(new Object());
        expectedException.expect(IllegalArgumentException.class);
        dump.execute(context);
    }

    @Test
    public void shouldThrowIllegalArgumentWhenMissingArgument() throws BucketNotFoundException {
        DumpDirectoryFiles dump = new DumpDirectoryFiles();
        Mockito.when(context.getArguments()).thenReturn(new String[]{ "not enough args" });
        expectedException.expect(IllegalArgumentException.class);
        dump.execute(context);
    }
}

