/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.qos.xml;


import com.thoughtworks.xstream.XStream;
import javax.xml.bind.JAXBException;
import org.geoserver.config.util.SecureXStream;
import org.junit.Test;


public class OperatingInfoTest {
    @Test
    public void testXmlSerialization() throws JAXBException {
        OperatingInfoTest.MyOperatingInfoRoot root = new OperatingInfoTest.MyOperatingInfoRoot();
        root.setOperatingInfo(OperatingInfoTest.buildOperationInfoEx1());
        XStream xstream = new SecureXStream();
        // xstream.toXML(root, System.out);
    }

    public static class MyOperatingInfoRoot {
        private OperatingInfo operatingInfo;

        public OperatingInfo getOperatingInfo() {
            return operatingInfo;
        }

        public void setOperatingInfo(OperatingInfo operatingInfo) {
            this.operatingInfo = operatingInfo;
        }
    }
}

