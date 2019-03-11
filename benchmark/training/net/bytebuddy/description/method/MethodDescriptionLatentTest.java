package net.bytebuddy.description.method;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;


public class MethodDescriptionLatentTest extends AbstractMethodDescriptionTest {
    @Test
    public void testTypeInitializer() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        MethodDescription.InDefinedShape typeInitializer = new MethodDescription.Latent.TypeInitializer(typeDescription);
        MatcherAssert.assertThat(typeInitializer.getDeclaringType(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(typeInitializer.getReturnType(), CoreMatchers.is(VOID));
        MatcherAssert.assertThat(typeInitializer.getParameters(), CoreMatchers.is(((ParameterList<ParameterDescription.InDefinedShape>) (new ParameterList.Empty<ParameterDescription.InDefinedShape>()))));
        MatcherAssert.assertThat(typeInitializer.getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(typeInitializer.getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
        MatcherAssert.assertThat(typeInitializer.getModifiers(), CoreMatchers.is(MethodDescription.TYPE_INITIALIZER_MODIFIER));
    }
}

