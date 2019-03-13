package samples.powermockito.junit4.doreturn;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Created by gauee on 12/11/15.
 * Test that demonstrates that <a
 * href="https://github.com/jayway/powermock/issues/599">issue 599</a>
 * is resolved.
 */
@RunWith(PowerMockRunner.class)
public class DoReturnTest {
    private static final String TEMP_DAY_FIRST = "41F";

    private static final String TEMP_DAY_SECOND = "44F";

    @Mock
    private DoReturnTest.Weather weather;

    interface Weather {
        String getTemperature();
    }

    @Test
    public void returnsDifferentTemperatureForEachInvocation() {
        MatcherAssert.assertThat(weather.getTemperature(), Is.is(IsEqual.equalTo(DoReturnTest.TEMP_DAY_FIRST)));
        MatcherAssert.assertThat(weather.getTemperature(), Is.is(IsEqual.equalTo(DoReturnTest.TEMP_DAY_SECOND)));
    }

    @Test
    public void returnsFirstTemperatureWhenPassedArrayIsEmpty() {
        doReturn(DoReturnTest.TEMP_DAY_FIRST, new Object[0]).when(weather).getTemperature();
        MatcherAssert.assertThat(weather.getTemperature(), Is.is(IsEqual.equalTo(DoReturnTest.TEMP_DAY_FIRST)));
        MatcherAssert.assertThat(weather.getTemperature(), Is.is(IsEqual.equalTo(DoReturnTest.TEMP_DAY_FIRST)));
    }
}

