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
package de.codecentric.boot.admin.server.notify.filter.web;


import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.eventstore.InMemoryEventStore;
import de.codecentric.boot.admin.server.web.servlet.AdminControllerHandlerMapping;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


public class NotificationFilterControllerTest {
    private final InstanceRepository repository = new de.codecentric.boot.admin.server.domain.entities.EventsourcingInstanceRepository(new InMemoryEventStore());

    private final NotificationFilterController controller = new NotificationFilterController(new de.codecentric.boot.admin.server.notify.filter.FilteringNotifier(new de.codecentric.boot.admin.server.notify.LoggingNotifier(repository), repository));

    private MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).setCustomHandlerMapping(() -> new AdminControllerHandlerMapping("/")).build();

    @Test
    public void test_missing_parameters() throws Exception {
        mvc.perform(post("/notifications/filters")).andExpect(status().isBadRequest());
    }

    @Test
    public void test_delete_notfound() throws Exception {
        mvc.perform(delete("/notifications/filters/abcdef")).andExpect(status().isNotFound());
    }

    @Test
    public void test_post_delete() throws Exception {
        String response = mvc.perform(post("/notifications/filters?instanceId=1337&ttl=10000")).andExpect(status().isOk()).andExpect(content().string(Matchers.not(Matchers.isEmptyString()))).andReturn().getResponse().getContentAsString();
        String id = extractId(response);
        mvc.perform(get("/notifications/filters")).andExpect(status().isOk());
        mvc.perform(delete("/notifications/filters/{id}", id)).andExpect(status().isOk());
        mvc.perform(get("/notifications/filters")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
    }
}

