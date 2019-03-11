package org.robolectric.shadows;


import Build.VERSION_CODES;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;


/**
 * Unit tests for ShadowShortcutManager.
 */
@Config(minSdk = VERSION_CODES.N_MR1)
@RunWith(AndroidJUnit4.class)
public final class ShadowShortcutManagerTest {
    private ShortcutManager shortcutManager;

    @Test
    public void testDynamicShortcuts_twoAdded() throws Exception {
        shortcutManager.addDynamicShortcuts(ImmutableList.of(ShadowShortcutManagerTest.createShortcut("id1"), ShadowShortcutManagerTest.createShortcut("id2")));
        assertThat(shortcutManager.getDynamicShortcuts()).hasSize(2);
    }

    @Test
    public void testDynamicShortcuts_duplicateGetsDeduped() throws Exception {
        shortcutManager.addDynamicShortcuts(ImmutableList.of(ShadowShortcutManagerTest.createShortcut("id1"), ShadowShortcutManagerTest.createShortcut("id1")));
        assertThat(shortcutManager.getDynamicShortcuts()).hasSize(1);
    }

    @Test
    public void testDynamicShortcuts_immutableShortcutDoesntGetUpdated() throws Exception {
        ShortcutInfo shortcut1 = /* isImmutable */
        ShadowShortcutManagerTest.createShortcut("id1", true);
        Mockito.when(shortcut1.getLongLabel()).thenReturn("original");
        ShortcutInfo shortcut2 = /* isImmutable */
        ShadowShortcutManagerTest.createShortcut("id1", true);
        Mockito.when(shortcut2.getLongLabel()).thenReturn("updated");
        shortcutManager.addDynamicShortcuts(ImmutableList.of(shortcut1));
        assertThat(shortcutManager.getDynamicShortcuts()).hasSize(1);
        shortcutManager.addDynamicShortcuts(ImmutableList.of(shortcut2));
        assertThat(shortcutManager.getDynamicShortcuts()).hasSize(1);
        assertThat(shortcutManager.getDynamicShortcuts().get(0).getLongLabel()).isEqualTo("original");
    }

    @Test
    public void testShortcutWithIdenticalIdGetsUpdated() throws Exception {
        ShortcutInfo shortcut1 = ShadowShortcutManagerTest.createShortcut("id1");
        Mockito.when(shortcut1.getLongLabel()).thenReturn("original");
        ShortcutInfo shortcut2 = ShadowShortcutManagerTest.createShortcut("id1");
        Mockito.when(shortcut2.getLongLabel()).thenReturn("updated");
        shortcutManager.addDynamicShortcuts(ImmutableList.of(shortcut1));
        assertThat(shortcutManager.getDynamicShortcuts()).hasSize(1);
        shortcutManager.addDynamicShortcuts(ImmutableList.of(shortcut2));
        assertThat(shortcutManager.getDynamicShortcuts()).hasSize(1);
        assertThat(shortcutManager.getDynamicShortcuts().get(0).getLongLabel()).isEqualTo("updated");
    }

    @Test
    public void testRemoveAllDynamicShortcuts() throws Exception {
        shortcutManager.addDynamicShortcuts(ImmutableList.of(ShadowShortcutManagerTest.createShortcut("id1"), ShadowShortcutManagerTest.createShortcut("id2")));
        assertThat(shortcutManager.getDynamicShortcuts()).hasSize(2);
        shortcutManager.removeAllDynamicShortcuts();
        assertThat(shortcutManager.getDynamicShortcuts()).isEmpty();
    }

    @Test
    public void testRemoveDynamicShortcuts() throws Exception {
        ShortcutInfo shortcut1 = ShadowShortcutManagerTest.createShortcut("id1");
        ShortcutInfo shortcut2 = ShadowShortcutManagerTest.createShortcut("id2");
        shortcutManager.addDynamicShortcuts(ImmutableList.of(shortcut1, shortcut2));
        assertThat(shortcutManager.getDynamicShortcuts()).hasSize(2);
        shortcutManager.removeDynamicShortcuts(ImmutableList.of("id1"));
        assertThat(shortcutManager.getDynamicShortcuts()).containsExactly(shortcut2);
    }

    @Test
    public void testSetDynamicShortcutsClearOutOldList() throws Exception {
        ShortcutInfo shortcut1 = ShadowShortcutManagerTest.createShortcut("id1");
        ShortcutInfo shortcut2 = ShadowShortcutManagerTest.createShortcut("id2");
        ShortcutInfo shortcut3 = ShadowShortcutManagerTest.createShortcut("id3");
        ShortcutInfo shortcut4 = ShadowShortcutManagerTest.createShortcut("id4");
        shortcutManager.addDynamicShortcuts(ImmutableList.of(shortcut1, shortcut2));
        assertThat(shortcutManager.getDynamicShortcuts()).containsExactly(shortcut1, shortcut2);
        shortcutManager.setDynamicShortcuts(ImmutableList.of(shortcut3, shortcut4));
        assertThat(shortcutManager.getDynamicShortcuts()).containsExactly(shortcut3, shortcut4);
    }

