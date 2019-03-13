/**
 * Copyright 2006-2014 the original author or authors.
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
package org.springframework.batch.sample.common;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StagingItemWriterTests {
    private JdbcOperations jdbcTemplate;

    @Autowired
    private StagingItemWriter<String> writer;

    @Transactional
    @Test
    public void testProcessInsertsNewItem() throws Exception {
        int before = jdbcTemplate.queryForObject("SELECT COUNT(*) from BATCH_STAGING", Integer.class);
        writer.write(Collections.singletonList("FOO"));
        int after = jdbcTemplate.queryForObject("SELECT COUNT(*) from BATCH_STAGING", Integer.class);
        Assert.assertEquals((before + 1), after);
    }
}

