/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.cfg;


import AvailableSettings.CFG_FILE;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import javax.persistence.Persistence;
import org.hibernate.internal.util.ConfigHelper;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-13227")
public class CfgFilePropertyTest extends BaseUnitTestCase {
    @Test
    public void test() throws InterruptedException {
        final AtomicReference<Exception> exceptionHolder = new AtomicReference<>();
        Thread thread = new Thread(() -> {
            try {
                final Properties props = new Properties();
                props.setProperty(CFG_FILE, "/org/hibernate/test/boot/cfgXml/hibernate.cfg.xml");
                Persistence.createEntityManagerFactory("ExcludeUnlistedClassesTest1", props);
            } catch (Exception e) {
                exceptionHolder.set(e);
            }
        });
        thread.setContextClassLoader(new ClassLoader() {
            @Override
            protected Enumeration<URL> findResources(String name) throws IOException {
                return name.equals("META-INF/persistence.xml") ? Collections.enumeration(Collections.singletonList(ConfigHelper.findAsResource("org/hibernate/jpa/test/persistenceunit/META-INF/persistence.xml"))) : Collections.emptyEnumeration();
            }
        });
        thread.start();
        thread.join();
        Assert.assertNull(exceptionHolder.get());
    }
}