    @Test
    public void testUpdateShortcut_dynamic() throws Exception {
        ShortcutInfo shortcut1 = ShadowShortcutManagerTest.createShortcut("id1");
        Mockito.when(shortcut1.getLongLabel()).thenReturn("original");
        ShortcutInfo shortcutUpdated = ShadowShortcutManagerTest.createShortcut("id1");
        Mockito.when(shortcutUpdated.getLongLabel()).thenReturn("updated");
        shortcutManager.addDynamicShortcuts(ImmutableList.of(shortcut1));
        assertThat(shortcutManager.getDynamicShortcuts()).containsExactly(shortcut1);
        shortcutManager.updateShortcuts(ImmutableList.of(shortcutUpdated));
        assertThat(shortcutManager.getDynamicShortcuts()).containsExactly(shortcutUpdated);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void testUpdateShortcut_pinned() throws Exception {
        ShortcutInfo shortcut1 = ShadowShortcutManagerTest.createShortcut("id1");
        Mockito.when(shortcut1.getLongLabel()).thenReturn("original");
        ShortcutInfo shortcutUpdated = ShadowShortcutManagerTest.createShortcut("id1");
        Mockito.when(shortcutUpdated.getLongLabel()).thenReturn("updated");
        /* resultIntent */
        shortcutManager.requestPinShortcut(shortcut1, null);
        assertThat(shortcutManager.getPinnedShortcuts()).containsExactly(shortcut1);
        shortcutManager.updateShortcuts(ImmutableList.of(shortcutUpdated));
        assertThat(shortcutManager.getPinnedShortcuts()).containsExactly(shortcutUpdated);
    }

    @Test
    public void testUpdateShortcutsOnlyUpdatesExistingShortcuts() throws Exception {
        ShortcutInfo shortcut1 = ShadowShortcutManagerTest.createShortcut("id1");
        Mockito.when(shortcut1.getLongLabel()).thenReturn("original");
        ShortcutInfo shortcutUpdated = ShadowShortcutManagerTest.createShortcut("id1");
        Mockito.when(shortcutUpdated.getLongLabel()).thenReturn("updated");
        ShortcutInfo shortcut2 = ShadowShortcutManagerTest.createShortcut("id2");
        shortcutManager.addDynamicShortcuts(ImmutableList.of(shortcut1));
        assertThat(shortcutManager.getDynamicShortcuts()).containsExactly(shortcut1);
        shortcutManager.updateShortcuts(ImmutableList.of(shortcutUpdated, shortcut2));
        assertThat(shortcutManager.getDynamicShortcuts()).containsExactly(shortcutUpdated);
        assertThat(shortcutManager.getDynamicShortcuts().get(0).getLongLabel()).isEqualTo("updated");
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void testPinningExistingDynamicShortcut() throws Exception {
        ShortcutInfo shortcut1 = ShadowShortcutManagerTest.createShortcut("id1");
        ShortcutInfo shortcut2 = ShadowShortcutManagerTest.createShortcut("id2");
        shortcutManager.addDynamicShortcuts(ImmutableList.of(shortcut1, shortcut2));
        assertThat(shortcutManager.getDynamicShortcuts()).hasSize(2);
        /* resultIntent */
        shortcutManager.requestPinShortcut(shortcut1, null);
        assertThat(shortcutManager.getDynamicShortcuts()).containsExactly(shortcut2);
        assertThat(shortcutManager.getPinnedShortcuts()).containsExactly(shortcut1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void testPinningNewShortcut() throws Exception {
        ShortcutInfo shortcut1 = ShadowShortcutManagerTest.createShortcut("id1");
        /* resultIntent */
        shortcutManager.requestPinShortcut(shortcut1, null);
        assertThat(shortcutManager.getPinnedShortcuts()).containsExactly(shortcut1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void testSetMaxShortcutCountPerActivity() {
        ShadowShortcutManager shadowShortcutManager = Shadow.extract(shortcutManager);
        shadowShortcutManager.setMaxShortcutCountPerActivity(42);
        assertThat(shortcutManager.getMaxShortcutCountPerActivity()).isEqualTo(42);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void testSetManifestShortcuts() {
        ImmutableList<ShortcutInfo> manifestShortcuts = ImmutableList.of(ShadowShortcutManagerTest.createShortcut("id1"));
        ShadowShortcutManager shadowShortcutManager = Shadow.extract(shortcutManager);
        shadowShortcutManager.setManifestShortcuts(manifestShortcuts);
        assertThat(shortcutManager.getManifestShortcuts()).isEqualTo(manifestShortcuts);
    }
}

