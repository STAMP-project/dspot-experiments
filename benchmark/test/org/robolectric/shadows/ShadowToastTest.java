package org.robolectric.shadows;


import Gravity.CENTER;
import Toast.LENGTH_LONG;
import Toast.LENGTH_SHORT;
import android.app.Application;
import android.view.View;
import android.widget.Toast;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Shadows;

import static org.robolectric.R.string.hello;


@RunWith(AndroidJUnit4.class)
public class ShadowToastTest {
    private Application context;

    @Test
    public void shouldHaveShortDuration() throws Exception {
        Toast toast = Toast.makeText(context, "short toast", LENGTH_SHORT);
        assertThat(toast).isNotNull();
        assertThat(toast.getDuration()).isEqualTo(LENGTH_SHORT);
    }

    @Test
    public void shouldHaveLongDuration() throws Exception {
        Toast toast = Toast.makeText(context, "long toast", LENGTH_LONG);
        assertThat(toast).isNotNull();
        assertThat(toast.getDuration()).isEqualTo(LENGTH_LONG);
    }

    @Test
    public void shouldMakeTextCorrectly() throws Exception {
        Toast toast = Toast.makeText(context, "short toast", LENGTH_SHORT);
        assertThat(toast).isNotNull();
        assertThat(toast.getDuration()).isEqualTo(LENGTH_SHORT);
        toast.show();
        assertThat(ShadowToast.getLatestToast()).isSameAs(toast);
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("short toast");
        assertThat(ShadowToast.showedToast("short toast")).isTrue();
    }

    @Test
    public void shouldSetTextCorrectly() throws Exception {
        Toast toast = Toast.makeText(context, "short toast", LENGTH_SHORT);
        toast.setText("other toast");
        toast.show();
        assertThat(ShadowToast.getLatestToast()).isSameAs(toast);
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("other toast");
        assertThat(ShadowToast.showedToast("other toast")).isTrue();
    }

    @Test
    public void shouldSetTextWithIdCorrectly() throws Exception {
        Toast toast = Toast.makeText(context, "short toast", LENGTH_SHORT);
        toast.setText(hello);
        toast.show();
        assertThat(ShadowToast.getLatestToast()).isSameAs(toast);
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Hello");
        assertThat(ShadowToast.showedToast("Hello")).isTrue();
    }

    @Test
    public void shouldSetViewCorrectly() throws Exception {
        Toast toast = new Toast(context);
        toast.setDuration(LENGTH_SHORT);
        final View view = new android.widget.TextView(context);
        toast.setView(view);
        assertThat(toast.getView()).isSameAs(view);
    }

    @Test
    public void shouldSetGravityCorrectly() throws Exception {
        Toast toast = Toast.makeText(context, "short toast", LENGTH_SHORT);
        assertThat(toast).isNotNull();
        toast.setGravity(CENTER, 0, 0);
        assertThat(toast.getGravity()).isEqualTo(CENTER);
    }

    @Test
    public void shouldSetOffsetsCorrectly() throws Exception {
        Toast toast = Toast.makeText(context, "short toast", LENGTH_SHORT);
        toast.setGravity(0, 12, 34);
        assertThat(toast.getXOffset()).isEqualTo(12);
        assertThat(toast.getYOffset()).isEqualTo(34);
    }

    @Test
    public void shouldCountToastsCorrectly() throws Exception {
        assertThat(ShadowToast.shownToastCount()).isEqualTo(0);
        Toast toast = Toast.makeText(context, "short toast", LENGTH_SHORT);
        assertThat(toast).isNotNull();
        toast.show();
        toast.show();
        toast.show();
        assertThat(ShadowToast.shownToastCount()).isEqualTo(3);
        ShadowToast.reset();
        assertThat(ShadowToast.shownToastCount()).isEqualTo(0);
        toast.show();
        toast.show();
        assertThat(ShadowToast.shownToastCount()).isEqualTo(2);
    }

    @Test
    public void shouldBeCancelled() throws Exception {
        Toast toast = Toast.makeText(context, "short toast", LENGTH_SHORT);
        toast.cancel();
        ShadowToast shadowToast = Shadows.shadowOf(toast);
        assertThat(shadowToast.isCancelled()).isTrue();
    }
}

