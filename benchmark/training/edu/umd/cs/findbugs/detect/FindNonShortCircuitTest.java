package edu.umd.cs.findbugs.detect;


import FindNonShortCircuit.NS_DANGEROUS_NON_SHORT_CIRCUIT;
import FindNonShortCircuit.NS_NON_SHORT_CIRCUIT;
import Priorities.HIGH_PRIORITY;
import Priorities.LOW_PRIORITY;
import Priorities.NORMAL_PRIORITY;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import org.junit.Assert;
import org.junit.Test;


public class FindNonShortCircuitTest {
    private AnalysisContext ctx;

    private FindNonShortCircuit check;

    @Test
    public void testDefaultBugTypeAndPriority() {
        BugInstance bug = check.createBugInstance();
        Assert.assertEquals(NS_NON_SHORT_CIRCUIT, bug.getType());
        Assert.assertEquals(LOW_PRIORITY, bug.getPriority());
    }

    @Test
    public void testBugTypeAndPriorityDangerOld() {
        check.sawDangerOld = true;
        BugInstance bug = check.createBugInstance();
        Assert.assertEquals(NS_NON_SHORT_CIRCUIT, bug.getType());
        Assert.assertEquals(NORMAL_PRIORITY, bug.getPriority());
    }

    @Test
    public void testBugTypeAndPriorityNullTestOld() {
        check.sawDangerOld = true;
        check.sawNullTestVeryOld = true;
        BugInstance bug = check.createBugInstance();
        Assert.assertEquals(NS_NON_SHORT_CIRCUIT, bug.getType());
        Assert.assertEquals(HIGH_PRIORITY, bug.getPriority());
    }

    @Test
    public void testBugTypeAndPriorityMethodCallOld() {
        check.sawDangerOld = true;
        check.sawMethodCallOld = true;
        BugInstance bug = check.createBugInstance();
        Assert.assertEquals(NS_DANGEROUS_NON_SHORT_CIRCUIT, bug.getType());
        Assert.assertEquals(HIGH_PRIORITY, bug.getPriority());
    }
}

