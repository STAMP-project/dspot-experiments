package com.hankcs.hanlp.corpus.document.sentence;


import com.hankcs.hanlp.corpus.document.sentence.word.CompoundWord;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.corpus.document.sentence.word.WordFactory;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;


public class SentenceTest extends TestCase {
    public void testFindFirstWordIteratorByLabel() throws Exception {
        Sentence sentence = Sentence.create("[??/ns ??/nz ??/n ?/w ??/n ?/w ??/n]/nt ???/n ???/nr ?/c ??/n ???/nr ??/v [??/ns ??/ns ??/t ??/n ???/n]/ns ??/v");
        ListIterator<IWord> nt = sentence.findFirstWordIteratorByLabel("nt");
        TestCase.assertNotNull(nt);
        TestCase.assertEquals("[??/ns ??/nz ??/n ?/w ??/n ?/w ??/n]/nt", nt.previous().toString());
        CompoundWord apple = CompoundWord.create("[??/n ??/n]/nt");
        nt.set(apple);
        TestCase.assertEquals(sentence.findFirstWordByLabel("nt"), apple);
        nt.remove();
        TestCase.assertEquals("???/n ???/nr ?/c ??/n ???/nr ??/v [??/ns ??/ns ??/t ??/n ???/n]/ns ??/v", sentence.toString());
        ListIterator<IWord> ns = sentence.findFirstWordIteratorByLabel("ns");
        TestCase.assertEquals("??/v", ns.next().toString());
    }

    public void testToStandoff() throws Exception {
        Sentence sentence = Sentence.create("[??/ns ??/nz ??/n ?/w ??/n ?/w ??/n]/nt ???/n ???/nr ?/c ??/n ???/nr ??/v [??/ns ??/ns ??/t ??/n ???/n]/ns ??/v");
        System.out.println(sentence.toStandoff(true));
    }

    public void testText() throws Exception {
        TestCase.assertEquals("???????", Sentence.create("???/nz [??/nsf ??/n]/nz").text());
    }

    public void testCreate() throws Exception {
        String text = "???/nz 1?1?/t ?/ng ?/p ?/w [??/nsf ??/n]/nz ?/w ??/v ?/w";
        Pattern pattern = Pattern.compile("(\\[(.+/[a-z]+)]/[a-z]+)|([^\\s]+/[a-z]+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String param = matcher.group();
            TestCase.assertEquals(param, WordFactory.create(param).toString());
        } 
        TestCase.assertEquals(text, Sentence.create(text).toString());
    }

    public void testCreateNoTag() throws Exception {
        String text = "?? ? ??";
        Sentence sentence = Sentence.create(text);
        System.out.println(sentence);
    }

    public void testMerge() throws Exception {
        Sentence sentence = Sentence.create("??????/TIME ?/v ?/n ?/v ?/v ??/n ??/PERSON ??/v ?/u [??/ns ?/w ???/nz ?/w ?/Vg ?/n ?/n]/ORGANIZATION ??/n ??/INTEGER ?/n ?/v ?/d ?/v ?/v ??/n ??/v [??/nz ??/n]/ORGANIZATION ?/u ?/a ???/n ?/v ??/r ??/d ?/v ?/u ??/LOCATION ??/n ?/v ??/d ????/i ?/u ??/v ??/n ?/u ??/vn ?/w");
        System.out.println(sentence.mergeCompoundWords());
    }
}

