package com.reactnativenavigation.viewcontrollers.overlay;


import android.widget.FrameLayout;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.presentation.OverlayManager;
import com.reactnativenavigation.utils.CommandListener;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OverlayManagerTest extends BaseTest {
    private static final String OVERLAY_ID_1 = "OVERLAY_1";

    private static final String OVERLAY_ID_2 = "OVERLAY_2";

    private OverlayManager uut;

    private SimpleViewController overlay1;

    private SimpleViewController overlay2;

    private FrameLayout root;

    @Test
    public void show() {
        CommandListenerAdapter listener = Mockito.spy(new CommandListenerAdapter());
        uut.show(root, overlay1, listener);
        Mockito.verify(listener, Mockito.times(1)).onSuccess(OverlayManagerTest.OVERLAY_ID_1);
        assertThat(getView().getParent()).isEqualTo(root);
    }

    @Test
    public void dismiss() {
        uut.show(root, overlay1, new CommandListenerAdapter());
        assertThat(uut.size()).isOne();
        CommandListener listener = Mockito.spy(new CommandListenerAdapter());
        uut.dismiss(getId(), listener);
        assertThat(uut.size()).isZero();
        Mockito.verify(listener, Mockito.times(1)).onSuccess(OverlayManagerTest.OVERLAY_ID_1);
        Mockito.verify(overlay1, Mockito.times(1)).destroy();
    }

    @Test
    public void dismiss_rejectIfOverlayNotFound() {
        CommandListener listener = Mockito.spy(new CommandListenerAdapter());
        uut.dismiss(getId(), listener);
        Mockito.verify(listener, Mockito.times(1)).onError(ArgumentMatchers.any());
    }

    @Test
    public void dismiss_onViewReturnedToFront() {
        uut.show(root, overlay1, new CommandListenerAdapter());
        uut.show(root, overlay2, new CommandListenerAdapter());
        onViewBroughtToFront();
        uut.dismiss(OverlayManagerTest.OVERLAY_ID_2, new CommandListenerAdapter());
        onViewBroughtToFront();
    }
}

