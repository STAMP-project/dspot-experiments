/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.lob;


import DialectChecks.SupportsExpectedLobUsagePattern;
import org.hibernate.Session;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.type.SerializableToBlobType;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test type definition for SerializableToBlobType
 *
 * @author Janario Oliveira
 */
@RequiresDialectFeature(SupportsExpectedLobUsagePattern.class)
public class SerializableToBlobTypeTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testTypeDefinition() {
        PersistentClass pc = metadata().getEntityBinding(EntitySerialize.class.getName());
        // explicitLob of SerializableToBlobType
        Type explicitLobType = pc.getProperty("explicitLob").getType();
        Assert.assertEquals(ExplicitSerializable.class, explicitLobType.getReturnedClass());
        Assert.assertEquals(SerializableToBlobType.class.getName(), explicitLobType.getName());
        // explicit of ExplicitSerializableType
        Type explicitType = pc.getProperty("explicit").getType();
        Assert.assertEquals(ExplicitSerializable.class, explicitType.getReturnedClass());
        Assert.assertEquals(ExplicitSerializableType.class.getName(), explicitType.getName());
        // implicit of ImplicitSerializableType
        Type implicitType = pc.getProperty("implicit").getType();
        Assert.assertEquals(ImplicitSerializable.class, implicitType.getReturnedClass());
        Assert.assertEquals(ImplicitSerializableType.class.getName(), implicitType.getName());
        // explicitOverridingImplicit ExplicitSerializableType overrides ImplicitSerializableType
        Type overrideType = pc.getProperty("explicitOverridingImplicit").getType();
        Assert.assertEquals(ImplicitSerializable.class, overrideType.getReturnedClass());
        Assert.assertEquals(ExplicitSerializableType.class.getName(), overrideType.getName());
    }

    @Test
    public void testPersist() {
        EntitySerialize entitySerialize = new EntitySerialize();
        entitySerialize.explicitLob = new ExplicitSerializable();
        entitySerialize.explicitLob.value = "explicitLob";
        entitySerialize.explicitLob.defaultValue = "defaultExplicitLob";
        entitySerialize.explicit = new ExplicitSerializable();
        entitySerialize.explicit.value = "explicit";
        entitySerialize.implicit = new ImplicitSerializable();
        entitySerialize.implicit.value = "implicit";
        entitySerialize.explicitOverridingImplicit = new ImplicitSerializable();
        entitySerialize.explicitOverridingImplicit.value = "explicitOverridingImplicit";
        Session session = openSession();
        session.getTransaction().begin();
        session.persist(entitySerialize);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        EntitySerialize persistedSerialize = ((EntitySerialize) (session.get(EntitySerialize.class, entitySerialize.id)));
        Assert.assertEquals("explicitLob", persistedSerialize.explicitLob.value);
        Assert.assertEquals("explicit", persistedSerialize.explicit.value);
        Assert.assertEquals("implicit", persistedSerialize.implicit.value);
        Assert.assertEquals("explicitOverridingImplicit", persistedSerialize.explicitOverridingImplicit.value);
        Assert.assertEquals("defaultExplicitLob", persistedSerialize.explicitLob.defaultValue);
        session.delete(persistedSerialize);
        session.getTransaction().commit();
        session.close();
    }
}

