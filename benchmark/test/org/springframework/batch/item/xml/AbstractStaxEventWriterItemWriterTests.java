/**
 * Copyright 2010-2019 the original author or authors.
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
package org.springframework.batch.item.xml;


import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.xml.domain.Trade;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StopWatch;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.matchers.CompareMatcher;


public abstract class AbstractStaxEventWriterItemWriterTests {
    private Log logger = LogFactory.getLog(getClass());

    private static final int MAX_WRITE = 100;

    protected StaxEventItemWriter<Trade> writer = new StaxEventItemWriter();

    private Resource resource;

    private File outputFile;

    protected Resource expected = new ClassPathResource("expected-output.xml", getClass());

    @SuppressWarnings("serial")
    protected List<Trade> objects = new ArrayList<Trade>() {
        {
            add(new Trade("isin1", 1, new BigDecimal(1.0), "customer1"));
            add(new Trade("isin2", 2, new BigDecimal(2.0), "customer2"));
            add(new Trade("isin3", 3, new BigDecimal(3.0), "customer3"));
        }
    };

    /**
     * Write list of domain objects and check the output file.
     */
    @SuppressWarnings("resource")
    @Test
    public void testWrite() throws Exception {
        StopWatch stopWatch = new StopWatch(getClass().getSimpleName());
        stopWatch.start();
        for (int i = 0; i < (AbstractStaxEventWriterItemWriterTests.MAX_WRITE); i++) {
            execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    try {
                        writer.write(objects);
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IllegalStateException("Exception encountered on write", e);
                    }
                    return null;
                }
            });
        }
        writer.close();
        stopWatch.stop();
        logger.info(("Timing for XML writer: " + stopWatch));
        Assert.assertThat(Input.from(expected.getFile()), CompareMatcher.isSimilarTo(Input.from(resource.getFile())).withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }
}

