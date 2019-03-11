/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util;


import org.junit.Assert;
import org.junit.Test;


public class PrimitivesTest {
    @Test
    public void should_not_return_null_for_primitives_wrappers() throws Exception {
        Assert.assertNotNull(Primitives.defaultValue(Boolean.class));
        Assert.assertNotNull(Primitives.defaultValue(Character.class));
        Assert.assertNotNull(Primitives.defaultValue(Byte.class));
        Assert.assertNotNull(Primitives.defaultValue(Short.class));
        Assert.assertNotNull(Primitives.defaultValue(Integer.class));
        Assert.assertNotNull(Primitives.defaultValue(Long.class));
        Assert.assertNotNull(Primitives.defaultValue(Float.class));
        Assert.assertNotNull(Primitives.defaultValue(Double.class));
    }

    @Test
    public void should_not_return_null_for_primitives() throws Exception {
        Assert.assertNotNull(Primitives.defaultValue(boolean.class));
        Assert.assertNotNull(Primitives.defaultValue(char.class));
        Assert.assertNotNull(Primitives.defaultValue(byte.class));
        Assert.assertNotNull(Primitives.defaultValue(short.class));
        Assert.assertNotNull(Primitives.defaultValue(int.class));
        Assert.assertNotNull(Primitives.defaultValue(long.class));
        Assert.assertNotNull(Primitives.defaultValue(float.class));
        Assert.assertNotNull(Primitives.defaultValue(double.class));
    }

    @Test
    public void should_default_values_for_primitive() {
        assertThat(Primitives.defaultValue(boolean.class)).isFalse();
        assertThat(Primitives.defaultValue(char.class)).isEqualTo('\u0000');
        assertThat(Primitives.defaultValue(byte.class)).isEqualTo(((byte) (0)));
        assertThat(Primitives.defaultValue(short.class)).isEqualTo(((short) (0)));
        assertThat(Primitives.defaultValue(int.class)).isEqualTo(0);
        assertThat(Primitives.defaultValue(long.class)).isEqualTo(0L);
        assertThat(Primitives.defaultValue(float.class)).isEqualTo(0.0F);
        assertThat(Primitives.defaultValue(double.class)).isEqualTo(0.0);
    }

    @Test
    public void should_default_values_for_wrapper() {
        assertThat(Primitives.defaultValue(Boolean.class)).isFalse();
        assertThat(Primitives.defaultValue(Character.class)).isEqualTo('\u0000');
        assertThat(Primitives.defaultValue(Byte.class)).isEqualTo(((byte) (0)));
        assertThat(Primitives.defaultValue(Short.class)).isEqualTo(((short) (0)));
        assertThat(Primitives.defaultValue(Integer.class)).isEqualTo(0);
        assertThat(Primitives.defaultValue(Long.class)).isEqualTo(0L);
        assertThat(Primitives.defaultValue(Float.class)).isEqualTo(0.0F);
        assertThat(Primitives.defaultValue(Double.class)).isEqualTo(0.0);
    }

    @Test
    public void should_return_null_for_everything_else() throws Exception {
        Assert.assertNull(Primitives.defaultValue(Object.class));
        Assert.assertNull(Primitives.defaultValue(String.class));
        Assert.assertNull(Primitives.defaultValue(null));
    }

    @Test
    public void should_check_that_value_type_is_assignable_to_wrapper_reference() {
        assertThat(Primitives.isAssignableFromWrapper(int.class, Integer.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(Integer.class, Integer.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(long.class, Long.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(Long.class, Long.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(double.class, Double.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(Double.class, Double.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(float.class, Float.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(Float.class, Float.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(char.class, Character.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(Character.class, Character.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(short.class, Short.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(Short.class, Short.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(byte.class, Byte.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(Byte.class, Byte.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(boolean.class, Boolean.class)).isTrue();
        assertThat(Primitives.isAssignableFromWrapper(Boolean.class, Boolean.class)).isTrue();
    }
}

