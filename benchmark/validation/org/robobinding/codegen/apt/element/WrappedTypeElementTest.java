package org.robobinding.codegen.apt.element;


import com.google.testing.compile.CompilationRule;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.robobinding.codegen.apt.MethodElementFilter;
import org.robobinding.codegen.apt.SetterElementFilter;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class WrappedTypeElementTest {
    @Rule
    public final CompilationRule compilation = new CompilationRule();

    private WrappedTypeElement klassTypeElement;

    @Test
    public void whenGetMethodsRecursively_thenReturnAllMethodsIncludeParent() {
        List<MethodElement> methods = klassTypeElement.methodsRecursively(WrappedTypeElementTest.ALL_METHODS);
        Assert.assertThat(methods.size(), Matchers.equalTo(((Klass.NUM_METHODS) + (ParentClass.NUM_METHODS))));
    }

    @Test
    public void whenGetterLooseSetters_thenReturnSettersFromKlassOnly() {
        List<SetterElement> setters = klassTypeElement.looseSetters(WrappedTypeElementTest.ALL_SETTERS);
        Assert.assertThat(setters.size(), Matchers.equalTo(Klass.NUM_SETTERS));
    }

    @Test
    public void shouldFoundDirectSuperclassOfParentClass() {
        Assert.assertThat(klassTypeElement.findDirectSuperclassOf(ParentClass.class), Matchers.notNullValue());
    }

    @Test
    public void shouldNotFoundDirectSuperclassOfObject() {
        Assert.assertThat(klassTypeElement.findDirectSuperclassOf(Object.class), Matchers.nullValue());
    }

    @Test
    public void shouldBeConcreteClass() {
        Assert.assertThat(klassTypeElement.isConcreteClass(), Matchers.equalTo(true));
    }

    @Test
    public void shouldNotBeConcreteClass() {
        WrappedTypeElement abstractClassTypeElement = typeElement(AbstractClass.class);
        Assert.assertThat(abstractClassTypeElement.isNotConcreteClass(), Matchers.equalTo(true));
    }

    @Test
    public void shouldParentClassFirstTypeArgumentOfInteger() {
        WrappedTypeElement parentClassTypeElement = klassTypeElement.findDirectSuperclassOf(ParentClass.class);
        WrappedTypeElement typeArgument = parentClassTypeElement.firstTypeArgument();
        Assert.assertThat(typeArgument.qName(), Matchers.equalTo(Integer.class.getName()));
    }

    private static MethodElementFilter ALL_METHODS = new MethodElementFilter() {
        @Override
        public boolean include(MethodElement element) {
            return true;
        }
    };

    private static SetterElementFilter ALL_SETTERS = new SetterElementFilter() {
        @Override
        public boolean include(SetterElement element) {
            return true;
        }
    };
}

