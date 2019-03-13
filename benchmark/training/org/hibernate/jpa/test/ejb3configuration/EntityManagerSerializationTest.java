/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ejb3configuration;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.jpa.test.Cat;
import org.hibernate.jpa.test.Item;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 * @author Scott Marlow
 */
public class EntityManagerSerializationTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testSerialization() throws Exception {
        EntityManager em = entityManagerFactory().createEntityManager();
        // em.getTransaction().begin();
        // em.setFlushMode( FlushModeType.NEVER );
        Cat cat = new Cat();
        cat.setAge(3);
        cat.setDateOfBirth(new Date());
        cat.setLength(22);
        cat.setName("Kitty");
        em.persist(cat);
        Item item = new Item();
        item.setName("Train Ticket");
        item.setDescr("Paris-London");
        em.persist(item);
        // em.getTransaction().commit();
        // em.getTransaction().begin();
        item.setDescr("Paris-Bruxelles");
        // em.getTransaction().commit();
        // fake the in container work
        getSession().disconnect();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(stream);
        out.writeObject(em);
        out.close();
        byte[] serialized = stream.toByteArray();
        stream.close();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(serialized);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        em = ((EntityManager) (in.readObject()));
        in.close();
        byteIn.close();
        // fake the in container work
        em.getTransaction().begin();
        item = em.find(Item.class, item.getName());
        item.setDescr(((item.getDescr()) + "-Amsterdam"));
        cat = ((Cat) (em.createQuery((("select c from " + (Cat.class.getName())) + " c")).getSingleResult()));
        cat.setLength(34);
        em.flush();
        em.remove(item);
        em.remove(cat);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }
}

