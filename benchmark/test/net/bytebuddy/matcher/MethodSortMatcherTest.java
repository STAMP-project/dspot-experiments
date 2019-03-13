package net.bytebuddy.matcher;


import net.bytebuddy.description.method.MethodDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class MethodSortMatcherTest extends AbstractElementMatcherTest<MethodSortMatcher<?>> {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private final MethodSortMatcher.Sort sort;

    private final MethodSortMatcherTest.MockImplementation mockImplementation;

    @Mock
    private MethodDescription methodDescription;

    @SuppressWarnings("unchecked")
    public MethodSortMatcherTest(MethodSortMatcher.Sort sort, MethodSortMatcherTest.MockImplementation mockImplementation) {
        super(((Class<MethodSortMatcher<?>>) ((Object) (MethodSortMatcher.class))), sort.getDescription());
        this.sort = sort;
        this.mockImplementation = mockImplementation;
    }

    @Test
    public void testMatch() throws Exception {
        mockImplementation.prepare(methodDescription);
        MatcherAssert.assertThat(new MethodSortMatcher<MethodDescription>(sort).matches(methodDescription), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatch() throws Exception {
        MatcherAssert.assertThat(new MethodSortMatcher<MethodDescription>(sort).matches(methodDescription), CoreMatchers.is(false));
    }

    @Test
    public void testStringRepresentation() throws Exception {
        MatcherAssert.assertThat(new MethodSortMatcher<MethodDescription>(sort).toString(), CoreMatchers.is(sort.getDescription()));
    }

    private enum MockImplementation {

        CONSTRUCTOR() {
            protected void prepare(MethodDescription target) {
                Mockito.when(target.isConstructor()).thenReturn(true);
            }
        },
        DEFAULT_METHOD() {
            protected void prepare(MethodDescription target) {
                Mockito.when(target.isDefaultMethod()).thenReturn(true);
            }
        },
        METHOD() {
            protected void prepare(MethodDescription target) {
                Mockito.when(target.isMethod()).thenReturn(true);
            }
        },
        VIRTUAL() {
            protected void prepare(MethodDescription target) {
                Mockito.when(target.isVirtual()).thenReturn(true);
            }
        },
        TYPE_INITIALIZER() {
            protected void prepare(MethodDescription target) {
                Mockito.when(target.isTypeInitializer()).thenReturn(true);
            }
        };
        protected abstract void prepare(MethodDescription target);
    }
}

