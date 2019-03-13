package org.terasology.input;


import ButtonState.DOWN;
import ButtonState.UP;
import Key.A;
import Key.B;
import Key.C;
import Key.W;
import KeyId.T;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.terasology.engine.SimpleUri;
import org.terasology.engine.subsystem.config.BindsManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.input.Keyboard.KeyId;
import org.terasology.input.device.KeyboardAction;
import org.terasology.input.device.KeyboardDevice;
import org.terasology.input.events.KeyEvent;

import static InputType.KEY;


public class InputSystemTests {
    private InputSystem inputSystem;

    private InputSystemTests.TestKeyboard testKeyboard;

    private EntityRef clientEntity;

    private List<InputSystemTests.CapturedKeyEvent> clientEntityKeyEvents;

    private EntityRef characterEntity;

    private List<InputSystemTests.CapturedKeyEvent> characterEntityKeyEvents;

    private BindsManager bindsManager;

    @Test
    public void testNoInput() {
        inputSystem.update(1.0F);
        Mockito.verify(clientEntity, Mockito.never()).send(ArgumentMatchers.any());
        Mockito.verify(characterEntity, Mockito.never()).send(ArgumentMatchers.any());
    }

    @Test
    public void testSingleKeyPress() {
        pressKey(W);
        float delta = 1.0F;
        inputSystem.update(delta);
        Assert.assertThat(clientEntityKeyEvents.size(), CoreMatchers.is(1));
        InputSystemTests.CapturedKeyEvent clientEvent = clientEntityKeyEvents.get(0);
        Assert.assertThat(clientEvent.key, CoreMatchers.is(W));
        Assert.assertThat(clientEvent.keyCharacter, CoreMatchers.is(InputSystemTests.characterFor(W)));
        Assert.assertThat(clientEvent.delta, CoreMatchers.is(delta));
        Assert.assertThat(clientEvent.buttonState, CoreMatchers.is(DOWN));
        Assert.assertThat(characterEntityKeyEvents.size(), CoreMatchers.is(1));
        InputSystemTests.CapturedKeyEvent characterEvent = characterEntityKeyEvents.get(0);
        Assert.assertThat(characterEvent.key, CoreMatchers.is(W));
        Assert.assertThat(characterEvent.keyCharacter, CoreMatchers.is(InputSystemTests.characterFor(W)));
        Assert.assertThat(characterEvent.delta, CoreMatchers.is(delta));
        Assert.assertThat(characterEvent.buttonState, CoreMatchers.is(DOWN));
    }

    @Test
    public void testSingleKeyRelease() {
        releaseKey(W);
        float delta = 1.0F;
        inputSystem.update(delta);
        Assert.assertThat(clientEntityKeyEvents.size(), CoreMatchers.is(1));
        InputSystemTests.CapturedKeyEvent clientEvent = clientEntityKeyEvents.get(0);
        Assert.assertThat(clientEvent.key, CoreMatchers.is(W));
        Assert.assertThat(clientEvent.keyCharacter, CoreMatchers.is(InputSystemTests.characterFor(W)));
        Assert.assertThat(clientEvent.delta, CoreMatchers.is(delta));
        Assert.assertThat(clientEvent.buttonState, CoreMatchers.is(UP));
        Assert.assertThat(characterEntityKeyEvents.size(), CoreMatchers.is(1));
        InputSystemTests.CapturedKeyEvent characterEvent = characterEntityKeyEvents.get(0);
        Assert.assertThat(characterEvent.key, CoreMatchers.is(W));
        Assert.assertThat(characterEvent.keyCharacter, CoreMatchers.is(InputSystemTests.characterFor(W)));
        Assert.assertThat(characterEvent.delta, CoreMatchers.is(delta));
        Assert.assertThat(characterEvent.buttonState, CoreMatchers.is(UP));
    }

    @Test
    public void testKeyOrder() {
        pressAndReleaseKey(A);
        pressAndReleaseKey(B);
        pressAndReleaseKey(C);
        inputSystem.update(1.0F);
        Assert.assertThat(clientEntityKeyEvents.size(), CoreMatchers.is(6));
        Assert.assertThat(clientEntityKeyEvents.get(0).key, CoreMatchers.is(A));
        Assert.assertThat(clientEntityKeyEvents.get(0).buttonState, CoreMatchers.is(DOWN));
        Assert.assertThat(clientEntityKeyEvents.get(1).key, CoreMatchers.is(A));
        Assert.assertThat(clientEntityKeyEvents.get(1).buttonState, CoreMatchers.is(UP));
        Assert.assertThat(clientEntityKeyEvents.get(2).key, CoreMatchers.is(B));
        Assert.assertThat(clientEntityKeyEvents.get(2).buttonState, CoreMatchers.is(DOWN));
        Assert.assertThat(clientEntityKeyEvents.get(3).key, CoreMatchers.is(B));
        Assert.assertThat(clientEntityKeyEvents.get(3).buttonState, CoreMatchers.is(UP));
        Assert.assertThat(clientEntityKeyEvents.get(4).key, CoreMatchers.is(C));
        Assert.assertThat(clientEntityKeyEvents.get(4).buttonState, CoreMatchers.is(DOWN));
        Assert.assertThat(clientEntityKeyEvents.get(5).key, CoreMatchers.is(C));
        Assert.assertThat(clientEntityKeyEvents.get(5).buttonState, CoreMatchers.is(UP));
    }

    @Test
    public void testKeyBinding() {
        Map<Integer, BindableButton> keyBinds = new HashMap<>();
        // mock binding to the TestEventButton, this is done by the BindsManager over the annotations by default
        keyBinds.put(T, new org.terasology.input.internal.BindableButtonImpl(new SimpleUri("engine-tests", "testEvent"), "theTestEvent", new InputSystemTests.TestEventButton()));
        Mockito.when(bindsManager.getKeyBinds()).thenReturn(keyBinds);
        pressKey(Key.T);
        inputSystem.update(1.0F);
        Mockito.verify(clientEntity).send(Mockito.any(InputSystemTests.TestEventButton.class));
    }

    private static class TestKeyboard implements KeyboardDevice {
        private Queue<KeyboardAction> queue = new LinkedBlockingQueue<>();

        @Override
        public Queue<KeyboardAction> getInputQueue() {
            return queue;
        }

        @Override
        public boolean isKeyDown(int key) {
            return false;
        }

        public void add(KeyboardAction action) {
            queue.add(action);
        }
    }

    // the annotations are not used in this tests but represent the way a binding is registered by default
    @RegisterBindButton(id = "testEvent", description = "${engine-tests:menu#theTestEvent}", repeating = false, category = "tests")
    @DefaultBinding(type = KEY, id = KeyId.T)
    public class TestEventButton extends BindButtonEvent {}

    private static class CapturedKeyEvent {
        public Input key;

        public float delta;

        public char keyCharacter;

        private ButtonState buttonState;

        public CapturedKeyEvent(KeyEvent event) {
            key = event.getKey();
            delta = event.getDelta();
            keyCharacter = event.getKeyCharacter();
            buttonState = event.getState();
        }
    }
}

