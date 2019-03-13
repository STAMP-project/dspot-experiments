package com.reactnativenavigation.viewcontrollers.modal;


import Options.EMPTY;
import android.app.Activity;
import android.view.ViewGroup;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.anim.ModalAnimator;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.react.EventEmitter;
import com.reactnativenavigation.utils.CommandListener;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.viewcontrollers.ChildControllersRegistry;
import com.reactnativenavigation.viewcontrollers.ViewController;
import com.reactnativenavigation.viewcontrollers.stack.StackController;
import java.util.EmptyStackException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


public class ModalStackTest extends BaseTest {
    private static final String MODAL_ID_1 = "modalId1";

    private static final String MODAL_ID_2 = "modalId2";

    private static final String MODAL_ID_3 = "modalId3";

    private static final String MODAL_ID_4 = "modalId4";

    private ModalStack uut;

    private ViewController modal1;

    private ViewController modal2;

    private ViewController modal3;

    private ViewController modal4;

    private StackController stack;

    private Activity activity;

    private ChildControllersRegistry childRegistry;

    private ModalPresenter presenter;

    private ModalAnimator animator;

    private ViewController root;

    private EventEmitter emitter;

    @Test
    public void modalRefIsSaved() {
        disableShowModalAnimation(modal1);
        CommandListener listener = Mockito.spy(new CommandListenerAdapter());
        uut.showModal(modal1, root, listener);
        Mockito.verify(listener, Mockito.times(1)).onSuccess(modal1.getId());
        assertThat(findModal(ModalStackTest.MODAL_ID_1)).isNotNull();
    }

    @Test
    public void showModal() {
        CommandListener listener = Mockito.spy(new CommandListenerAdapter());
        uut.showModal(modal1, root, listener);
        Mockito.verify(listener, Mockito.times(1)).onSuccess(modal1.getId());
        assertThat(uut.size()).isOne();
        Mockito.verify(presenter, Mockito.times(1)).showModal(modal1, root, listener);
        assertThat(findModal(ModalStackTest.MODAL_ID_1)).isNotNull();
    }

    @SuppressWarnings("Convert2Lambda")
    @Test
    public void dismissModal() {
        uut.showModal(modal1, root, new CommandListenerAdapter());
        CommandListener listener = Mockito.spy(new CommandListenerAdapter());
        uut.dismissModal(modal1.getId(), root, listener);
        assertThat(findModal(modal1.getId())).isNull();
        Mockito.verify(presenter, Mockito.times(1)).dismissModal(eq(modal1), eq(root), eq(root), any());
        Mockito.verify(listener).onSuccess(modal1.getId());
    }

    @Test
    public void dismissModal_listenerAndEmitterAreInvokedWithGivenId() {
        uut.showModal(stack, root, new CommandListenerAdapter());
        CommandListener listener = Mockito.spy(new CommandListenerAdapter());
        uut.dismissModal(modal4.getId(), root, listener);
        Mockito.verify(listener).onSuccess(modal4.getId());
        Mockito.verify(emitter).emitModalDismissed(modal4.getId(), 1);
    }

    @SuppressWarnings("Convert2Lambda")
    @Test
    public void dismissModal_rejectIfModalNotFound() {
        CommandListener listener = Mockito.spy(new CommandListenerAdapter());
        Runnable onModalWillDismiss = Mockito.spy(new Runnable() {
            @Override
            public void run() {
            }
        });
        uut.dismissModal(ModalStackTest.MODAL_ID_1, root, listener);
        Mockito.verify(onModalWillDismiss, Mockito.times(0)).run();
        Mockito.verify(listener, Mockito.times(1)).onError(ArgumentMatchers.anyString());
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void dismissModal_dismissDeepModal() {
        disableShowModalAnimation(modal1, modal2, modal3);
        disableDismissModalAnimation(modal1, modal2, modal3);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.showModal(modal2, root, new CommandListenerAdapter());
        uut.showModal(modal3, root, new CommandListenerAdapter());
        assertThat(root.getView().getParent()).isNull();
        uut.dismissModal(modal1.getId(), root, new CommandListenerAdapter());
        assertThat(root.getView().getParent()).isNull();
        uut.dismissModal(modal3.getId(), root, new CommandListenerAdapter());
        uut.dismissModal(modal2.getId(), root, new CommandListenerAdapter());
        assertThat(root.getView().getParent()).isNotNull();
        assertThat(root.getView().isShown()).isTrue();
    }

    @Test
    public void dismissAllModals() {
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.showModal(modal2, root, new CommandListenerAdapter());
        CommandListener listener = Mockito.spy(new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(findModal(modal1.getId())).isNull();
                assertThat(findModal(modal2.getId())).isNull();
                assertThat(uut.isEmpty()).isTrue();
            }
        });
        uut.dismissAllModals(root, EMPTY, listener);
        Mockito.verify(listener, Mockito.times(1)).onSuccess(ArgumentMatchers.anyString());
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void dismissAllModals_rejectIfEmpty() {
        CommandListener spy = Mockito.spy(new CommandListenerAdapter());
        uut.dismissAllModals(root, EMPTY, spy);
        Mockito.verify(spy, Mockito.times(1)).onError(any());
    }

