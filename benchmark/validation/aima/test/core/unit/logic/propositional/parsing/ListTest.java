package aima.test.core.unit.logic.propositional.parsing;


import aima.core.logic.propositional.parsing.ast.PropositionSymbol;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class ListTest {
    @Test
    public void testListOfSymbolsClone() {
        ArrayList<PropositionSymbol> l = new ArrayList<PropositionSymbol>();
        l.add(new PropositionSymbol("A"));
        l.add(new PropositionSymbol("B"));
        l.add(new PropositionSymbol("C"));
        List<PropositionSymbol> l2 = new ArrayList<PropositionSymbol>(l);
        l2.remove(new PropositionSymbol("B"));
        Assert.assertEquals(3, l.size());
        Assert.assertEquals(2, l2.size());
    }

    @Test
    public void testListRemove() {
        List<Integer> one = new ArrayList<Integer>();
        one.add(new Integer(1));
        Assert.assertEquals(1, one.size());
        one.remove(0);
        Assert.assertEquals(0, one.size());
    }
}

