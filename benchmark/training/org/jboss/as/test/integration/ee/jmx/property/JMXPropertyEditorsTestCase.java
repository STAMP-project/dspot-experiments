/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ee.jmx.property;


import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author baranowb
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JMXPropertyEditorsTestCase {
    private static final String SAR_DEPLOMENT_NAME = "property-editors-beans";

    private static final String SAR_DEPLOMENT_FILE = (JMXPropertyEditorsTestCase.SAR_DEPLOMENT_NAME) + ".sar";

    @ContainerResource
    private ManagementClient managementClient;

    private MBeanServerConnection connection;

    private JMXConnector connector;

    private static final String USER_SYS_PROP;

    static {
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows")) {
            if (System.getenv().containsKey("USERNAME")) {
                USER_SYS_PROP = "USERNAME";
            } else {
                USER_SYS_PROP = "USER";
            }
        } else
            if (osName.contains("SunOS")) {
                USER_SYS_PROP = "LOGNAME";
            } else {
                USER_SYS_PROP = "USER";
            }

    }

    private static class AssetTestBuilder {
        private StringBuilder xml;

        public JMXPropertyEditorsTestCase.AssetTestBuilder begin() {
            xml = new StringBuilder(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (("<server xmlns=\"urn:jboss:service:7.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + "xsi:schemaLocation=\"urn:jboss:service:7.0 jboss-service_7_0.xsd\">") + "<mbean code=\"org.jboss.as.test.integration.ee.jmx.property.WithProperties\" name=\"test:service=WithProperties\">")));
            return this;
        }

        JMXPropertyEditorsTestCase.AssetTestBuilder addAttribute(String attributeName, String attributeValue) {
            xml.append("<attribute name=\"").append(attributeName).append("\">").append(attributeValue).append("</attribute>");
            return this;
        }

        Asset end() {
            return new StringAsset(xml.append("</mbean>").append("</server>").toString());
        }
    }

    @Test
    public void testAtomicBoolean() throws Exception {
        performTest("AtomicBoolean", new AtomicBoolean(true), Comparator.comparing(AtomicBoolean::get));
    }

    @Test
    public void testAtomicInteger() throws Exception {
        performTest("AtomicInteger", new AtomicInteger(3), Comparator.comparing(AtomicInteger::get));
    }

    @Test
    public void testAtomicLong() throws Exception {
        performTest("AtomicLong", new AtomicLong(2), Comparator.comparing(AtomicLong::get));
    }

    @Test
    public void testBigDecimal() throws Exception {
        performTest("BigDecimal", new BigDecimal(100000000));
    }

    @Test
    public void testBigInteger() throws Exception {
        performTest("BigInteger", new BigInteger("100000000"));
    }

    @Test
    public void testBoolean() throws Exception {
        performTest("Boolean", new Boolean(true));
    }

    @Test
    public void testBooleanArray() throws Exception {
        performTest("BooleanArray", new boolean[]{ true, false });
    }

    @Test
    public void testByte() throws Exception {
        performTest("Byte", new Byte(((byte) (1))));
    }

    @Test
    public void testByteArray() throws Exception {
        performTest("ByteArray", new byte[]{ 1, 2, 3 });
    }

    @Test
    public void testChar() throws Exception {
        performTest("Char", new Character('R'));
    }

    @Test
    public void testCharacterArray() throws Exception {
        performTest("CharacterArray", new char[]{ 'R', 'R', 'X' });
    }

    @Test
    public void testClazz() throws Exception {
        performTest("Clazz", String.class);
    }

    @Test
    public void testClassArray() throws Exception {
        performTest("ClassArray", new Class[]{ String.class, List.class });
    }

    @Test
    public void testDouble() throws Exception {
        performTest("Double", new Double(4));
    }

    @Test
    public void testFile() throws Exception {
        performTest("File", new File("/I_DONT_EXIST/DUNNO").getAbsoluteFile());
    }

    @Test
    public void testFloat() throws Exception {
        performTest("Float", new Float("1.5"));
    }

    @Test
    public void testFloatArray() throws Exception {
        performTest("FloatArray", new float[]{ 1.5F, 2.5F });
    }

    @Test
    public void testInetAddress() throws Exception {
        performTest("InetAddress", InetAddress.getByAddress(new byte[]{ 10, 10, 10, 1 }));
    }

    @Test
    public void testInetAddressArray() throws Exception {
        performTest("InetAddressArray", new InetAddress[]{ InetAddress.getByAddress(new byte[]{ 10, 10, 10, 1 }), InetAddress.getByName("localhost") });
    }

    @Test
    public void testInteger() throws Exception {
        performTest("Integer", new Integer("1"));
    }

    @Test
    public void testIntegerArray() throws Exception {
        performTest("IntegerArray", new int[]{ 1, 5, 4 });
    }

    @Test
    public void testLocale() throws Exception {
        performTest("Locale", Locale.ENGLISH);
    }

    @Test
    public void testLong() throws Exception {
        performTest("Long", new Long(14));
    }

    @Test
    public void testLongArray() throws Exception {
        performTest("LongArray", new long[]{ 14, 15 });
    }

    @Test
    public void testObjectBoolean() throws Exception {
        performTest("ObjectBoolean", new Boolean(true));
    }

    @Test
    public void testObjectByte() throws Exception {
        performTest("ObjectByte", new Byte(((byte) (10))));
    }

    @Test
    public void testObjectCharacter() throws Exception {
        performTest("ObjectChar", new Character('Z'));
    }

    @Test
    public void testObjectDouble() throws Exception {
        performTest("ObjectDouble", new Double(10));
    }

    @Test
    public void testObjectFloat() throws Exception {
        performTest("ObjectFloat", new Float(10));
    }

    @Test
    public void testObjectInteger() throws Exception {
        performTest("ObjectInteger", new Integer(10));
    }

    @Test
    public void testObjectLong() throws Exception {
        performTest("ObjectLong", new Long(10));
    }

    @Test
    public void testObjectShort() throws Exception {
        performTest("ObjectShort", new Short(((short) (10))));
    }

    @Test
    public void testProperties() throws Exception {
        Properties props = new Properties();
        props.put("prop1", "ugabuga");
        props.put("prop2", "HAHA");
        props.put("env", System.getenv(JMXPropertyEditorsTestCase.USER_SYS_PROP));
        performTest("Properties", props, ( o1, o2) -> {
            Properties p1 = ((Properties) (o1));
            Properties p2 = ((Properties) (o2));
            if ((p1.size()) != (p2.size())) {
                return 1;
            }
            if (!(p1.keySet().containsAll(p2.keySet()))) {
                return 1;
            }
            Set<Object> keys1 = p1.keySet();
            for (Object key : keys1) {
                Object v1 = p1.get(key);
                Object v2 = p2.get(key);
                if (!(v1.equals(v2))) {
                    return 1;
                }
            }
            return 0;
        });
    }

    @Test
    public void testShort() throws Exception {
        performTest("Short", new Short(((short) (1))));
    }

    @Test
    public void testShortArray() throws Exception {
        performTest("ShortArray", new short[]{ 1, 20 });
    }

    @Test
    public void testStringArray() throws Exception {
        performTest("StringArray", new String[]{ "1", "20" });
    }

    @Test
    public void testURI() throws Exception {
        performTest("URI", new URI("http://nowhere.com"));
    }

    @Test
    public void testURL() throws Exception {
        performTest("URL", new URL("http://nowhere.com"));
    }
}

