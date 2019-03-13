/**
 * Copyright (C) 2014 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.rt;


import org.junit.Assert;
import org.junit.Test;
import org.robovm.rt.Signals.InstallSignalsCallback;
import org.robovm.rt.bro.Bro;
import org.robovm.rt.bro.annotation.Library;


/**
 * Tests {@link Signals}.
 */
@Library("c")
public class SignalsTest {
    static {
        Bro.bind(SignalsTest.class);
    }

    private static final int SIGBUS = 10;

    private static final int SIGSEGV = 11;

    private static Object nullObj;

    private static long oldHandlerSigbus;

    private static long oldHandlerSigsegv;

    @Test
    public void testInstallSignals() throws Exception {
        // This is hard to test properly without generating a fatal signal and
        // kill the process. We just make sure that NPE/SOE works fine after
        // installSignals() has been called and that the handlers aren't the
        // original non-chaining once and aren't handler().
        final long handlerPtr = VM.getCallbackMethodImpl(SignalsTest.class.getDeclaredMethod("handler", int.class, long.class, long.class));
        long oldSigactionSigbus = VM.malloc(512);
        long oldSigactionSigsegv = VM.malloc(512);
        try {
            if ((SignalsTest.sigaction(SignalsTest.SIGBUS, 0, oldSigactionSigbus)) != 0) {
                Assert.fail("sigaction");
            }
            if ((SignalsTest.sigaction(SignalsTest.SIGSEGV, 0, oldSigactionSigsegv)) != 0) {
                Assert.fail("sigaction");
            }
            try {
                Signals.installSignals(new InstallSignalsCallback() {
                    @Override
                    public void install() {
                        if (Bro.IS_DARWIN) {
                            SignalsTest.oldHandlerSigbus = SignalsTest.signal(SignalsTest.SIGBUS, handlerPtr);
                        }
                        SignalsTest.oldHandlerSigsegv = SignalsTest.signal(SignalsTest.SIGSEGV, handlerPtr);
                    }
                });
                // Make sure NPEs and SOEs still work properly
                try {
                    SignalsTest.nullObj.hashCode();
                    Assert.fail("NullPointerException expected");
                } catch (NullPointerException e) {
                }
                try {
                    triggerSOE();
                    Assert.fail("StackOverflowError expected");
                } catch (StackOverflowError e) {
                }
                if (Bro.IS_DARWIN) {
                    Assert.assertNotEquals(handlerPtr, SignalsTest.currentHandler(SignalsTest.SIGBUS));
                    Assert.assertNotEquals(SignalsTest.oldHandlerSigbus, SignalsTest.currentHandler(SignalsTest.SIGBUS));
                }
                Assert.assertNotEquals(handlerPtr, SignalsTest.currentHandler(SignalsTest.SIGSEGV));
                Assert.assertNotEquals(SignalsTest.oldHandlerSigsegv, SignalsTest.currentHandler(SignalsTest.SIGSEGV));
                try {
                    Signals.installSignals(new InstallSignalsCallback() {
                        @Override
                        public void install() {
                        }
                    });
                    Assert.fail("IllegalStateException expected");
                } catch (IllegalStateException e) {
                }
            } finally {
                SignalsTest.sigaction(SignalsTest.SIGBUS, oldSigactionSigbus, 0);
                SignalsTest.sigaction(SignalsTest.SIGSEGV, oldSigactionSigsegv, 0);
            }
        } finally {
            VM.free(oldSigactionSigbus);
            VM.free(oldSigactionSigsegv);
        }
    }
}

