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
package org.springframework.boot.actuate.mongo;


import Status.DOWN;
import Status.UP;
import com.mongodb.MongoException;
import org.bson.Document;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;


/**
 * Tests for {@link MongoHealthIndicator}.
 *
 * @author Christian Dupuis
 */
public class MongoHealthIndicatorTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void mongoIsUp() {
        Document commandResult = Mockito.mock(Document.class);
        BDDMockito.given(commandResult.getString("version")).willReturn("2.6.4");
        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        BDDMockito.given(mongoTemplate.executeCommand("{ buildInfo: 1 }")).willReturn(commandResult);
        MongoHealthIndicator healthIndicator = new MongoHealthIndicator(mongoTemplate);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("version")).isEqualTo("2.6.4");
        Mockito.verify(commandResult).getString("version");
        Mockito.verify(mongoTemplate).executeCommand("{ buildInfo: 1 }");
    }

    @Test
    public void mongoIsDown() {
        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        BDDMockito.given(mongoTemplate.executeCommand("{ buildInfo: 1 }")).willThrow(new MongoException("Connection failed"));
        MongoHealthIndicator healthIndicator = new MongoHealthIndicator(mongoTemplate);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(((String) (health.getDetails().get("error")))).contains("Connection failed");
        Mockito.verify(mongoTemplate).executeCommand("{ buildInfo: 1 }");
    }
}

