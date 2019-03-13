/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.qos.xml;


import com.thoughtworks.xstream.XStream;
import org.geoserver.config.util.SecureXStream;
import org.junit.Test;


public class OperatingInfoTimeTest {
    @Test
    public void testDeserialize() {
        OperatingInfoTime ot = OperatingInfoTimeTest.buildOperatingInfoTimeEx1();
        XStream xstream = new SecureXStream();
        // xstream.toXML(ot, System.out);
    }
}

