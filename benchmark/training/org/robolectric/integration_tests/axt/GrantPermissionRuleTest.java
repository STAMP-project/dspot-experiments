package org.robolectric.integration_tests.axt;


import PackageManager.PERMISSION_DENIED;
import PackageManager.PERMISSION_GRANTED;
import android.Manifest.permission.READ_CONTACTS;
import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import permission.WRITE_CONTACTS;


/**
 * Integration tests for {@link androidx.test.rule.GrantPermissionRule} that verify it behaves
 * consistently on device and Robolectric.
 */
@RunWith(AndroidJUnit4.class)
public class GrantPermissionRuleTest {
    @Rule
    public final GrantPermissionRule rule = GrantPermissionRule.grant(READ_CONTACTS);

    @Test
    public void some_test_with_permissions() {
        Context context = getApplicationContext();
        assertThat(context.checkPermission(permission.READ_CONTACTS, myPid(), myUid())).isEqualTo(PERMISSION_GRANTED);
        assertThat(context.checkPermission(WRITE_CONTACTS, myPid(), myUid())).isEqualTo(PERMISSION_DENIED);
    }
}

