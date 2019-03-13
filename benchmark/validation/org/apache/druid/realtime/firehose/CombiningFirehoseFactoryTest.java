/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.realtime.firehose;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.druid.data.input.Firehose;
import org.apache.druid.data.input.FirehoseFactory;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.java.util.common.parsers.ParseException;
import org.apache.druid.utils.Runnables;
import org.junit.Assert;
import org.junit.Test;


public class CombiningFirehoseFactoryTest {
    @Test
    public void testCombiningfirehose() throws IOException {
        List<InputRow> list1 = Arrays.asList(makeRow(1, 1), makeRow(2, 2));
        List<InputRow> list2 = Arrays.asList(makeRow(3, 3), makeRow(4, 4), makeRow(5, 5));
        FirehoseFactory combiningFactory = new org.apache.druid.segment.realtime.firehose.CombiningFirehoseFactory(Arrays.asList(new CombiningFirehoseFactoryTest.ListFirehoseFactory(list1), new CombiningFirehoseFactoryTest.ListFirehoseFactory(list2)));
        final Firehose firehose = combiningFactory.connect(null, null);
        for (int i = 1; i < 6; i++) {
            Assert.assertTrue(firehose.hasMore());
            final InputRow inputRow = firehose.nextRow();
            Assert.assertEquals(i, inputRow.getTimestampFromEpoch());
            Assert.assertEquals(i, inputRow.getMetric("test").floatValue(), 0);
        }
        Assert.assertFalse(firehose.hasMore());
    }

    public static class ListFirehoseFactory implements FirehoseFactory<InputRowParser> {
        private final List<InputRow> rows;

        ListFirehoseFactory(List<InputRow> rows) {
            this.rows = rows;
        }

        @Override
        public Firehose connect(InputRowParser inputRowParser, File temporaryDirectory) throws ParseException {
            final Iterator<InputRow> iterator = rows.iterator();
            return new Firehose() {
                @Override
                public boolean hasMore() {
                    return iterator.hasNext();
                }

                @Nullable
                @Override
                public InputRow nextRow() {
                    return iterator.next();
                }

                @Override
                public Runnable commit() {
                    return Runnables.getNoopRunnable();
                }

                @Override
                public void close() {
                    // Do nothing
                }
            };
        }
    }
}

