/**
 * Copyright 2008-2014 the original author or authors.
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
package org.springframework.batch.core.step.tasklet;


import ConfigurableSystemProcessExitCodeMapper.ELSE_KEY;
import ExitStatus.COMPLETED;
import ExitStatus.EXECUTING;
import ExitStatus.FAILED;
import ExitStatus.NOOP;
import ExitStatus.UNKNOWN;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;


/**
 * Tests for {@link ConfigurableSystemProcessExitCodeMapper}
 */
public class ConfigurableSystemProcessExitCodeMapperTests {
    private ConfigurableSystemProcessExitCodeMapper mapper = new ConfigurableSystemProcessExitCodeMapper();

    /**
     * Regular usage scenario - mapping adheres to injected values
     */
    @Test
    public void testMapping() {
        @SuppressWarnings("serial")
        Map<Object, ExitStatus> mappings = new HashMap<Object, ExitStatus>() {
            {
                put(0, COMPLETED);
                put(1, FAILED);
                put(2, EXECUTING);
                put(3, NOOP);
                put(4, UNKNOWN);
                put(ELSE_KEY, UNKNOWN);
            }
        };
        mapper.setMappings(mappings);
        // check explicitly defined values
        for (Map.Entry<Object, ExitStatus> entry : mappings.entrySet()) {
            if (entry.getKey().equals(ELSE_KEY))
                continue;

            int exitCode = ((Integer) (entry.getKey()));
            Assert.assertSame(entry.getValue(), mapper.getExitStatus(exitCode));
        }
        // check the else clause
        Assert.assertSame(mappings.get(ELSE_KEY), mapper.getExitStatus(5));
    }

    /**
     * Else clause is required in the injected map - setter checks its presence.
     */
    @Test
    public void testSetMappingsMissingElseClause() {
        Map<Object, ExitStatus> missingElse = new HashMap<>();
        try {
            mapper.setMappings(missingElse);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
        Map<Object, ExitStatus> containsElse = Collections.<Object, ExitStatus>singletonMap(ELSE_KEY, FAILED);
        // no error expected now
        mapper.setMappings(containsElse);
    }
}

