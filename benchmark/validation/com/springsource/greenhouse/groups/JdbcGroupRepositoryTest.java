/**
 * Copyright 2012 the original author or authors.
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
package com.springsource.greenhouse.groups;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class JdbcGroupRepositoryTest {
    private EmbeddedDatabase db;

    private JdbcTemplate jdbcTemplate;

    private GroupRepository groupRepository;

    @Test
    public void shouldFindGroupByProfileKey() {
        Group group = groupRepository.findGroupBySlug("test-group");
        Assert.assertEquals("Test Group", group.getName());
        Assert.assertEquals("This is a test group", group.getDescription());
        Assert.assertEquals("#test", group.getHashtag());
    }
}

