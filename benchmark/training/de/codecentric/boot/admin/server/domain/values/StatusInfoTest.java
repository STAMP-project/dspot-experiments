/**
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.boot.admin.server.domain.values;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;


public class StatusInfoTest {
    @Test
    public void invariants() {
        assertThatThrownBy(() -> StatusInfo.valueOf("")).isInstanceOf(IllegalArgumentException.class).hasMessage("'status' must not be empty.");
    }

    @Test
    public void test_isMethods() {
        assertThat(StatusInfo.valueOf("FOO").isUp()).isFalse();
        assertThat(StatusInfo.valueOf("FOO").isDown()).isFalse();
        assertThat(StatusInfo.valueOf("FOO").isUnknown()).isFalse();
        assertThat(StatusInfo.valueOf("FOO").isOffline()).isFalse();
        assertThat(StatusInfo.ofUp().isUp()).isTrue();
        assertThat(StatusInfo.ofUp().isDown()).isFalse();
        assertThat(StatusInfo.ofUp().isUnknown()).isFalse();
        assertThat(StatusInfo.ofUp().isOffline()).isFalse();
        assertThat(StatusInfo.ofDown().isUp()).isFalse();
        assertThat(StatusInfo.ofDown().isDown()).isTrue();
        assertThat(StatusInfo.ofDown().isUnknown()).isFalse();
        assertThat(StatusInfo.ofDown().isOffline()).isFalse();
        assertThat(StatusInfo.ofUnknown().isUp()).isFalse();
        assertThat(StatusInfo.ofUnknown().isDown()).isFalse();
        assertThat(StatusInfo.ofUnknown().isUnknown()).isTrue();
        assertThat(StatusInfo.ofUnknown().isOffline()).isFalse();
        assertThat(StatusInfo.ofOffline().isUp()).isFalse();
        assertThat(StatusInfo.ofOffline().isDown()).isFalse();
        assertThat(StatusInfo.ofOffline().isUnknown()).isFalse();
        assertThat(StatusInfo.ofOffline().isOffline()).isTrue();
    }

    @Test
    public void from_map_should_return_same_result() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", "UP");
        map.put("details", Collections.singletonMap("foo", "bar"));
        assertThat(StatusInfo.from(map)).isEqualTo(StatusInfo.ofUp(Collections.singletonMap("foo", "bar")));
    }

    @Test
    public void should_sort_by_status_order() {
        List<String> unordered = Arrays.asList(StatusInfo.STATUS_OUT_OF_SERVICE, StatusInfo.STATUS_UNKNOWN, StatusInfo.STATUS_OFFLINE, StatusInfo.STATUS_DOWN, StatusInfo.STATUS_UP, StatusInfo.STATUS_RESTRICTED);
        List<String> ordered = unordered.stream().sorted(StatusInfo.severity()).collect(Collectors.toList());
        assertThat(ordered).containsExactly(StatusInfo.STATUS_DOWN, StatusInfo.STATUS_OUT_OF_SERVICE, StatusInfo.STATUS_OFFLINE, StatusInfo.STATUS_UNKNOWN, StatusInfo.STATUS_RESTRICTED, StatusInfo.STATUS_UP);
    }
}

