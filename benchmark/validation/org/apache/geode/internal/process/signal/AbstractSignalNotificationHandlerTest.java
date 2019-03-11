/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.process.signal;


import Signal.SIGINT;
import Signal.SIGQUIT;
import Signal.SIGTERM;
import java.util.Set;
import org.apache.geode.internal.util.CollectionUtils;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static Signal.SIGINT;
import static Signal.SIGIO;
import static Signal.SIGQUIT;
import static Signal.SIGTERM;


/**
 * Unit tests for {@link AbstractSignalNotificationHandler}.
 *
 * @since GemFire 7.0
 */
public class AbstractSignalNotificationHandlerTest {
    @Test
    public void assertNotNullWithNonNullValueDoesNotThrow() {
        AbstractSignalNotificationHandler.assertNotNull(new Object(), "TEST");
    }

    @Test
    public void assertNotNullWithNullValueThrowsNullPointerException() {
        assertThatThrownBy(() -> AbstractSignalNotificationHandler.assertNotNull(null, "Expected %1$s message!", "test")).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void assertStateWithValidStateDoesNotThrow() {
        AbstractSignalNotificationHandler.assertState(true, "TEST");
    }

    @Test
    public void assertStateWithInvalidStateThrowsIllegalStateException() {
        assertThatThrownBy(() -> AbstractSignalNotificationHandler.assertState(false, "Expected %1$s message!", "test")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void assertValidArgumentWithLegalArgumentDoesNotThrow() {
        AbstractSignalNotificationHandler.assertValidArgument(true, "TEST");
    }

    @Test
    public void assertValidArgumentWithIllegalArgumentThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> AbstractSignalNotificationHandler.assertValidArgument(false, "Expected %1$s message!", "test")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void registerListener() {
        AbstractSignalNotificationHandler signalHandler = createSignalNotificationHandler();
        SignalListener mockListenerOne = Mockito.mock(SignalListener.class, "SIGALL1");
        SignalListener mockListenerTwo = Mockito.mock(SignalListener.class, "SIGALL2");
        assertThat(signalHandler.isListening(mockListenerOne)).isFalse();
        assertThat(signalHandler.isListening(mockListenerTwo)).isFalse();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isFalse();
            assertThat(signalHandler.isListening(mockListenerOne, signal)).isFalse();
            assertThat(signalHandler.isListening(mockListenerTwo, signal)).isFalse();
        }
        assertThat(signalHandler.registerListener(mockListenerOne)).isTrue();
        assertThat(signalHandler.registerListener(mockListenerTwo)).isTrue();
        assertThat(signalHandler.registerListener(mockListenerTwo)).isFalse();
        assertThat(signalHandler.isListening(mockListenerOne)).isTrue();
        assertThat(signalHandler.isListening(mockListenerTwo)).isTrue();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isTrue();
            assertThat(signalHandler.isListening(mockListenerOne, signal)).isTrue();
            assertThat(signalHandler.isListening(mockListenerTwo, signal)).isTrue();
        }
    }

    @Test
    public void registerListenerWithNullSignalListenerThrowsNullPointerException() {
        assertThatThrownBy(() -> createSignalNotificationHandler().registerListener(null)).isInstanceOf(NullPointerException.class).hasMessage("The SignalListener to register, listening for all signals cannot be null!");
    }

    @Test
    public void registerListenerWithSignal() {
        AbstractSignalNotificationHandler signalHandler = createSignalNotificationHandler();
        SignalListener mockSigIntListener = Mockito.mock(SignalListener.class, "SIGINT");
        SignalListener mockSigIntTermListener = Mockito.mock(SignalListener.class, "SIGINT + SIGTERM");
        assertThat(signalHandler.isListening(mockSigIntListener)).isFalse();
        assertThat(signalHandler.isListening(mockSigIntTermListener)).isFalse();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isFalse();
            assertThat(signalHandler.isListening(mockSigIntListener, signal)).isFalse();
            assertThat(signalHandler.isListening(mockSigIntTermListener, signal)).isFalse();
        }
        assertThat(signalHandler.registerListener(mockSigIntListener, SIGINT)).isTrue();
        assertThat(signalHandler.registerListener(mockSigIntTermListener, SIGINT)).isTrue();
        assertThat(signalHandler.registerListener(mockSigIntTermListener, SIGTERM)).isTrue();
        assertThat(signalHandler.registerListener(mockSigIntTermListener, SIGINT)).isFalse();
        assertThat(signalHandler.isListening(mockSigIntListener)).isTrue();
        assertThat(signalHandler.isListening(mockSigIntTermListener)).isTrue();
        Set<Signal> expectedSignals = CollectionUtils.asSet(SIGINT, SIGTERM);
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isEqualTo(expectedSignals.contains(signal));
            switch (signal) {
                case SIGINT :
                    assertThat(signalHandler.isListening(mockSigIntListener, signal)).isTrue();
                    assertThat(signalHandler.isListening(mockSigIntTermListener, signal)).isTrue();
                    break;
                case SIGTERM :
                    assertThat(signalHandler.isListening(mockSigIntListener, signal)).isFalse();
                    assertThat(signalHandler.isListening(mockSigIntTermListener, signal)).isTrue();
                    break;
                default :
                    assertThat(signalHandler.isListening(mockSigIntListener, signal)).isFalse();
                    assertThat(signalHandler.isListening(mockSigIntTermListener, signal)).isFalse();
            }
        }
    }

    @Test
    public void registerListenerWithNullSignalThrowsNullPointerException() {
        assertThatThrownBy(() -> createSignalNotificationHandler().registerListener(mock(.class, "SIGALL"), null)).isInstanceOf(NullPointerException.class).hasMessage("The signal to register the listener for cannot be null!");
    }

    @Test
    public void registerListenerWithSignalAndNullSignalListenerThrowsNullPointerException() {
        assertThatThrownBy(() -> createSignalNotificationHandler().registerListener(null, Signal.SIGQUIT)).isInstanceOf(NullPointerException.class).hasMessage(signalListenerForSignalCannotBeNullErrorMessage(SIGQUIT));
    }

    @Test
    public void unregisterListener() {
        AbstractSignalNotificationHandler signalHandler = createSignalNotificationHandler();
        SignalListener mockSignalListener = Mockito.mock(SignalListener.class, "SIGALL");
        assertThat(signalHandler.isListening(mockSignalListener)).isFalse();
        assertThat(signalHandler.registerListener(mockSignalListener)).isTrue();
        assertThat(signalHandler.isListening(mockSignalListener)).isTrue();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isTrue();
        }
        assertThat(signalHandler.unregisterListener(mockSignalListener)).isTrue();
        assertThat(signalHandler.isListening(mockSignalListener)).isFalse();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isFalse();
        }
        assertThat(signalHandler.unregisterListener(mockSignalListener)).isFalse();
    }

    @Test
    public void unregisterListenerWithSignalListenerAndAllSignals() {
        AbstractSignalNotificationHandler signalHandler = createSignalNotificationHandler();
        SignalListener mockSignalListener = Mockito.mock(SignalListener.class, "SIGALL");
        assertThat(signalHandler.isListening(mockSignalListener)).isFalse();
        assertThat(signalHandler.registerListener(mockSignalListener)).isTrue();
        assertThat(signalHandler.isListening(mockSignalListener)).isTrue();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isTrue();
            assertThat(signalHandler.isListening(mockSignalListener, signal)).isTrue();
            assertThat(signalHandler.unregisterListener(mockSignalListener, signal)).isTrue();
            assertThat(signalHandler.isListening(mockSignalListener, signal)).isFalse();
            assertThat(signalHandler.hasListeners(signal)).isFalse();
        }
        assertThat(signalHandler.unregisterListener(mockSignalListener)).isFalse();
    }

    @Test
    public void unregisterListenerWithSignalListenerAndSigint() {
        AbstractSignalNotificationHandler signalHandler = createSignalNotificationHandler();
        SignalListener mockSignalListener = Mockito.mock(SignalListener.class, "SIGALL");
        assertThat(signalHandler.isListening(mockSignalListener)).isFalse();
        assertThat(signalHandler.registerListener(mockSignalListener, SIGINT)).isTrue();
        assertThat(signalHandler.isListening(mockSignalListener)).isTrue();
        assertThat(signalHandler.isListening(mockSignalListener, SIGINT)).isTrue();
        for (Signal signal : Signal.values()) {
            if (!(SIGINT.equals(signal))) {
                assertThat(signalHandler.hasListeners(signal)).isFalse();
                assertThat(signalHandler.isListening(mockSignalListener, signal)).isFalse();
            }
        }
        assertThat(signalHandler.isListening(mockSignalListener)).isTrue();
        assertThat(signalHandler.isListening(mockSignalListener, SIGINT)).isTrue();
        assertThat(signalHandler.unregisterListener(mockSignalListener, SIGINT)).isTrue();
        assertThat(signalHandler.isListening(mockSignalListener, SIGINT)).isFalse();
        assertThat(signalHandler.isListening(mockSignalListener)).isFalse();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isFalse();
        }
        assertThat(signalHandler.unregisterListener(mockSignalListener)).isFalse();
    }

    @Test
    public void unregisterListenerWithSignalListenerAndNullSignalThrowsNullPointerException() {
        assertThatThrownBy(() -> createSignalNotificationHandler().unregisterListener(mock(.class, "SIGALL"), null)).isInstanceOf(NullPointerException.class).hasMessage("The signal from which to unregister the listener cannot be null!");
    }

    @Test
    public void unregisterListeners() {
        AbstractSignalNotificationHandler signalHandler = createSignalNotificationHandler();
        SignalListener mockSigQuitListener = Mockito.mock(SignalListener.class, "SIGQUIT");
        SignalListener mockSigTermListener = Mockito.mock(SignalListener.class, "SIGTERM");
        SignalListener mockSigTermQuitListener = Mockito.mock(SignalListener.class, "SIGTERM + SIGQUIT");
        assertThat(signalHandler.isListening(mockSigQuitListener)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermListener)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermQuitListener)).isFalse();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isFalse();
        }
        // register sigquit and sigterm listeners...
        assertThat(signalHandler.registerListener(mockSigQuitListener, SIGQUIT)).isTrue();
        assertThat(signalHandler.registerListener(mockSigTermListener, SIGTERM)).isTrue();
        assertThat(signalHandler.registerListener(mockSigTermQuitListener, SIGQUIT)).isTrue();
        assertThat(signalHandler.registerListener(mockSigTermQuitListener, SIGTERM)).isTrue();
        assertThat(signalHandler.isListening(mockSigQuitListener)).isTrue();
        assertThat(signalHandler.isListening(mockSigQuitListener, SIGINT)).isFalse();
        assertThat(signalHandler.isListening(mockSigQuitListener, SIGQUIT)).isTrue();
        assertThat(signalHandler.isListening(mockSigQuitListener, SIGTERM)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermListener)).isTrue();
        assertThat(signalHandler.isListening(mockSigTermListener, SIGINT)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermListener, SIGQUIT)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermListener, SIGTERM)).isTrue();
        assertThat(signalHandler.isListening(mockSigTermQuitListener)).isTrue();
        assertThat(signalHandler.isListening(mockSigTermQuitListener, SIGINT)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermQuitListener, SIGQUIT)).isTrue();
        assertThat(signalHandler.isListening(mockSigTermQuitListener, SIGTERM)).isTrue();
        assertThat(signalHandler.hasListeners(SIGINT)).isFalse();
        assertThat(signalHandler.hasListeners(SIGQUIT)).isTrue();
        assertThat(signalHandler.hasListeners(SIGTERM)).isTrue();
        // unregister all sigterm listeners...
        assertThat(signalHandler.unregisterListeners(SIGTERM)).isTrue();
        assertThat(signalHandler.isListening(mockSigQuitListener)).isTrue();
        assertThat(signalHandler.isListening(mockSigQuitListener, SIGINT)).isFalse();
        assertThat(signalHandler.isListening(mockSigQuitListener, SIGQUIT)).isTrue();
        assertThat(signalHandler.isListening(mockSigQuitListener, SIGTERM)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermListener)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermListener, SIGINT)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermListener, SIGQUIT)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermListener, SIGTERM)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermQuitListener)).isTrue();
        assertThat(signalHandler.isListening(mockSigTermQuitListener, SIGINT)).isFalse();
        assertThat(signalHandler.isListening(mockSigTermQuitListener, SIGQUIT)).isTrue();
        assertThat(signalHandler.isListening(mockSigTermQuitListener, SIGTERM)).isFalse();
        assertThat(signalHandler.hasListeners(SIGINT)).isFalse();
        assertThat(signalHandler.hasListeners(SIGQUIT)).isTrue();
        assertThat(signalHandler.hasListeners(SIGTERM)).isFalse();
    }

    @Test
    public void unregisterListenersWithNullSignalThrowsNullPointerException() {
        assertThatThrownBy(() -> createSignalNotificationHandler().unregisterListeners(null)).isInstanceOf(NullPointerException.class).hasMessage("The signal from which to unregister all listeners cannot be null!");
    }

    @Test
    public void notifyListeners() {
        AbstractSignalNotificationHandler signalHandler = createSignalNotificationHandler();
        SignalListener mockSigAllListener = Mockito.mock(SignalListener.class, "SIGALL");
        SignalListener mockSigIntListener = Mockito.mock(SignalListener.class, "SIGINT");
        SignalListener mockSigQuitListener = Mockito.mock(SignalListener.class, "SIGQUIT");
        SignalListener mockSigQuitTermListener = Mockito.mock(SignalListener.class, "SIGQUIT + SIGTERM");
        SignalEvent sigintEvent = new SignalEvent(this, SIGINT);
        SignalEvent sigioEvent = new SignalEvent(this, SIGIO);
        SignalEvent sigquitEvent = new SignalEvent(this, SIGQUIT);
        SignalEvent sigtermEvent = new SignalEvent(this, SIGTERM);
        assertThat(signalHandler.isListening(mockSigAllListener)).isFalse();
        assertThat(signalHandler.isListening(mockSigIntListener)).isFalse();
        assertThat(signalHandler.isListening(mockSigQuitListener)).isFalse();
        assertThat(signalHandler.isListening(mockSigQuitTermListener)).isFalse();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isFalse();
        }
        assertThat(signalHandler.registerListener(mockSigAllListener)).isTrue();
        assertThat(signalHandler.registerListener(mockSigIntListener, SIGINT)).isTrue();
        assertThat(signalHandler.registerListener(mockSigQuitListener, SIGQUIT)).isTrue();
        assertThat(signalHandler.registerListener(mockSigQuitTermListener, SIGQUIT)).isTrue();
        assertThat(signalHandler.registerListener(mockSigQuitTermListener, SIGTERM)).isTrue();
        assertThat(signalHandler.isListening(mockSigAllListener)).isTrue();
        assertThat(signalHandler.isListening(mockSigIntListener)).isTrue();
        assertThat(signalHandler.isListening(mockSigQuitListener)).isTrue();
        assertThat(signalHandler.isListening(mockSigQuitTermListener)).isTrue();
        for (Signal signal : Signal.values()) {
            assertThat(signalHandler.hasListeners(signal)).isTrue();
            assertThat(signalHandler.isListening(mockSigAllListener, signal)).isTrue();
            switch (signal) {
                case SIGINT :
                    assertThat(signalHandler.isListening(mockSigIntListener, signal)).isTrue();
                    assertThat(signalHandler.isListening(mockSigQuitListener, signal)).isFalse();
                    assertThat(signalHandler.isListening(mockSigQuitTermListener, signal)).isFalse();
                    break;
                case SIGQUIT :
                    assertThat(signalHandler.isListening(mockSigIntListener, signal)).isFalse();
                    assertThat(signalHandler.isListening(mockSigQuitListener, signal)).isTrue();
                    assertThat(signalHandler.isListening(mockSigQuitTermListener, signal)).isTrue();
                    break;
                case SIGTERM :
                    assertThat(signalHandler.isListening(mockSigIntListener, signal)).isFalse();
                    assertThat(signalHandler.isListening(mockSigQuitListener, signal)).isFalse();
                    assertThat(signalHandler.isListening(mockSigQuitTermListener, signal)).isTrue();
                    break;
                default :
                    assertThat(signalHandler.isListening(mockSigIntListener, signal)).isFalse();
                    assertThat(signalHandler.isListening(mockSigQuitListener, signal)).isFalse();
                    assertThat(signalHandler.isListening(mockSigQuitTermListener, signal)).isFalse();
            }
        }
        signalHandler.notifyListeners(sigintEvent);
        signalHandler.notifyListeners(sigioEvent);
        signalHandler.notifyListeners(sigquitEvent);
        signalHandler.notifyListeners(sigtermEvent);
        Mockito.verify(mockSigAllListener, Mockito.times(1)).handle(ArgumentMatchers.eq(sigintEvent));
        Mockito.verify(mockSigAllListener, Mockito.times(1)).handle(ArgumentMatchers.eq(sigioEvent));
        Mockito.verify(mockSigAllListener, Mockito.times(1)).handle(ArgumentMatchers.eq(sigquitEvent));
        Mockito.verify(mockSigAllListener, Mockito.times(1)).handle(ArgumentMatchers.eq(sigtermEvent));
        Mockito.verify(mockSigIntListener, Mockito.times(1)).handle(ArgumentMatchers.eq(sigintEvent));
        Mockito.verify(mockSigQuitListener, Mockito.times(1)).handle(ArgumentMatchers.eq(sigquitEvent));
        Mockito.verify(mockSigQuitTermListener, Mockito.times(1)).handle(ArgumentMatchers.eq(sigquitEvent));
        Mockito.verify(mockSigQuitTermListener, Mockito.times(1)).handle(ArgumentMatchers.eq(sigtermEvent));
    }

    // nothing here
    private static class TestSignalNotificationHandler extends AbstractSignalNotificationHandler {}
}

