package net.bytebuddy.dynamic;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class DynamicTypeDefaultLoadedTest {
    private static final Class<?> MAIN_TYPE = Void.class;

    private static final Class<?> AUXILIARY_TYPE = Object.class;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private LoadedTypeInitializer mainLoadedTypeInitializer;

    @Mock
    private LoadedTypeInitializer auxiliaryLoadedTypeInitializer;

    @Mock
    private TypeDescription mainTypeDescription;

    @Mock
    private TypeDescription auxiliaryTypeDescription;

    private DynamicType.Loaded<?> dynamicType;

    @Test
    public void testLoadedTypeDescription() throws Exception {
        MatcherAssert.assertThat(dynamicType.getLoaded(), CoreMatchers.<Class<?>>is(DynamicTypeDefaultLoadedTest.MAIN_TYPE));
        MatcherAssert.assertThat(dynamicType.getTypeDescription(), CoreMatchers.is(mainTypeDescription));
        MatcherAssert.assertThat(dynamicType.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(dynamicType.getLoadedAuxiliaryTypes().keySet(), CoreMatchers.hasItem(auxiliaryTypeDescription));
        MatcherAssert.assertThat(dynamicType.getLoadedAuxiliaryTypes().get(auxiliaryTypeDescription), CoreMatchers.<Class<?>>is(DynamicTypeDefaultLoadedTest.AUXILIARY_TYPE));
    }
}

