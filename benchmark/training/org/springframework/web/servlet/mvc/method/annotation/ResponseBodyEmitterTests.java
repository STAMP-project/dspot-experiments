/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.web.servlet.mvc.method.annotation;


import MediaType.TEXT_PLAIN;
import ResponseBodyEmitter.Handler;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ResponseBodyEmitter}.
 *
 * @author Rossen Stoyanchev
 * @author Tomasz Nurkiewicz
 */
public class ResponseBodyEmitterTests {
    private ResponseBodyEmitter emitter;

    @Mock
    private Handler handler;

    @Test
    public void sendBeforeHandlerInitialized() throws Exception {
        this.emitter.send("foo", TEXT_PLAIN);
        this.emitter.send("bar", TEXT_PLAIN);
        this.emitter.complete();
        Mockito.verifyNoMoreInteractions(this.handler);
        this.emitter.initialize(this.handler);
        Mockito.verify(this.handler).send("foo", TEXT_PLAIN);
        Mockito.verify(this.handler).send("bar", TEXT_PLAIN);
        Mockito.verify(this.handler).complete();
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void sendDuplicateBeforeHandlerInitialized() throws Exception {
        this.emitter.send("foo", TEXT_PLAIN);
        this.emitter.send("foo", TEXT_PLAIN);
        this.emitter.complete();
        Mockito.verifyNoMoreInteractions(this.handler);
        this.emitter.initialize(this.handler);
        Mockito.verify(this.handler, Mockito.times(2)).send("foo", TEXT_PLAIN);
        Mockito.verify(this.handler).complete();
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void sendBeforeHandlerInitializedWithError() throws Exception {
        IllegalStateException ex = new IllegalStateException();
        this.emitter.send("foo", TEXT_PLAIN);
        this.emitter.send("bar", TEXT_PLAIN);
        this.emitter.completeWithError(ex);
        Mockito.verifyNoMoreInteractions(this.handler);
        this.emitter.initialize(this.handler);
        Mockito.verify(this.handler).send("foo", TEXT_PLAIN);
        Mockito.verify(this.handler).send("bar", TEXT_PLAIN);
        Mockito.verify(this.handler).completeWithError(ex);
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test(expected = IllegalStateException.class)
    public void sendFailsAfterComplete() throws Exception {
        this.emitter.complete();
        this.emitter.send("foo");
    }

    @Test
    public void sendAfterHandlerInitialized() throws Exception {
        this.emitter.initialize(this.handler);
        Mockito.verify(this.handler).onTimeout(ArgumentMatchers.any());
        Mockito.verify(this.handler).onError(ArgumentMatchers.any());
        Mockito.verify(this.handler).onCompletion(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(this.handler);
        this.emitter.send("foo", TEXT_PLAIN);
        this.emitter.send("bar", TEXT_PLAIN);
        this.emitter.complete();
        Mockito.verify(this.handler).send("foo", TEXT_PLAIN);
        Mockito.verify(this.handler).send("bar", TEXT_PLAIN);
        Mockito.verify(this.handler).complete();
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void sendAfterHandlerInitializedWithError() throws Exception {
        this.emitter.initialize(this.handler);
        Mockito.verify(this.handler).onTimeout(ArgumentMatchers.any());
        Mockito.verify(this.handler).onError(ArgumentMatchers.any());
        Mockito.verify(this.handler).onCompletion(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(this.handler);
        IllegalStateException ex = new IllegalStateException();
        this.emitter.send("foo", TEXT_PLAIN);
        this.emitter.send("bar", TEXT_PLAIN);
        this.emitter.completeWithError(ex);
        Mockito.verify(this.handler).send("foo", TEXT_PLAIN);
        Mockito.verify(this.handler).send("bar", TEXT_PLAIN);
        Mockito.verify(this.handler).completeWithError(ex);
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void sendWithError() throws Exception {
        this.emitter.initialize(this.handler);
        Mockito.verify(this.handler).onTimeout(ArgumentMatchers.any());
        Mockito.verify(this.handler).onError(ArgumentMatchers.any());
        Mockito.verify(this.handler).onCompletion(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(this.handler);
        IOException failure = new IOException();
        Mockito.doThrow(failure).when(this.handler).send("foo", TEXT_PLAIN);
        try {
            this.emitter.send("foo", TEXT_PLAIN);
            Assert.fail("Expected exception");
        } catch (IOException ex) {
            // expected
        }
        Mockito.verify(this.handler).send("foo", TEXT_PLAIN);
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void onTimeoutBeforeHandlerInitialized() throws Exception {
        Runnable runnable = Mockito.mock(Runnable.class);
        this.emitter.onTimeout(runnable);
        this.emitter.initialize(this.handler);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(this.handler).onTimeout(captor.capture());
        Mockito.verify(this.handler).onCompletion(ArgumentMatchers.any());
        Assert.assertNotNull(captor.getValue());
        captor.getValue().run();
        Mockito.verify(runnable).run();
    }

    @Test
    public void onTimeoutAfterHandlerInitialized() throws Exception {
        this.emitter.initialize(this.handler);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(this.handler).onTimeout(captor.capture());
        Mockito.verify(this.handler).onCompletion(ArgumentMatchers.any());
        Runnable runnable = Mockito.mock(Runnable.class);
        this.emitter.onTimeout(runnable);
        Assert.assertNotNull(captor.getValue());
        captor.getValue().run();
        Mockito.verify(runnable).run();
    }

    @Test
    public void onCompletionBeforeHandlerInitialized() throws Exception {
        Runnable runnable = Mockito.mock(Runnable.class);
        this.emitter.onCompletion(runnable);
        this.emitter.initialize(this.handler);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(this.handler).onTimeout(ArgumentMatchers.any());
        Mockito.verify(this.handler).onCompletion(captor.capture());
        Assert.assertNotNull(captor.getValue());
        captor.getValue().run();
        Mockito.verify(runnable).run();
    }

    @Test
    public void onCompletionAfterHandlerInitialized() throws Exception {
        this.emitter.initialize(this.handler);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(this.handler).onTimeout(ArgumentMatchers.any());
        Mockito.verify(this.handler).onCompletion(captor.capture());
        Runnable runnable = Mockito.mock(Runnable.class);
        this.emitter.onCompletion(runnable);
        Assert.assertNotNull(captor.getValue());
        captor.getValue().run();
        Mockito.verify(runnable).run();
    }
}

