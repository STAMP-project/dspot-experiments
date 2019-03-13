package samples.junit4.annotationbased;


import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.injectmocks.InjectDemo;
import samples.injectmocks.InjectDependencyHolder;
import samples.injectmocks.InjectDependencyHolderQualifier;


/**
 * Asserts that {@link @TestSubject} with PowerMock and mock witch created via @org.easymock.Mock.
 */
@RunWith(PowerMockRunner.class)
public class TestSubjectEasymockAnnotationTest {
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
    public void injectMocksWorksWithEasymock() {
        Assert.assertNotNull("dependencyHolder is null", dependencyHolder.getInjectDemo());
    }

    @Test
    public void injectMocksWorksWithEasymockQualifier() {
        Assert.assertNotNull("dependencyHolder is null", dependencyHolderQualifier.getInjectDemoQualifier());
    }
}

