/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.examples.datastore.snippets;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.QueryResults;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ITQuerySnippets {
    private static Datastore datastore;

    private static Entity entity1;

    private static Entity entity2;

    private static final String KIND = "kind_" + (UUID.randomUUID().toString().replace("-", ""));

    private static final Function<ProjectionEntity, String> ENTITY_TO_DESCRIPTION_FUNCTION = new Function<ProjectionEntity, String>() {
        @Override
        public String apply(ProjectionEntity entity) {
            return entity.getString("description");
        }
    };

    @Rule
    public Timeout globalTimeout = Timeout.seconds(60);

    @Test
    public void testNewQuery() throws InterruptedException {
        QuerySnippets transactionSnippets = new QuerySnippets(ITQuerySnippets.datastore);
        QueryResults<?> results = transactionSnippets.newQuery(ITQuerySnippets.KIND);
        Set<?> resultSet = Sets.newHashSet(results);
        while ((!(resultSet.contains(ITQuerySnippets.entity1))) || (!(resultSet.contains(ITQuerySnippets.entity2)))) {
            Thread.sleep(500);
            resultSet = Sets.newHashSet(results);
        } 
    }

    @Test
    public void testNewTypedQuery() throws InterruptedException {
        QuerySnippets transactionSnippets = new QuerySnippets(ITQuerySnippets.datastore);
        QueryResults<Entity> results = transactionSnippets.newTypedQuery(ITQuerySnippets.KIND);
        Set<Entity> resultSet = Sets.newHashSet(results);
        while ((!(resultSet.contains(ITQuerySnippets.entity1))) || (!(resultSet.contains(ITQuerySnippets.entity2)))) {
            Thread.sleep(500);
            resultSet = Sets.newHashSet(results);
        } 
    }

    @Test
    public void testNewEntityQuery() throws InterruptedException {
        QuerySnippets transactionSnippets = new QuerySnippets(ITQuerySnippets.datastore);
        QueryResults<Entity> results = transactionSnippets.newEntityQuery(ITQuerySnippets.KIND);
        Set<Entity> resultSet = Sets.newHashSet(results);
        while ((!(resultSet.contains(ITQuerySnippets.entity1))) || (!(resultSet.contains(ITQuerySnippets.entity2)))) {
            Thread.sleep(500);
            resultSet = Sets.newHashSet(results);
        } 
    }

    @Test
    public void testNewKeyQuery() throws InterruptedException {
        QuerySnippets transactionSnippets = new QuerySnippets(ITQuerySnippets.datastore);
        QueryResults<Key> results = transactionSnippets.newKeyQuery(ITQuerySnippets.KIND);
        Set<Key> resultSet = Sets.newHashSet(results);
        while ((!(resultSet.contains(ITQuerySnippets.entity1.getKey()))) || (!(resultSet.contains(ITQuerySnippets.entity2.getKey())))) {
            Thread.sleep(500);
            resultSet = Sets.newHashSet(results);
        } 
    }

    @Test
    public void testNewProjectionEntityQuery() throws InterruptedException {
        QuerySnippets transactionSnippets = new QuerySnippets(ITQuerySnippets.datastore);
        QueryResults<ProjectionEntity> results = transactionSnippets.newProjectionEntityQuery(ITQuerySnippets.KIND, "description");
        Set<String> resultSet = Sets.newHashSet(Iterators.transform(results, ITQuerySnippets.ENTITY_TO_DESCRIPTION_FUNCTION));
        while ((!(resultSet.contains(ITQuerySnippets.entity1.getString("description")))) || (!(resultSet.contains(ITQuerySnippets.entity2.getString("description"))))) {
            Thread.sleep(500);
            resultSet = Sets.newHashSet(Iterators.transform(results, ITQuerySnippets.ENTITY_TO_DESCRIPTION_FUNCTION));
        } 
    }
}

