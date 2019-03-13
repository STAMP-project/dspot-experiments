package org.robobinding.codegen.apt.type;


import com.google.testing.compile.CompilationRule;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
@RunWith(Theories.class)
public class WrappedPrimitiveTypeTest {
    @ClassRule
    public static final CompilationRule compilation = new CompilationRule();

    @Test
    public void givenBooleanType_whenIsBoolean_thenReturnTrue() {
        WrappedPrimitiveType type = WrappedPrimitiveTypeTest.wrappedPrimitiveType(TypeKind.BOOLEAN);
        Assert.assertThat(type.isBoolean(), Matchers.equalTo(true));
    }

    private static class PrimitiveTypeToClassName {
        public final PrimitiveType type;

        public String className;

        public PrimitiveTypeToClassName(PrimitiveType type) {
            this.type = type;
        }

        public WrappedPrimitiveTypeTest.PrimitiveTypeToClassName itsClassName(String name) {
            this.className = name;
            return this;
        }
    }

    private static class ClassToPrimitiveType {
        public final Class<?> klass;

        public WrappedPrimitiveType type;

        public ClassToPrimitiveType(Class<?> klass) {
            this.klass = klass;
        }

        public boolean isOfType() {
            return type.isOfType(klass);
        }

        public WrappedPrimitiveTypeTest.ClassToPrimitiveType itsPrimitiveType(WrappedPrimitiveType type) {
            this.type = type;
            return this;
        }
    }
}

