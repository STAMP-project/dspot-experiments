package net.bytebuddy.dynamic.loading;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ByteArrayClassLoader.PackageLookupStrategy.ForLegacyVm.INSTANCE;


public class ByteArrayClassLoaderPackageLookupStrategy {
    @Test
    public void testGetPackage() throws Exception {
        ByteArrayClassLoader byteArrayClassLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(ByteArrayClassLoaderPackageLookupStrategy.Foo.class));
        byteArrayClassLoader.loadClass(ByteArrayClassLoaderPackageLookupStrategy.Foo.class.getName());
        MatcherAssert.assertThat(INSTANCE.apply(byteArrayClassLoader, ByteArrayClassLoaderPackageLookupStrategy.Foo.class.getPackage().getName()).getName(), CoreMatchers.is(ByteArrayClassLoaderPackageLookupStrategy.Foo.class.getPackage().getName()));
    }

    /* empty */
    private static class Foo {}
}

