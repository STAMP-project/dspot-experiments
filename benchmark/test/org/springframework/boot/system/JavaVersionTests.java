/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.system;


import JavaVersion.EIGHT;
import JavaVersion.NINE;
import org.junit.Test;


/**
 * Tests for {@link JavaVersion}.
 *
 * @author Stephane Nicoll
 */
public class JavaVersionTests {
    @Test
    public void getJavaVersionShouldBeAvailable() {
        assertThat(JavaVersion.getJavaVersion()).isNotNull();
    }

    @Test
    public void compareToWhenComparingSmallerToGreaterShouldBeLessThanZero() {
        assertThat(EIGHT.compareTo(NINE)).isLessThan(0);
    }

    @Test
    public void compareToWhenComparingGreaterToSmallerShouldBeGreaterThanZero() {
        assertThat(NINE.compareTo(EIGHT)).isGreaterThan(0);
    }

    @Test
    public void compareToWhenComparingSameShouldBeZero() {
        assertThat(EIGHT.compareTo(EIGHT)).isEqualTo(0);
    }

    @Test
    public void isEqualOrNewerThanWhenComparingSameShouldBeTrue() {
        assertThat(EIGHT.isEqualOrNewerThan(EIGHT)).isTrue();
    }

    @Test
    public void isEqualOrNewerThanWhenSmallerToGreaterShouldBeFalse() {
        assertThat(EIGHT.isEqualOrNewerThan(NINE)).isFalse();
    }

    @Test
    public void isEqualOrNewerThanWhenGreaterToSmallerShouldBeTrue() {
        assertThat(NINE.isEqualOrNewerThan(EIGHT)).isTrue();
    }

    @Test
    public void isOlderThanThanWhenComparingSameShouldBeFalse() {
        assertThat(EIGHT.isOlderThan(EIGHT)).isFalse();
    }

    @Test
    public void isOlderThanWhenSmallerToGreaterShouldBeTrue() {
        assertThat(EIGHT.isOlderThan(NINE)).isTrue();
    }

    @Test
    public void isOlderThanWhenGreaterToSmallerShouldBeFalse() {
        assertThat(NINE.isOlderThan(EIGHT)).isFalse();
    }
}

