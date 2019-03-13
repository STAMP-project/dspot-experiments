/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.text.sentenceiterator;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class PrefetchingSentenceIteratorTest {
    protected static final Logger log = LoggerFactory.getLogger(PrefetchingSentenceIteratorTest.class);

    @Test
    public void testHasMoreLinesFile() throws Exception {
        ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
        File file = resource.getFile();
        BasicLineIterator iterator = new BasicLineIterator(file);
        PrefetchingSentenceIterator fetcher = setFetchSize(1000).build();
        PrefetchingSentenceIteratorTest.log.info("Phase 1 starting");
        int cnt = 0;
        while (fetcher.hasNext()) {
            String line = fetcher.nextSentence();
            // log.info(line);
            cnt++;
        } 
        Assert.assertEquals(97162, cnt);
        PrefetchingSentenceIteratorTest.log.info("Phase 2 starting");
        fetcher.reset();
        cnt = 0;
        while (fetcher.hasNext()) {
            String line = fetcher.nextSentence();
            cnt++;
        } 
        Assert.assertEquals(97162, cnt);
    }

    @Test
    public void testLoadedIterator1() throws Exception {
        ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
        File file = resource.getFile();
        BasicLineIterator iterator = new BasicLineIterator(file);
        PrefetchingSentenceIterator fetcher = setFetchSize(1000).build();
        PrefetchingSentenceIteratorTest.log.info("Phase 1 starting");
        int cnt = 0;
        while (fetcher.hasNext()) {
            String line = fetcher.nextSentence();
            // we'll imitate some workload in current thread by using ThreadSleep.
            // there's no need to keep it enabled forever, just uncomment next line if you're going to test this iterator.
            // otherwise this test will
            // Thread.sleep(0, 10);
            cnt++;
            if ((cnt % 10000) == 0)
                PrefetchingSentenceIteratorTest.log.info(("Line processed: " + cnt));

        } 
    }

    @Test
    public void testPerformance1() throws Exception {
        ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
        File file = resource.getFile();
        BasicLineIterator iterator = new BasicLineIterator(file);
        PrefetchingSentenceIterator fetcher = setFetchSize(500000).build();
        long time01 = System.currentTimeMillis();
        int cnt0 = 0;
        while (iterator.hasNext()) {
            iterator.nextSentence();
            cnt0++;
        } 
        long time02 = System.currentTimeMillis();
        long time11 = System.currentTimeMillis();
        int cnt1 = 0;
        while (fetcher.hasNext()) {
            fetcher.nextSentence();
            cnt1++;
        } 
        long time12 = System.currentTimeMillis();
        PrefetchingSentenceIteratorTest.log.info(("Basic iterator: " + (time02 - time01)));
        PrefetchingSentenceIteratorTest.log.info(("Prefetched iterator: " + (time12 - time11)));
        long difference = (time12 - time11) - (time02 - time01);
        PrefetchingSentenceIteratorTest.log.info(("Difference: " + difference));
        // on small corpus time difference can fluctuate a lot
        // but it's still can be used as effectiveness measurement
        Assert.assertTrue((difference < 150));
    }
}

