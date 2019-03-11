package cucumber.runtime;


import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import java.net.URI;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class BackendModuleBackendSupplierTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_create_a_backend() {
        ClassLoader classLoader = getClass().getClassLoader();
        RuntimeOptions runtimeOptions = new RuntimeOptions(Collections.<String>emptyList());
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        ClassFinder classFinder = new cucumber.runtime.io.ResourceLoaderClassFinder(resourceLoader, classLoader);
        BackendSupplier backendSupplier = new BackendModuleBackendSupplier(resourceLoader, classFinder, runtimeOptions);
        Assert.assertThat(backendSupplier.get().iterator().next(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void should_throw_an_exception_when_no_backend_could_be_found() {
        ClassLoader classLoader = getClass().getClassLoader();
        RuntimeOptions runtimeOptions = new RuntimeOptions(Collections.<String>emptyList());
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        ClassFinder classFinder = new cucumber.runtime.io.ResourceLoaderClassFinder(resourceLoader, classLoader);
        BackendSupplier backendSupplier = new BackendModuleBackendSupplier(resourceLoader, classFinder, runtimeOptions, Collections.singletonList(URI.create("classpath:no/backend/here")));
        expectedException.expect(CucumberException.class);
        Assert.assertThat(backendSupplier.get().iterator().next(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }
}

