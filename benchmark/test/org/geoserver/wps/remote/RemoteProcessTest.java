/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.remote;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.geoserver.wps.WPSTestSupport;
import org.geoserver.wps.remote.plugin.MockRemoteClient;
import org.geoserver.wps.remote.plugin.XMPPClient;
import org.geoserver.wps.remote.plugin.XMPPLoadAverageMessage;
import org.geoserver.wps.remote.plugin.XMPPMessage;
import org.geoserver.wps.remote.plugin.XMPPRegisterMessage;
import org.geoserver.wps.remote.plugin.server.XMPPServerEmbedded;
import org.geotools.feature.NameImpl;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.type.Name;


/**
 * This class tests checks if the RemoteProcess class behaves correctly.
 *
 * @author "Alessio Fabiani - alessio.fabiani@geo-solutions.it"
 */
public class RemoteProcessTest extends WPSTestSupport {
    private static final boolean DISABLE = "false".equalsIgnoreCase(System.getProperty("disableTest", "true"));

    private RemoteProcessFactory factory;

    @Test
    public void testNames() {
        setupFactory();
        Assert.assertNotNull(factory);
        Set<Name> names = factory.getNames();
        Assert.assertNotNull(names);
        Assert.assertTrue(((names.size()) == 0));
        final NameImpl name = new NameImpl("default", "Service");
        factory.registerProcess(new RemoteServiceDescriptor(name, "Service", "A test service", null, null, null));
        Assert.assertTrue(((names.size()) == 1));
        Assert.assertTrue(names.contains(name));
        factory.deregisterProcess(name);
        Assert.assertTrue(((names.size()) == 0));
    }

