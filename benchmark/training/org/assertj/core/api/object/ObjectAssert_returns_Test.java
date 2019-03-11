/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api.object;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssertBaseTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.test.Jedi;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link AbstractObjectAssert#returns(Object, Function)}</code>.
 *
 * @author Takuya "Mura-Mi" Murakami
 */
public class ObjectAssert_returns_Test extends ObjectAssertBaseTest {
    @Test
    public void should_fail_with_throwing_NullPointerException_if_method_is_null() {
        ThrowingCallable code = () -> assertions.returns("May the force be with you.", null);
        Assertions.assertThatThrownBy(code).isExactlyInstanceOf(NullPointerException.class).hasMessage("The given getter method/Function must not be null");
    }

    @Test
    public void perform_assertion_like_users() {
        Jedi yoda = new Jedi("Yoda", "Green");
        Assertions.assertThat(yoda).returns("Yoda", Assertions.from(Jedi::getName)).returns("Yoda", Jedi::getName);
    }
}