    @Test
    public void dismissAllModals_optionsAreMergedOnTopModal() {
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.showModal(modal2, root, new CommandListenerAdapter());
        uut.showModal(modal3, root, new CommandListenerAdapter());
        Options mergeOptions = new Options();
        uut.dismissAllModals(root, mergeOptions, new CommandListenerAdapter());
        Mockito.verify(modal3).mergeOptions(mergeOptions);
        Mockito.verify(modal1, Mockito.times(0)).mergeOptions(mergeOptions);
        Mockito.verify(modal2, Mockito.times(0)).mergeOptions(mergeOptions);
    }

    @SuppressWarnings("Convert2Lambda")
    @Test
    public void dismissAllModals_onlyTopModalIsAnimated() {
        modal2 = Mockito.spy(modal2);
        Options defaultOptions = new Options();
        uut.setDefaultOptions(defaultOptions);
        Options resolvedOptions = new Options();
        Mockito.when(modal2.resolveCurrentOptions(defaultOptions)).then(( invocation) -> resolvedOptions);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.showModal(modal2, root, new CommandListenerAdapter());
        ViewGroup view1 = modal1.getView();
        ViewGroup view2 = modal2.getView();
        CommandListener listener = Mockito.spy(new CommandListenerAdapter());
        uut.dismissAllModals(root, EMPTY, listener);
        Mockito.verify(presenter).dismissModal(eq(modal2), eq(root), eq(root), any());
        Mockito.verify(listener).onSuccess(modal2.getId());
        Mockito.verify(animator, Mockito.times(0)).dismiss(eq(view1), eq(modal1.options.animations.dismissModal), any());
        Mockito.verify(animator).dismiss(eq(view2), eq(resolvedOptions.animations.dismissModal), any());
        assertThat(uut.size()).isEqualTo(0);
    }

    @Test
    public void dismissAllModals_bottomModalsAreDestroyed() {
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.showModal(modal2, root, new CommandListenerAdapter());
        uut.dismissAllModals(root, EMPTY, new CommandListenerAdapter());
        Mockito.verify(modal1, Mockito.times(1)).destroy();
        Mockito.verify(modal1, Mockito.times(1)).onViewDisappear();
        assertThat(uut.size()).isEqualTo(0);
    }

    @Test
    public void isEmpty() {
        assertThat(uut.isEmpty()).isTrue();
        uut.showModal(modal1, root, new CommandListenerAdapter());
        assertThat(uut.isEmpty()).isFalse();
        uut.dismissAllModals(root, EMPTY, new CommandListenerAdapter());
        assertThat(uut.isEmpty()).isTrue();
    }

    @Test
    public void peek() {
        assertThat(uut.isEmpty()).isTrue();
        assertThatThrownBy(() -> uut.peek()).isInstanceOf(EmptyStackException.class);
        uut.showModal(modal1, root, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(uut.peek()).isEqualTo(modal1);
            }
        });
    }

    @Test
    public void onDismiss_onViewAppearedInvokedOnPreviousModal() {
        disableShowModalAnimation(modal1, modal2);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.showModal(modal2, root, new CommandListenerAdapter());
        uut.dismissModal(modal2.getId(), root, new CommandListenerAdapter());
        Mockito.verify(modal1, Mockito.times(2)).onViewAppeared();
    }

    @Test
    public void onDismiss_dismissModalInTheMiddleOfStack() {
        disableShowModalAnimation(modal1, modal2, modal3);
        disableDismissModalAnimation(modal1, modal2, modal3);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        uut.showModal(modal2, root, new CommandListenerAdapter());
        uut.showModal(modal3, root, new CommandListenerAdapter());
        uut.dismissModal(modal2.getId(), root, new CommandListenerAdapter());
        assertThat(uut.size()).isEqualTo(2);
        Mockito.verify(modal2, Mockito.times(1)).onViewDisappear();
        Mockito.verify(modal2, Mockito.times(1)).destroy();
        assertThat(modal1.getView().getParent()).isNull();
    }

    @Test
    public void handleBack_doesNothingIfModalStackIsEmpty() {
        assertThat(uut.isEmpty()).isTrue();
        assertThat(uut.handleBack(new CommandListenerAdapter(), root)).isFalse();
    }

    @Test
    public void handleBack_dismissModal() {
        disableDismissModalAnimation(modal1);
        uut.showModal(modal1, root, new CommandListenerAdapter());
        assertThat(uut.handleBack(new CommandListenerAdapter(), root)).isTrue();
        Mockito.verify(modal1, Mockito.times(1)).onViewDisappear();
    }

    @Test
    public void handleBack_ViewControllerTakesPrecedenceOverModal() {
        ViewController backHandlingModal = Mockito.spy(new SimpleViewController(activity, childRegistry, "stack", new Options()) {
            @Override
            public boolean handleBack(CommandListener listener) {
                return true;
            }
        });
        uut.showModal(backHandlingModal, root, new CommandListenerAdapter());
        root.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        assertThat(uut.handleBack(new CommandListenerAdapter(), any())).isTrue();
        Mockito.verify(backHandlingModal, Mockito.times(1)).handleBack(any());
        Mockito.verify(backHandlingModal, Mockito.times(0)).onViewDisappear();
    }

    @Test
    public void setDefaultOptions() {
        Options defaultOptions = new Options();
        uut.setDefaultOptions(defaultOptions);
        Mockito.verify(presenter).setDefaultOptions(defaultOptions);
    }

    @Test
    public void destroy() {
        showModalsWithoutAnimation(modal1, modal2);
        uut.destroy();
        Mockito.verify(modal1).destroy();
        Mockito.verify(modal2).destroy();
    }
}

