package samples.powermockito.junit4.enums;


import EnumWithConstructor.SOME_ENUM_VALUE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.enummocking.EnumWithConstructor;
import samples.enummocking.SomeObjectInterfaceImpl;


/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { EnumWithConstructor.class, SomeObjectInterfaceImpl.class }, fullyQualifiedNames = "samples.enummocking.EnumWithConstructor$1")
public class EnumWithConstructorTest {
    @Mock(name = "expectedSomeObjectInterfaceImpl")
    private SomeObjectInterfaceImpl someImplMock;

    @Test
    public void testCallMethodWithConstructor() throws Exception {
        whenNew(SomeObjectInterfaceImpl.class).withNoArguments().thenReturn(someImplMock);
        SomeObjectInterfaceImpl actual = ((SomeObjectInterfaceImpl) (SOME_ENUM_VALUE.create()));
        Assert.assertThat(actual, CoreMatchers.is(CoreMatchers.sameInstance(someImplMock)));
    }
}

