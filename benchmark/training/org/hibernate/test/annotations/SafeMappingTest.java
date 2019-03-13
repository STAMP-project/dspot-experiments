/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.test.annotations;


import Environment.HBM2DDL_AUTO;
import org.hibernate.AnnotationException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.ServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class SafeMappingTest {
    @Test
    public void testDeclarativeMix() throws Exception {
        Configuration cfg = new Configuration();
        cfg.addAnnotatedClass(IncorrectEntity.class);
        cfg.setProperty(HBM2DDL_AUTO, "create-drop");
        ServiceRegistry serviceRegistry = null;
        SessionFactory sessionFactory = null;
        try {
            serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry(cfg.getProperties());
            sessionFactory = cfg.buildSessionFactory(serviceRegistry);
            Assert.fail("Entity wo id should fail");
        } catch (AnnotationException e) {
            // success
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
            if (serviceRegistry != null) {
                ServiceRegistryBuilder.destroy(serviceRegistry);
            }
        }
    }
}

