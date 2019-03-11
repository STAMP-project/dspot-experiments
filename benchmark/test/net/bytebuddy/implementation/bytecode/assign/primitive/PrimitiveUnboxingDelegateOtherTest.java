package net.bytebuddy.implementation.bytecode.assign.primitive;


import net.bytebuddy.description.type.TypeDescription;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;


public class PrimitiveUnboxingDelegateOtherTest {
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalSourceTypeThrowsException() throws Exception {
        PrimitiveUnboxingDelegate.forReferenceType(of(int.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVoidIllegal() throws Exception {
        PrimitiveUnboxingDelegate.forPrimitive(VOID);
    }
}

