/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id;


import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
public abstract class AbstractHolderTest extends BaseUnitTestCase {
    @Test
    @SuppressWarnings({ "EmptyCatchBlock" })
    public void testInitializationChecking() {
        IntegralDataTypeHolder holder = makeHolder();
        try {
            holder.increment();
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.add(1);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.decrement();
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.subtract(1);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.multiplyBy(holder);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.multiplyBy(1);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.eq(holder);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.eq(1);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.lt(holder);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.lt(1);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.gt(holder);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.gt(1);
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
        try {
            holder.makeValue();
            Assert.fail();
        } catch (IdentifierGenerationException expected) {
        }
    }

    @Test
    public void testBasicHiloAlgorithm() {
        // mimic an initialValue of 1 and increment of 20
        final long initialValue = 1;
        final long incrementSize = 2;
        // initialization
        IntegralDataTypeHolder lastSourceValue = makeHolder().initialize(1);
        IntegralDataTypeHolder upperLimit = lastSourceValue.copy().multiplyBy(incrementSize).increment();
        IntegralDataTypeHolder value = upperLimit.copy().subtract(incrementSize);
        Assert.assertEquals(1, lastSourceValue.makeValue().longValue());
        Assert.assertEquals(3, upperLimit.makeValue().longValue());
        Assert.assertEquals(1, value.makeValue().longValue());
        value.increment();
        value.increment();
        Assert.assertFalse(upperLimit.gt(value));
        // at which point we would "clock over"
        lastSourceValue.increment();
        upperLimit = lastSourceValue.copy().multiplyBy(incrementSize).increment();
        Assert.assertEquals(2, lastSourceValue.makeValue().longValue());
        Assert.assertEquals(5, upperLimit.makeValue().longValue());
        Assert.assertEquals(3, value.makeValue().longValue());
    }
}

