package junit.tests.runner;


import junit.framework.TestCase;
import junit.runner.BaseTestRunner;


public class StackFilterTest extends TestCase {
    String fFiltered;

    String fUnfiltered;

    public void testFilter() {
        TestCase.assertEquals(fFiltered, BaseTestRunner.getFilteredTrace(fUnfiltered));
    }
}

