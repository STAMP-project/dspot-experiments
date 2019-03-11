/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type.descriptor.java;


import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public abstract class AbstractDescriptorTest<T> extends BaseUnitTestCase {
    protected class Data<T> {
        private final T originalValue;

        private final T copyOfOriginalValue;

        private final T differentValue;

        public Data(T originalValue, T copyOfOriginalValue, T differentValue) {
            this.originalValue = originalValue;
            this.copyOfOriginalValue = copyOfOriginalValue;
            this.differentValue = differentValue;
        }
    }

    private final JavaTypeDescriptor<T> typeDescriptor;

    public AbstractDescriptorTest(JavaTypeDescriptor<T> typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
    }

    private AbstractDescriptorTest<T>.Data<T> testData;

    @Test
    public void testEquality() {
        Assert.assertFalse(((testData.originalValue) == (testData.copyOfOriginalValue)));
        Assert.assertTrue(typeDescriptor.areEqual(testData.originalValue, testData.originalValue));
        Assert.assertTrue(typeDescriptor.areEqual(testData.originalValue, testData.copyOfOriginalValue));
        Assert.assertFalse(typeDescriptor.areEqual(testData.originalValue, testData.differentValue));
    }

    @Test
    public void testExternalization() {
        // ensure the symmetry of toString/fromString
        String externalized = typeDescriptor.toString(testData.originalValue);
        T consumed = typeDescriptor.fromString(externalized);
        Assert.assertTrue(typeDescriptor.areEqual(testData.originalValue, consumed));
    }

    @Test
    public void testMutabilityPlan() {
        Assert.assertTrue(((shouldBeMutable()) == (typeDescriptor.getMutabilityPlan().isMutable())));
        if ((Clob.class.isInstance(testData.copyOfOriginalValue)) || (Blob.class.isInstance(testData.copyOfOriginalValue))) {
            return;
        }
        T copy = typeDescriptor.getMutabilityPlan().deepCopy(testData.copyOfOriginalValue);
        Assert.assertTrue(typeDescriptor.areEqual(copy, testData.copyOfOriginalValue));
        if (!(shouldBeMutable())) {
            Assert.assertTrue((copy == (testData.copyOfOriginalValue)));
        }
        // ensure the symmetry of assemble/disassebly
        Serializable cached = typeDescriptor.getMutabilityPlan().disassemble(testData.copyOfOriginalValue);
        if (!(shouldBeMutable())) {
            Assert.assertTrue((cached == (testData.copyOfOriginalValue)));
        }
        T reassembled = typeDescriptor.getMutabilityPlan().assemble(cached);
        Assert.assertTrue(typeDescriptor.areEqual(testData.originalValue, reassembled));
    }
}

