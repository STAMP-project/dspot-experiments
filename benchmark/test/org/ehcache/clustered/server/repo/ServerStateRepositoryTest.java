/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered.server.repo;


import EhcacheEntityResponse.MapValue;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityResponse;
import org.ehcache.clustered.common.internal.messages.StateRepositoryOpMessage;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ServerStateRepositoryTest {
    @Test
    public void testInvokeOnNonExistentRepositorySucceeds() throws Exception {
        ServerStateRepository repository = new ServerStateRepository();
        EhcacheEntityResponse.MapValue response = ((EhcacheEntityResponse.MapValue) (repository.invoke(new StateRepositoryOpMessage.PutIfAbsentMessage("foo", "bar", "key1", "value1"))));
        Assert.assertThat(response.getValue(), Matchers.nullValue());
        response = ((EhcacheEntityResponse.MapValue) (repository.invoke(new StateRepositoryOpMessage.GetMessage("foo", "bar", "key1"))));
        Assert.assertThat(response.getValue(), Matchers.is("value1"));
    }

    @Test
    public void testInvokePutIfAbsent() throws Exception {
        ServerStateRepository repository = new ServerStateRepository();
        EhcacheEntityResponse.MapValue response = ((EhcacheEntityResponse.MapValue) (repository.invoke(new StateRepositoryOpMessage.PutIfAbsentMessage("foo", "bar", "key1", "value1"))));
        Assert.assertThat(response.getValue(), Matchers.nullValue());
        response = ((EhcacheEntityResponse.MapValue) (repository.invoke(new StateRepositoryOpMessage.PutIfAbsentMessage("foo", "bar", "key1", "value2"))));
        Assert.assertThat(response.getValue(), Matchers.is("value1"));
    }

    @Test
    public void testInvokeGet() throws Exception {
        ServerStateRepository repository = new ServerStateRepository();
        repository.invoke(new StateRepositoryOpMessage.PutIfAbsentMessage("foo", "bar", "key1", "value1"));
        EhcacheEntityResponse.MapValue response = ((EhcacheEntityResponse.MapValue) (repository.invoke(new StateRepositoryOpMessage.GetMessage("foo", "bar", "key1"))));
        Assert.assertThat(response.getValue(), Matchers.is("value1"));
    }

    @Test
    public void testInvokeEntrySet() throws Exception {
        ServerStateRepository repository = new ServerStateRepository();
        repository.invoke(new StateRepositoryOpMessage.PutIfAbsentMessage("foo", "bar", "key1", "value1"));
        repository.invoke(new StateRepositoryOpMessage.PutIfAbsentMessage("foo", "bar", "key2", "value2"));
        repository.invoke(new StateRepositoryOpMessage.PutIfAbsentMessage("foo", "bar", "key3", "value3"));
        EhcacheEntityResponse.MapValue response = ((EhcacheEntityResponse.MapValue) (repository.invoke(new StateRepositoryOpMessage.EntrySetMessage("foo", "bar"))));
        @SuppressWarnings("unchecked")
        Set<Map.Entry<String, String>> entrySet = ((Set<Map.Entry<String, String>>) (response.getValue()));
        Assert.assertThat(entrySet.size(), Matchers.is(3));
        Map.Entry<String, String> entry1 = new AbstractMap.SimpleEntry<>("key1", "value1");
        Map.Entry<String, String> entry2 = new AbstractMap.SimpleEntry<>("key2", "value2");
        Map.Entry<String, String> entry3 = new AbstractMap.SimpleEntry<>("key3", "value3");
        @SuppressWarnings("unchecked")
        Matcher<Iterable<? extends Map.Entry<String, String>>> matcher = Matchers.containsInAnyOrder(entry1, entry2, entry3);
        Assert.assertThat(entrySet, matcher);
    }
}

