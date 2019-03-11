package jadx.core.dex.info;


import AccessFlags.ACC_PUBLIC;
import com.android.dx.rop.code.AccessFlags;
import jadx.core.dex.info.AccessInfo.AFType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AccessInfoTest {
    @Test
    public void changeVisibility() {
        AccessInfo accessInfo = new AccessInfo(((AccessFlags.ACC_PROTECTED) | (AccessFlags.ACC_STATIC)), AFType.METHOD);
        AccessInfo result = accessInfo.changeVisibility(ACC_PUBLIC);
        Assert.assertThat(result.isPublic(), Matchers.is(true));
        Assert.assertThat(result.isPrivate(), Matchers.is(false));
        Assert.assertThat(result.isProtected(), Matchers.is(false));
        Assert.assertThat(result.isStatic(), Matchers.is(true));
    }

    @Test
    public void changeVisibilityNoOp() {
        AccessInfo accessInfo = new AccessInfo(AccessFlags.ACC_PUBLIC, AFType.METHOD);
        AccessInfo result = accessInfo.changeVisibility(ACC_PUBLIC);
        Assert.assertSame(accessInfo, result);
    }
}

