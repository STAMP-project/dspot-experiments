package org.wildfly.extension.messaging.activemq.jms;


import Pooled.ALLOW_LOCAL_TRANSACTIONS_PROP_NAME;
import Pooled.REBALANCE_CONNECTIONS_PROP_NAME;
import Pooled.SETUP_ATTEMPTS_PROP_NAME;
import Pooled.SETUP_INTERVAL_PROP_NAME;
import Pooled.USE_JNDI_PROP_NAME;
import PooledConnectionFactoryDefinition.ATTRIBUTES;
import PooledConnectionFactoryService.CONNECTION_PARAMETERS;
import PooledConnectionFactoryService.CONNECTOR_CLASSNAME;
import PooledConnectionFactoryService.DISCOVERY_INITIAL_WAIT_TIMEOUT;
import PooledConnectionFactoryService.DISCOVERY_LOCAL_BIND_ADDRESS;
import PooledConnectionFactoryService.GROUP_ADDRESS;
import PooledConnectionFactoryService.GROUP_PORT;
import PooledConnectionFactoryService.IGNORE_JTA;
import PooledConnectionFactoryService.JGROUPS_CHANNEL_LOCATOR_CLASS;
import PooledConnectionFactoryService.JGROUPS_CHANNEL_NAME;
import PooledConnectionFactoryService.JGROUPS_CHANNEL_REF_NAME;
import PooledConnectionFactoryService.REFRESH_TIMEOUT;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.activemq.artemis.ra.ActiveMQResourceAdapter;
import org.junit.Test;


public class PooledConnectionFactoryAttributesTestCase extends AttributesTestBase {
    private static final SortedSet<String> UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES;

    private static final SortedSet<String> KNOWN_ATTRIBUTES;

    static {
        UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES = new TreeSet<String>();
        // we configure discovery group using discoveryGroupName instead of individual params:
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(GROUP_ADDRESS);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(GROUP_PORT);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(REFRESH_TIMEOUT);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(DISCOVERY_LOCAL_BIND_ADDRESS);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(DISCOVERY_INITIAL_WAIT_TIMEOUT);
        // these properties must not be exposed by the AS7 messaging subsystem
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(CONNECTION_PARAMETERS);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(CONNECTOR_CLASSNAME);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add("managedConnectionFactory");
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(JGROUPS_CHANNEL_LOCATOR_CLASS);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(JGROUPS_CHANNEL_REF_NAME);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(JGROUPS_CHANNEL_NAME);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add(IGNORE_JTA);
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add("jgroupsFile");
        // these 2 props will *not* be supported since AS7 relies on vaulted passwords + expressions instead
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add("passwordCodec");
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add("useMaskedPassword");
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add("connectionPoolName");
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add("cacheDestinations");
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add("ignoreJTA");
        PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES.add("enable1xPrefixes");
        KNOWN_ATTRIBUTES = new TreeSet<String>();
        // these are supported but it is not found by JavaBeans introspector because of the type
        // difference b/w the getter and the setters (Long vs long)
        PooledConnectionFactoryAttributesTestCase.KNOWN_ATTRIBUTES.add(SETUP_ATTEMPTS_PROP_NAME);
        PooledConnectionFactoryAttributesTestCase.KNOWN_ATTRIBUTES.add(SETUP_INTERVAL_PROP_NAME);
        PooledConnectionFactoryAttributesTestCase.KNOWN_ATTRIBUTES.add(USE_JNDI_PROP_NAME);
        PooledConnectionFactoryAttributesTestCase.KNOWN_ATTRIBUTES.add(REBALANCE_CONNECTIONS_PROP_NAME);
        PooledConnectionFactoryAttributesTestCase.KNOWN_ATTRIBUTES.add(ALLOW_LOCAL_TRANSACTIONS_PROP_NAME);
    }

    @Test
    public void compareWildFlyPooledConnectionFactoryAndActiveMQConnectionFactoryProperties() throws Exception {
        SortedSet<String> pooledConnectionFactoryAttributes = PooledConnectionFactoryAttributesTestCase.findAllResourceAdapterProperties(ATTRIBUTES);
        pooledConnectionFactoryAttributes.removeAll(PooledConnectionFactoryAttributesTestCase.KNOWN_ATTRIBUTES);
        SortedSet<String> activemqRAProperties = findAllPropertyNames(ActiveMQResourceAdapter.class);
        activemqRAProperties.removeAll(PooledConnectionFactoryAttributesTestCase.UNSUPPORTED_ACTIVEMQ_RA_PROPERTIES);
        AttributesTestBase.compare("AS7 PooledConnectionFactoryAttributes", pooledConnectionFactoryAttributes, "ActiveMQ Resource Adapter", activemqRAProperties);
    }
}

