/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import java.lang.reflect.Field;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class LenientCopyToolTest extends TestBase {
    private LenientCopyTool tool = new LenientCopyTool();

    static class InheritMe {
        protected String protectedInherited = "protected";

        private String privateInherited = "private";
    }

    public static class SomeObject extends LenientCopyToolTest.InheritMe {
        // required because static fields needs to be excluded from copying
        @SuppressWarnings("unused")
        private static int staticField = -100;

        private int privateField = -100;

        private transient int privateTransientField = -100;

        String defaultField = "-100";

        protected Object protectedField = new Object();

        public LenientCopyToolTest.SomeOtherObject instancePublicField = new LenientCopyToolTest.SomeOtherObject();

        final int finalField;

        public SomeObject(int finalField) {
            this.finalField = finalField;
        }
    }

    public static class SomeOtherObject {}

    private LenientCopyToolTest.SomeObject from = new LenientCopyToolTest.SomeObject(100);

    private LenientCopyToolTest.SomeObject to = Mockito.mock(LenientCopyToolTest.SomeObject.class);

    @Test
    public void shouldShallowCopyBasicFinalField() throws Exception {
        // given
        Assert.assertEquals(100, from.finalField);
        assertThat(to.finalField).isNotEqualTo(100);
        // when
        tool.copyToMock(from, to);
        // then
        Assert.assertEquals(100, to.finalField);
    }

    @Test
    public void shouldShallowCopyTransientPrivateFields() throws Exception {
        // given
        from.privateTransientField = 1000;
        assertThat(to.privateTransientField).isNotEqualTo(1000);
        // when
        tool.copyToMock(from, to);
        // then
        Assert.assertEquals(1000, to.privateTransientField);
    }

    @Test
    public void shouldShallowCopyLinkedListIntoMock() throws Exception {
        // given
        LinkedList fromList = new LinkedList();
        LinkedList toList = Mockito.mock(LinkedList.class);
        // when
        tool.copyToMock(fromList, toList);
        // then no exception is thrown
    }

    @Test
    public void shouldShallowCopyFieldValuesIntoMock() throws Exception {
        // given
        from.defaultField = "foo";
        from.instancePublicField = new LenientCopyToolTest.SomeOtherObject();
        from.privateField = 1;
        from.privateTransientField = 2;
        from.protectedField = 3;
        assertThat(to.defaultField).isNotEqualTo(from.defaultField);
        assertThat(to.instancePublicField).isNotEqualTo(from.instancePublicField);
        assertThat(to.privateField).isNotEqualTo(from.privateField);
        assertThat(to.privateTransientField).isNotEqualTo(from.privateTransientField);
        assertThat(to.protectedField).isNotEqualTo(from.protectedField);
        // when
        tool.copyToMock(from, to);
        // then
        Assert.assertEquals(from.defaultField, to.defaultField);
        Assert.assertEquals(from.instancePublicField, to.instancePublicField);
        Assert.assertEquals(from.privateField, to.privateField);
        Assert.assertEquals(from.privateTransientField, to.privateTransientField);
        Assert.assertEquals(from.protectedField, to.protectedField);
    }

    @Test
    public void shouldCopyValuesOfInheritedFields() throws Exception {
        // given
        ((LenientCopyToolTest.InheritMe) (from)).privateInherited = "foo";
        ((LenientCopyToolTest.InheritMe) (from)).protectedInherited = "bar";
        assertThat(((LenientCopyToolTest.InheritMe) (to)).privateInherited).isNotEqualTo(((LenientCopyToolTest.InheritMe) (from)).privateInherited);
        // when
        tool.copyToMock(from, to);
        // then
        Assert.assertEquals(((LenientCopyToolTest.InheritMe) (from)).privateInherited, ((LenientCopyToolTest.InheritMe) (to)).privateInherited);
    }

    @Test
    public void shouldEnableAndThenDisableAccessibility() throws Exception {
        // given
        Field privateField = LenientCopyToolTest.SomeObject.class.getDeclaredField("privateField");
        Assert.assertFalse(privateField.isAccessible());
        // when
        tool.copyToMock(from, to);
        // then
        privateField = LenientCopyToolTest.SomeObject.class.getDeclaredField("privateField");
        Assert.assertFalse(privateField.isAccessible());
    }

    @Test
    public void shouldContinueEvenIfThereAreProblemsCopyingSingleFieldValue() throws Exception {
        // given
        tool.fieldCopier = Mockito.mock(FieldCopier.class);
        Mockito.doNothing().doThrow(new IllegalAccessException()).doNothing().when(tool.fieldCopier).copyValue(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject(), ArgumentMatchers.any(Field.class));
        // when
        tool.copyToMock(from, to);
        // then
        Mockito.verify(tool.fieldCopier, Mockito.atLeast(3)).copyValue(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(Field.class));
    }

    @Test
    public void shouldBeAbleToCopyFromRealObjectToRealObject() throws Exception {
        // given
        from.defaultField = "defaultField";
        from.instancePublicField = new LenientCopyToolTest.SomeOtherObject();
        from.privateField = 1;
        from.privateTransientField = 2;
        from.protectedField = "protectedField";
        from.protectedInherited = "protectedInherited";
        to = new LenientCopyToolTest.SomeObject(0);
        // when
        tool.copyToRealObject(from, to);
        // then
        Assert.assertEquals(from.defaultField, to.defaultField);
        Assert.assertEquals(from.instancePublicField, to.instancePublicField);
        Assert.assertEquals(from.privateField, to.privateField);
        Assert.assertEquals(from.privateTransientField, to.privateTransientField);
        Assert.assertEquals(from.protectedField, to.protectedField);
        Assert.assertEquals(from.protectedInherited, to.protectedInherited);
    }
}

