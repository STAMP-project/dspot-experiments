package net.bytebuddy.implementation.bytecode.collection;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ArrayFactoryPrimitiveTest extends AbstractArrayFactoryTest {
    private final Class<?> primitiveType;

    private final int createOpcode;

    private final int storeOpcode;

    public ArrayFactoryPrimitiveTest(Class<?> primitiveType, int createOpcode, int storeOpcode) {
        this.primitiveType = primitiveType;
        this.createOpcode = createOpcode;
        this.storeOpcode = storeOpcode;
    }

    @Test
    public void testArrayCreation() throws Exception {
        testCreationUsing(primitiveType, storeOpcode);
    }
}

