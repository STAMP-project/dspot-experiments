package net.bytebuddy.description;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.description.TypeVariableSource.Visitor.NoOp.INSTANCE;


public class TypeVariableSourceVisitorNoOpTest {
    @Test
    public void testVisitType() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        MatcherAssert.assertThat(INSTANCE.onType(typeDescription), CoreMatchers.is(((TypeVariableSource) (typeDescription))));
    }

    @Test
    public void testVisitMethod() throws Exception {
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        MatcherAssert.assertThat(INSTANCE.onMethod(methodDescription), CoreMatchers.is(((TypeVariableSource) (methodDescription))));
    }
}

