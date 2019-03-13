/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type.descriptor.java;


import PrimitiveByteArrayTypeDescriptor.INSTANCE;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class PrimitiveByteArrayDescriptorTest extends AbstractDescriptorTest<byte[]> {
    private final byte[] original = new byte[]{ 1, 2, 3 };

    private final byte[] copy = new byte[]{ 1, 2, 3 };

    private final byte[] different = new byte[]{ 3, 2, 1 };

    public PrimitiveByteArrayDescriptorTest() {
        super(INSTANCE);
    }

    @Test
    public void testExtractLoggableRepresentation() {
        Assert.assertEquals("null", INSTANCE.extractLoggableRepresentation(null));
        Assert.assertEquals("[]", INSTANCE.extractLoggableRepresentation(new byte[]{  }));
        Assert.assertEquals("[1, 2, 3]", INSTANCE.extractLoggableRepresentation(original));
    }
}

