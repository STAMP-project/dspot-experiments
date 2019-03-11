/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class DirtyCheckingTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void dirtyCheckAgainstNewNameInstance() {
        DirtyCheckingTest.SomeEntity simpleEntity = new DirtyCheckingTest.SomeEntity();
        simpleEntity.setId(1L);
        simpleEntity.setName(new DirtyCheckingTest.Name("Steven"));
        Session session = openSession();
        session.getTransaction().begin();
        session.save(simpleEntity);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        DirtyCheckingTest.SomeEntity loaded = session.byId(DirtyCheckingTest.SomeEntity.class).load(1L);
        loaded.setName(new DirtyCheckingTest.Name("Steve"));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        loaded = session.byId(DirtyCheckingTest.SomeEntity.class).load(1L);
        Assert.assertEquals("Steve", loaded.getName().getText());
        session.delete(loaded);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void dirtyCheckAgainstMutatedNameInstance() {
        DirtyCheckingTest.SomeEntity simpleEntity = new DirtyCheckingTest.SomeEntity();
        simpleEntity.setId(1L);
        simpleEntity.setName(new DirtyCheckingTest.Name("Steven"));
        Session session = openSession();
        session.getTransaction().begin();
        session.save(simpleEntity);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        DirtyCheckingTest.SomeEntity loaded = session.byId(DirtyCheckingTest.SomeEntity.class).load(1L);
        loaded.getName().setText("Steve");
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        loaded = session.byId(DirtyCheckingTest.SomeEntity.class).load(1L);
        Assert.assertEquals("Steve", loaded.getName().getText());
        session.delete(loaded);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void dirtyCheckAgainstNewNumberInstance() {
        // numbers (and most other java types) are actually immutable...
        DirtyCheckingTest.SomeEntity simpleEntity = new DirtyCheckingTest.SomeEntity();
        simpleEntity.setId(1L);
        simpleEntity.setNumber(1);
        Session session = openSession();
        session.getTransaction().begin();
        session.save(simpleEntity);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        DirtyCheckingTest.SomeEntity loaded = session.byId(DirtyCheckingTest.SomeEntity.class).load(1L);
        loaded.setNumber(2);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        loaded = session.byId(DirtyCheckingTest.SomeEntity.class).load(1L);
        Assert.assertEquals(2, loaded.getNumber().intValue());
        session.delete(loaded);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void checkConverterMutabilityPlans() {
        final EntityPersister persister = sessionFactory().getEntityPersister(DirtyCheckingTest.SomeEntity.class.getName());
        Assert.assertFalse(persister.getPropertyType("number").isMutable());
        Assert.assertTrue(persister.getPropertyType("name").isMutable());
    }

    public static class Name {
        private String text;

        public Name() {
        }

        public Name(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class NameConverter implements AttributeConverter<DirtyCheckingTest.Name, String> {
        public String convertToDatabaseColumn(DirtyCheckingTest.Name name) {
            return name == null ? null : name.getText();
        }

        public DirtyCheckingTest.Name convertToEntityAttribute(String s) {
            return s == null ? null : new DirtyCheckingTest.Name(s);
        }
    }

    public static class IntegerConverter implements AttributeConverter<Integer, String> {
        public String convertToDatabaseColumn(Integer value) {
            return value == null ? null : value.toString();
        }

        public Integer convertToEntityAttribute(String s) {
            return s == null ? null : Integer.parseInt(s);
        }
    }

    @Entity(name = "SomeEntity")
    public static class SomeEntity {
        @Id
        private Long id;

        @Convert(converter = DirtyCheckingTest.IntegerConverter.class)
        @Column(name = "num")
        private Integer number;

        @Convert(converter = DirtyCheckingTest.NameConverter.class)
        @Column(name = "name")
        private DirtyCheckingTest.Name name = new DirtyCheckingTest.Name();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public DirtyCheckingTest.Name getName() {
            return name;
        }

        public void setName(DirtyCheckingTest.Name name) {
            this.name = name;
        }
    }
}

