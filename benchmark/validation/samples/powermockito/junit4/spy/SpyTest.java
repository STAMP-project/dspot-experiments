/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samples.powermockito.junit4.spy;


import org.assertj.core.api.Java6Assertions;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.spy.SpyObject;
import samples.suppressmethod.SuppressMethod;
import samples.suppressmethod.SuppressMethodParent;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ SpyObject.class, SuppressMethod.class, SuppressMethodParent.class })
public class SpyTest {
    private SpyObject partialMock = null;

    @Test
    public void should_stub_spying_on_private_method_works() throws Exception {
        when(partialMock, "getMyString").thenReturn("ikk2");
        MatcherAssert.assertThat(partialMock.getMyString(), CoreMatchers.equalTo("ikk2"));
        MatcherAssert.assertThat(partialMock.getStringTwo(), CoreMatchers.equalTo("two"));
    }

    @Test
    public void should_call_real_method_when_spy_method_is_not_stubbed() {
        Java6Assertions.assertThat(partialMock.getMyString()).as("Real method is called").isEqualTo(new SpyObject().getMyString());
    }

    @Test
    public void testSuppressMethodWhenObjectIsSpy() throws Exception {
        suppress(method(SuppressMethod.class, "myMethod"));
        SuppressMethod tested = spy(new SuppressMethod());
        Assert.assertEquals(0, tested.myMethod());
    }

    @Test
    public void testSuppressMethodInParentOnlyWhenObjectIsSpy() throws Exception {
        suppress(method(SuppressMethodParent.class, "myMethod"));
        SuppressMethod tested = spy(new SuppressMethod());
        Assert.assertEquals(20, tested.myMethod());
    }

    @Test
    public void testDoNothingForSpy() {
        doNothing().when(partialMock).throwException();
        partialMock.throwException();
    }
}

