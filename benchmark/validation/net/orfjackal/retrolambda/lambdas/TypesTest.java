/**
 * Copyright ? 2013-2016 Esko Luontola and other Retrolambda contributors
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.lambdas;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Predicate;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


@SuppressWarnings("UnnecessaryLocalVariable")
public class TypesTest {
    private ClassLoader classLoader = getClass().getClassLoader();

    private MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Test
    public void asmToJdkType_MethodType() throws Exception {
        Type input = Type.getMethodType("(I)Ljava/util/function/Predicate;");
        MethodType output = MethodType.methodType(Predicate.class, int.class);
        MatcherAssert.assertThat(Types.asmToJdkType(input, classLoader, lookup), is(output));
    }

    @Test
    public void asmToJdkType_MethodHandle() throws Exception {
        Handle input = new Handle(Opcodes.H_INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
        MethodHandle output = lookup.findStatic(String.class, "valueOf", MethodType.methodType(String.class, int.class));
        MatcherAssert.assertThat(Types.asmToJdkType(input, classLoader, lookup).toString(), is(output.toString()));
    }

    @Test
    public void asmToJdkType_Class() throws Exception {
        Type input = Type.getType(String.class);
        Class<?> output = String.class;
        MatcherAssert.assertThat(Types.asmToJdkType(input, classLoader, lookup), is(equalTo(output)));
    }

    @Test
    public void asmToJdkType_everything_else() throws Exception {
        String input = "foo";
        String output = input;
        MatcherAssert.assertThat(Types.asmToJdkType(input, classLoader, lookup), is(output));
    }
}

