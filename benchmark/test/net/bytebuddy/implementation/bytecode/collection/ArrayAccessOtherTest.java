package net.bytebuddy.implementation.bytecode.collection;


import java.util.Collections;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public class ArrayAccessOtherTest {
    @Test(expected = IllegalArgumentException.class)
    public void testVoidThrowsException() throws Exception {
        ArrayAccess.of(TypeDescription.VOID);
    }

    @Test
    public void testForEach() throws Exception {
        StackManipulation stackManipulation = Mockito.mock(StackManipulation.class);
        MatcherAssert.assertThat(ArrayAccess.REFERENCE.forEach(Collections.singletonList(stackManipulation)), FieldByFieldComparison.hasPrototype(((StackManipulation) (new StackManipulation.Compound(new StackManipulation.Compound(Duplication.SINGLE, IntegerConstant.forValue(0), ArrayAccess.REFERENCE.new Loader(), stackManipulation))))));
    }
}

