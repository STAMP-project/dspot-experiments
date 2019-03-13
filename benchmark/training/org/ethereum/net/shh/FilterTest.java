/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.net.shh;


import org.ethereum.crypto.ECKey;
import org.junit.Assert;
import org.junit.Test;


public class FilterTest {
    String to = WhisperImpl.toIdentity(new ECKey());

    String from = WhisperImpl.toIdentity(new ECKey());

    String[] topics = new String[]{ "topic1", "topic2", "topic3", "topic4" };

    class FilterStub extends MessageWatcher {
        public FilterStub() {
        }

        public FilterStub(String to, String from, Topic[] filterTopics) {
            super(to, from, filterTopics);
        }

        @Override
        protected void newMessage(WhisperMessage msg) {
        }
    }

    @Test
    public void test1() {
        MessageWatcher matcher = new FilterTest.FilterStub();
        Assert.assertTrue(matcher.match(to, from, Topic.createTopics(topics)));
    }

    @Test
    public void test2() {
        MessageWatcher matcher = setTo(to);
        Assert.assertTrue(matcher.match(to, from, Topic.createTopics(topics)));
    }

    @Test
    public void test3() {
        MessageWatcher matcher = setTo(to);
        Assert.assertFalse(matcher.match(null, from, Topic.createTopics(topics)));
    }

    @Test
    public void test4() {
        MessageWatcher matcher = setFrom(from);
        Assert.assertTrue(matcher.match(null, from, Topic.createTopics(topics)));
    }

    @Test
    public void test5() {
        MessageWatcher matcher = setFrom(from);
        Assert.assertTrue((!(matcher.match(to, null, Topic.createTopics(topics)))));
    }

    @Test
    public void test6() {
        MessageWatcher matcher = new FilterTest.FilterStub(null, from, Topic.createTopics(topics));
        Assert.assertTrue(matcher.match(to, from, Topic.createTopics(topics)));
    }

    @Test
    public void test7() {
        MessageWatcher matcher = new FilterTest.FilterStub(null, null, Topic.createTopics(topics));
        Assert.assertTrue((!(matcher.match(to, from, Topic.createTopics(new String[]{  })))));
    }
}

