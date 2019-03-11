/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.test.annotations.namingstrategy;


import ImplicitNamingStrategyJpaCompliantImpl.INSTANCE;
import java.util.Locale;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test harness for ANN-716.
 *
 * @author Hardy Ferentschik
 */
public class NamingStrategyTest extends BaseUnitTestCase {
    private ServiceRegistry serviceRegistry;

    @Test
    public void testWithCustomNamingStrategy() throws Exception {
        addAnnotatedClass(Person.class).getMetadataBuilder().applyPhysicalNamingStrategy(new DummyNamingStrategy()).build();
    }

    @Test
    public void testWithUpperCaseNamingStrategy() throws Exception {
        Metadata metadata = addAnnotatedClass(A.class).getMetadataBuilder().applyPhysicalNamingStrategy(new PhysicalNamingStrategyStandardImpl() {
            @Override
            public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
                return new Identifier(name.getText().toUpperCase(), name.isQuoted());
            }
        }).build();
        PersistentClass entityBinding = metadata.getEntityBinding(A.class.getName());
        Assert.assertEquals("NAME", getText());
        Assert.assertEquals("VALUE", getText());
    }

    @Test
    public void testWithJpaCompliantNamingStrategy() throws Exception {
        Metadata metadata = addAnnotatedClass(AddressEntry.class).getMetadataBuilder().applyImplicitNamingStrategy(INSTANCE).build();
        Collection collectionBinding = metadata.getCollectionBinding(((A.class.getName()) + ".address"));
        Assert.assertEquals("Expecting A#address collection table name (implicit) to be [A_address] per JPA spec (section 11.1.8)", "A_ADDRESS", collectionBinding.getCollectionTable().getQuotedName().toUpperCase(Locale.ROOT));
    }

    @Test
    public void testWithoutCustomNamingStrategy() throws Exception {
        addAnnotatedClass(Person.class).buildMetadata();
    }
}

