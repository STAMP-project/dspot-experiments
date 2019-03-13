package aima.test.core.unit.logic.fol;


import aima.core.logic.fol.PredicateCollector;
import aima.core.logic.fol.parsing.FOLParser;
import aima.core.logic.fol.parsing.ast.Predicate;
import aima.core.logic.fol.parsing.ast.Sentence;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class PredicateCollectorTest {
    PredicateCollector collector;

    FOLParser parser;

    @Test
    public void testSimpleSentence() {
        Sentence s = parser.parse("(Missile(x) => Weapon(x))");
        List<Predicate> predicates = collector.getPredicates(s);
        Assert.assertNotNull(predicates);
    }
}

