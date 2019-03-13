/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.wildfly.integrationtest;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hamcrest.core.IsEqual;
import org.hibernate.Session;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Integration test for using the current Hibernate ORM version on WildFly.
 * <p>
 * Gradle will unzip the targeted WildFly version and unpack the module ZIP created by this build into the server's
 * module directory. Arquillian is used to start this WildFly instance, run this test on the server and stop the server
 * again.
 *
 * @author Gunnar Morling
 */
@RunWith(Arquillian.class)
public class HibernateModulesOnWildflyTest {
    private static final String ORM_VERSION = Session.class.getPackage().getImplementationVersion();

    private static final String ORM_MINOR_VERSION = HibernateModulesOnWildflyTest.ORM_VERSION.substring(0, HibernateModulesOnWildflyTest.ORM_VERSION.indexOf(".", ((HibernateModulesOnWildflyTest.ORM_VERSION.indexOf(".")) + 1)));

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void shouldUseHibernateOrm52() {
        Session session = entityManager.unwrap(Session.class);
        Kryptonite kryptonite1 = new Kryptonite();
        kryptonite1.id = 1L;
        kryptonite1.description = "Some Kryptonite";
        session.persist(kryptonite1);
        // EntityManager methods exposed through Session only as of 5.2
        Kryptonite loaded = session.find(Kryptonite.class, 1L);
        Assert.assertThat(loaded.description, IsEqual.equalTo("Some Kryptonite"));
    }
}

