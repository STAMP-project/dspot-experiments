/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.receiver.service;


import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class LimitedListTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testMaxSize() {
        Comparator<ThreadDump> threadDump = Collections.reverseOrder(new ThreadDumpComparator());
        final int maxSize = 10;
        Collection<ThreadDump> limitedList = new LimitedList<ThreadDump>(maxSize, threadDump);
        final int id = 100;
        final long startTime = System.currentTimeMillis();
        final long threadId = 1000;
        logger.debug("startTime:{}", startTime);
        final List<ThreadDump> testData = newTestData(id, startTime, threadId, (maxSize * 2));
        final long lastTime = getLastObject(testData).getActiveTraceSnapshot().getStartTime();
        logger.debug("addAll ");
        limitedList.addAll(testData);
        logger.debug("size:{}", limitedList.size());
        for (ThreadDump activeTraceSnapshot : limitedList) {
            logger.debug("priorityQueue:{}", activeTraceSnapshot);
        }
        List<ThreadDump> sortedList = Lists.newArrayList(limitedList);
        Collections.sort(sortedList, threadDump);
        for (ThreadDump activeTraceSnapshot : sortedList) {
            logger.debug("poll:{}", activeTraceSnapshot);
        }
        ThreadDump last = getLastObject(sortedList);
        logger.debug("last pool:{}", last);
        logger.debug("poll.startTime:{}", last.getActiveTraceSnapshot().getStartTime());
        logger.debug("startTime:{}", lastTime);
        Assert.assertEquals(last.getActiveTraceSnapshot().getStartTime(), startTime);
    }
}

