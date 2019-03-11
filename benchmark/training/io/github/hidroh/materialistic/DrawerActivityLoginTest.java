package io.github.hidroh.materialistic;


import DialogInterface.BUTTON_NEGATIVE;
import DialogInterface.BUTTON_NEUTRAL;
import DialogInterface.BUTTON_POSITIVE;
import R.id.drawer;
import R.id.drawer_layout;
import R.id.drawer_more;
import R.id.drawer_more_container;
import R.string.login;
import UserActivity.EXTRA_USERNAME;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import io.github.hidroh.materialistic.test.TestListActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowSupportDrawerLayout;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowAlertDialog;

import static BuildConfig.APPLICATION_ID;


@SuppressWarnings("ConstantConditions")
@Config(shadows = { ShadowSupportDrawerLayout.class })
@RunWith(TestRunner.class)
public class DrawerActivityLoginTest {
    private ActivityController<TestListActivity> controller;

    private TestListActivity activity;

    private TextView drawerAccount;

    private View drawerLogout;

    private View drawerUser;

    @Test
    public void testNoExistingAccount() {
        assertThat(drawerAccount).hasText(login);
        assertThat(drawerLogout).isNotVisible();
        assertThat(drawerUser).isNotVisible();
        Preferences.setUsername(activity, "username");
        assertThat(drawerAccount).hasText("username");
        assertThat(drawerLogout).isVisible();
        assertThat(drawerUser).isVisible();
        drawerLogout.performClick();
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        alertDialog.getButton(BUTTON_POSITIVE).performClick();
        assertThat(drawerAccount).hasText(login);
        assertThat(drawerLogout).isNotVisible();
    }

    @Test
    public void testOpenUserProfile() {
        Preferences.setUsername(activity, "username");
        drawerUser.performClick();
        ((ShadowSupportDrawerLayout) (Shadow.extract(activity.findViewById(drawer_layout)))).getDrawerListeners().get(0).onDrawerClosed(activity.findViewById(drawer));
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, UserActivity.class).hasExtra(EXTRA_USERNAME, "username");
    }

    @Test
    public void testExistingAccount() {
        AccountManager.get(activity).addAccountExplicitly(new android.accounts.Account("existing", APPLICATION_ID), "password", null);
        drawerAccount.performClick();
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        assertThat(alertDialog.getListView().getAdapter()).hasCount(1);
        Shadows.shadowOf(alertDialog).clickOnItem(0);
        alertDialog.getButton(BUTTON_POSITIVE).performClick();
        assertThat(alertDialog).isNotShowing();
        assertThat(drawerAccount).hasText("existing");
        assertThat(drawerLogout).isVisible();
        drawerAccount.performClick();
        alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alertDialog.getListView().getAdapter()).hasCount(1);
    }

    @Test
    public void testAddAccount() {
        AccountManager.get(activity).addAccountExplicitly(new android.accounts.Account("existing", APPLICATION_ID), "password", null);
        drawerAccount.performClick();
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        assertThat(alertDialog.getListView().getAdapter()).hasCount(1);
        alertDialog.getButton(BUTTON_NEGATIVE).performClick();
        assertThat(alertDialog).isNotShowing();
        ((ShadowSupportDrawerLayout) (Shadow.extract(activity.findViewById(drawer_layout)))).getDrawerListeners().get(0).onDrawerClosed(activity.findViewById(drawer));
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, LoginActivity.class);
    }

    @Config(sdk = 21)
    @Test
    public void testRemoveAccount() {
        AccountManager.get(activity).addAccountExplicitly(new android.accounts.Account("existing", APPLICATION_ID), "password", null);
        Preferences.setUsername(activity, "existing");
        drawerAccount.performClick();
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        Assert.assertNotNull(alertDialog);
        assertThat(alertDialog.getListView().getAdapter()).hasCount(1);
        alertDialog.getButton(BUTTON_NEUTRAL).performClick();
        assertThat(alertDialog).isNotShowing();
        assertThat(AccountManager.get(activity).getAccounts()).isEmpty();
    }

    @Test
    public void testMoreToggle() {
        activity.findViewById(drawer_more).performClick();
        assertThat(((View) (activity.findViewById(drawer_more_container)))).isVisible();
        activity.findViewById(drawer_more).performClick();
        assertThat(((View) (activity.findViewById(drawer_more_container)))).isNotVisible();
    }
}

