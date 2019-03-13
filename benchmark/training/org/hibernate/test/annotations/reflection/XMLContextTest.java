/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.reflection;


import ClassLoaderServiceTestingImpl.INSTANCE;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.annotations.reflection.XMLContext;
import org.hibernate.internal.util.xml.ErrorLogger;
import org.hibernate.internal.util.xml.XMLHelper;
import org.hibernate.testing.boot.BootstrapContextImpl;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotSupportedException;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class XMLContextTest {
    @Test
    public void testAll() throws Exception {
        final XMLHelper xmlHelper = new XMLHelper();
        final XMLContext context = new XMLContext(BootstrapContextImpl.INSTANCE);
        InputStream is = INSTANCE.locateResourceStream("org/hibernate/test/annotations/reflection/orm.xml");
        Assert.assertNotNull("ORM.xml not found", is);
        final ErrorLogger errorLogger = new ErrorLogger();
        final SAXReader saxReader = xmlHelper.createSAXReader(errorLogger, EJB3DTDEntityResolver.INSTANCE);
        try {
            saxReader.setFeature("http://apache.org/xml/features/validation/schema", true);
        } catch (SAXNotSupportedException e) {
            saxReader.setValidation(false);
        }
        Document doc;
        try {
            doc = saxReader.read(new InputSource(new BufferedInputStream(is)));
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                // log.warn( "Could not close input stream", ioe );
            }
        }
        Assert.assertFalse(errorLogger.hasErrors());
        context.addDocument(doc);
    }
}

