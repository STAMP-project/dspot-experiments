package fj;


import fj.data.List;
import fj.data.Natural;
import fj.data.Option;
import fj.data.Tree;
import java.lang.reflect.Type;
import java.util.Collection;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class ClassTest {
    @Test
    public void testInheritance() {
        Class<Natural> c = Class.clas(Class, Natural.class);
        List<Class<? super Natural>> l = c.inheritance();
        Assert.assertThat(l.length(), Is.is(3));
    }

    @Test
    public void testClassParameters() {
        Class<? extends Option> c = Class.clas(Option.none().getClass());
        Tree<Type> cp = c.classParameters();
        Assert.assertThat(cp.length(), Is.is(1));
    }

    @Test
    public void testSuperclassParameters() {
        Class<? extends Option> c = Class.clas(Option.none().getClass());
        Tree<Type> cp = c.superclassParameters();
        Assert.assertThat(cp.length(), Is.is(2));
    }

    @Test
    public void testInterfaceParameters() {
        Class<? extends Option> c = Class.clas(Option.none().getClass());
        List<Tree<Type>> l = c.interfaceParameters();
        Assert.assertThat(l.length(), Is.is(0));
    }

    @Test
    public void testTypeParameterTree() {
        Class<? extends Option> c = Class.clas(Option.none().getClass());
        Collection<Type> coll = c.classParameters().toCollection();
        for (Type t : coll) {
            Assert.assertThat(Class.typeParameterTree(Class, t).toString(), Is.is("Tree(class fj.data.Option$None)"));
        }
    }
}

