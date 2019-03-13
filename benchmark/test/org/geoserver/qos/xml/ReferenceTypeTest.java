/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.qos.xml;


import com.thoughtworks.xstream.XStream;
import java.util.Arrays;
import javax.xml.bind.JAXBException;
import org.geoserver.config.util.SecureXStream;
import org.junit.Test;


public class ReferenceTypeTest {
    @Test
    public void testOperationalAnomalyFeedXmlDeserial() throws JAXBException {
        QoSMetadata mdata = new QoSMetadata();
        mdata.setOperationAnomalyFeed(new java.util.ArrayList(Arrays.asList(new ReferenceType[]{ ReferenceTypeTest.buildOperationalAnomalyFeed_ex1() })));
        XStream xstream = new SecureXStream();
        // xstream.toXML(mdata, System.out);
    }
}

