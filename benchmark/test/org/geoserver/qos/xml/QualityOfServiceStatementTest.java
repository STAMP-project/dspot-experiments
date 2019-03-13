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


public class QualityOfServiceStatementTest {
    @Test
    public void testXmlSerialization() throws JAXBException {
        QualityOfServiceStatementTest.QualityOfServiceStatementRoot root = new QualityOfServiceStatementTest.QualityOfServiceStatementRoot();
        root.setQualityOfServiceStatement(QualityOfServiceStatementTest.buildStatement_ex1());
        XStream xstream = new SecureXStream();
        // xstream.toXML(root, System.out);
    }

    public static class QualityOfServiceStatementRoot {
        private QualityOfServiceStatement qualityOfServiceStatement;

        public QualityOfServiceStatementRoot() {
        }

        public QualityOfServiceStatement getQualityOfServiceStatement() {
            return qualityOfServiceStatement;
        }

        public void setQualityOfServiceStatement(QualityOfServiceStatement qualityOfServiceStatement) {
            this.qualityOfServiceStatement = qualityOfServiceStatement;
        }
    }
}

