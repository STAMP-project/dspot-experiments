package aima.test.core.unit.logic.fol.kb;


import aima.core.logic.fol.kb.FOLKnowledgeBase;
import aima.core.logic.fol.kb.data.Clause;
import aima.core.logic.fol.kb.data.Literal;
import aima.core.logic.fol.parsing.ast.Term;
import aima.core.logic.fol.parsing.ast.Variable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 */
public class FOLKnowledgeBaseTest {
    private FOLKnowledgeBase weaponsKB;

    private FOLKnowledgeBase kingsKB;

    @Test
    public void testAddRuleAndFact() {
        weaponsKB.tell("(Missile(x) => Weapon(x))");
        Assert.assertEquals(1, weaponsKB.getNumberRules());
        weaponsKB.tell("American(West)");
        Assert.assertEquals(1, weaponsKB.getNumberRules());
        Assert.assertEquals(1, weaponsKB.getNumberFacts());
    }

    @Test
    public void testAddComplexRule() {
        weaponsKB.tell("( (((American(x) AND Weapon(y)) AND Sells(x,y,z)) AND Hostile(z)) => Criminal(x))");
        Assert.assertEquals(1, weaponsKB.getNumberRules());
        weaponsKB.tell("American(West)");
        Assert.assertEquals(1, weaponsKB.getNumberRules());
        List<Term> terms = new ArrayList<Term>();
        terms.add(new Variable("v0"));
        Clause dcRule = weaponsKB.getAllDefiniteClauseImplications().get(0);
        Assert.assertNotNull(dcRule);
        Assert.assertEquals(true, dcRule.isImplicationDefiniteClause());
        Assert.assertEquals(new Literal(new aima.core.logic.fol.parsing.ast.Predicate("Criminal", terms)), dcRule.getPositiveLiterals().get(0));
    }

    @Test
    public void testFactNotAddedWhenAlreadyPresent() {
        kingsKB.tell("((King(x) AND Greedy(x)) => Evil(x))");
        kingsKB.tell("King(John)");
        kingsKB.tell("King(Richard)");
        kingsKB.tell("Greedy(John)");
        Assert.assertEquals(1, kingsKB.getNumberRules());
        Assert.assertEquals(3, kingsKB.getNumberFacts());
        kingsKB.tell("King(John)");
        kingsKB.tell("King(Richard)");
        kingsKB.tell("Greedy(John)");
        Assert.assertEquals(1, kingsKB.getNumberRules());
        Assert.assertEquals(3, kingsKB.getNumberFacts());
        kingsKB.tell("(((King(John))))");
        kingsKB.tell("(((King(Richard))))");
        kingsKB.tell("(((Greedy(John))))");
        Assert.assertEquals(1, kingsKB.getNumberRules());
        Assert.assertEquals(3, kingsKB.getNumberFacts());
    }
}

