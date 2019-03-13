package com.reactnativenavigation.viewcontrollers.modal;


import android.widget.FrameLayout;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.anim.ModalAnimator;
import com.reactnativenavigation.parse.AnimationOptions;
import com.reactnativenavigation.parse.ModalPresentationStyle;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.utils.CommandListener;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.viewcontrollers.ChildController;
import com.reactnativenavigation.viewcontrollers.ViewController;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ModalPresenterTest extends BaseTest {
    private static final String MODAL_ID_1 = "modalId1";

    private static final String MODAL_ID_2 = "modalId2";

    private ChildController modal1;

    private ChildController modal2;

    private ModalPresenter uut;

    private ModalAnimator animator;

    private ViewController root;

    private FrameLayout modalsLayout;

    @Test
    public void showModal() {
        Options defaultOptions = new Options();
        uut.setDefaultOptions(defaultOptions);
        disableShowModalAnimation(modal1);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        Mockito.verify(modal1).setWaitForRender(ArgumentMatchers.any());
        Mockito.verify(modal1).resolveCurrentOptions(defaultOptions);
    }

    @Test
    public void showModal_noAnimation() {
        disableShowModalAnimation(modal1);
        CommandListener listener = Mockito.spy(new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(modal1.getView().getParent()).isEqualTo(modalsLayout);
                Mockito.verify(modal1, Mockito.times(1)).onViewAppeared();
            }
        });
        uut.showModal(modal1, root, listener);
        Mockito.verify(animator, Mockito.times(0)).show(ArgumentMatchers.eq(modal1.getView()), ArgumentMatchers.eq(modal1.options.animations.showModal), ArgumentMatchers.any());
        Mockito.verify(listener, Mockito.times(1)).onSuccess(ModalPresenterTest.MODAL_ID_1);
    }

    @Test
    public void showModal_resolvesDefaultOptions() throws JSONException {
        Options defaultOptions = new Options();
        JSONObject disabledShowModalAnimation = new JSONObject().put("enabled", false);
        defaultOptions.animations.showModal = AnimationOptions.parse(disabledShowModalAnimation);
        uut.setDefaultOptions(defaultOptions);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        Mockito.verifyZeroInteractions(animator);
    }

    @Test
    public void showModal_previousModalIsRemovedFromHierarchy() {
        uut.showModal(modal1, null, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.showModal(modal2, modal1, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        assertThat(modal1.getView().getParent()).isNull();
                        Mockito.verify(modal1, Mockito.times(1)).onViewDisappear();
                    }
                });
                assertThat(modal1.getView().getParent()).isEqualTo(modal2.getView().getParent());
            }
        });
    }

    @Test
    public void showModal_animatesByDefault() {
        uut.showModal(modal1, null, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                Mockito.verify(animator, Mockito.times(1)).show(ArgumentMatchers.eq(modal1.getView()), ArgumentMatchers.eq(modal1.options.animations.showModal), ArgumentMatchers.any());
                assertThat(animator.isRunning()).isFalse();
            }
        });
    }

    @Test
    public void showModal_waitForRender() {
        modal1.options.animations.showModal.waitForRender = new Bool(true);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        Mockito.verify(modal1).addOnAppearedListener(ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(animator);
    }

    @Test
    public void showModal_rejectIfContentIsNull() {
        uut.setModalsLayout(null);
        CommandListenerAdapter listener = Mockito.mock(CommandListenerAdapter.class);
        uut.showModal(modal1, modal2, listener);
        Mockito.verify(listener).onError(ArgumentMatchers.any());
    }

    @Test
    public void dismissModal_animatesByDefault() {
        disableShowModalAnimation(modal1);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.dismissModal(modal1, root, root, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                Mockito.verify(modal1, Mockito.times(1)).onViewDisappear();
                Mockito.verify(modal1, Mockito.times(1)).destroy();
            }
        });
        Mockito.verify(animator).dismiss(ArgumentMatchers.eq(modal1.getView()), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void dismissModal_previousViewIsAddedAtIndex0() {
        disableShowModalAnimation(modal1);
        FrameLayout spy = Mockito.spy(new FrameLayout(newActivity()));
        uut.setRootLayout(spy);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.dismissModal(modal1, root, root, new CommandListenerAdapter());
        Mockito.verify(spy).addView(root.getView(), 0);
    }

    @Test
    public void dismissModal_noAnimation() {
        disableShowModalAnimation(modal1);
        disableDismissModalAnimation(modal1);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.dismissModal(modal1, root, root, new CommandListenerAdapter());
        Mockito.verify(modal1, Mockito.times(1)).onViewDisappear();
        Mockito.verify(modal1, Mockito.times(1)).destroy();
        Mockito.verify(animator, Mockito.times(0)).dismiss(ArgumentMatchers.any(), ArgumentMatchers.eq(modal1.options.animations.dismissModal), ArgumentMatchers.any());
    }

    @Test
    public void dismissModal_previousModalIsAddedBackToHierarchy() {
        disableShowModalAnimation(modal1, modal2);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        Mockito.verify(modal1).onViewAppeared();
        uut.showModal(modal2, modal1, new CommandListenerAdapter());
        assertThat(modal1.getView().getParent()).isNull();
        uut.dismissModal(modal2, modal1, root, new CommandListenerAdapter());
        assertThat(modal1.getView().getParent()).isNotNull();
        Mockito.verify(modal1, Mockito.times(2)).onViewAppeared();
    }

    @Test
    public void dismissModal_previousControllerIsNotAddedIfDismissedModalIsNotTop() {
        disableShowModalAnimation(modal1, modal2);
        disableDismissModalAnimation(modal1, modal2);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.showModal(modal2, modal1, new CommandListenerAdapter());
        assertThat(modal1.getView().getParent()).isNull();
        assertThat(root.getView().getParent()).isNull();
        uut.dismissModal(modal1, null, root, new CommandListenerAdapter());
        assertThat(root.getView().getParent()).isNull();
        uut.dismissModal(modal2, root, root, new CommandListenerAdapter());
        assertThat(root.getView().getParent()).isNotNull();
    }

    @Test
    public void dismissModal_previousViewIsNotDetachedIfOverCurrentContext() {
        modal1.options.modal.presentationStyle = ModalPresentationStyle.OverCurrentContext;
        disableShowModalAnimation(modal1, modal2);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        assertThat(root.getView().getParent()).isNotNull();
        Mockito.verify(root, Mockito.times(0)).onViewDisappear();
    }

    @Test
    public void dismissModal_rejectIfContentIsNull() {
        uut.setModalsLayout(null);
        CommandListenerAdapter listener = Mockito.mock(CommandListenerAdapter.class);
        uut.dismissModal(modal1, root, root, listener);
        Mockito.verify(listener).onError(ArgumentMatchers.any());
    }
}

