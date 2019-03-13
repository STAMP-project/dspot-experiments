/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.spi;


import LockMode.OPTIMISTIC;
import LockMode.PESSIMISTIC_READ;
import Status.DELETED;
import Status.GONE;
import Status.MANAGED;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.hibernate.engine.internal.MutableEntityEntry;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for setting and getting the enum/boolean values stored in the compressed state int.
 *
 * @author Gunnar Morling
 */
public class EntityEntryTest {
    @Test
    public void packedAttributesAreSetByConstructor() {
        EntityEntry entityEntry = createEntityEntry();
        Assert.assertEquals(OPTIMISTIC, entityEntry.getLockMode());
        Assert.assertEquals(MANAGED, entityEntry.getStatus());
        Assert.assertEquals(true, entityEntry.isExistsInDatabase());
        Assert.assertEquals(true, entityEntry.isBeingReplicated());
    }

    @Test
    public void testLockModeCanBeSetAndDoesNotAffectOtherPackedAttributes() {
        // Given
        EntityEntry entityEntry = createEntityEntry();
        Assert.assertEquals(OPTIMISTIC, entityEntry.getLockMode());
        Assert.assertEquals(MANAGED, entityEntry.getStatus());
        Assert.assertEquals(true, entityEntry.isExistsInDatabase());
        Assert.assertEquals(true, entityEntry.isBeingReplicated());
        // When
        entityEntry.setLockMode(PESSIMISTIC_READ);
        // Then
        Assert.assertEquals(PESSIMISTIC_READ, entityEntry.getLockMode());
        Assert.assertEquals(MANAGED, entityEntry.getStatus());
        Assert.assertEquals(true, entityEntry.isExistsInDatabase());
        Assert.assertEquals(true, entityEntry.isBeingReplicated());
    }

    @Test
    public void testStatusCanBeSetAndDoesNotAffectOtherPackedAttributes() {
        // Given
        EntityEntry entityEntry = createEntityEntry();
        // When
        entityEntry.setStatus(DELETED);
        // Then
        Assert.assertEquals(OPTIMISTIC, entityEntry.getLockMode());
        Assert.assertEquals(DELETED, entityEntry.getStatus());
        Assert.assertEquals(true, entityEntry.isExistsInDatabase());
        Assert.assertEquals(true, entityEntry.isBeingReplicated());
    }

    @Test
    public void testPostDeleteSetsStatusAndExistsInDatabaseWithoutAffectingOtherPackedAttributes() {
        // Given
        EntityEntry entityEntry = createEntityEntry();
        // When
        entityEntry.postDelete();
        // Then
        Assert.assertEquals(OPTIMISTIC, entityEntry.getLockMode());
        Assert.assertEquals(GONE, entityEntry.getStatus());
        Assert.assertEquals(false, entityEntry.isExistsInDatabase());
        Assert.assertEquals(true, entityEntry.isBeingReplicated());
    }

    @Test
    public void testSerializationAndDeserializationKeepCorrectPackedAttributes() throws Exception {
        EntityEntry entityEntry = createEntityEntry();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        entityEntry.serialize(oos);
        oos.flush();
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        EntityEntry deserializedEntry = MutableEntityEntry.deserialize(new ObjectInputStream(is), getPersistenceContextMock());
        Assert.assertEquals(OPTIMISTIC, deserializedEntry.getLockMode());
        Assert.assertEquals(MANAGED, deserializedEntry.getStatus());
        Assert.assertEquals(true, deserializedEntry.isExistsInDatabase());
        Assert.assertEquals(true, deserializedEntry.isBeingReplicated());
    }
}

