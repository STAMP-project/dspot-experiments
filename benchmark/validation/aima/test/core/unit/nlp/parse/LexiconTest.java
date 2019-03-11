package aima.test.core.unit.nlp.parse;


import aima.core.nlp.parsing.Lexicon;
import aima.core.nlp.parsing.grammars.Rule;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class LexiconTest {
    Lexicon l;

    Lexicon wumpusLex;

    @Test
    public void testAddEntry() {
        l.addEntry("EXAMPLE", "word", ((float) (0.1)));
        Assert.assertTrue(l.containsKey("EXAMPLE"));
        Assert.assertEquals(l.get("EXAMPLE").size(), 1);
    }

    @Test
    public void testAddEntryExistingCategory() {
        l.addEntry("EXAMPLE", "word", ((float) (0.1)));
        l.addEntry("EXAMPLE", "second", ((float) (0.9)));
        Assert.assertTrue(l.containsKey("EXAMPLE"));
        Assert.assertTrue(((l.keySet().size()) == 1));
        Assert.assertTrue(l.get("EXAMPLE").get(1).getWord().equals("second"));
    }

    @Test
    public void testAddLexWords() {
        String key = "EXAMPLE";
        l.addLexWords(key, "stench", "0.05", "breeze", "0.10", "wumpus", "0.15");
        Assert.assertTrue(((l.get(key).size()) == 3));
        Assert.assertTrue(l.get(key).get(0).getWord().equals("stench"));
    }

    @Test
    public void testAddLexWordsWithInvalidArgs() {
        String key = "EXAMPLE";
        Assert.assertFalse(l.addLexWords(key, "stench", "0.05", "breeze"));
        Assert.assertFalse(l.containsKey(key));
    }

    @Test
    public void testGetTerminalRules() {
        String key1 = "A";
        String key2 = "B";
        String key3 = "C";
        l.addLexWords(key1, "apple", "0.25", "alpha", "0.5", "arrow", "0.25");
        l.addLexWords(key2, "ball", "0.25", "bench", "0.25", "blue", "0.25", "bell", "0.25");
        l.addLexWords(key3, "carrot", "0.25", "canary", "0.5", "caper", "0.25");
        ArrayList<Rule> rules1 = l.getTerminalRules(key1);
        ArrayList<Rule> rules2 = l.getTerminalRules(key2);
        ArrayList<Rule> rules3 = l.getTerminalRules(key3);
        Assert.assertEquals(rules1.size(), 3);
        Assert.assertEquals(rules1.get(0).rhs.get(0), "apple");
        Assert.assertEquals(rules2.size(), 4);
        Assert.assertEquals(rules2.get(3).rhs.get(0), "bell");
        Assert.assertEquals(rules3.size(), 3);
        Assert.assertEquals(rules3.get(1).lhs.get(0), "C");
    }

    @Test
    public void testGetAllTerminalRules() {
        String key1 = "A";
        String key2 = "B";
        String key3 = "C";
        l.addLexWords(key1, "apple", "0.25", "alpha", "0.5", "arrow", "0.25");
        l.addLexWords(key2, "ball", "0.25", "bench", "0.25", "blue", "0.25", "bell", "0.25");
        l.addLexWords(key3, "carrot", "0.25", "canary", "0.5", "caper", "0.25");
        ArrayList<Rule> allRules = l.getAllTerminalRules();
        Assert.assertEquals(allRules.size(), 10);
        Assert.assertTrue((((allRules.get(0).rhs.get(0).equals("apple")) || (allRules.get(0).rhs.get(0).equals("ball"))) || (allRules.get(0).rhs.get(0).equals("carrot"))));
    }
}

