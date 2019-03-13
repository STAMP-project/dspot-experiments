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
package org.apache.hadoop.mapred;


import MRJobConfig.COMBINE_RECORDS_BEFORE_PROGRESS;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.IFile.Writer;
import org.apache.hadoop.mapred.Task.CombineOutputCollector;
import org.apache.hadoop.mapred.Task.TaskReporter;
import org.junit.Test;
import org.mockito.Mockito;

import static Task.DEFAULT_COMBINE_RECORDS_BEFORE_PROGRESS;


public class TestCombineOutputCollector {
    private CombineOutputCollector<String, Integer> coc;

    Counter outCounter = new Counters.Counter() {
        private long value;

        @Override
        public void setValue(long value) {
            this.value = value;
        }

        @Override
        public void setDisplayName(String displayName) {
            // TODO Auto-generated method stub
        }

        @Override
        public void increment(long incr) {
            this.value += incr;
        }

        @Override
        public long getValue() {
            return value;
        }

        @Override
        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDisplayName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String makeEscapedCompactString() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getCounter() {
            return value;
        }

        @Override
        public boolean contentEquals(Counter counter) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void write(DataOutput out) throws IOException {
        }

        @Override
        public void readFields(DataInput in) throws IOException {
        }
    };

    @Test
    public void testCustomCollect() throws Throwable {
        // mock creation
        TaskReporter mockTaskReporter = Mockito.mock(TaskReporter.class);
        @SuppressWarnings("unchecked")
        Writer<String, Integer> mockWriter = Mockito.mock(Writer.class);
        Configuration conf = new Configuration();
        conf.set(COMBINE_RECORDS_BEFORE_PROGRESS, "2");
        coc = new CombineOutputCollector<String, Integer>(outCounter, mockTaskReporter, conf);
        coc.setWriter(mockWriter);
        Mockito.verify(mockTaskReporter, Mockito.never()).progress();
        coc.collect("dummy", 1);
        Mockito.verify(mockTaskReporter, Mockito.never()).progress();
        coc.collect("dummy", 2);
        Mockito.verify(mockTaskReporter, Mockito.times(1)).progress();
    }

    @Test
    public void testDefaultCollect() throws Throwable {
        // mock creation
        TaskReporter mockTaskReporter = Mockito.mock(TaskReporter.class);
        @SuppressWarnings("unchecked")
        Writer<String, Integer> mockWriter = Mockito.mock(Writer.class);
        Configuration conf = new Configuration();
        coc = new CombineOutputCollector<String, Integer>(outCounter, mockTaskReporter, conf);
        coc.setWriter(mockWriter);
        Mockito.verify(mockTaskReporter, Mockito.never()).progress();
        for (int i = 0; i < (DEFAULT_COMBINE_RECORDS_BEFORE_PROGRESS); i++) {
            coc.collect("dummy", i);
        }
        Mockito.verify(mockTaskReporter, Mockito.times(1)).progress();
        for (int i = 0; i < (DEFAULT_COMBINE_RECORDS_BEFORE_PROGRESS); i++) {
            coc.collect("dummy", i);
        }
        Mockito.verify(mockTaskReporter, Mockito.times(2)).progress();
    }
}

