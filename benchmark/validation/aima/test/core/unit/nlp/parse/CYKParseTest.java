package aima.test.core.unit.nlp.parse;


import aima.core.nlp.data.grammars.ProbCNFGrammarExamples;
import aima.core.nlp.parsing.CYK;
import aima.core.nlp.parsing.grammars.ProbCNFGrammar;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class CYKParseTest {
    CYK parser;

    ArrayList<String> words1;

    ArrayList<String> words2;

    ProbCNFGrammar trivGrammar = ProbCNFGrammarExamples.buildTrivialGrammar();

    @Test
    public void testParseReturn() {
        float[][][] probTable = null;
        probTable = parser.parse(words1, trivGrammar);
        Assert.assertNotNull(probTable);
    }

    @Test
    public void testParse() {
        float[][][] probTable;
        probTable = parser.parse(words1, trivGrammar);
        Assert.assertTrue(((probTable[5][0][4]) > 0));// probTable[5,0,4] = [S][Start=0][Length=5]

    }
}

