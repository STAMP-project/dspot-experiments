/**
 * Copyright 2018 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.dag;


import Status.BLOCKED;
import Status.CANCELED;
import Status.DISABLED;
import Status.FAILURE;
import Status.KILLED;
import Status.KILLING;
import Status.READY;
import Status.RUNNING;
import Status.SUCCESS;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class StatusTest {
    @Test
    public void is_terminal() {
        // given
        final Map<Status, Boolean> isStatusTerminalMap = new HashMap<>();
        isStatusTerminalMap.put(BLOCKED, Boolean.FALSE);
        isStatusTerminalMap.put(CANCELED, Boolean.TRUE);
        isStatusTerminalMap.put(DISABLED, Boolean.TRUE);
        isStatusTerminalMap.put(FAILURE, Boolean.TRUE);
        isStatusTerminalMap.put(KILLED, Boolean.TRUE);
        isStatusTerminalMap.put(KILLING, Boolean.FALSE);
        isStatusTerminalMap.put(READY, Boolean.FALSE);
        isStatusTerminalMap.put(RUNNING, Boolean.FALSE);
        isStatusTerminalMap.put(SUCCESS, Boolean.TRUE);
        assertMapSizeMatchEnumSize(isStatusTerminalMap);
        for (final Map.Entry<Status, Boolean> entry : isStatusTerminalMap.entrySet()) {
            final Status key = entry.getKey();
            assertThat(key.isTerminal()).as("key: %s", key).isEqualTo(entry.getValue());
        }
    }

    @Test
    public void is_effectively_success() {
        // given
        final Map<Status, Boolean> isStatusEffectivelySuccess = new HashMap<>();
        isStatusEffectivelySuccess.put(BLOCKED, Boolean.FALSE);
        isStatusEffectivelySuccess.put(CANCELED, Boolean.FALSE);
        isStatusEffectivelySuccess.put(DISABLED, Boolean.TRUE);
        isStatusEffectivelySuccess.put(FAILURE, Boolean.FALSE);
        isStatusEffectivelySuccess.put(KILLED, Boolean.FALSE);
        isStatusEffectivelySuccess.put(KILLING, Boolean.FALSE);
        isStatusEffectivelySuccess.put(READY, Boolean.FALSE);
        isStatusEffectivelySuccess.put(RUNNING, Boolean.FALSE);
        isStatusEffectivelySuccess.put(SUCCESS, Boolean.TRUE);
        assertMapSizeMatchEnumSize(isStatusEffectivelySuccess);
        for (final Map.Entry<Status, Boolean> entry : isStatusEffectivelySuccess.entrySet()) {
            final Status key = entry.getKey();
            assertThat(key.isSuccessEffectively()).as("key: %s", key).isEqualTo(entry.getValue());
        }
    }

    @Test
    public void is_pre_run_state() {
        // given
        final Map<Status, Boolean> isPrerunStateMap = new HashMap<>();
        isPrerunStateMap.put(BLOCKED, Boolean.TRUE);
        isPrerunStateMap.put(CANCELED, Boolean.FALSE);
        isPrerunStateMap.put(DISABLED, Boolean.TRUE);
        isPrerunStateMap.put(FAILURE, Boolean.FALSE);
        isPrerunStateMap.put(KILLED, Boolean.FALSE);
        isPrerunStateMap.put(KILLING, Boolean.FALSE);
        isPrerunStateMap.put(READY, Boolean.TRUE);
        isPrerunStateMap.put(RUNNING, Boolean.FALSE);
        isPrerunStateMap.put(SUCCESS, Boolean.FALSE);
        assertMapSizeMatchEnumSize(isPrerunStateMap);
        for (final Map.Entry<Status, Boolean> entry : isPrerunStateMap.entrySet()) {
            final Status key = entry.getKey();
            assertThat(key.isPreRunState()).as("key: %s", key).isEqualTo(entry.getValue());
        }
    }
}

