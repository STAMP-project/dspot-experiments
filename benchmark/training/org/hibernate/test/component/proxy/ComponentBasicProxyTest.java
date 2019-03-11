/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.component.proxy;


import EntityMode.POJO;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.test.annotations.basic.CollectionAsBasicTest;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.type.ComponentType;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Guillaume Smet
 * @author Oliver Libutzki
 */
public class ComponentBasicProxyTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12786")
    public void testBasicProxyingWithProtectedMethodCalledInConstructor() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Adult adult = new Adult();
            adult.setName("Arjun Kumar");
            entityManager.persist(adult);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Adult> adultsCalledArjun = entityManager.createQuery("SELECT a from Adult a WHERE a.name = :name", .class).setParameter("name", "Arjun Kumar").getResultList();
            Adult adult = adultsCalledArjun.iterator().next();
            entityManager.remove(adult);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12791")
    public void testOnlyOneProxyClassGenerated() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            Metadata metadata = addAnnotatedClass(Person.class).getMetadataBuilder().applyBasicType(new CollectionAsBasicTest.DelimitedStringsType()).build();
            PersistentClass persistentClass = metadata.getEntityBinding(Person.class.getName());
            ComponentType componentType1 = ((ComponentType) (persistentClass.getIdentifierMapper().getType()));
            Object instance1 = componentType1.instantiate(POJO);
            ComponentType componentType2 = ((ComponentType) (persistentClass.getIdentifierMapper().getType()));
            Object instance2 = componentType2.instantiate(POJO);
            Assert.assertEquals(instance1.getClass(), instance2.getClass());
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

