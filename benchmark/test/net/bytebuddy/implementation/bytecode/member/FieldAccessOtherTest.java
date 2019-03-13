package net.bytebuddy.implementation.bytecode.member;


import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FieldAccessOtherTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private EnumerationDescription enumerationDescription;

    @Mock
    private TypeDescription.Generic genericType;

    @Mock
    private TypeDescription.Generic declaredType;

    @Mock
    private TypeDescription enumerationType;

    @Mock
    private FieldDescription.InDefinedShape fieldDescription;

    @Mock
    private FieldDescription genericField;

    @Test
    public void testEnumerationDescription() throws Exception {
        Mockito.when(fieldDescription.isPublic()).thenReturn(true);
        Mockito.when(fieldDescription.isStatic()).thenReturn(true);
        Mockito.when(fieldDescription.isEnum()).thenReturn(true);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldAccessOtherTest.FOO);
        StackManipulation stackManipulation = FieldAccess.forEnumeration(enumerationDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testEnumerationDescriptionWithIllegalName() throws Exception {
        Mockito.when(fieldDescription.isPublic()).thenReturn(true);
        Mockito.when(fieldDescription.isStatic()).thenReturn(true);
        Mockito.when(fieldDescription.isEnum()).thenReturn(true);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldAccessOtherTest.BAR);
        StackManipulation stackManipulation = FieldAccess.forEnumeration(enumerationDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testEnumerationDescriptionWithIllegalOwnership() throws Exception {
        Mockito.when(fieldDescription.isPublic()).thenReturn(true);
        Mockito.when(fieldDescription.isStatic()).thenReturn(false);
        Mockito.when(fieldDescription.isEnum()).thenReturn(true);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldAccessOtherTest.FOO);
        StackManipulation stackManipulation = FieldAccess.forEnumeration(enumerationDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testEnumerationDescriptionWithIllegalVisibility() throws Exception {
        Mockito.when(fieldDescription.isPublic()).thenReturn(false);
        Mockito.when(fieldDescription.isStatic()).thenReturn(true);
        Mockito.when(fieldDescription.isEnum()).thenReturn(true);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldAccessOtherTest.FOO);
        StackManipulation stackManipulation = FieldAccess.forEnumeration(enumerationDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testEnumerationDescriptionNonEnumeration() throws Exception {
        Mockito.when(fieldDescription.isPublic()).thenReturn(true);
        Mockito.when(fieldDescription.isStatic()).thenReturn(true);
        Mockito.when(fieldDescription.isEnum()).thenReturn(false);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldAccessOtherTest.FOO);
        StackManipulation stackManipulation = FieldAccess.forEnumeration(enumerationDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testGenericFieldAccessGetter() throws Exception {
        TypeDescription genericErasure = Mockito.mock(TypeDescription.class);
        TypeDescription declaredErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(genericErasure.asErasure()).thenReturn(genericErasure);
        Mockito.when(genericType.asErasure()).thenReturn(genericErasure);
        Mockito.when(declaredType.asErasure()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = FieldAccess.forField(genericField).read();
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(((StackManipulation) (new StackManipulation.Compound(FieldAccess.forField(fieldDescription).read(), TypeCasting.to(genericErasure))))));
    }

    @Test
    public void testGenericFieldAccessPutter() throws Exception {
        TypeDescription genericErasure = Mockito.mock(TypeDescription.class);
        TypeDescription declaredErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(genericType.asErasure()).thenReturn(genericErasure);
        Mockito.when(declaredType.asErasure()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = FieldAccess.forField(genericField).write();
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(FieldAccess.forField(fieldDescription).write()));
    }

    @Test
    public void testGenericFieldAccessGetterEqualErasure() throws Exception {
        TypeDescription declaredErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(genericType.asErasure()).thenReturn(declaredErasure);
        Mockito.when(declaredType.asErasure()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = FieldAccess.forField(genericField).read();
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(FieldAccess.forField(fieldDescription).read()));
    }

    @Test
    public void testGenericFieldAccessPutterEqualErasure() throws Exception {
        TypeDescription declaredErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(genericType.asErasure()).thenReturn(declaredErasure);
        Mockito.when(declaredType.asErasure()).thenReturn(declaredErasure);
        StackManipulation stackManipulation = FieldAccess.forField(genericField).write();
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(stackManipulation, FieldByFieldComparison.hasPrototype(FieldAccess.forField(fieldDescription).write()));
    }
}

