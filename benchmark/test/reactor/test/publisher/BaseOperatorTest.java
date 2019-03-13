/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.test.publisher;


import java.util.function.Consumer;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.Exceptions;
import reactor.core.Fuseable;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Stephane Maldini
 */
public abstract class BaseOperatorTest<I, PI extends Publisher<? extends I>, O, PO extends Publisher<? extends O>> {
    OperatorScenario<I, PI, O, PO> defaultScenario;

    boolean defaultEmpty = false;

    @Test
    public final void cancelOnSubscribe() {
        defaultEmpty = true;
        forEachScenario(scenarios_operatorSuccess(), ( s) -> {
            OperatorScenario<I, PI, O, PO> scenario = s.duplicate().receiverEmpty().receiverDemand(0);
            this.inputHiddenOutputBackpressured(scenario).consumeSubscriptionWith(Subscription::cancel).thenCancel().verify();
            this.inputHidden(scenario).consumeSubscriptionWith(Subscription::cancel).thenCancel().verify();
            this.inputHiddenOutputBackpressured(scenario).consumeSubscriptionWith(Subscription::cancel).thenCancel().verify();
            this.inputFusedConditionalOutputConditional(scenario).consumeSubscriptionWith(Subscription::cancel).thenCancel().verify();
            this.inputHiddenOutputConditionalCancel(scenario);
            this.inputFusedAsyncOutputFusedAsyncCancel(scenario);
            this.inputFusedAsyncOutputFusedAsyncConditionalCancel(scenario);
            this.inputFusedSyncOutputFusedSyncCancel(scenario);
            this.inputFusedSyncOutputFusedSyncConditionalCancel(scenario);
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void assertPrePostState() {
        forEachScenario(scenarios_touchAndAssertState(), ( scenario) -> {
            this.inputHiddenOutputState(scenario);
            this.inputHiddenOutputConditionalState(scenario);
            this.inputFusedOutputState(scenario);
            this.inputFusedOutputConditionalState(scenario);
        });
    }

    @Test
    public final void sequenceOfNextAndComplete() {
        forEachScenario(scenarios_operatorSuccess(), ( scenario) -> {
            Consumer<StepVerifier.Step<O>> verifier = verifier();
            if (verifier == null) {
                verifier = ( step) -> scenario.applySteps(step).verifyComplete();
            }
            int fusion = scenario.fusionMode();
            this.inputHiddenOutputBackpressured(scenario).consumeSubscriptionWith(( s) -> s.request(0)).verifyComplete();
            verifier.accept(this.inputHidden(scenario));
            verifier.accept(this.inputHiddenOutputConditionalTryNext(scenario));
            verifier.accept(this.inputFused(scenario));
            verifier.accept(this.inputFusedConditionalTryNext(scenario));
            if ((fusion & (Fuseable.SYNC)) != 0) {
                verifier.accept(this.inputFusedSyncOutputFusedSync(scenario));
                verifier.accept(this.inputFusedSyncOutputFusedSyncConditional(scenario));
            }
            if ((fusion & (Fuseable.ASYNC)) != 0) {
                verifier.accept(this.inputFusedAsyncOutputFusedAsync(scenario));
                verifier.accept(this.inputFusedAsyncOutputFusedAsyncConditional(scenario));
            }
            verifier.accept(this.inputConditionalTryNext(scenario));
            verifier.accept(this.inputConditionalOutputConditional(scenario));
            verifier.accept(this.inputFusedConditionalOutputConditional(scenario));
            verifier.accept(this.inputFusedConditionalOutputConditionalTryNext(scenario));
        });
    }

    @Test
    public final void sequenceOfNextWithCallbackError() {
        defaultEmpty = true;
        defaultScenario.producerError(new RuntimeException("test"));
        forEachScenario(scenarios_operatorError(), ( scenario) -> {
            Consumer<StepVerifier.Step<O>> verifier = verifier();
            String m = scenario.producerError.getMessage();
            Consumer<StepVerifier.Step<O>> errorVerifier = ( step) -> {
                try {
                    step.verifyErrorSatisfies(( e) -> {
                        if (((e instanceof NullPointerException) || (e instanceof IllegalStateException)) || (e.getMessage().equals(m))) {
                            return;
                        }
                        throw Exceptions.propagate(e);
                    });
                    // step.expectErrorMessage(m)
                    // .verifyThenAssertThat()
                    // .hasOperatorErrorWithMessage(m);
                } catch ( e) {
                    if (e instanceof AssertionError) {
                        throw ((AssertionError) (e));
                    }
                    e = Exceptions.unwrap(e);
                    if (((e instanceof NullPointerException) || (e instanceof IllegalStateException)) || (e.getMessage().equals(m))) {
                        return;
                    }
                    throw Exceptions.propagate(e);
                }
            };
            if (verifier == null) {
                verifier = ( step) -> errorVerifier.accept(scenario.applySteps(step));
                errorVerifier.accept(this.inputHiddenOutputBackpressured(scenario));
            } else {
                verifier.accept(this.inputHiddenOutputBackpressured(scenario));
            }
            int fusion = scenario.fusionMode();
            verifier.accept(this.inputHidden(scenario));
            verifier.accept(this.inputHiddenOutputConditionalTryNext(scenario));
            verifier.accept(this.inputFused(scenario));
            if (((scenario.producerCount()) > 0) && ((fusion & (Fuseable.SYNC)) != 0)) {
                verifier.accept(this.inputFusedSyncOutputFusedSync(scenario));
                verifier.accept(this.inputFusedSyncOutputFusedSyncConditional(scenario));
            }
            if (((scenario.producerCount()) > 0) && ((fusion & (Fuseable.ASYNC)) != 0)) {
                verifier.accept(this.inputFusedAsyncOutputFusedAsync(scenario));
                verifier.accept(this.inputFusedAsyncOutputFusedAsyncConditional(scenario));
                this.inputFusedAsyncOutputFusedAsyncCancel(scenario);
                this.inputFusedAsyncOutputFusedAsyncConditionalCancel(scenario);
            }
            verifier.accept(this.inputConditionalTryNext(scenario));
            verifier.accept(this.inputConditionalOutputConditional(scenario));
            verifier.accept(this.inputFusedConditionalTryNext(scenario));
            verifier.accept(this.inputFusedConditionalOutputConditional(scenario));
            verifier.accept(this.inputFusedConditionalOutputConditionalTryNext(scenario));
        });
    }

    @Test
    public final void errorOnSubscribe() {
        defaultEmpty = true;
        defaultScenario.producerError(new RuntimeException("test"));
        forEachScenario(scenarios_errorFromUpstreamFailure(), ( s) -> {
            OperatorScenario<I, PI, O, PO> scenario = s.duplicate();
            Consumer<StepVerifier.Step<O>> verifier = scenario.verifier();
            if (verifier == null) {
                String m = exception().getMessage();
                verifier = ( step) -> {
                    try {
                        if ((scenario.shouldHitDropErrorHookAfterTerminate()) || (scenario.shouldHitDropNextHookAfterTerminate())) {
                            StepVerifier.Assertions assertions = scenario.applySteps(step).expectErrorMessage(m).verifyThenAssertThat();
                            if (scenario.shouldHitDropErrorHookAfterTerminate()) {
                                assertions.hasDroppedErrorsSatisfying(( c) -> {
                                    assertThat(c.stream().findFirst().get()).hasMessage(scenario.droppedError.getMessage());
                                });
                            }
                            if (scenario.shouldHitDropNextHookAfterTerminate()) {
                                assertions.hasDropped(scenario.droppedItem);
                            }
                        } else {
                            scenario.applySteps(step).verifyErrorMessage(m);
                        }
                    } catch ( e) {
                        assertThat(Exceptions.unwrap(e)).hasMessage(m);
                    }
                };
            }
            int fusion = scenario.fusionMode();
            verifier.accept(this.inputHiddenError(scenario));
            verifier.accept(this.inputHiddenErrorOutputConditional(scenario));
            verifier.accept(this.inputConditionalError(scenario));
            verifier.accept(this.inputConditionalErrorOutputConditional(scenario));
            scenario.shouldHitDropErrorHookAfterTerminate(false).shouldHitDropNextHookAfterTerminate(false);
            verifier.accept(this.inputFusedError(scenario));
            verifier.accept(this.inputFusedErrorOutputFusedConditional(scenario));
            if (((scenario.prefetch()) != (-1)) || ((fusion & (Fuseable.SYNC)) != 0)) {
                verifier.accept(this.inputFusedSyncErrorOutputFusedSync(scenario));
            }
            if (((scenario.prefetch()) != (-1)) || ((fusion & (Fuseable.ASYNC)) != 0)) {
                verifier.accept(this.inputFusedAsyncErrorOutputFusedAsync(scenario));
            }
        });
    }

    @Test
    public final void sequenceOfNextAndCancel() {
        forEachScenario(scenarios_operatorSuccess(), ( scenario) -> {
        });
    }

    @Test
    public final void sequenceOfNextAndError() {
        forEachScenario(scenarios_operatorSuccess(), ( scenario) -> {
        });
    }
}

