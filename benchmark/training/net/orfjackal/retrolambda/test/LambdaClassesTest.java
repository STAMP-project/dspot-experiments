/**
 * Copyright ? 2013-2017 Esko Luontola and other Retrolambda contributors
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.test;


import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.objectweb.asm.tree.ClassNode;


public class LambdaClassesTest {
    @Test
    public void the_sequence_number_starts_from_1_for_each_enclosing_class() {
        TestUtil.assertClassExists(((LambdaClassesTest.Dummy1.class.getName()) + "$$Lambda$1"));
        TestUtil.assertClassExists(((LambdaClassesTest.Dummy1.class.getName()) + "$$Lambda$2"));
        TestUtil.assertClassExists(((LambdaClassesTest.Dummy2.class.getName()) + "$$Lambda$1"));
        TestUtil.assertClassExists(((LambdaClassesTest.Dummy2.class.getName()) + "$$Lambda$2"));
    }

    @SuppressWarnings("UnusedDeclaration")
    private class Dummy1 {
        private Dummy1() {
            Runnable lambda1 = () -> {
            };
            Runnable lambda2 = () -> {
            };
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private class Dummy2 {
        private Dummy2() {
            Runnable lambda1 = () -> {
            };
            Runnable lambda2 = () -> {
            };
        }
    }

    @Test
    public void capturing_lambda_classes_contain_no_unnecessary_methods() throws ClassNotFoundException {
        MatcherAssert.assertThat(LambdaClassesTest.getMethodNames(LambdaClassesTest.findLambdaClass(LambdaClassesTest.Capturing.class)), is(ImmutableSet.of("lambdaFactory$", "run")));
    }

    @SuppressWarnings("UnusedDeclaration")
    private class Capturing {
        private Capturing() {
            Runnable lambda = () -> System.out.println(hashCode());
        }
    }

    @Test
    public void non_capturing_lambda_classes_contain_no_unnecessary_methods() throws ClassNotFoundException {
        MatcherAssert.assertThat(LambdaClassesTest.getMethodNames(LambdaClassesTest.findLambdaClass(LambdaClassesTest.NonCapturing.class)), is(ImmutableSet.of("lambdaFactory$", "run")));
    }

    @SuppressWarnings("UnusedDeclaration")
    private class NonCapturing {
        private NonCapturing() {
            Runnable lambda = () -> {
            };
        }
    }

    @Test
    public void enclosing_classes_contain_no_unnecessary_methods_in_addition_to_the_lambda_body() throws ClassNotFoundException {
        MatcherAssert.assertThat("non-capturing lambda", LambdaClassesTest.getMethodNames(LambdaClassesTest.NonCapturing.class), contains(startsWith("lambda$new$")));
        MatcherAssert.assertThat("capturing lambda", LambdaClassesTest.getMethodNames(LambdaClassesTest.Capturing.class), contains(startsWith("lambda$new$")));
    }

    @Test
    public void does_not_contain_references_to_JDK_lambda_classes() throws IOException {
        ConstantPool constantPool = TestUtil.getConstantPool("net/orfjackal/retrolambda/test/LambdaClassesTest$Dummy1$$Lambda$1");
        for (Constant constant : constantPool.getConstantPool()) {
            if (constant != null) {
                String s = constantPool.constantToString(constant);
                MatcherAssert.assertThat(s, not(containsString("java/lang/invoke/LambdaForm")));
            }
        }
    }

    @Test
    public void has_the_same_source_file_attribute_as_the_enclosing_class() throws IOException {
        ClassNode enclosing = LambdaClassesTest.readClass("net/orfjackal/retrolambda/test/LambdaClassesTest");
        ClassNode lambda = LambdaClassesTest.readClass("net/orfjackal/retrolambda/test/LambdaClassesTest$Dummy1$$Lambda$1");
        MatcherAssert.assertThat(lambda.sourceFile, is(notNullValue()));
        MatcherAssert.assertThat(lambda.sourceFile, is(enclosing.sourceFile));
    }
}

