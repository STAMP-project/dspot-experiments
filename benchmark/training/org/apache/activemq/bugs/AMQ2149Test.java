package org.apache.activemq.bugs;


import ActiveMQDestination.QUEUE_TYPE;
import ActiveMQDestination.TOPIC_TYPE;
import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.io.File;
import java.util.HashSet;
import java.util.Timer;
import java.util.Vector;
import javax.javax.jms;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.util.LoggingBrokerPlugin;
import org.apache.activemq.command.ActiveMQDestination;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Session.AUTO_ACKNOWLEDGE;
import static Session.SESSION_TRANSACTED;


public class AMQ2149Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ2149Test.class);

    @Rule
    public TestName testName = new TestName();

    private static final String BROKER_CONNECTOR = "tcp://localhost:61617";

    private static final String DEFAULT_BROKER_URL = ("failover:(" + (AMQ2149Test.BROKER_CONNECTOR)) + ")?maxReconnectDelay=1000&useExponentialBackOff=false";

    private final String SEQ_NUM_PROPERTY = "seqNum";

    final int MESSAGE_LENGTH_BYTES = 75 * 1024;

    final long SLEEP_BETWEEN_SEND_MS = 25;

    final int NUM_SENDERS_AND_RECEIVERS = 10;

    final Object brokerLock = new Object();

    private static final long DEFAULT_BROKER_STOP_PERIOD = 10 * 1000;

    private static final long DEFAULT_NUM_TO_SEND = 1400;

    long brokerStopPeriod = AMQ2149Test.DEFAULT_BROKER_STOP_PERIOD;

    long numtoSend = AMQ2149Test.DEFAULT_NUM_TO_SEND;

    long sleepBetweenSend = SLEEP_BETWEEN_SEND_MS;

    String brokerURL = AMQ2149Test.DEFAULT_BROKER_URL;

    int numBrokerRestarts = 0;

    static final int MAX_BROKER_RESTARTS = 4;

    BrokerService broker;

    Vector<Throwable> exceptions = new Vector<Throwable>();

    protected File dataDirFile;

    final LoggingBrokerPlugin[] plugins = new LoggingBrokerPlugin[]{ new LoggingBrokerPlugin() };

    HashSet<Connection> connections = new HashSet<Connection>();

    private class Receiver implements MessageListener {
        private final Destination dest;

        private final Connection connection;

        private final Session session;

        private final MessageConsumer messageConsumer;

        private volatile long nextExpectedSeqNum = 1;

        private final boolean transactional;

        private String lastId = null;

        public Receiver(javax.jms.Destination dest, boolean transactional) throws JMSException {
            this.dest = dest;
            this.transactional = transactional;
            connection = new ActiveMQConnectionFactory(brokerURL).createConnection();
            connection.setClientID(dest.toString());
            session = connection.createSession(transactional, (transactional ? SESSION_TRANSACTED : AUTO_ACKNOWLEDGE));
            if (ActiveMQDestination.transform(dest).isTopic()) {
                messageConsumer = session.createDurableSubscriber(((Topic) (dest)), dest.toString());
            } else {
                messageConsumer = session.createConsumer(dest);
            }
            messageConsumer.setMessageListener(this);
            connection.start();
            connections.add(connection);
        }

        public void close() throws JMSException {
            connection.close();
        }

        public long getNextExpectedSeqNo() {
            return nextExpectedSeqNum;
        }

        final int TRANSACITON_BATCH = 500;

        boolean resumeOnNextOrPreviousIsOk = false;

        public void onMessage(Message message) {
            try {
                final long seqNum = message.getLongProperty(SEQ_NUM_PROPERTY);
                if ((seqNum % (TRANSACITON_BATCH)) == 0) {
                    AMQ2149Test.LOG.info((((dest) + " received ") + seqNum));
                    if (transactional) {
                        AMQ2149Test.LOG.info("committing..");
                        session.commit();
                    }
                }
                if (resumeOnNextOrPreviousIsOk) {
                    // after an indoubt commit we need to accept what we get
                    // either a batch replay or next batch
                    if (seqNum != (nextExpectedSeqNum)) {
                        if (seqNum == ((nextExpectedSeqNum) - (TRANSACITON_BATCH))) {
                            nextExpectedSeqNum -= TRANSACITON_BATCH;
                            AMQ2149Test.LOG.info(("In doubt commit failed, getting replay at:" + (nextExpectedSeqNum)));
                        }
                    }
                    resumeOnNextOrPreviousIsOk = false;
                }
                if (seqNum != (nextExpectedSeqNum)) {
                    AMQ2149Test.LOG.warn((((((((((((dest) + " received ") + seqNum) + " in msg: ") + (message.getJMSMessageID())) + " expected ") + (nextExpectedSeqNum)) + ", lastId: ") + (lastId)) + ", message:") + message));
                    Assert.fail((((((dest) + " received ") + seqNum) + " expected ") + (nextExpectedSeqNum)));
                }
                ++(nextExpectedSeqNum);
                lastId = message.getJMSMessageID();
            } catch (TransactionRolledBackException expectedSometimesOnFailoverRecovery) {
                ++(nextExpectedSeqNum);
                AMQ2149Test.LOG.info(("got rollback: " + expectedSometimesOnFailoverRecovery));
                if (expectedSometimesOnFailoverRecovery.getMessage().contains("completion in doubt")) {
                    // in doubt - either commit command or reply missing
                    // don't know if we will get a replay
                    resumeOnNextOrPreviousIsOk = true;
                    AMQ2149Test.LOG.info(("in doubt transaction completion: ok to get next or previous batch. next:" + (nextExpectedSeqNum)));
                } else {
                    resumeOnNextOrPreviousIsOk = false;
                    // batch will be replayed
                    nextExpectedSeqNum -= TRANSACITON_BATCH;
                }
            } catch (Throwable e) {
                AMQ2149Test.LOG.error((((dest) + " onMessage error:") + e));
                exceptions.add(e);
            }
        }
    }

    private class Sender implements Runnable {
        private final Destination dest;

        private final Connection connection;

        private final Session session;

        private final MessageProducer messageProducer;

        private volatile long nextSequenceNumber = 0;

        public Sender(javax.jms.Destination dest) throws JMSException {
            this.dest = dest;
            connection = new ActiveMQConnectionFactory(brokerURL).createConnection();
            session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(dest);
            messageProducer.setDeliveryMode(PERSISTENT);
            connection.start();
            connections.add(connection);
        }

        public void run() {
            final String longString = buildLongString();
            while ((nextSequenceNumber) < (numtoSend)) {
                try {
                    final Message message = session.createTextMessage(longString);
                    message.setLongProperty(SEQ_NUM_PROPERTY, (++(nextSequenceNumber)));
                    messageProducer.send(message);
                    if (((nextSequenceNumber) % 500) == 0) {
                        AMQ2149Test.LOG.info((((dest) + " sent ") + (nextSequenceNumber)));
                    }
                } catch (javax.jms e) {
                    AMQ2149Test.LOG.error(((dest) + " bailing on send error"), e);
                    exceptions.add(e);
                    break;
                } catch (Exception e) {
                    AMQ2149Test.LOG.error(((dest) + " send error"), e);
                    exceptions.add(e);
                }
                if ((sleepBetweenSend) > 0) {
                    try {
                        Thread.sleep(sleepBetweenSend);
                    } catch (InterruptedException e) {
                        AMQ2149Test.LOG.warn(((dest) + " sleep interrupted"), e);
                    }
                }
            } 
            try {
                connection.close();
            } catch (JMSException ignored) {
            }
        }
    }

    @Test(timeout = (10 * 60) * 1000)
    public void testOrderWithRestart() throws Exception {
        createBroker(new Configurer() {
            public void configure(BrokerService broker) throws Exception {
                broker.deleteAllMessages();
            }
        });
        final Timer timer = new Timer();
        schedualRestartTask(timer, new Configurer() {
            public void configure(BrokerService broker) throws Exception {
            }
        });
        try {
            verifyOrderedMessageReceipt();
        } finally {
            timer.cancel();
        }
        verifyStats(true);
    }

    @Test(timeout = (10 * 60) * 1000)
    public void testTopicOrderWithRestart() throws Exception {
        createBroker(new Configurer() {
            public void configure(BrokerService broker) throws Exception {
                broker.deleteAllMessages();
            }
        });
        final Timer timer = new Timer();
        schedualRestartTask(timer, null);
        try {
            verifyOrderedMessageReceipt(TOPIC_TYPE);
        } finally {
            timer.cancel();
        }
        verifyStats(true);
    }

    @Test(timeout = (10 * 60) * 1000)
    public void testQueueTransactionalOrderWithRestart() throws Exception {
        doTestTransactionalOrderWithRestart(QUEUE_TYPE);
    }

    @Test(timeout = (10 * 60) * 1000)
    public void testTopicTransactionalOrderWithRestart() throws Exception {
        doTestTransactionalOrderWithRestart(TOPIC_TYPE);
    }
}

