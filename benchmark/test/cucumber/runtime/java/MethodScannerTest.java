package cucumber.runtime.java;


import cucumber.api.java.ObjectFactory;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Glue;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import java.net.URI;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class MethodScannerTest {
    private ResourceLoaderClassFinder classFinder;

    private ObjectFactory factory;

    private JavaBackend backend;

    @Test
    public void loadGlue_registers_the_methods_declaring_class_in_the_object_factory() throws NoSuchMethodException {
        MethodScanner methodScanner = new MethodScanner(classFinder);
        Glue world = Mockito.mock(Glue.class);
        backend.loadGlue(world, Collections.<URI>emptyList());
        // this delegates to methodScanner.scan which we test
        methodScanner.scan(backend, MethodScannerTest.BaseStepDefs.class.getMethod("m"), MethodScannerTest.BaseStepDefs.class);
        Mockito.verify(factory, Mockito.times(1)).addClass(MethodScannerTest.BaseStepDefs.class);
        Mockito.verifyNoMoreInteractions(factory);
    }

    @Test
    public void loadGlue_fails_when_class_is_not_method_declaring_class() throws NoSuchMethodException {
        try {
            backend.loadGlue(null, MethodScannerTest.BaseStepDefs.class.getMethod("m"), MethodScannerTest.Stepdefs2.class);
            Assert.fail();
        } catch (CucumberException e) {
            Assert.assertEquals("You're not allowed to extend classes that define Step Definitions or hooks. class cucumber.runtime.java.MethodScannerTest$Stepdefs2 extends class cucumber.runtime.java.MethodScannerTest$BaseStepDefs", e.getMessage());
        }
    }

    @Test
    public void loadGlue_fails_when_class_is_not_subclass_of_declaring_class() throws NoSuchMethodException {
        try {
            backend.loadGlue(null, MethodScannerTest.BaseStepDefs.class.getMethod("m"), String.class);
            Assert.fail();
        } catch (CucumberException e) {
            Assert.assertEquals("class cucumber.runtime.java.MethodScannerTest$BaseStepDefs isn't assignable from class java.lang.String", e.getMessage());
        }
    }

    public static class Stepdefs2 extends MethodScannerTest.BaseStepDefs {
        public interface Interface1 {}
    }

    public static class BaseStepDefs {
        @cucumber.api.java.Before
        public void m() {
        }
    }
}

