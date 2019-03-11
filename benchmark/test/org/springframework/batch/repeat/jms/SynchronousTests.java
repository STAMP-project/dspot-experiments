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
package org.springframework.batch.repeat.jms;


import java.util.ArrayList;
import java.util.List;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.connection.SessionProxy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/org/springframework/batch/jms/jms-context.xml")
@DirtiesContext
public class SynchronousTests implements ApplicationContextAware {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private RepeatTemplate repeatTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private JdbcTemplate jdbcTemplate;

    private ApplicationContext applicationContext;

    private List<String> list = new ArrayList<>();

    @Transactional
    @Test
    public void testCommit() throws Exception {
        assertInitialState();
        repeatTemplate.iterate(new RepeatCallback() {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                String text = ((String) (jmsTemplate.receiveAndConvert("queue")));
                list.add(text);
                jdbcTemplate.update("INSERT into T_BARS (id,name,foo_date) values (?,?,null)", list.size(), text);
                return RepeatStatus.continueIf((text != null));
            }
        });
        int count = jdbcTemplate.queryForObject("select count(*) from T_BARS", Integer.class);
        Assert.assertEquals(2, count);
        Assert.assertTrue(list.contains("foo"));
        Assert.assertTrue(list.contains("bar"));
        String text = ((String) (jmsTemplate.receiveAndConvert("queue")));
        Assert.assertEquals(null, text);
    }

    @Test
    public void testFullRollback() throws Exception {
        onSetUpBeforeTransaction();
        assertInitialState();
        new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                repeatTemplate.iterate(new RepeatCallback() {
                    @Override
                    public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                        String text = ((String) (jmsTemplate.receiveAndConvert("queue")));
                        list.add(text);
                        jdbcTemplate.update("INSERT into T_BARS (id,name,foo_date) values (?,?,null)", list.size(), text);
                        return RepeatStatus.continueIf((text != null));
                    }
                });
                // force rollback...
                status.setRollbackOnly();
                return null;
            }
        });
        String text = "";
        List<String> msgs = new ArrayList<>();
        while (text != null) {
            text = ((String) (jmsTemplate.receiveAndConvert("queue")));
            msgs.add(text);
        } 
        // The database portion rolled back...
        int count = jdbcTemplate.queryForObject("select count(*) from T_BARS", Integer.class);
        Assert.assertEquals(0, count);
        // ... and so did the message session. The rollback should have restored
        // the queue, so this should now be non-null
        Assert.assertTrue("Foo not on queue", msgs.contains("foo"));
    }

    @Transactional
    @Test
    public void testPartialRollback() throws Exception {
        // The JmsTemplate is used elsewhere outside a transaction, so
        // we need to use one here that is transaction aware.
        final JmsTemplate txJmsTemplate = new JmsTemplate(((ConnectionFactory) (applicationContext.getBean("txAwareConnectionFactory"))));
        txJmsTemplate.setReceiveTimeout(100L);
        txJmsTemplate.setSessionTransacted(true);
        assertInitialState();
        execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                repeatTemplate.iterate(new RepeatCallback() {
                    @Override
                    public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                        String text = ((String) (txJmsTemplate.receiveAndConvert("queue")));
                        list.add(text);
                        jdbcTemplate.update("INSERT into T_BARS (id,name,foo_date) values (?,?,null)", list.size(), text);
                        return RepeatStatus.continueIf((text != null));
                    }
                });
                // Simulate a message system failure before the main transaction
                // commits...
                txJmsTemplate.execute(new org.springframework.jms.core.SessionCallback<Void>() {
                    @Override
                    public Void doInJms(Session session) throws JMSException {
                        try {
                            Assert.assertTrue("Not a SessionProxy - wrong spring version?", (session instanceof SessionProxy));
                            getTargetSession().rollback();
                        } catch (JMSException e) {
                            throw e;
                        } catch (Exception e) {
                            // swallow it
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
                return null;
            }
        });
        String text = "";
        List<String> msgs = new ArrayList<>();
        while (text != null) {
            text = ((String) (txJmsTemplate.receiveAndConvert("queue")));
            msgs.add(text);
        } 
        // The database portion committed...
        int count = jdbcTemplate.queryForObject("select count(*) from T_BARS", Integer.class);
        Assert.assertEquals(2, count);
        // ...but the JMS session rolled back, so the message is still there
        Assert.assertTrue("Foo not on queue", msgs.contains("foo"));
        Assert.assertTrue("Bar not on queue", msgs.contains("bar"));
    }
}

