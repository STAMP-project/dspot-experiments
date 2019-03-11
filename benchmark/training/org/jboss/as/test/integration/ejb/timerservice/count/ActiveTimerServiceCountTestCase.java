package org.jboss.as.test.integration.ejb.timerservice.count;


import java.util.Collection;
import javax.ejb.Timer;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Testcase for the {@link javax.ejb.TimerService#getAllTimers()} API introduced in EJB 3.2 spec
 *
 * @unknown Jaikiran Pai
 */
@RunWith(Arquillian.class)
public class ActiveTimerServiceCountTestCase {
    private static final String APP_NAME = "ejb-3.2-active-timers";

    private static final String MODULE_ONE_NAME = "ejb-3.2-active-timers-one";

    private static final String MODULE_TWO_NAME = "ejb-3.2-active-timers-two";

    /**
     * Tests that the {@link javax.ejb.TimerService#getAllTimers()} API introduced in EJB 3.2 works as expected
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testActiveTimersCountInEJBModule() throws Exception {
        final SimpleTimerBean timerBean = InitialContext.doLookup(((("java:module/" + (SimpleTimerBean.class.getSimpleName())) + "!") + (SimpleTimerBean.class.getName())));
        final String infoOne = "gangnam style";
        timerBean.createTimerForNextDay(false, infoOne);
        final String infoTwo = "PSY";
        timerBean.createTimerForNextDay(true, infoTwo);
        final OtherTimerBeanInSameModule otherTimerBean = InitialContext.doLookup(((("java:module/" + (OtherTimerBeanInSameModule.class.getSimpleName())) + "!") + (OtherTimerBeanInSameModule.class.getName())));
        final String infoThree = "hello world!";
        otherTimerBean.createTimerForNextDay(false, infoThree);
        final TimerBeanInOtherModule timerBeanInOtherModule = InitialContext.doLookup(((((((("java:global/" + (ActiveTimerServiceCountTestCase.APP_NAME)) + "/") + (ActiveTimerServiceCountTestCase.MODULE_TWO_NAME)) + "/") + (TimerBeanInOtherModule.class.getSimpleName())) + "!") + (TimerBeanInOtherModule.class.getName())));
        final String infoForTimerBeanInOtherModule = "irrelevant";
        timerBeanInOtherModule.createTimerForNextDay(false, infoForTimerBeanInOtherModule);
        final Collection<Timer> activeTimers = timerBean.getAllActiveTimersInEJBModule();
        // now start testing
        Assert.assertFalse(("No active timers found in EJB module " + (ActiveTimerServiceCountTestCase.MODULE_ONE_NAME)), activeTimers.isEmpty());
        // now make sure that the active timers are indeed the one we expected
        Assert.assertTrue((("@Schedule timer on " + (SimpleTimerBean.class.getSimpleName())) + " not found in active timers"), this.removeSheduleOneTimerOfSimpleTimerBean(activeTimers));
        Assert.assertTrue((("@Schedule timer on " + (SimpleTimerBean.class.getSimpleName())) + " not found in active timers"), this.removeSheduleTwoTimerOfSimpleTimerBean(activeTimers));
        Assert.assertTrue((("@Schedule timer on " + (OtherTimerBeanInSameModule.class.getSimpleName())) + " not found in active timers"), this.removeSheduleOneTimerOfOtherTimerBean(activeTimers));
        Assert.assertTrue((("Programmatic timer on " + (SimpleTimerBean.class.getSimpleName())) + " not found in active timers"), this.removeProgramaticTimer(activeTimers, infoOne, false));
        Assert.assertTrue((("Programmatic timer on " + (SimpleTimerBean.class.getSimpleName())) + " not found in active timers"), this.removeProgramaticTimer(activeTimers, infoTwo, true));
        Assert.assertTrue((("Programmatic timer on " + (OtherTimerBeanInSameModule.class.getSimpleName())) + " not found in active timers"), this.removeProgramaticTimer(activeTimers, infoThree, false));
        // make sure there isn't an unexpected timer in the active timers
        for (final Timer remainingTimer : activeTimers) {
            Assert.assertNotEquals(("Unexpectedly found a timer from other EJB module " + (ActiveTimerServiceCountTestCase.MODULE_TWO_NAME)), TimerBeanInOtherModule.SCHEDULE_ONE_INFO.equals(remainingTimer.getInfo()));
            Assert.assertNotEquals(("Unexpectedly found a timer from other EJB module " + (ActiveTimerServiceCountTestCase.MODULE_TWO_NAME)), infoForTimerBeanInOtherModule.equals(remainingTimer.getInfo()));
        }
        // Now fetch the same info, this time from the SFSB. We expect the same number of active timers in the module.
        final StatefulBean statefulBean = InitialContext.doLookup(((("java:module/" + (StatefulBean.class.getSimpleName())) + "!") + (StatefulBean.class.getName())));
        final Collection<Timer> activeTimersReturnedFromSFSB = statefulBean.getAllActiveTimersInEJBModule();
        Assert.assertFalse((("No active timers found in EJB module " + (ActiveTimerServiceCountTestCase.MODULE_ONE_NAME)) + " when queried from a stateful bean"), activeTimersReturnedFromSFSB.isEmpty());
        // now make sure that the active timers are indeed the one we expected
        Assert.assertTrue((("@Schedule timer on " + (SimpleTimerBean.class.getSimpleName())) + " not found in active timers when queried from a stateful bean"), this.removeSheduleOneTimerOfSimpleTimerBean(activeTimersReturnedFromSFSB));
        Assert.assertTrue((("@Schedule timer on " + (SimpleTimerBean.class.getSimpleName())) + " not found in active timers when queried from a stateful bean"), this.removeSheduleTwoTimerOfSimpleTimerBean(activeTimersReturnedFromSFSB));
        Assert.assertTrue((("@Schedule timer on " + (OtherTimerBeanInSameModule.class.getSimpleName())) + " not found in active timers when queried from a stateful bean"), this.removeSheduleOneTimerOfOtherTimerBean(activeTimersReturnedFromSFSB));
        Assert.assertTrue((("Programmatic timer on " + (SimpleTimerBean.class.getSimpleName())) + " not found in active timers when queried from a stateful bean"), this.removeProgramaticTimer(activeTimersReturnedFromSFSB, infoOne, false));
        Assert.assertTrue((("Programmatic timer on " + (SimpleTimerBean.class.getSimpleName())) + " not found in active timers when queried from a stateful bean"), this.removeProgramaticTimer(activeTimersReturnedFromSFSB, infoTwo, true));
        Assert.assertTrue((("Programmatic timer on " + (OtherTimerBeanInSameModule.class.getSimpleName())) + " not found in active timers when queried from a stateful bean"), this.removeProgramaticTimer(activeTimersReturnedFromSFSB, infoThree, false));
        // make sure there isn't an unexpected timer in the active timers
        for (final Timer remainingTimer : activeTimersReturnedFromSFSB) {
            Assert.assertNotEquals((("Unexpectedly found a timer from other EJB module " + (ActiveTimerServiceCountTestCase.MODULE_TWO_NAME)) + " when queried from a stateful bean"), TimerBeanInOtherModule.SCHEDULE_ONE_INFO.equals(remainingTimer.getInfo()));
            Assert.assertNotEquals((("Unexpectedly found a timer from other EJB module " + (ActiveTimerServiceCountTestCase.MODULE_TWO_NAME)) + " when queried from a stateful bean"), infoForTimerBeanInOtherModule.equals(remainingTimer.getInfo()));
        }
    }
}

