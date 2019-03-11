/**
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.step.item;


import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Dave Syer
 */
@RunWith(Parameterized.class)
public class AlmostStatefulRetryChunkTests {
    private Log logger = LogFactory.getLog(getClass());

    private final Chunk<String> chunk;

    private final int retryLimit;

    private int retryAttempts = 0;

    private static final int BACKSTOP_LIMIT = 1000;

    private int count = 0;

    public AlmostStatefulRetryChunkTests(String[] args, int limit) {
        chunk = new Chunk();
        for (String string : args) {
            chunk.add(string);
        }
        this.retryLimit = limit;
    }

    @Test
    public void testRetry() throws Exception {
        logger.debug("Starting simple scenario");
        List<String> items = new java.util.ArrayList(chunk.getItems());
        int before = items.size();
        items.removeAll(Collections.singleton("fail"));
        boolean error = true;
        while (error && (((count)++) < (AlmostStatefulRetryChunkTests.BACKSTOP_LIMIT))) {
            try {
                statefulRetry(chunk);
                error = false;
            } catch (Exception e) {
                error = true;
            }
        } 
        logger.debug(("Chunk: " + (chunk)));
        Assert.assertTrue("Backstop reached.  Probably an infinite loop...", ((count) < (AlmostStatefulRetryChunkTests.BACKSTOP_LIMIT)));
        Assert.assertFalse(chunk.getItems().contains("fail"));
        Assert.assertEquals(items, chunk.getItems());
        Assert.assertEquals((before - (chunk.getItems().size())), chunk.getSkips().size());
    }
}

