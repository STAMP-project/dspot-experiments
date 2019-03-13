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
package org.assertj.core.api.date;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.DateAssert;
import org.assertj.core.api.DateAssertBaseTest;
import org.junit.jupiter.api.Test;


/**
 * Abstract class that factorize DateAssert tests with an int arg.
 * <p>
 * For the most part, date assertion tests are (whatever the concrete date assertion method invoked is) :
 * <ul>
 * <li>successful assertion test with an int</li>
 * <li>checking that DateAssert instance used for assertions is returned to allow fluent assertions chaining</li>
 * </ul>
 *
 * Subclasses are expected to define what is the invoked assertion method.
 *
 * @author Joel Costigliola
 */
public abstract class AbstractDateAssertWithOneIntArg_Test extends DateAssertBaseTest {
    protected int intArg;

    @Test
    public void should_verify_assertion_with_int_arg() {
        assertionInvocationWithOneIntArg();
        verifyAssertionInvocation();
    }

    @Test
    public void should_return_this() {
        DateAssert returned = assertionInvocationWithOneIntArg();
        Assertions.assertThat(returned).isSameAs(assertions);
    }
}

