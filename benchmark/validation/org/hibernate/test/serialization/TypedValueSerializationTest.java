/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.serialization;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.testing.TestForIssue;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Brett Meyer
 */
public class TypedValueSerializationTest {
    @Test
    @TestForIssue(jiraKey = "HHH-9024")
    public void testTypedValueSerialization() throws Exception {
        final Type mockType = Mockito.mock(Type.class);
        final String value = "foo";
        final TypedValue typedValue = new TypedValue(mockType, value);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(typedValue);
        final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        final TypedValue typedValueClone = ((TypedValue) (ois.readObject()));
        Assert.assertEquals(typedValue.hashCode(), typedValueClone.hashCode());
        Assert.assertEquals(typedValue.toString(), typedValueClone.toString());
        Assert.assertEquals(typedValue.getValue(), typedValueClone.getValue());
    }
}

