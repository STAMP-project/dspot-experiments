/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws.ddbstream;


import com.amazonaws.services.dynamodbv2.model.Shard;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ShardListTest {
    @Test
    public void nextReturnsShardWithParent() throws Exception {
        Shard first = new Shard().withShardId("first_shard").withParentShardId("other_shard_id");
        Shard second = new Shard().withParentShardId("first_shard").withShardId("second_shard");
        ShardList shards = new ShardList();
        shards.add(first);
        shards.add(second);
        Assert.assertThat(shards.nextAfter(first), CoreMatchers.is(second));
    }

    @Test
    public void nextWithNullReturnsFirstKnownShard() throws Exception {
        Shard first = new Shard().withShardId("first_shard");
        Shard second = new Shard().withParentShardId("first_shard").withShardId("second_shard");
        ShardList shards = new ShardList();
        shards.add(first);
        shards.add(second);
        Assert.assertThat(shards.nextAfter(first), CoreMatchers.is(second));
    }

    @Test
    public void reAddingEntriesMaintainsOrder() throws Exception {
        Shard first = new Shard().withShardId("first_shard");
        Shard second = new Shard().withParentShardId("first_shard").withShardId("second_shard");
        ShardList shards = new ShardList();
        shards.add(first);
        shards.add(second);
        Assert.assertThat(shards.nextAfter(first), CoreMatchers.is(second));
        Shard second2 = new Shard().withParentShardId("first_shard").withShardId("second_shard");
        Shard third = new Shard().withParentShardId("second_shard").withShardId("third_shard");
        shards.add(second2);
        shards.add(third);
        Assert.assertThat(shards.nextAfter(first), CoreMatchers.is(second));
        Assert.assertThat(shards.nextAfter(second), CoreMatchers.is(third));
    }

    @Test
    public void firstShardGetsTheFirstWithoutAParent() throws Exception {
        ShardList shards = new ShardList();
        shards.addAll(ShardListTest.createShards(null, "a", "b", "c", "d"));
        Assert.assertThat(shards.first().getShardId(), CoreMatchers.is("a"));
    }

    @Test
    public void firstShardGetsTheFirstWithAnUnknownParent() throws Exception {
        ShardList shards = new ShardList();
        shards.addAll(ShardListTest.createShards("a", "b", "c", "d"));
        Assert.assertThat(shards.first().getShardId(), CoreMatchers.is("b"));
    }

    @Test
    public void lastShardGetsTheShardWithNoChildren() throws Exception {
        ShardList shards = new ShardList();
        shards.addAll(ShardListTest.createShards("a", "b", "c", "d"));
        Assert.assertThat(shards.last().getShardId(), CoreMatchers.is("d"));
    }

    @Test
    public void removingShards() throws Exception {
        ShardList shards = new ShardList();
        shards.addAll(ShardListTest.createShards(null, "a", "b", "c", "d"));
        Shard removeBefore = new Shard().withShardId("c").withParentShardId("b");
        shards.removeOlderThan(removeBefore);
        Assert.assertThat(shards.first().getShardId(), CoreMatchers.is("c"));
    }
}

