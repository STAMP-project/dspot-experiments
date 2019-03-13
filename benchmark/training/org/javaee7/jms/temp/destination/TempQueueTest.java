package org.javaee7.jms.temp.destination;


import javax.ejb.EJB;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Temporary queues are JMS queues that exist for the lifetime of single JMS connection.
 * Also the reception of the messages is exclusive to the connection, therefore no
 * reasonable use case exist for temporary topic within Java EE container, as connection
 * is exclusive to single component.
 *
 * Temporary queues are usually used as reply channels for request / response communication
 * over JMS.
 */
@RunWith(Arquillian.class)
public class TempQueueTest {
    @EJB
    private JmsClient client;

    /**
     * We invoke the client, and verify that the response is processed
     */
    @Test
    public void testRequestResposne() {
        Assert.assertEquals("Processed: Hello", client.process("Hello"));
    }
}

