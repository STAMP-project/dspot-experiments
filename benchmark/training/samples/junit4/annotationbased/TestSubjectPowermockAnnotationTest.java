package samples.junit4.annotationbased;


import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import samples.injectmocks.InjectDemo;
import samples.injectmocks.InjectDependencyHolder;
import samples.injectmocks.InjectDependencyHolderQualifier;


/**
 * Asserts that {@link @TestSubject} with PowerMock and mock witch created via @org.powermock.api.easymock.annotation.Mock.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(InjectDemo.class)
public class TestSubjectPowermockAnnotationTest {
    @SuppressWarnings("unused")
    @Mock
    private InjectDemo injectDemoEasymock;

    @TestSubject
    private final InjectDependencyHolder dependencyHolder = new InjectDependencyHolder();

    @SuppressWarnings("unused")
    @Mock(fieldName = "injectDemoQualifier")
    private InjectDemo injectDemoQualifierEasymock;

    @TestSubject
    private final InjectDependencyHolderQualifier dependencyHolderQualifier = new InjectDependencyHolderQualifier();

    @Test
    public void should_inject_mock_without_quantifier() throws Exception {
        final InjectDemo tested = dependencyHolder.getInjectDemo();
        Assert.assertNotNull("dependencyHolder is null", tested);
        String expected = "Hello altered World";
        PowerMock.expectPrivate(tested, "say", "hello").andReturn("Hello altered World");
        PowerMock.replay(tested);
        String actual = Whitebox.invokeMethod(tested, "say", "hello");
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void should_inject_mock_with_quantifier() throws Exception {
        InjectDemo tested = dependencyHolderQualifier.getInjectDemoQualifier();
        Assert.assertNotNull("dependencyHolderQualifier is null", tested);
        String expected = "Hello altered World";
        PowerMock.expectPrivate(tested, "say", "hello").andReturn("Hello altered World");
        PowerMock.replay(tested);
        String actual = Whitebox.invokeMethod(tested, "say", "hello");
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }
}

