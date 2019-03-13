/**
 * Copyright 2006-2008 the original author or authors.
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


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 *
 *
 * @author Dave Syer
 * @author Thomas Risberg
 * @author Will Schipp
 */
public class JdbcBatchItemWriterClassicTests {
    private JdbcBatchItemWriter<String> writer = new JdbcBatchItemWriter();

    private JdbcTemplate jdbcTemplate;

    protected List<Object> list = new ArrayList<>();

    private PreparedStatement ps;

    /**
     * Test method for
     * {@link org.springframework.batch.item.database.JdbcBatchItemWriter#afterPropertiesSet()}
     * .
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAfterPropertiesSet() throws Exception {
        writer = new JdbcBatchItemWriter();
        try {
            writer.afterPropertiesSet();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
            String message = e.getMessage();
            Assert.assertTrue("Message does not contain ' NamedParameterJdbcTemplate'.", ((message.indexOf("NamedParameterJdbcTemplate")) >= 0));
        }
        writer.setJdbcTemplate(new org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate(jdbcTemplate));
        try {
            writer.afterPropertiesSet();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
            String message = e.getMessage().toLowerCase();
            Assert.assertTrue("Message does not contain 'sql'.", ((message.indexOf("sql")) >= 0));
        }
        writer.setSql("select * from foo where id = ?");
        try {
            writer.afterPropertiesSet();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
            String message = e.getMessage();
            Assert.assertTrue("Message does not contain 'ItemPreparedStatementSetter'.", ((message.indexOf("ItemPreparedStatementSetter")) >= 0));
        }
        writer.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<String>() {
            @Override
            public void setValues(String item, PreparedStatement ps) throws SQLException {
            }
        });
        writer.afterPropertiesSet();
    }

    @Test
    public void testWriteAndFlush() throws Exception {
        ps.addBatch();
        Mockito.when(ps.executeBatch()).thenReturn(new int[]{ 123 });
        writer.write(Collections.singletonList("bar"));
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains("SQL"));
    }

    @Test
    public void testWriteAndFlushWithEmptyUpdate() throws Exception {
        ps.addBatch();
        Mockito.when(ps.executeBatch()).thenReturn(new int[]{ 0 });
        try {
            writer.write(Collections.singletonList("bar"));
            Assert.fail("Expected EmptyResultDataAccessException");
        } catch (EmptyResultDataAccessException e) {
            // expected
            String message = e.getMessage();
            Assert.assertTrue(("Wrong message: " + message), ((message.indexOf("did not update")) >= 0));
        }
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains("SQL"));
    }

    @Test
    public void testWriteAndFlushWithFailure() throws Exception {
        final RuntimeException ex = new RuntimeException("bar");
        writer.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<String>() {
            @Override
            public void setValues(String item, PreparedStatement ps) throws SQLException {
                list.add(item);
                throw ex;
            }
        });
        ps.addBatch();
        Mockito.when(ps.executeBatch()).thenReturn(new int[]{ 123 });
        try {
            writer.write(Collections.singletonList("foo"));
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("bar", e.getMessage());
        }
        Assert.assertEquals(2, list.size());
        writer.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<String>() {
            @Override
            public void setValues(String item, PreparedStatement ps) throws SQLException {
                list.add(item);
            }
        });
        writer.write(Collections.singletonList("foo"));
        Assert.assertEquals(4, list.size());
        Assert.assertTrue(list.contains("SQL"));
        Assert.assertTrue(list.contains("foo"));
    }
}

