/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.messaging.simp;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectFactory;


/**
 * Unit tests for {@link org.springframework.messaging.simp.SimpSessionScope}.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class SimpSessionScopeTests {
    private SimpSessionScope scope;

    @SuppressWarnings("rawtypes")
    private ObjectFactory objectFactory;

    private SimpAttributes simpAttributes;

    @Test
    public void get() {
        this.simpAttributes.setAttribute("name", "value");
        Object actual = this.scope.get("name", this.objectFactory);
        Assert.assertThat(actual, is("value"));
    }

    @Test
    public void getWithObjectFactory() {
        BDDMockito.given(this.objectFactory.getObject()).willReturn("value");
        Object actual = this.scope.get("name", this.objectFactory);
        Assert.assertThat(actual, is("value"));
        Assert.assertThat(this.simpAttributes.getAttribute("name"), is("value"));
    }

    @Test
    public void remove() {
        this.simpAttributes.setAttribute("name", "value");
        Object removed = this.scope.remove("name");
        Assert.assertThat(removed, is("value"));
        Assert.assertThat(this.simpAttributes.getAttribute("name"), nullValue());
        removed = this.scope.remove("name");
        Assert.assertThat(removed, nullValue());
    }

    @Test
    public void registerDestructionCallback() {
        Runnable runnable = Mockito.mock(Runnable.class);
        this.scope.registerDestructionCallback("name", runnable);
        this.simpAttributes.sessionCompleted();
        Mockito.verify(runnable, Mockito.times(1)).run();
    }

    @Test
    public void getSessionId() {
        Assert.assertThat(this.scope.getConversationId(), is("session1"));
    }
}