    @Test
    public void testListeners() {
        setupFactory();
        Assert.assertNotNull(factory);
        RemoteProcessClient remoteClient = factory.getRemoteClient();
        Assert.assertNotNull(remoteClient);
        Assert.assertTrue((remoteClient instanceof MockRemoteClient));
        Set<Name> names = factory.getNames();
        Assert.assertNotNull(names);
        Assert.assertTrue(((names.size()) == 0));
        final NameImpl name = new NameImpl("default", "Service");
        try {
            remoteClient.execute(name, null, null, null);
            Assert.assertTrue(((names.size()) == 1));
            Assert.assertTrue(names.contains(name));
            factory.deregisterProcess(name);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getLocalizedMessage());
        } finally {
            Assert.assertTrue(((names.size()) == 0));
        }
    }

    @Test
    public void testXMPPClient() {
        if (RemoteProcessTest.DISABLE) {
            return;
        }
        setupFactory();
        XMPPServerEmbedded server = null;
        XMPPClient xmppRemoteClient = null;
        try {
            final RemoteProcessFactoryConfiguration configuration = factory.getRemoteClient().getConfiguration();
            final String xmppDomain = configuration.get("xmpp_domain");
            final String xmppUserName = configuration.get("xmpp_manager_username");
            final String[] serviceChannels = configuration.get("xmpp_service_channels").split(",");
            final Entity adminJID = EntityImpl.parseUnchecked(((xmppUserName + "@") + xmppDomain));
            // /
            server = ((XMPPServerEmbedded) (applicationContext.getBean("xmppServerEmbedded")));
            Assert.assertNotNull(server);
            Thread.sleep(3000);
            LOGGER.info("vysper server is running...");
            // /
            xmppRemoteClient = ((XMPPClient) (applicationContext.getBean("xmppRemoteProcessClient")));
            Assert.assertNotNull(xmppRemoteClient);
            xmppRemoteClient.init();
            // /
            Thread.sleep(1000);
            Presence presence = getPresence(server, adminJID);
            Assert.assertNotNull(presence);
            Assert.assertTrue(presence.isAvailable());
            Assert.assertTrue("Orchestrator Active".equals(presence.getStatus()));
            List<RemoteMachineDescriptor> remoteMachines = xmppRemoteClient.getRegisteredProcessingMachines();
            Assert.assertNotNull(remoteMachines);
            Assert.assertTrue(((remoteMachines.size()) > 0));
            for (RemoteMachineDescriptor machine : remoteMachines) {
                if (!("test".equals(machine.getServiceName().getNamespaceURI()))) {
                    Assert.assertTrue(xmppUserName.equals(machine.getServiceName().getLocalPart()));
                    Assert.assertTrue(Arrays.asList(serviceChannels).contains(machine.getServiceName().getNamespaceURI()));
                    Assert.assertTrue(machine.getNodeJID().equals((((((((machine.getServiceName().getNamespaceURI()) + "@") + (configuration.get("xmpp_bus"))) + ".") + xmppDomain) + "/") + xmppUserName)));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            Assert.fail(e.getLocalizedMessage());
        } finally {
            if (server != null) {
                try {
                    xmppRemoteClient.destroy();
                    server.stop();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }

    @Test
    public void testRegisterMessage() {
        // /
        XMPPClient xmppRemoteClient = ((XMPPClient) (applicationContext.getBean("xmppRemoteProcessClient")));
        Assert.assertNotNull(xmppRemoteClient);
        XMPPMessage msg = new XMPPRegisterMessage();
        // build register body
        Map<String, String> signalArgs = new HashMap<String, String>();
        signalArgs.put("topic", "register");
        signalArgs.put("service", "test.Service");
        /**
         * JSON URL Encoded Body
         *
         * <p>{ "title": "test.Service", "description": "This is a test Service!", "input": [
         * ["simpleType", "{\"type\": \"string\", \"description\": \"A simple string parameter\",
         * \"max\": 1}"], ["complexType", "{\"type\": \"complex\", \"description\": \"A complex
         * parameter\", \"min\": 1, \"max\": 10}"] ] }
         */
        signalArgs.put("message", "%7B%0A%20%20%22title%22%3A%20%22test.Service%22%2C%0A%20%20%22description%22%3A%20%22This%20is%20a%20test%20Service!%22%2C%0A%20%20%22input%22%3A%20%5B%0A%20%20%20%20%5B%22simpleType%22%2C%20%22%7B%5C%22type%5C%22%3A%20%5C%22string%5C%22%2C%20%5C%22description%5C%22%3A%20%5C%22A%20simple%20string%20parameter%5C%22%2C%20%5C%22max%5C%22%3A%201%7D%22%5D%2C%0A%20%20%20%20%5B%22complexType%22%2C%20%22%7B%5C%22type%5C%22%3A%20%5C%22complex%5C%22%2C%20%5C%22description%5C%22%3A%20%5C%22A%20complex%20parameter%5C%22%2C%20%5C%22min%5C%22%3A%201%2C%20%5C%22max%5C%22%3A%2010%7D%22%5D%0A%20%20%5D%0A%7D");
        // handle signal
        Packet packet = new Packet() {
            @Override
            public String getFrom() {
                return "test@geoserver.org";
            }

            @Override
            public CharSequence toXML() {
                return null;
            }
        };
        try {
            msg.handleSignal(xmppRemoteClient, packet, null, signalArgs);
        } catch (IOException e) {
            Assert.assertFalse(e.getLocalizedMessage(), true);
        }
    }

    @Test
    public void testLoadAverageMessage() {
        // /
        XMPPClient xmppRemoteClient = ((XMPPClient) (applicationContext.getBean("xmppRemoteProcessClient")));
        Assert.assertNotNull(xmppRemoteClient);
        List<RemoteMachineDescriptor> registeredProcessingMachines = new ArrayList<RemoteMachineDescriptor>();
        registeredProcessingMachines.add(new RemoteMachineDescriptor("test@geoserver.org", new NameImpl("test", "Service"), true, 90.0, 90.0));
        xmppRemoteClient.setRegisteredProcessingMachines(registeredProcessingMachines);
        XMPPMessage msg = new XMPPLoadAverageMessage();
        // build register body
        Map<String, String> signalArgs = new HashMap<String, String>();
        signalArgs.put("topic", "loadavg");
        signalArgs.put("message", "loadavg");
        signalArgs.put("service", "test.Service");
        signalArgs.put("id", "master");
        /**
         * JSON URL Encoded Body
         *
         * <p>{ "title": "test.Service", "description": "This is a test Service!", "input": [
         * ["simpleType", "{\"type\": \"string\", \"description\": \"A simple string parameter\",
         * \"max\": 1}"], ["complexType", "{\"type\": \"complex\", \"description\": \"A complex
         * parameter\", \"min\": 1, \"max\": 10}"] ] }
         */
        signalArgs.put("result_vmem", "%7B%22vmem_value%22%3A%2089.3%2C%20%22vmem_description%22%3A%20%22Percentage%20of%20Memory%20used%20by%20the%20server.%22%7D");
        signalArgs.put("result_loadavg", "%7B%22loadavg_description%22%3A%20%22Average%20Load%20on%20CPUs%20during%20the%20last%2015%20minutes.%22%2C%20%22loadavg_value%22%3A%2014.6%7D");
        // handle signal
        Packet packet = new Packet() {
            @Override
            public String getFrom() {
                return "test@geoserver.org";
            }

            @Override
            public CharSequence toXML() {
                return null;
            }
        };
        try {
            msg.handleSignal(xmppRemoteClient, packet, null, signalArgs);
        } catch (IOException e) {
            Assert.assertFalse(e.getLocalizedMessage(), true);
        }
        Assert.assertTrue("LoadAverage does not match!", registeredProcessingMachines.get(0).getLoadAverage().equals(14.6));
        Assert.assertTrue("MemoryPerc does not match!", registeredProcessingMachines.get(0).getMemPercUsed().equals(89.3));
    }
}

