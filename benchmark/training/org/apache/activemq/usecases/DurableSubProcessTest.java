/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.usecases;


import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// see https://issues.apache.org/activemq/browse/AMQ-2985
// this demonstrated receiving old messages eventually along with validating order receipt
public class DurableSubProcessTest extends TestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DurableSubProcessTest.class);

    public static final long RUNTIME = (4 * 60) * 1000;

    public static final int SERVER_SLEEP = 2 * 1000;// max


    public static final int CARGO_SIZE = 10;// max


    public static final int MAX_CLIENTS = 7;

    public static final DurableSubProcessTest.Random CLIENT_LIFETIME = new DurableSubProcessTest.Random((30 * 1000), ((2 * 60) * 1000));

    public static final DurableSubProcessTest.Random CLIENT_ONLINE = new DurableSubProcessTest.Random((2 * 1000), (15 * 1000));

    public static final DurableSubProcessTest.Random CLIENT_OFFLINE = new DurableSubProcessTest.Random((1 * 1000), (20 * 1000));

    public static final boolean PERSISTENT_BROKER = true;

    public static final boolean ALLOW_SUBSCRIPTION_ABANDONMENT = true;

    private BrokerService broker;

    private ActiveMQTopic topic;

    private DurableSubProcessTest.ClientManager clientManager;

    private DurableSubProcessTest.Server server;

    private DurableSubProcessTest.HouseKeeper houseKeeper;

    static final Vector<Throwable> exceptions = new Vector<Throwable>();

    @Test
    public void testProcess() {
        try {
            server.start();
            clientManager.start();
            if (DurableSubProcessTest.ALLOW_SUBSCRIPTION_ABANDONMENT)
                houseKeeper.start();

            Thread.sleep(DurableSubProcessTest.RUNTIME);
            assertTrue(("no exceptions: " + (DurableSubProcessTest.exceptions)), DurableSubProcessTest.exceptions.isEmpty());
        } catch (Throwable e) {
            DurableSubProcessTest.exit("DurableSubProcessTest.testProcess failed.", e);
        }
        DurableSubProcessTest.LOG.info("DONE.");
    }

    /**
     * Creates batch of messages in a transaction periodically.
     * The last message in the transaction is always a special
     * message what contains info about the whole transaction.
     * <p>Notifies the clients about the created messages also.
     */
    final class Server extends Thread {
        final String url = ((((((("vm://" + (DurableSubProcessTest.this.getName())) + "?") + "jms.redeliveryPolicy.maximumRedeliveries=2&jms.redeliveryPolicy.initialRedeliveryDelay=500&") + "jms.producerWindowSize=20971520&jms.prefetchPolicy.all=100&") + "jms.copyMessageOnSend=false&jms.disableTimeStampsByDefault=false&") + "jms.alwaysSyncSend=true&jms.dispatchAsync=false&") + "jms.watchTopicAdvisories=false&") + "waitForStart=200&create=false";

        final ConnectionFactory cf = new ActiveMQConnectionFactory(url);

        final Object sendMutex = new Object();

        final String[] cargos = new String[500];

        int transRover = 0;

        int messageRover = 0;

        public Server() {
            super("Server");
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    DurableSubProcessTest.sleepRandom(DurableSubProcessTest.SERVER_SLEEP);
                    send();
                } 
            } catch (Throwable e) {
                DurableSubProcessTest.exit("Server.run failed", e);
            }
        }

        public void send() throws JMSException {
            // do not create new clients now
            // ToDo: Test this case later.
            synchronized(sendMutex) {
                int trans = ++(transRover);
                boolean relevantTrans = (DurableSubProcessTest.random(2)) > 1;
                DurableSubProcessTest.ClientType clientType = (relevantTrans) ? DurableSubProcessTest.ClientType.randomClientType() : null;// sends this types

                int count = DurableSubProcessTest.random(200);
                DurableSubProcessTest.LOG.info((((((("Sending Trans[id=" + trans) + ", count=") + count) + ", clientType=") + clientType) + "]"));
                Connection con = cf.createConnection();
                Session sess = con.createSession(true, AUTO_ACKNOWLEDGE);
                MessageProducer prod = sess.createProducer(null);
                for (int i = 0; i < count; i++) {
                    Message message = sess.createMessage();
                    message.setIntProperty("ID", (++(messageRover)));
                    String type = (clientType != null) ? clientType.randomMessageType() : DurableSubProcessTest.ClientType.randomNonRelevantMessageType();
                    message.setStringProperty("TYPE", type);
                    if ((DurableSubProcessTest.CARGO_SIZE) > 0)
                        message.setStringProperty("CARGO", getCargo(DurableSubProcessTest.CARGO_SIZE));

                    prod.send(topic, message);
                    clientManager.onServerMessage(message);
                }
                Message message = sess.createMessage();
                message.setIntProperty("ID", (++(messageRover)));
                message.setIntProperty("TRANS", trans);
                message.setBooleanProperty("COMMIT", true);
                message.setBooleanProperty("RELEVANT", relevantTrans);
                prod.send(topic, message);
                clientManager.onServerMessage(message);
                sess.commit();
                sess.close();
                con.close();
            }
        }

        private String getCargo(int length) {
            if (length == 0)
                return null;

            if (length < (cargos.length)) {
                String result = cargos[length];
                if (result == null) {
                    result = getCargoImpl(length);
                    cargos[length] = result;
                }
                return result;
            }
            return getCargoImpl(length);
        }

        private String getCargoImpl(int length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = length; (--i) >= 0;) {
                sb.append('a');
            }
            return sb.toString();
        }
    }

    /**
     * Clients listen on different messages in the topic.
     * The 'TYPE' property helps the client to select the
     * proper messages.
     */
    private enum ClientType {

        A("a", "b", "c"),
        B("c", "d", "e"),
        C("d", "e", "f"),
        D("g", "h");
        public final String[] messageTypes;

        public final HashSet<String> messageTypeSet;

        public final String selector;

        ClientType(String... messageTypes) {
            this.messageTypes = messageTypes;
            messageTypeSet = new HashSet<String>(Arrays.asList(messageTypes));
            StringBuilder sb = new StringBuilder("TYPE in (");
            for (int i = 0; i < (messageTypes.length); i++) {
                if (i > 0)
                    sb.append(", ");

                sb.append('\'').append(messageTypes[i]).append('\'');
            }
            sb.append(')');
            selector = sb.toString();
        }

        public static DurableSubProcessTest.ClientType randomClientType() {
            return DurableSubProcessTest.ClientType.values()[DurableSubProcessTest.random(((DurableSubProcessTest.ClientType.values().length) - 1))];
        }

        public final String randomMessageType() {
            return messageTypes[DurableSubProcessTest.random(((messageTypes.length) - 1))];
        }

        public static String randomNonRelevantMessageType() {
            return Integer.toString(DurableSubProcessTest.random(20));
        }

        public final boolean isRelevant(String messageType) {
            return messageTypeSet.contains(messageType);
        }

        @Override
        public final String toString() {
            /* + '[' + selector + ']' */
            return this.name();
        }
    }

    /**
     * Creates new cliens.
     */
    private final class ClientManager extends Thread {
        private int clientRover = 0;

        private final CopyOnWriteArrayList<DurableSubProcessTest.Client> clients = new CopyOnWriteArrayList<DurableSubProcessTest.Client>();

        public ClientManager() {
            super("ClientManager");
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if ((clients.size()) < (DurableSubProcessTest.MAX_CLIENTS))
                        createNewClient();

                    int size = clients.size();
                    DurableSubProcessTest.sleepRandom(((size * 3) * 1000), ((size * 6) * 1000));
                } 
            } catch (Throwable e) {
                DurableSubProcessTest.exit("ClientManager.run failed.", e);
            }
        }

        private void createNewClient() throws JMSException {
            DurableSubProcessTest.ClientType type = DurableSubProcessTest.ClientType.randomClientType();
            DurableSubProcessTest.Client client;
            synchronized(server.sendMutex) {
                client = new DurableSubProcessTest.Client((++(clientRover)), type, DurableSubProcessTest.CLIENT_LIFETIME, DurableSubProcessTest.CLIENT_ONLINE, DurableSubProcessTest.CLIENT_OFFLINE);
                clients.add(client);
            }
            client.start();
            DurableSubProcessTest.LOG.info((((client.toString()) + " created. ") + (this)));
        }

        public void removeClient(DurableSubProcessTest.Client client) {
            clients.remove(client);
        }

        public void onServerMessage(Message message) throws JMSException {
            for (DurableSubProcessTest.Client client : clients) {
                client.onServerMessage(message);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("ClientManager[count=");
            sb.append(clients.size());
            sb.append(", clients=");
            boolean sep = false;
            for (DurableSubProcessTest.Client client : clients) {
                if (sep)
                    sb.append(", ");
                else
                    sep = true;

                sb.append(client.toString());
            }
            sb.append(']');
            return sb.toString();
        }
    }

    /**
     * Consumes massages from a durable subscription.
     * Goes online/offline periodically. Checks the incoming messages
     * against the sent messages of the server.
     */
    private final class Client extends Thread {
        String url = "failover:(tcp://localhost:61656?wireFormat.maxInactivityDuration=0)?" + ((((("jms.watchTopicAdvisories=false&" + "jms.alwaysSyncSend=true&jms.dispatchAsync=true&") + "jms.producerWindowSize=20971520&") + "jms.copyMessageOnSend=false&") + "initialReconnectDelay=100&maxReconnectDelay=30000&") + "useExponentialBackOff=true");

        final ConnectionFactory cf = new ActiveMQConnectionFactory(url);

        public static final String SUBSCRIPTION_NAME = "subscription";

        private final int id;

        private final String conClientId;

        private final DurableSubProcessTest.Random lifetime;

        private final DurableSubProcessTest.Random online;

        private final DurableSubProcessTest.Random offline;

        private final DurableSubProcessTest.ClientType clientType;

        private final String selector;

        private final ConcurrentLinkedQueue<Message> waitingList = new ConcurrentLinkedQueue<Message>();

        public Client(int id, DurableSubProcessTest.ClientType clientType, DurableSubProcessTest.Random lifetime, DurableSubProcessTest.Random online, DurableSubProcessTest.Random offline) throws JMSException {
            super(("Client" + id));
            setDaemon(true);
            this.id = id;
            conClientId = "cli" + id;
            this.clientType = clientType;
            selector = "(COMMIT = true and RELEVANT = true) or " + (clientType.selector);
            this.lifetime = lifetime;
            this.online = online;
            this.offline = offline;
            subscribe();
        }

        @Override
        public void run() {
            long end = (System.currentTimeMillis()) + (lifetime.next());
            try {
                boolean sleep = false;
                while (true) {
                    long max = end - (System.currentTimeMillis());
                    if (max <= 0)
                        break;

                    if (sleep)
                        offline.sleepRandom();
                    else
                        sleep = true;

                    process(online.next());
                } 
                if ((!(DurableSubProcessTest.ALLOW_SUBSCRIPTION_ABANDONMENT)) || ((DurableSubProcessTest.random(1)) > 0))
                    unsubscribe();
                else {
                    DurableSubProcessTest.LOG.info(("Client abandon the subscription. " + (this)));
                    // housekeeper should sweep these abandoned subscriptions
                    houseKeeper.abandonedSubscriptions.add(conClientId);
                }
            } catch (Throwable e) {
                DurableSubProcessTest.exit(((toString()) + " failed."), e);
            }
            clientManager.removeClient(this);
            DurableSubProcessTest.LOG.info(((toString()) + " DONE."));
        }

        private void process(long millis) throws JMSException {
            long end = (System.currentTimeMillis()) + millis;
            long hardEnd = end + 2000;// wait to finish the transaction.

            boolean inTransaction = false;
            int transCount = 0;
            DurableSubProcessTest.LOG.info(((toString()) + " ONLINE."));
            Connection con = openConnection();
            Session sess = con.createSession(false, CLIENT_ACKNOWLEDGE);
            MessageConsumer consumer = sess.createDurableSubscriber(topic, DurableSubProcessTest.Client.SUBSCRIPTION_NAME, selector, false);
            try {
                do {
                    long max = end - (System.currentTimeMillis());
                    if (max <= 0) {
                        if (!inTransaction)
                            break;

                        max = hardEnd - (System.currentTimeMillis());
                        if (max <= 0)
                            DurableSubProcessTest.exit((("" + (this)) + " failed: Transaction is not finished."));

                    }
                    Message message = consumer.receive(max);
                    if (message == null)
                        continue;

                    onClientMessage(message);
                    if (message.propertyExists("COMMIT")) {
                        message.acknowledge();
                        DurableSubProcessTest.LOG.info((((((("Received Trans[id=" + (message.getIntProperty("TRANS"))) + ", count=") + transCount) + "] in ") + (this)) + "."));
                        inTransaction = false;
                        transCount = 0;
                    } else {
                        inTransaction = true;
                        transCount++;
                    }
                } while (true );
            } finally {
                sess.close();
                con.close();
                DurableSubProcessTest.LOG.info(((toString()) + " OFFLINE."));
                // Check if the messages are in the waiting
                // list for long time.
                Message topMessage = waitingList.peek();
                if (topMessage != null)
                    checkDeliveryTime(topMessage);

            }
        }

        public void onServerMessage(Message message) throws JMSException {
            if (Boolean.TRUE.equals(message.getObjectProperty("COMMIT"))) {
                if (Boolean.TRUE.equals(message.getObjectProperty("RELEVANT")))
                    waitingList.add(message);

            } else {
                String messageType = message.getStringProperty("TYPE");
                if (clientType.isRelevant(messageType))
                    waitingList.add(message);

            }
        }

        public void onClientMessage(Message message) {
            Message serverMessage = waitingList.poll();
            try {
                if (serverMessage == null)
                    DurableSubProcessTest.exit(((("" + (this)) + " failed: There is no next server message, but received: ") + message));

                Integer receivedId = ((Integer) (message.getObjectProperty("ID")));
                Integer serverId = ((Integer) (serverMessage.getObjectProperty("ID")));
                if ((receivedId == null) || (serverId == null))
                    DurableSubProcessTest.exit(((((((("" + (this)) + " failed: message ID not found.\r\n") + " received: ") + message) + "\r\n") + "   server: ") + serverMessage));

                if (!(serverId.equals(receivedId)))
                    DurableSubProcessTest.exit(((((((("" + (this)) + " failed: Received wrong message.\r\n") + " received: ") + message) + "\r\n") + "   server: ") + serverMessage));

                checkDeliveryTime(message);
            } catch (Throwable e) {
                DurableSubProcessTest.exit(((((((("" + (this)) + ".onClientMessage failed.\r\n") + " received: ") + message) + "\r\n") + "   server: ") + serverMessage), e);
            }
        }

        /**
         * Checks if the message was not delivered fast enough.
         */
        public void checkDeliveryTime(Message message) throws JMSException {
            long creation = message.getJMSTimestamp();
            long min = (System.currentTimeMillis()) - ((offline.max) + (online.min));
            if (min > creation) {
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
                DurableSubProcessTest.exit(((((((("" + (this)) + ".checkDeliveryTime failed. Message time: ") + (df.format(new Date(creation)))) + ", min: ") + (df.format(new Date(min)))) + "\r\n") + message));
            }
        }

        private Connection openConnection() throws JMSException {
            Connection con = cf.createConnection();
            con.setClientID(conClientId);
            con.start();
            return con;
        }

        private void subscribe() throws JMSException {
            Connection con = openConnection();
            Session session = con.createSession(false, AUTO_ACKNOWLEDGE);
            session.createDurableSubscriber(topic, DurableSubProcessTest.Client.SUBSCRIPTION_NAME, selector, true);
            session.close();
            con.close();
        }

        private void unsubscribe() throws JMSException {
            Connection con = openConnection();
            Session session = con.createSession(false, AUTO_ACKNOWLEDGE);
            session.unsubscribe(DurableSubProcessTest.Client.SUBSCRIPTION_NAME);
            session.close();
            con.close();
        }

        @Override
        public String toString() {
            return ((("Client[id=" + (id)) + ", type=") + (clientType)) + "]";
        }
    }

    /**
     * Sweeps out not-used durable subscriptions.
     */
    private final class HouseKeeper extends Thread {
        private HouseKeeper() {
            super("HouseKeeper");
            setDaemon(true);
        }

        public final CopyOnWriteArrayList<String> abandonedSubscriptions = new CopyOnWriteArrayList<String>();

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep((60 * 1000));
                    sweep();
                } catch (InterruptedException ex) {
                    break;
                } catch (Throwable e) {
                    Exception log = new Exception("HouseKeeper failed.", e);
                    log.printStackTrace();
                }
            } 
        }

        private void sweep() throws Exception {
            DurableSubProcessTest.LOG.info("Housekeeper sweeping.");
            int closed = 0;
            ArrayList<String> sweeped = new ArrayList<String>();
            try {
                for (String clientId : abandonedSubscriptions) {
                    sweeped.add(clientId);
                    DurableSubProcessTest.LOG.info((("Sweeping out subscription of " + clientId) + "."));
                    broker.getAdminView().destroyDurableSubscriber(clientId, DurableSubProcessTest.Client.SUBSCRIPTION_NAME);
                    closed++;
                }
            } finally {
                abandonedSubscriptions.removeAll(sweeped);
            }
            DurableSubProcessTest.LOG.info((("Housekeeper sweeped out " + closed) + " subscriptions."));
        }
    }

    public static final class Random {
        final int min;

        final int max;

        Random(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int next() {
            return DurableSubProcessTest.random(min, max);
        }

        public void sleepRandom() throws InterruptedException {
            DurableSubProcessTest.sleepRandom(min, max);
        }
    }
}

