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
package org.springframework.boot.actuate.audit;


import java.util.Collections;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


/**
 * Tests for {@link AuditEvent}.
 *
 * @author Dave Syer
 * @author Vedran Pavic
 */
public class AuditEventTests {
    @Test
    public void nowEvent() {
        AuditEvent event = new AuditEvent("phil", "UNKNOWN", Collections.singletonMap("a", ((Object) ("b"))));
        assertThat(event.getData().get("a")).isEqualTo("b");
        assertThat(event.getType()).isEqualTo("UNKNOWN");
        assertThat(event.getPrincipal()).isEqualTo("phil");
        assertThat(event.getTimestamp()).isNotNull();
    }

    @Test
    public void convertStringsToData() {
        AuditEvent event = new AuditEvent("phil", "UNKNOWN", "a=b", "c=d");
        assertThat(event.getData().get("a")).isEqualTo("b");
        assertThat(event.getData().get("c")).isEqualTo("d");
    }

    @Test
    public void nullPrincipalIsMappedToEmptyString() {
        AuditEvent auditEvent = new AuditEvent(null, "UNKNOWN", Collections.singletonMap("a", ((Object) ("b"))));
        assertThat(auditEvent.getPrincipal()).isEmpty();
    }

    @Test
    public void nullTimestamp() {
        assertThatIllegalArgumentException().isThrownBy(() -> new AuditEvent(null, "phil", "UNKNOWN", Collections.singletonMap("a", ((Object) ("b"))))).withMessageContaining("Timestamp must not be null");
    }

    @Test
    public void nullType() {
        assertThatIllegalArgumentException().isThrownBy(() -> new AuditEvent("phil", null, Collections.singletonMap("a", ((Object) ("b"))))).withMessageContaining("Type must not be null");
    }

    @Test
    public void jsonFormat() throws Exception {
        AuditEvent event = new AuditEvent("johannes", "UNKNOWN", Collections.singletonMap("type", ((Object) ("BadCredentials"))));
        String json = Jackson2ObjectMapperBuilder.json().build().writeValueAsString(event);
        JSONObject jsonObject = new JSONObject(json);
        assertThat(jsonObject.getString("type")).isEqualTo("UNKNOWN");
        assertThat(jsonObject.getJSONObject("data").getString("type")).isEqualTo("BadCredentials");
    }
}

