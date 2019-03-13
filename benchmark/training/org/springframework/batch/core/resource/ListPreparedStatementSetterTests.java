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
package org.springframework.batch.core.resource;


import BatchStatus.COMPLETED;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.support.ListPreparedStatementSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Lucas Ward
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/org/springframework/batch/core/resource/ListPreparedStatementSetterTests-context.xml", "/org/springframework/batch/core/repository/dao/data-source-context.xml" })
public class ListPreparedStatementSetterTests {
    ListPreparedStatementSetter pss;

    StepExecution stepExecution;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AbstractJob job;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private ListPreparedStatementSetterTests.FooStoringItemWriter fooStoringItemWriter;

    @Transactional
    @Test
    public void testSetValues() {
        final List<String> results = new ArrayList<>();
        jdbcTemplate.query("SELECT NAME from T_FOOS where ID > ? and ID < ?", pss, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                results.add(rs.getString(1));
            }
        });
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("bar2", results.get(0));
        Assert.assertEquals("bar3", results.get(1));
    }

    @Transactional
    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSet() throws Exception {
        pss = new ListPreparedStatementSetter(null);
        pss.afterPropertiesSet();
    }

    @Test
    public void testXmlConfiguration() throws Exception {
        this.jdbcTemplate.update("create table FOO (ID integer, NAME varchar(40), VALUE integer)");
        try {
            this.jdbcTemplate.update("insert into FOO values (?,?,?)", 0, "zero", 0);
            this.jdbcTemplate.update("insert into FOO values (?,?,?)", 1, "one", 1);
            this.jdbcTemplate.update("insert into FOO values (?,?,?)", 2, "two", 2);
            this.jdbcTemplate.update("insert into FOO values (?,?,?)", 3, "three", 3);
            JobParametersBuilder builder = new JobParametersBuilder().addLong("min.id", 1L).addLong("max.id", 2L);
            JobExecution jobExecution = this.jobLauncher.run(this.job, builder.toJobParameters());
            Assert.assertEquals(COMPLETED, jobExecution.getStatus());
            List<Foo> foos = fooStoringItemWriter.getFoos();
            Assert.assertEquals(2, foos.size());
            System.err.println(foos.get(0));
            System.err.println(foos.get(1));
            Assert.assertEquals(new Foo(1, "one", 1), foos.get(0));
            Assert.assertEquals(new Foo(2, "two", 2), foos.get(1));
        } finally {
            this.jdbcTemplate.update("drop table FOO");
        }
    }

    public static class FooStoringItemWriter implements ItemWriter<Foo> {
        private List<Foo> foos = new ArrayList<>();

        @Override
        public void write(List<? extends Foo> items) throws Exception {
            foos.addAll(items);
        }

        public List<Foo> getFoos() {
            return foos;
        }
    }
}

