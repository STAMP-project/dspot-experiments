package samples.junit4.suppressfield;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.suppressfield.SuppressField;


/**
 * Unit tests that asserts that field suppression works.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressField.class)
public class SuppressFieldTest {
    @Test
    public void assertThatSpecificStaticFinalFieldSuppressionWorks() throws Exception {
        MemberModifier.suppress(MemberMatcher.field(SuppressField.class, "MY_OBJECT"));
        Assert.assertNull(SuppressField.getMyObject());
    }

    @Test
    public void assertThatSpecificInstanceFinalFieldSuppressionWorks() throws Exception {
        MemberModifier.suppress(MemberMatcher.field(SuppressField.class, "myWrappedBoolean"));
        SuppressField suppressField = new SuppressField();
        Assert.assertNull(suppressField.getMyWrappedBoolean());
    }

    @Test
    public void assertThatSpecificPrimitiveInstanceFieldSuppressionWorks() throws Exception {
        MemberModifier.suppress(MemberMatcher.field(SuppressField.class, "myChar"));
        SuppressField suppressField = new SuppressField();
        Assert.assertEquals(' ', suppressField.getMyChar());
    }

    @Test
    public void assertThatSpecificInstanceFieldSuppressionWorks() throws Exception {
        MemberModifier.suppress(MemberMatcher.field(SuppressField.class, "mySecondValue"));
        SuppressField suppressField = new SuppressField();
        Assert.assertNull(suppressField.getMySecondValue());
    }

    @Test
    public void assertThatSpecificInstanceFieldSuppressionWhenSpecifingClassAndFieldNameWorks() throws Exception {
        MemberModifier.suppress(MemberMatcher.field(SuppressField.class, "mySecondValue"));
        SuppressField suppressField = new SuppressField();
        Assert.assertNull(suppressField.getMySecondValue());
    }

    @Test
    public void assertThatMultipleInstanceFieldSuppressionWorks() throws Exception {
        MemberModifier.suppress(MemberMatcher.fields(SuppressField.class, "mySecondValue", "myChar"));
        SuppressField suppressField = new SuppressField();
        Assert.assertNull(suppressField.getMySecondValue());
        Assert.assertEquals(' ', suppressField.getMyChar());
        Assert.assertEquals(Boolean.TRUE, suppressField.getMyWrappedBoolean());
    }

    // TODO Add final tests here as well when they work
    @Test
    public void assertThatAllFieldSuppressionWorks() throws Exception {
        MemberModifier.suppress(MemberMatcher.fields(SuppressField.class));
        SuppressField suppressField = new SuppressField();
        Assert.assertNull(suppressField.getMySecondValue());
        Assert.assertEquals(' ', suppressField.getMyChar());
        Assert.assertNull(suppressField.getMyWrappedBoolean());
        Assert.assertNull(SuppressField.getMyObject());
    }

    @Test
    public void assertThatObjectIsNeverInstansiated() throws Exception {
        MemberModifier.suppress(MemberMatcher.field(SuppressField.class, "domainObject"));
        SuppressField suppressField = new SuppressField();
        Assert.assertNull(suppressField.getDomainObject());
    }
}

