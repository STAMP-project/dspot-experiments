/**
 * Copyright 2010-2014 the original author or authors.
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
package org.springframework.batch.item.database;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "JpaPagingItemReaderCommonTests-context.xml")
public class JpaPagingItemReaderAsyncTests {
    /**
     * The number of items to read
     */
    private static final int ITEM_COUNT = 1000;

    /**
     * The number of threads to create
     */
    private static final int THREAD_COUNT = 10;

    private static final int PAGE_SIZE = 10;

    private static Log logger = LogFactory.getLog(JpaPagingItemReaderAsyncTests.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private int maxId;

    @Test
    public void testAsyncReader() throws Throwable {
        List<Throwable> throwables = new ArrayList<>();
        int max = 10;
        for (int i = 0; i < max; i++) {
            try {
                doTest();
            } catch (Throwable e) {
                throwables.add(e);
            }
        }
        if (!(throwables.isEmpty())) {
            throw new IllegalStateException(String.format("Failed %d out of %d", throwables.size(), max), throwables.get(0));
        }
    }
}

