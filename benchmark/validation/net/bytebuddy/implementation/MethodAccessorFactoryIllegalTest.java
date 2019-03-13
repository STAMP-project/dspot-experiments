package net.bytebuddy.implementation;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.implementation.MethodAccessorFactory.AccessType.DEFAULT;
import static net.bytebuddy.implementation.MethodAccessorFactory.Illegal.INSTANCE;


public class MethodAccessorFactoryIllegalTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Implementation.SpecialMethodInvocation specialMethodInvocation;

    @Mock
    private FieldDescription fieldDescription;

    @Test(expected = IllegalStateException.class)
    public void testAccessorIsIllegal() throws Exception {
        INSTANCE.registerAccessorFor(specialMethodInvocation, DEFAULT);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetterIsIllegal() throws Exception {
        INSTANCE.registerSetterFor(fieldDescription, DEFAULT);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetterIsIllegal() throws Exception {
        INSTANCE.registerGetterFor(fieldDescription, DEFAULT);
    }
}

