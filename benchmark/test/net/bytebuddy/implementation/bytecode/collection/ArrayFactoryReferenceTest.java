package net.bytebuddy.implementation.bytecode.collection;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


@RunWith(Parameterized.class)
public class ArrayFactoryReferenceTest extends AbstractArrayFactoryTest {
    private final Class<?> type;

    private final String internalTypeName;

    public ArrayFactoryReferenceTest(Class<?> type) {
        this.type = type;
        internalTypeName = Type.getInternalName(type);
    }

    @Test
    public void testArrayCreation() throws Exception {
        testCreationUsing(type, Opcodes.AASTORE);
    }
}

