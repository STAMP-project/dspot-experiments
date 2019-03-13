package com.baeldung.hashtable;


import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import org.junit.Assert;
import org.junit.Test;


public class HashtableUnitTest {
    @Test
    public void whenPutAndGet_thenReturnsValue() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        Word word = new Word("cat");
        table.put(word, "an animal");
        String definition = table.get(word);
        Assert.assertEquals("an animal", definition);
        definition = table.remove(word);
        Assert.assertEquals("an animal", definition);
    }

    @Test
    public void whenThesameInstanceOfKey_thenReturnsValue() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        Word word = new Word("cat");
        table.put(word, "an animal");
        String extracted = table.get(word);
        Assert.assertEquals("an animal", extracted);
    }

    @Test
    public void whenEqualsOverridden_thenReturnsValue() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        Word word = new Word("cat");
        table.put(word, "an animal");
        String extracted = table.get(new Word("cat"));
        Assert.assertEquals("an animal", extracted);
    }

    @Test(expected = NullPointerException.class)
    public void whenNullKey_thenException() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(null, "an animal");
    }

    @Test(expected = ConcurrentModificationException.class)
    public void whenIterate_thenFailFast() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("cat"), "an animal");
        table.put(new Word("dog"), "another animal");
        Iterator<Word> it = table.keySet().iterator();
        System.out.println("iterator created");
        table.remove(new Word("dog"));
        System.out.println("element removed");
        while (it.hasNext()) {
            Word key = it.next();
            System.out.println(table.get(key));
        } 
    }

    @Test
    public void whenEnumerate_thenNotFailFast() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("1"), "one");
        table.put(new Word("2"), "two");
        table.put(new Word("3"), "three");
        table.put(new Word("4"), "four");
        table.put(new Word("5"), "five");
        table.put(new Word("6"), "six");
        table.put(new Word("7"), "seven");
        table.put(new Word("8"), "eight");
        Enumeration<Word> enumKey = table.keys();
        System.out.println("Enumeration created");
        table.remove(new Word("1"));
        System.out.println("element removed");
        while (enumKey.hasMoreElements()) {
            Word key = enumKey.nextElement();
            System.out.println(table.get(key));
        } 
    }

    @Test
    public void whenAddElements_thenIterationOrderUnpredicable() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("1"), "one");
        table.put(new Word("2"), "two");
        table.put(new Word("3"), "three");
        table.put(new Word("4"), "four");
        table.put(new Word("5"), "five");
        table.put(new Word("6"), "six");
        table.put(new Word("7"), "seven");
        table.put(new Word("8"), "eight");
        Iterator<Map.Entry<Word, String>> it = table.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Word, String> entry = it.next();
            System.out.println(entry.getValue());
        } 
    }

    @Test
    public void whenGetOrDefault_thenDefaultGot() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("cat"), "a small domesticated carnivorous mammal");
        Word key = new Word("dog");
        String definition;
        // old way
        /* if (table.containsKey(key)) {
        definition = table.get(key);
        } else {
        definition = "not found";
        }
         */
        // new way
        definition = table.getOrDefault(key, "not found");
        Assert.assertThat(definition, is("not found"));
    }

    @Test
    public void whenPutifAbsent_thenNotRewritten() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("cat"), "a small domesticated carnivorous mammal");
        String definition = "an animal";
        // old way
        /* if (!table.containsKey(new Word("cat"))) {
        table.put(new Word("cat"), definition);
        }
         */
        // new way
        table.putIfAbsent(new Word("cat"), definition);
        Assert.assertThat(table.get(new Word("cat")), is("a small domesticated carnivorous mammal"));
    }

    @Test
    public void whenRemovePair_thenCheckKeyAndValue() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("cat"), "a small domesticated carnivorous mammal");
        // old way
        /* if (table.get(new Word("cat")).equals("an animal")) {
        table.remove(new Word("cat"));
        }
         */
        // new way
        boolean result = table.remove(new Word("cat"), "an animal");
        Assert.assertThat(result, is(false));
    }

    @Test
    public void whenReplacePair_thenValueChecked() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("cat"), "a small domesticated carnivorous mammal");
        String definition = "an animal";
        // old way
        /* if (table.containsKey(new Word("cat")) && table.get(new Word("cat")).equals("a small domesticated carnivorous mammal")) {
        table.put(new Word("cat"), definition);
        }
         */
        // new way
        table.replace(new Word("cat"), "a small domesticated carnivorous mammal", definition);
        Assert.assertThat(table.get(new Word("cat")), is("an animal"));
    }

    @Test
    public void whenKeyIsAbsent_thenNotRewritten() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("cat"), "a small domesticated carnivorous mammal");
        // old way
        /* if (!table.containsKey(cat)) {
        String definition = "an animal";// calculate
        table.put(new Word("cat"), definition);
        }
         */
        // new way
        table.computeIfAbsent(new Word("cat"), ( key) -> "an animal");
        Assert.assertThat(table.get(new Word("cat")), is("a small domesticated carnivorous mammal"));
    }

    @Test
    public void whenKeyIsPresent_thenComputeIfPresent() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("cat"), "a small domesticated carnivorous mammal");
        Word cat = new Word("cat");
        // old way
        /* if (table.containsKey(cat)) {
        String concatination = cat.getName() + " - " + table.get(cat);
        table.put(cat, concatination);
        }
         */
        // new way
        table.computeIfPresent(cat, ( key, value) -> ((key.getName()) + " - ") + value);
        Assert.assertThat(table.get(cat), is("cat - a small domesticated carnivorous mammal"));
    }

    @Test
    public void whenCompute_thenForAllKeys() {
        Hashtable<String, Integer> table = new Hashtable<String, Integer>();
        String[] animals = new String[]{ "cat", "dog", "dog", "cat", "bird", "mouse", "mouse" };
        for (String animal : animals) {
            table.compute(animal, ( key, value) -> value == null ? 1 : value + 1);
        }
        Assert.assertThat(table.values(), hasItems(2, 2, 2, 1));
    }

    @Test
    public void whenInsteadOfCompute_thenMerge() {
        Hashtable<String, Integer> table = new Hashtable<String, Integer>();
        String[] animals = new String[]{ "cat", "dog", "dog", "cat", "bird", "mouse", "mouse" };
        for (String animal : animals) {
            table.merge(animal, 1, ( oldValue, value) -> oldValue + value);
        }
        Assert.assertThat(table.values(), hasItems(2, 2, 2, 1));
    }

    @Test
    public void whenForeach_thenIterate() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("cat"), "a small domesticated carnivorous mammal");
        table.put(new Word("dog"), "another animal");
        table.forEach(( k, v) -> System.out.println((((k.getName()) + " - ") + v)));
    }

    @Test
    public void whenReplaceall_thenNoIterationNeeded() {
        Hashtable<Word, String> table = new Hashtable<Word, String>();
        table.put(new Word("cat"), "a small domesticated carnivorous mammal");
        table.put(new Word("dog"), "another animal");
        table.replaceAll(( k, v) -> ((k.getName()) + " - ") + v);
        Assert.assertThat(table.values(), hasItems("cat - a small domesticated carnivorous mammal", "dog - another animal"));
    }
}

