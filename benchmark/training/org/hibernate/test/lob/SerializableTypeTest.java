/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lob;


import org.hibernate.Session;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of {@link org.hibernate.type.SerializableType}
 *
 * @author Steve Ebersole
 */
public class SerializableTypeTest extends BaseCoreFunctionalTestCase {
    @Test
    @SkipForDialect(value = SybaseASE15Dialect.class, jiraKey = "HHH-6425")
    public void testNewSerializableType() {
        final String initialPayloadText = "Initial payload";
        final String changedPayloadText = "Changed payload";
        final String empty = "";
        Session s = openSession();
        s.beginTransaction();
        SerializableHolder holder = new SerializableHolder();
        s.save(holder);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        holder = ((SerializableHolder) (s.get(SerializableHolder.class, holder.getId())));
        Assert.assertNull(holder.getSerialData());
        holder.setSerialData(new SerializableData(initialPayloadText));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        holder = ((SerializableHolder) (s.get(SerializableHolder.class, holder.getId())));
        SerializableData serialData = ((SerializableData) (holder.getSerialData()));
        Assert.assertEquals(initialPayloadText, serialData.getPayload());
        holder.setSerialData(new SerializableData(changedPayloadText));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        holder = ((SerializableHolder) (s.get(SerializableHolder.class, holder.getId())));
        serialData = ((SerializableData) (holder.getSerialData()));
        Assert.assertEquals(changedPayloadText, serialData.getPayload());
        holder.setSerialData(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        holder = ((SerializableHolder) (s.get(SerializableHolder.class, holder.getId())));
        Assert.assertNull(holder.getSerialData());
        holder.setSerialData(new SerializableData(empty));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        holder = ((SerializableHolder) (s.get(SerializableHolder.class, holder.getId())));
        serialData = ((SerializableData) (holder.getSerialData()));
        Assert.assertEquals(empty, serialData.getPayload());
        s.delete(holder);
        s.getTransaction().commit();
        s.close();
    }
}

