package com.bumptech.glide.signature;


import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.tests.KeyTester;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ApplicationVersionSignatureTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    private Context context;

    @Test
    public void testCanGetKeyForSignature() {
        Key key = ApplicationVersionSignature.obtain(context);
        Assert.assertNotNull(key);
    }

    @Test
    public void testKeyForSignatureIsTheSameAcrossCallsInTheSamePackage() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        keyTester.addEquivalenceGroup(ApplicationVersionSignature.obtain(context), ApplicationVersionSignature.obtain(context)).addEquivalenceGroup(new ObjectKey("test")).addRegressionTest(ApplicationVersionSignature.obtain(context), "5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9").test();
    }

    @Test
    public void testUnresolvablePackageInfo() throws NameNotFoundException {
        Context context = Mockito.mock(Context.class, Answers.RETURNS_DEEP_STUBS.get());
        String packageName = "my.package";
        Mockito.when(context.getPackageName()).thenReturn(packageName);
        Mockito.when(context.getPackageManager().getPackageInfo(packageName, 0)).thenThrow(new NameNotFoundException("test"));
        Key key = ApplicationVersionSignature.obtain(context);
        Assert.assertNotNull(key);
    }

    @Test
    public void testMissingPackageInfo() throws NameNotFoundException {
        Context context = Mockito.mock(Context.class, Answers.RETURNS_DEEP_STUBS.get());
        String packageName = "my.package";
        Mockito.when(context.getPackageName()).thenReturn(packageName);
        Mockito.when(context.getPackageManager().getPackageInfo(packageName, 0)).thenReturn(null);
        Key key = ApplicationVersionSignature.obtain(context);
        Assert.assertNotNull(key);
    }
}

