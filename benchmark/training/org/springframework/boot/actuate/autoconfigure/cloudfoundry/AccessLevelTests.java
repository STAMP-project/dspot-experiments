/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.cloudfoundry;


import AccessLevel.FULL;
import AccessLevel.RESTRICTED;
import org.junit.Test;


/**
 * Tests for {@link AccessLevel}.
 *
 * @author Madhura Bhave
 */
public class AccessLevelTests {
    @Test
    public void accessToHealthEndpointShouldNotBeRestricted() {
        assertThat(RESTRICTED.isAccessAllowed("health")).isTrue();
        assertThat(FULL.isAccessAllowed("health")).isTrue();
    }

    @Test
    public void accessToInfoEndpointShouldNotBeRestricted() {
        assertThat(RESTRICTED.isAccessAllowed("info")).isTrue();
        assertThat(FULL.isAccessAllowed("info")).isTrue();
    }

    @Test
    public void accessToDiscoveryEndpointShouldNotBeRestricted() {
        assertThat(RESTRICTED.isAccessAllowed("")).isTrue();
        assertThat(FULL.isAccessAllowed("")).isTrue();
    }

    @Test
    public void accessToAnyOtherEndpointShouldBeRestricted() {
        assertThat(RESTRICTED.isAccessAllowed("env")).isFalse();
        assertThat(FULL.isAccessAllowed("")).isTrue();
    }
}

