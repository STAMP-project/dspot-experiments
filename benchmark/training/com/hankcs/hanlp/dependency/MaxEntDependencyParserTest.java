package com.hankcs.hanlp.dependency;


import junit.framework.TestCase;


// public void testEvaluate() throws Exception
// {
// LinkedList<CoNLLSentence> sentenceList = CoNLLLoader.loadSentenceList("D:\\Doc\\???\\????????\\THU\\dev.conll");
// Evaluator evaluator = new Evaluator();
// int id = 1;
// for (CoNLLSentence sentence : sentenceList)
// {
// System.out.printf("%d / %d...", id++, sentenceList.size());
// long start = System.currentTimeMillis();
// List<Term> termList = new LinkedList<Term>();
// for (CoNLLWord word : sentence.word)
// {
// termList.add(new Term(word.LEMMA, Nature.valueOf(word.POSTAG)));
// }
// CoNLLSentence out = CRFDependencyParser.compute(termList);
// evaluator.e(sentence, out);
// System.out.println("done in " + (System.currentTimeMillis() - start) + " ms.");
// }
// System.out.println(evaluator);
// }
public class MaxEntDependencyParserTest extends TestCase {
    public void testMaxEntParser() throws Exception {
        // HanLP.Config.enableDebug();
        // System.out.println(MaxEntDependencyParser.compute("???????"));
    }
}

