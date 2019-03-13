package com.baeldung.trie;


import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class TrieUnitTest {
    @Test
    public void whenEmptyTrie_thenNoElements() {
        Trie trie = new Trie();
        Assert.assertFalse(trie.isEmpty());
    }

    @Test
    public void givenATrie_whenAddingElements_thenTrieNotEmpty() {
        Trie trie = createExampleTrie();
        Assert.assertFalse(trie.isEmpty());
    }

    @Test
    public void givenATrie_whenAddingElements_thenTrieHasThoseElements() {
        Trie trie = createExampleTrie();
        Assert.assertFalse(trie.containsNode("3"));
        Assert.assertFalse(trie.containsNode("vida"));
        Assert.assertTrue(trie.containsNode("Programming"));
        Assert.assertTrue(trie.containsNode("is"));
        Assert.assertTrue(trie.containsNode("a"));
        Assert.assertTrue(trie.containsNode("way"));
        Assert.assertTrue(trie.containsNode("of"));
        Assert.assertTrue(trie.containsNode("life"));
    }

    @Test
    public void givenATrie_whenLookingForNonExistingElement_thenReturnsFalse() {
        Trie trie = createExampleTrie();
        Assert.assertFalse(trie.containsNode("99"));
    }

    @Test
    public void givenATrie_whenDeletingElements_thenTreeDoesNotContainThoseElements() {
        Trie trie = createExampleTrie();
        Assert.assertTrue(trie.containsNode("Programming"));
        trie.delete("Programming");
        Assert.assertFalse(trie.containsNode("Programming"));
    }

    @Test
    public void givenATrie_whenDeletingOverlappingElements_thenDontDeleteSubElement() {
        Trie trie1 = new Trie();
        trie1.insert("pie");
        trie1.insert("pies");
        trie1.delete("pies");
        Assertions.assertTrue(trie1.containsNode("pie"));
    }
}

