package roboguice.event;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import roboguice.event.eventListener.ObserverMethodListener;


/**
 * Test class exercising the ObserverReferences
 *
 * @author John Ericksen
 */
public class ObserverReferenceTest {
    protected ObserverReferenceTest.EqualityTestClass test;

    protected ObserverReferenceTest.EqualityTestClass test2;

    protected Method methodOneBase;

    protected Method methodOne;

    protected Method methodTwoBase;

    protected Method methodTwo;

    @Test
    public void testEquality() {
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefOne = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodOne);
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefTwo = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodOneBase);
        Assert.assertEquals(observerRefOne, observerRefTwo);
        Assert.assertEquals(observerRefOne.hashCode(), observerRefTwo.hashCode());
    }

    @Test
    public void testEqualityOfSameGuts() {
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefOne = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodOne);
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefTwo = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodOne);
        Assert.assertEquals(observerRefOne, observerRefTwo);
        Assert.assertEquals(observerRefOne.hashCode(), observerRefTwo.hashCode());
    }

    @Test
    public void testInequalityBetweenSameClass() {
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefOne = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodOne);
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefTwo = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodTwo);
        assert !(observerRefOne.equals(observerRefTwo));
        assert !(Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode()));
    }

    @Test
    public void testInequalityBetweenDifferentClass() {
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefOne = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodOne);
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefTwo = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodTwoBase);
        assert !(observerRefOne.equals(observerRefTwo));
        assert !(Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode()));
    }

    @Test
    public void testInequalityBetweenDifferentInstances() {
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefOne = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodOne);
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefTwo = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test2, methodOne);
        assert !(observerRefOne.equals(observerRefTwo));
        assert !(Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode()));
    }

    @Test
    public void testInequalityBetweenDifferentInstancesAndDifferentMethods() {
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefOne = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test, methodOne);
        ObserverMethodListener<ObserverReferenceTest.EqualityTestClass> observerRefTwo = new ObserverMethodListener<ObserverReferenceTest.EqualityTestClass>(test2, methodTwoBase);
        assert !(observerRefOne.equals(observerRefTwo));
        assert !(Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode()));
    }

    /* @Test
    public void testEqualityBetweenDecoration() {

    EventListener<EqualityTestClass> observerRefOne = new ObserverMethodListener<EqualityTestClass>(test, methodOne);
    EventListener<EqualityTestClass> observerRefTwo = new AsynchronousEventListenerDecorator<EqualityTestClass>(
    new ObserverMethodListener<EqualityTestClass>(test, methodOne), new RunnableAsyncTaskAdaptorFactory());

    assert !observerRefOne.equals(observerRefTwo) ;
    assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }
     */
    public class EqualityTestClass {
        public void one(EventOne one) {
        }

        public void two(EventTwo two) {
        }
    }

    public class EqualityTestOverrideClass extends ObserverReferenceTest.EqualityTestClass {
        public void one(EventOne one) {
        }

        public void two(EventTwo two) {
        }
    }
}

