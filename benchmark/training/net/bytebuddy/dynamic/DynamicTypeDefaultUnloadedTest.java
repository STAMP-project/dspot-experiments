package net.bytebuddy.dynamic;


import java.util.Collections;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DynamicTypeDefaultUnloadedTest {
    private static final Class<?> MAIN_TYPE = Void.class;

    private static final Class<?> AUXILIARY_TYPE = Object.class;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private LoadedTypeInitializer mainLoadedTypeInitializer;

    @Mock
    private LoadedTypeInitializer auxiliaryLoadedTypeInitializer;

    @Mock
    private DynamicType auxiliaryType;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private ClassLoadingStrategy<ClassLoader> classLoadingStrategy;

    @Mock
    private TypeResolutionStrategy.Resolved typeResolutionStrategy;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription auxiliaryTypeDescription;

    private byte[] binaryRepresentation;

    private byte[] auxiliaryTypeByte;

    private DynamicType.Unloaded<?> unloaded;

    @Test
    public void testQueries() throws Exception {
        DynamicType.Loaded<?> loaded = unloaded.load(classLoader, classLoadingStrategy);
        MatcherAssert.assertThat(loaded.getTypeDescription(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(loaded.getBytes(), CoreMatchers.is(binaryRepresentation));
        MatcherAssert.assertThat(loaded.getAuxiliaryTypes(), CoreMatchers.is(Collections.singletonMap(auxiliaryTypeDescription, auxiliaryTypeByte)));
    }

    @Test
    public void testTypeLoading() throws Exception {
        DynamicType.Loaded<?> loaded = unloaded.load(classLoader, classLoadingStrategy);
        MatcherAssert.assertThat(loaded.getLoaded(), CoreMatchers.<Class<?>>is(DynamicTypeDefaultUnloadedTest.MAIN_TYPE));
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().get(auxiliaryTypeDescription), CoreMatchers.<Class<?>>is(DynamicTypeDefaultUnloadedTest.AUXILIARY_TYPE));
        Mockito.verify(typeResolutionStrategy).initialize(unloaded, classLoader, classLoadingStrategy);
        Mockito.verifyNoMoreInteractions(typeResolutionStrategy);
    }

    @Test
    public void testTypeInclusion() throws Exception {
        DynamicType additionalType = Mockito.mock(DynamicType.class);
        TypeDescription additionalTypeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(additionalType.getTypeDescription()).thenReturn(additionalTypeDescription);
        DynamicType.Unloaded<?> dynamicType = unloaded.include(additionalType);
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().containsKey(additionalTypeDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().containsKey(auxiliaryTypeDescription), CoreMatchers.is(true));
    }
}

