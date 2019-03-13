package org.robolectric.shadows;


import android.content.ClipData;
import android.content.ClipboardManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(AndroidJUnit4.class)
public class ShadowClipboardManagerTest {
    private ClipboardManager clipboardManager;

    @Test
    public void shouldStoreText() throws Exception {
        clipboardManager.setText("BLARG!!!");
        assertThat(clipboardManager.getText().toString()).isEqualTo("BLARG!!!");
    }

    @Test
    public void shouldNotHaveTextIfTextIsNull() throws Exception {
        clipboardManager.setText(null);
        assertThat(clipboardManager.hasText()).isFalse();
    }

    @Test
    public void shouldNotHaveTextIfTextIsEmpty() throws Exception {
        clipboardManager.setText("");
        assertThat(clipboardManager.hasText()).isFalse();
    }

    @Test
    public void shouldHaveTextIfEmptyString() throws Exception {
        clipboardManager.setText(" ");
        assertThat(clipboardManager.hasText()).isTrue();
    }

    @Test
    public void shouldHaveTextIfString() throws Exception {
        clipboardManager.setText("BLARG");
        assertThat(clipboardManager.hasText()).isTrue();
    }

    @Test
    public void shouldStorePrimaryClip() {
        ClipData clip = ClipData.newPlainText(null, "BLARG?");
        clipboardManager.setPrimaryClip(clip);
        assertThat(clipboardManager.getPrimaryClip()).isEqualTo(clip);
    }

    @Test
    public void shouldNotHaveTextIfPrimaryClipIsNull() throws Exception {
        clipboardManager.setPrimaryClip(null);
        assertThat(clipboardManager.hasText()).isFalse();
    }

    @Test
    public void shouldNotHaveTextIfPrimaryClipIsEmpty() throws Exception {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ""));
        assertThat(clipboardManager.hasText()).isFalse();
    }

    @Test
    public void shouldHaveTextIfEmptyPrimaryClip() throws Exception {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, " "));
        assertThat(clipboardManager.hasText()).isTrue();
    }

    @Test
    public void shouldHaveTextIfPrimaryClip() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, "BLARG?"));
        assertThat(clipboardManager.hasText()).isTrue();
    }

    @Test
    public void shouldHavePrimaryClipIfText() {
        clipboardManager.setText("BLARG?");
        assertThat(clipboardManager.hasPrimaryClip()).isTrue();
    }

    @Test
    public void shouldFireListeners() {
        OnPrimaryClipChangedListener listener = Mockito.mock(OnPrimaryClipChangedListener.class);
        clipboardManager.addPrimaryClipChangedListener(listener);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, "BLARG?"));
        Mockito.verify(listener).onPrimaryClipChanged();
        clipboardManager.removePrimaryClipChangedListener(listener);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, "BLARG?"));
        Mockito.verifyNoMoreInteractions(listener);
    }
}

