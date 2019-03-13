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
package org.springframework.boot.actuate.health;


import Status.DOWN;
import Status.OUT_OF_SERVICE;
import Status.UNKNOWN;
import Status.UP;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;


/**
 * Tests for {@link Health}.
 *
 * @author Phillip Webb
 * @author Michael Pratt
 * @author Stephane Nicoll
 */
public class HealthTests {
    @Test
    public void statusMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Health.Builder(null, null)).withMessageContaining("Status must not be null");
    }

    @Test
    public void createWithStatus() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).isEmpty();
    }

    @Test
    public void createWithDetails() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).containsOnly(entry("a", "b"));
    }

    @Test
    public void equalsAndHashCode() {
        Health h1 = build();
        Health h2 = build();
        Health h3 = build();
        assertThat(h1).isEqualTo(h1);
        assertThat(h1).isEqualTo(h2);
        assertThat(h1).isNotEqualTo(h3);
        assertThat(h1.hashCode()).isEqualTo(h1.hashCode());
        assertThat(h1.hashCode()).isEqualTo(h2.hashCode());
        assertThat(h1.hashCode()).isNotEqualTo(h3.hashCode());
    }

    @Test
    public void withException() {
        RuntimeException ex = new RuntimeException("bang");
        Health health = build();
        assertThat(health.getDetails()).containsOnly(entry("a", "b"), entry("error", "java.lang.RuntimeException: bang"));
    }

    @Test
    public void withDetails() {
        Health health = build();
        assertThat(health.getDetails()).containsOnly(entry("a", "b"), entry("c", "d"));
    }

    @Test
    public void withDetailsMap() {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("a", "b");
        details.put("c", "d");
        Health health = build();
        assertThat(health.getDetails()).containsOnly(entry("a", "b"), entry("c", "d"));
    }

    @Test
    public void withDetailsMapDuplicateKeys() {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("c", "d");
        details.put("a", "e");
        Health health = build();
        assertThat(health.getDetails()).containsOnly(entry("a", "e"), entry("c", "d"));
    }

    @Test
    public void withDetailsMultipleMaps() {
        Map<String, Object> details1 = new LinkedHashMap<>();
        details1.put("a", "b");
        details1.put("c", "d");
        Map<String, Object> details2 = new LinkedHashMap<>();
        details1.put("a", "e");
        details1.put("1", "2");
        Health health = build();
        assertThat(health.getDetails()).containsOnly(entry("a", "e"), entry("c", "d"), entry("1", "2"));
    }

    @Test
    public void unknownWithDetails() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(UNKNOWN);
        assertThat(health.getDetails()).containsOnly(entry("a", "b"));
    }

    @Test
    public void unknown() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(UNKNOWN);
        assertThat(health.getDetails()).isEmpty();
    }

    @Test
    public void upWithDetails() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).containsOnly(entry("a", "b"));
    }

    @Test
    public void up() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).isEmpty();
    }

    @Test
    public void downWithException() {
        RuntimeException ex = new RuntimeException("bang");
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails()).containsOnly(entry("error", "java.lang.RuntimeException: bang"));
    }

    @Test
    public void down() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails()).isEmpty();
    }

    @Test
    public void outOfService() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(OUT_OF_SERVICE);
        assertThat(health.getDetails()).isEmpty();
    }

    @Test
    public void statusCode() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).isEmpty();
    }

    @Test
    public void status() {
        Health health = build();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails()).isEmpty();
    }
}

