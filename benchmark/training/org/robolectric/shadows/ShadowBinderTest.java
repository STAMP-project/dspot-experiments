package org.robolectric.shadows;


import android.content.Context;
import android.os.Binder;
import android.os.Build.VERSION_CODES;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowBinderTest {
    private UserManager userManager;

    private Context context;

    @Test
    public void transactCallsOnTransact() throws Exception {
        ShadowBinderTest.TestBinder testBinder = new ShadowBinderTest.TestBinder();
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeString("Hello Robolectric");
        Assert.assertTrue(testBinder.transact(2, data, reply, 3));
        assertThat(testBinder.code).isEqualTo(2);
        assertThat(testBinder.data).isSameAs(data);
        assertThat(testBinder.reply).isSameAs(reply);
        assertThat(testBinder.flags).isEqualTo(3);
        reply.readException();
        assertThat(reply.readString()).isEqualTo("Hello Robolectric");
    }

    static class TestBinder extends Binder {
        int code;

        Parcel data;

        Parcel reply;

        int flags;

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            this.code = code;
            this.data = data;
            this.reply = reply;
            this.flags = flags;
            String string = data.readString();
            reply.writeNoException();
            reply.writeString(string);
            return true;
        }
    }

    @Test
    public void thrownExceptionIsParceled() throws Exception {
        ShadowBinderTest.TestThrowingBinder testThrowingBinder = new ShadowBinderTest.TestThrowingBinder();
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        testThrowingBinder.transact(2, data, reply, 3);
        try {
            reply.readException();
            Assert.fail();// Expect thrown

        } catch (SecurityException e) {
            assertThat(e.getMessage()).isEqualTo("Halt! Who goes there?");
        }
    }

    static class TestThrowingBinder extends Binder {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            throw new SecurityException("Halt! Who goes there?");
        }
    }

    @Test
    public void testSetCallingUid() {
        ShadowBinder.setCallingUid(37);
        assertThat(Binder.getCallingUid()).isEqualTo(37);
    }

    @Test
    public void testSetCallingPid() {
        ShadowBinder.setCallingPid(25);
        assertThat(Binder.getCallingPid()).isEqualTo(25);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testSetCallingUserHandle() {
        UserHandle newUser = Shadows.shadowOf(userManager).addUser(10, "secondary_user", 0);
        ShadowBinder.setCallingUserHandle(newUser);
        assertThat(Binder.getCallingUserHandle()).isEqualTo(newUser);
    }

    @Test
    public void testGetCallingUidShouldUseProcessUidByDefault() {
        assertThat(Binder.getCallingUid()).isEqualTo(Process.myUid());
    }

    @Test
    public void testGetCallingPidShouldUseProcessPidByDefault() {
        assertThat(Binder.getCallingPid()).isEqualTo(Process.myPid());
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testGetCallingUserHandleShouldUseThatOfProcessByDefault() {
        assertThat(Binder.getCallingUserHandle()).isEqualTo(Process.myUserHandle());
    }

    @Test
    public void testResetUpdatesCallingUidAndPid() {
        ShadowBinder.setCallingPid(48);
        ShadowBinder.setCallingUid(49);
        ShadowBinder.reset();
        assertThat(Binder.getCallingPid()).isEqualTo(Process.myPid());
        assertThat(Binder.getCallingUid()).isEqualTo(Process.myUid());
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void testResetUpdatesCallingUserHandle() {
        UserHandle newUser = Shadows.shadowOf(userManager).addUser(10, "secondary_user", 0);
        ShadowBinder.setCallingUserHandle(newUser);
        ShadowBinder.reset();
        assertThat(Binder.getCallingUserHandle()).isEqualTo(Process.myUserHandle());
    }
}

