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
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.container.jms.BatchMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/org/springframework/batch/jms/jms-context.xml")
@DirtiesContext
public class AsynchronousTests {
    @Autowired
    private BatchMessageListenerContainer container;

    @Autowired
    private JmsTemplate jmsTemplate;

    private JdbcTemplate jdbcTemplate;

    private volatile List<String> list = new ArrayList<>();

    @Test
    public void testSunnyDay() throws Exception {
        assertInitialState();
        container.setMessageListener(new org.springframework.jms.listener.SessionAwareMessageListener<Message>() {
            @Override
            public void onMessage(Message message, Session session) throws JMSException {
                list.add(message.toString());
                String text = getText();
                jdbcTemplate.update("INSERT into T_BARS (id,name,foo_date) values (?,?,null)", list.size(), text);
            }
        });
        container.initializeProxy();
        container.start();
        // Need to sleep for at least a second here...
        waitFor(list, 2, 2000);
        System.err.println(jdbcTemplate.queryForList("select * from T_BARS"));
        Assert.assertEquals(2, list.size());
        String foo = ((String) (jmsTemplate.receiveAndConvert("queue")));
        Assert.assertEquals(null, foo);
        int count = jdbcTemplate.queryForObject("select count(*) from T_BARS", Integer.class);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testRollback() throws Exception {
        assertInitialState();
        // Prevent us from being overwhelmed after rollback
        container.setRecoveryInterval(500);
        container.setMessageListener(new org.springframework.jms.listener.SessionAwareMessageListener<Message>() {
            @Override
            public void onMessage(Message message, Session session) throws JMSException {
                list.add(message.toString());
                final String text = getText();
                jdbcTemplate.update("INSERT into T_BARS (id,name,foo_date) values (?,?,null)", list.size(), text);
                // This causes the DB to rollback but not the message
                if (text.equals("bar")) {
                    throw new RuntimeException("Rollback!");
                }
            }
        });
        container.initializeProxy();
        container.start();
        // Need to sleep here, but not too long or the
        // container goes into its own recovery cycle and spits out the bad
        // message...
        waitFor(list, 2, 500);
        container.stop();
        // We rolled back so the messages might come in many times...
        Assert.assertTrue(((list.size()) >= 1));
        String text = "";
        List<String> msgs = new ArrayList<>();
        while (text != null) {
            text = ((String) (jmsTemplate.receiveAndConvert("queue")));
            msgs.add(text);
        } 
        int count = jdbcTemplate.queryForObject("select count(*) from T_BARS", Integer.class);
        Assert.assertEquals(0, count);
        Assert.assertTrue("Foo not on queue", msgs.contains("foo"));
    }
}

