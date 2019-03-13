/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.onetoone.formula;


import ClobTypeDescriptor.DEFAULT;
import TextType.INSTANCE;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Property;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class OneToOneFormulaTest extends BaseCoreFunctionalTestCase {
    private static class TextAsMaterializedClobType extends AbstractSingleColumnStandardBasicType<String> {
        public static final OneToOneFormulaTest.TextAsMaterializedClobType INSTANCE = new OneToOneFormulaTest.TextAsMaterializedClobType();

        public TextAsMaterializedClobType() {
            super(DEFAULT, TextType.INSTANCE.getJavaTypeDescriptor());
        }

        public String getName() {
            return TextType.INSTANCE.getName();
        }
    }

    private Person person;

    private Address address;

    @Test
    public void testOneToOneFormula() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Person p = ((Person) (s.createQuery("from Person").uniqueResult()));
            assertNotNull(p.getAddress());
            assertTrue(Hibernate.isInitialized(p.getAddress()));
            assertNull(p.getMailingAddress());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Person p = ((Person) (s.createQuery("from Person p left join fetch p.mailingAddress left join fetch p.address").uniqueResult()));
            assertNotNull(p.getAddress());
            assertTrue(Hibernate.isInitialized(p.getAddress()));
            assertNull(p.getMailingAddress());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Person p = ((Person) (s.createQuery("from Person p left join fetch p.address").uniqueResult()));
            assertNotNull(p.getAddress());
            assertTrue(Hibernate.isInitialized(p.getAddress()));
            assertNull(p.getMailingAddress());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Person p = ((Person) (s.createCriteria(.class).createCriteria("address").add(Property.forName("zip").eq("3181")).uniqueResult()));
            assertNotNull(p);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Person p = ((Person) (s.createCriteria(.class).setFetchMode("address", FetchMode.JOIN).uniqueResult()));
            assertNotNull(p.getAddress());
            assertTrue(Hibernate.isInitialized(p.getAddress()));
            assertNull(p.getMailingAddress());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Person p = ((Person) (s.createCriteria(.class).setFetchMode("mailingAddress", FetchMode.JOIN).uniqueResult()));
            assertNotNull(p.getAddress());
            assertTrue(Hibernate.isInitialized(p.getAddress()));
            assertNull(p.getMailingAddress());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-5757")
    public void testQuery() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Person p = ((Person) (session.createQuery("from Person p where p.address = :address").setParameter("address", address).uniqueResult()));
            assertThat(p, notNullValue());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Address a = ((Address) (session.createQuery("from Address a where a.person = :person").setParameter("person", person).uniqueResult()));
            assertThat(a, notNullValue());
        });
    }

    @Test
    public void testOneToOneEmbeddedCompositeKey() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Address a = new Address();
            a.setType("HOME");
            a.setPerson(person);
            a = session.load(.class, a);
            assertFalse(Hibernate.isInitialized(a));
            a.getPerson();
            a.getType();
            assertFalse(Hibernate.isInitialized(a));
            assertEquals(a.getZip(), "3181");
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Address a = new Address();
            a.setType("HOME");
            a.setPerson(person);
            Address a2 = session.get(.class, a);
            assertTrue(Hibernate.isInitialized(a));
            assertSame(a2, a);
            assertSame(a2.getPerson(), person);// this is a little bit desirable

            assertEquals(a.getZip(), "3181");
        });
        // s.delete(a2);
        // s.delete( s.get( Person.class, p.getName() ) ); //this is certainly undesirable! oh well...
        // 
        // t.commit();
        // s.close();
    }
}

