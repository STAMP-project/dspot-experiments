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
package org.apache.nifi.provenance.store.iterator;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.serialization.RecordReaders;
import org.apache.nifi.provenance.store.RecordReaderFactory;
import org.junit.Assert;
import org.junit.Test;


public class TestSelectiveRecordReaderEventIterator {
    @Test
    public void testFilterUnneededFiles() {
        final File file1 = new File("1.prov");
        final File file1000 = new File("1000.prov");
        final File file2000 = new File("2000.prov");
        final File file3000 = new File("3000.prov");
        // Filter out the first file.
        final List<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file1000);
        files.add(file2000);
        files.add(file3000);
        List<Long> eventIds = new ArrayList<>();
        eventIds.add(1048L);
        eventIds.add(2048L);
        eventIds.add(3048L);
        List<File> filteredFiles = SelectiveRecordReaderEventIterator.filterUnneededFiles(files, eventIds);
        Assert.assertEquals(Arrays.asList(new File[]{ file1000, file2000, file3000 }), filteredFiles);
        // Filter out file at end
        eventIds.clear();
        eventIds.add(1L);
        eventIds.add(1048L);
        filteredFiles = SelectiveRecordReaderEventIterator.filterUnneededFiles(files, eventIds);
        Assert.assertEquals(Arrays.asList(new File[]{ file1, file1000 }), filteredFiles);
    }

    @Test
    public void testFileNotFound() throws IOException {
        final File file1 = new File("1.prov");
        // Filter out the first file.
        final List<File> files = new ArrayList<>();
        files.add(file1);
        List<Long> eventIds = new ArrayList<>();
        eventIds.add(1L);
        eventIds.add(5L);
        final RecordReaderFactory readerFactory = ( file, logs, maxChars) -> {
            return RecordReaders.newRecordReader(file, logs, maxChars);
        };
        final SelectiveRecordReaderEventIterator itr = new SelectiveRecordReaderEventIterator(files, readerFactory, eventIds, 65536);
        final Optional<ProvenanceEventRecord> firstRecordOption = itr.nextEvent();
        Assert.assertFalse(firstRecordOption.isPresent());
    }
}

