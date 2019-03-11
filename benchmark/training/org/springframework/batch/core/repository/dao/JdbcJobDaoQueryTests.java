/**
 * Copyright 2006-2013 the original author or authors.
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
package org.springframework.batch.core.repository.dao;


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 *
 *
 * @author Dave Syer
 */
public class JdbcJobDaoQueryTests extends TestCase {
    JdbcJobExecutionDao jobExecutionDao;

    List<String> list = new ArrayList<>();

    public void testTablePrefix() throws Exception {
        jobExecutionDao.setTablePrefix("FOO_");
        jobExecutionDao.setJdbcTemplate(new JdbcTemplate() {
            @Override
            public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException {
                list.add(sql);
                return 1;
            }
        });
        JobExecution jobExecution = new JobExecution(new JobInstance(new Long(11), "testJob"), new JobParameters());
        jobExecutionDao.saveJobExecution(jobExecution);
        TestCase.assertEquals(1, list.size());
        String query = list.get(0);
        TestCase.assertTrue(("Query did not contain FOO_:" + query), ((query.indexOf("FOO_")) >= 0));
    }
}

