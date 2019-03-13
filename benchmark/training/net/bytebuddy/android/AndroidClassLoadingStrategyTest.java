package net.bytebuddy.android;


import AndroidClassLoadingStrategy.DexProcessor;
import AndroidClassLoadingStrategy.DexProcessor.Conversion;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AndroidClassLoadingStrategyTest {
    private static final String FOO = "foo";

    private static final String TEMP = "tmp";

    private static final String TO_STRING = "toString";

    private static final byte[] QUX = new byte[]{ 1, 2, 3 };

    private static final byte[] BAZ = new byte[]{ 4, 5, 6 };

    @Rule
    public TestRule dexCompilerRule = new MockitoRule(this);

    private File folder;

    @Mock
    private TypeDescription firstType;

    @Mock
    private TypeDescription secondType;

    @Test
    public void testProcessing() throws Exception {
        AndroidClassLoadingStrategy.DexProcessor dexProcessor = Mockito.mock(DexProcessor.class);
        AndroidClassLoadingStrategy.DexProcessor.Conversion conversion = Mockito.mock(Conversion.class);
        Mockito.when(dexProcessor.create()).thenReturn(conversion);
        AndroidClassLoadingStrategy classLoadingStrategy = Mockito.spy(new AndroidClassLoadingStrategyTest.StubbedClassLoadingStrategy(folder, dexProcessor));
        Map<TypeDescription, byte[]> unloaded = new HashMap<TypeDescription, byte[]>();
        unloaded.put(firstType, AndroidClassLoadingStrategyTest.QUX);
        unloaded.put(secondType, AndroidClassLoadingStrategyTest.BAZ);
        Map<TypeDescription, Class<?>> loaded = new HashMap<TypeDescription, Class<?>>();
        loaded.put(firstType, AndroidClassLoadingStrategyTest.Foo.class);
        loaded.put(secondType, AndroidClassLoadingStrategyTest.Bar.class);
        Mockito.doReturn(loaded).when(classLoadingStrategy).doLoad(ArgumentMatchers.eq(getClass().getClassLoader()), ArgumentMatchers.eq(unloaded.keySet()), ArgumentMatchers.any(File.class));
        Map<TypeDescription, Class<?>> result = classLoadingStrategy.load(getClass().getClassLoader(), unloaded);
        MatcherAssert.assertThat(result.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(result.get(firstType), CoreMatchers.<Class<?>>is(AndroidClassLoadingStrategyTest.Foo.class));
        MatcherAssert.assertThat(result.get(secondType), CoreMatchers.<Class<?>>is(AndroidClassLoadingStrategyTest.Bar.class));
        Mockito.verify(dexProcessor).create();
        Mockito.verifyNoMoreInteractions(dexProcessor);
        Mockito.verify(conversion).register(AndroidClassLoadingStrategyTest.Foo.class.getName(), AndroidClassLoadingStrategyTest.QUX);
        Mockito.verify(conversion).register(AndroidClassLoadingStrategyTest.Bar.class.getName(), AndroidClassLoadingStrategyTest.BAZ);
        Mockito.verify(conversion).drainTo(ArgumentMatchers.any(OutputStream.class));
        Mockito.verifyNoMoreInteractions(conversion);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAndroidClassLoaderRequiresDirectory() throws Exception {
        new AndroidClassLoadingStrategyTest.StubbedClassLoadingStrategy(Mockito.mock(File.class), Mockito.mock(DexProcessor.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInjectBootstrapLoader() throws Exception {
        File file = Mockito.mock(File.class);
        Mockito.when(file.isDirectory()).thenReturn(true);
        new StubbedClassLoadingStrategy.Injecting(file, Mockito.mock(DexProcessor.class)).load(ClassLoadingStrategy.BOOTSTRAP_LOADER, Collections.<TypeDescription, byte[]>emptyMap());
    }

    @Test
    public void testStubbedClassLoading() throws Exception {
        final DynamicType.Unloaded<?> dynamicType = new ByteBuddy(ClassFileVersion.JAVA_V6).subclass(Object.class).method(ElementMatchers.named(AndroidClassLoadingStrategyTest.TO_STRING)).intercept(FixedValue.value(AndroidClassLoadingStrategyTest.FOO)).make();
        AndroidClassLoadingStrategy classLoadingStrategy = Mockito.spy(new AndroidClassLoadingStrategyTest.StubbedClassLoadingStrategy(folder, new AndroidClassLoadingStrategyTest.StubbedClassLoaderDexCompilation()));
        Mockito.doReturn(Collections.singletonMap(dynamicType.getTypeDescription(), AndroidClassLoadingStrategyTest.Foo.class)).when(classLoadingStrategy).doLoad(ArgumentMatchers.eq(getClass().getClassLoader()), ArgumentMatchers.eq(Collections.singleton(dynamicType.getTypeDescription())), ArgumentMatchers.any(File.class));
        Map<TypeDescription, Class<?>> map = classLoadingStrategy.load(getClass().getClassLoader(), dynamicType.getAllTypes());
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(1));
    }

    private static class StubbedClassLoadingStrategy extends AndroidClassLoadingStrategy {
        public StubbedClassLoadingStrategy(File privateDirectory, DexProcessor dexProcessor) {
            super(privateDirectory, dexProcessor);
        }

        protected Map<TypeDescription, Class<?>> doLoad(ClassLoader classLoader, Set<TypeDescription> typeDescriptions, File jar) throws IOException {
            throw new AssertionError();
        }
    }

    private static class StubbedClassLoaderDexCompilation implements AndroidClassLoadingStrategy.DexProcessor {
        public Conversion create() {
            return new AndroidClassLoadingStrategy.DexProcessor.ForSdkCompiler(new com.android.dx.dex.DexOptions(), new com.android.dx.dex.cf.CfOptions()).create();
        }
    }

    /* empty */
    private static class Foo {}

    /* empty */
    private static class Bar {}
}

