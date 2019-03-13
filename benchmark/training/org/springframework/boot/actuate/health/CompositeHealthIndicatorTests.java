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


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mock;


/**
 * Tests for {@link CompositeHealthIndicator}
 *
 * @author Tyler J. Frederick
 * @author Phillip Webb
 * @author Christian Dupuis
 */
public class CompositeHealthIndicatorTests {
    private HealthAggregator healthAggregator;

    @Mock
    private HealthIndicator one;

    @Mock
    private HealthIndicator two;

    @Test
    public void createWithIndicators() {
        Map<String, HealthIndicator> indicators = new HashMap<>();
        indicators.put("one", this.one);
        indicators.put("two", this.two);
        CompositeHealthIndicator composite = new CompositeHealthIndicator(this.healthAggregator, indicators);
        Health result = composite.health();
        assertThat(result.getDetails()).hasSize(2);
        assertThat(result.getDetails()).containsEntry("one", new Health.Builder().unknown().withDetail("1", "1").build());
        assertThat(result.getDetails()).containsEntry("two", new Health.Builder().unknown().withDetail("2", "2").build());
    }

    @Test
    public void testSerialization() throws Exception {
        Map<String, HealthIndicator> indicators = new HashMap<>();
        indicators.put("db1", this.one);
        indicators.put("db2", this.two);
        CompositeHealthIndicator innerComposite = new CompositeHealthIndicator(this.healthAggregator, indicators);
        CompositeHealthIndicator composite = new CompositeHealthIndicator(this.healthAggregator, Collections.singletonMap("db", innerComposite));
        Health result = composite.health();
        ObjectMapper mapper = new ObjectMapper();
        assertThat(mapper.writeValueAsString(result)).isEqualTo(("{\"status\":\"UNKNOWN\",\"details\":{\"db\":{\"status\":\"UNKNOWN\"" + ((",\"details\":{\"db1\":{\"status\":\"UNKNOWN\",\"details\"" + ":{\"1\":\"1\"}},\"db2\":{\"status\":\"UNKNOWN\",\"details\"") + ":{\"2\":\"2\"}}}}}}")));
    }
}

