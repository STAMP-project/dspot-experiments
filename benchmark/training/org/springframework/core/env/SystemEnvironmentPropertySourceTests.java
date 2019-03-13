/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.core.env;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link SystemEnvironmentPropertySource}.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public class SystemEnvironmentPropertySourceTests {
    private Map<String, Object> envMap;

    private PropertySource<?> ps;

    @Test
    public void none() {
        Assert.assertThat(ps.containsProperty("a.key"), CoreMatchers.equalTo(false));
        Assert.assertThat(ps.getProperty("a.key"), CoreMatchers.equalTo(null));
    }

    @Test
    public void normalWithoutPeriod() {
        envMap.put("akey", "avalue");
        Assert.assertThat(ps.containsProperty("akey"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.getProperty("akey"), CoreMatchers.equalTo(((Object) ("avalue"))));
    }

    @Test
    public void normalWithPeriod() {
        envMap.put("a.key", "a.value");
        Assert.assertThat(ps.containsProperty("a.key"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.getProperty("a.key"), CoreMatchers.equalTo(((Object) ("a.value"))));
    }

    @Test
    public void withUnderscore() {
        envMap.put("a_key", "a_value");
        Assert.assertThat(ps.containsProperty("a_key"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("a.key"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.getProperty("a_key"), CoreMatchers.equalTo(((Object) ("a_value"))));
        Assert.assertThat(ps.getProperty("a.key"), CoreMatchers.equalTo(((Object) ("a_value"))));
    }

    @Test
    public void withBothPeriodAndUnderscore() {
        envMap.put("a_key", "a_value");
        envMap.put("a.key", "a.value");
        Assert.assertThat(ps.getProperty("a_key"), CoreMatchers.equalTo(((Object) ("a_value"))));
        Assert.assertThat(ps.getProperty("a.key"), CoreMatchers.equalTo(((Object) ("a.value"))));
    }

    @Test
    public void withUppercase() {
        envMap.put("A_KEY", "a_value");
        envMap.put("A_LONG_KEY", "a_long_value");
        envMap.put("A_DOT.KEY", "a_dot_value");
        envMap.put("A_HYPHEN-KEY", "a_hyphen_value");
        Assert.assertThat(ps.containsProperty("A_KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A.KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A-KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("a_key"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("a.key"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("a-key"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A_LONG_KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A.LONG.KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A-LONG-KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A.LONG-KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A-LONG.KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A_long_KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A.long.KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A-long-KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A.long-KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A-long.KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A_DOT.KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A-DOT.KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A_dot.KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A-dot.KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A_HYPHEN-KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A.HYPHEN-KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A_hyphen-KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.containsProperty("A.hyphen-KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.getProperty("A_KEY"), CoreMatchers.equalTo("a_value"));
        Assert.assertThat(ps.getProperty("A.KEY"), CoreMatchers.equalTo("a_value"));
        Assert.assertThat(ps.getProperty("A-KEY"), CoreMatchers.equalTo("a_value"));
        Assert.assertThat(ps.getProperty("a_key"), CoreMatchers.equalTo("a_value"));
        Assert.assertThat(ps.getProperty("a.key"), CoreMatchers.equalTo("a_value"));
        Assert.assertThat(ps.getProperty("a-key"), CoreMatchers.equalTo("a_value"));
        Assert.assertThat(ps.getProperty("A_LONG_KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A.LONG.KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A-LONG-KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A.LONG-KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A-LONG.KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A_long_KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A.long.KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A-long-KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A.long-KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A-long.KEY"), CoreMatchers.equalTo("a_long_value"));
        Assert.assertThat(ps.getProperty("A_DOT.KEY"), CoreMatchers.equalTo("a_dot_value"));
        Assert.assertThat(ps.getProperty("A-DOT.KEY"), CoreMatchers.equalTo("a_dot_value"));
        Assert.assertThat(ps.getProperty("A_dot.KEY"), CoreMatchers.equalTo("a_dot_value"));
        Assert.assertThat(ps.getProperty("A-dot.KEY"), CoreMatchers.equalTo("a_dot_value"));
        Assert.assertThat(ps.getProperty("A_HYPHEN-KEY"), CoreMatchers.equalTo("a_hyphen_value"));
        Assert.assertThat(ps.getProperty("A.HYPHEN-KEY"), CoreMatchers.equalTo("a_hyphen_value"));
        Assert.assertThat(ps.getProperty("A_hyphen-KEY"), CoreMatchers.equalTo("a_hyphen_value"));
        Assert.assertThat(ps.getProperty("A.hyphen-KEY"), CoreMatchers.equalTo("a_hyphen_value"));
    }

    @Test
    @SuppressWarnings("serial")
    public void withSecurityConstraints() throws Exception {
        envMap = new HashMap<String, Object>() {
            @Override
            public boolean containsKey(Object key) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Set<String> keySet() {
                return new HashSet<>(super.keySet());
            }
        };
        envMap.put("A_KEY", "a_value");
        ps = new SystemEnvironmentPropertySource("sysEnv", envMap) {
            @Override
            protected boolean isSecurityManagerPresent() {
                return true;
            }
        };
        Assert.assertThat(ps.containsProperty("A_KEY"), CoreMatchers.equalTo(true));
        Assert.assertThat(ps.getProperty("A_KEY"), CoreMatchers.equalTo(((Object) ("a_value"))));
    }
}

