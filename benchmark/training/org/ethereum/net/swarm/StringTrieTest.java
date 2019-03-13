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
package org.ethereum.net.swarm;


import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Admin on 11.06.2015.
 */
public class StringTrieTest {
    class A extends StringTrie.TrieNode<StringTrieTest.A> {
        String id;

        public A() {
        }

        public A(StringTrieTest.A parent, String relPath) {
            super(parent, relPath);
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        protected StringTrieTest.A createNode(StringTrieTest.A parent, String path) {
            return new StringTrieTest.A(parent, path);
        }

        @Override
        public String toString() {
            return ("A[" + ((id) != null ? id : "")) + "]";
        }
    }

    class T extends StringTrie<StringTrieTest.A> {
        public T() {
            super(new StringTrieTest.A());
        }

        @Override
        public StringTrieTest.A add(String path) {
            StringTrieTest.A ret = super.add(path);
            ret.setId(path);
            return ret;
        }
    }

    @Test
    public void testAdd() {
        StringTrieTest.T trie = new StringTrieTest.T();
        trie.add("aaa");
        trie.add("bbb");
        trie.add("aad");
        trie.add("aade");
        trie.add("aadd");
        System.out.println(Util.dumpTree(trie.rootNode));
        Assert.assertEquals("aaa", get("aaa").getAbsolutePath());
        Assert.assertEquals("bbb", get("bbb").getAbsolutePath());
        Assert.assertEquals("aad", get("aad").getAbsolutePath());
        Assert.assertEquals("aa", get("aaqqq").getAbsolutePath());
        Assert.assertEquals("", get("bbe").getAbsolutePath());
    }

    @Test
    public void testAddRootLeaf() {
        StringTrieTest.T trie = new StringTrieTest.T();
        trie.add("ax");
        trie.add("ay");
        trie.add("a");
        System.out.println(Util.dumpTree(trie.rootNode));
    }

    @Test
    public void testAddDuplicate() {
        StringTrieTest.T trie = new StringTrieTest.T();
        StringTrieTest.A a = trie.add("a");
        StringTrieTest.A ay = trie.add("ay");
        StringTrieTest.A a1 = trie.add("a");
        Assert.assertTrue((a == a1));
        StringTrieTest.A ay1 = trie.add("ay");
        Assert.assertTrue((ay == ay1));
    }

    @Test
    public void testAddLeafRoot() {
        StringTrieTest.T trie = new StringTrieTest.T();
        trie.add("a");
        trie.add("ax");
        System.out.println(Util.dumpTree(trie.rootNode));
    }

    @Test
    public void testAddDelete() {
        StringTrieTest.T trie = new StringTrieTest.T();
        trie.add("aaaa");
        trie.add("aaaaxxxx");
        trie.add("aaaaxxxxeeee");
        System.out.println(Util.dumpTree(trie.rootNode));
        delete("aaaa");
        System.out.println(Util.dumpTree(trie.rootNode));
        delete("aaaaxxxx");
        System.out.println(Util.dumpTree(trie.rootNode));
    }
}

