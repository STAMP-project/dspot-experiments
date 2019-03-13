/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samples.powermockito.junit4.argumentmatcher;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.argumentmatcher.ArgumentMatcherDemo;


@RunWith(PowerMockRunner.class)
public class ArgumentMatcherTest {
    @Test
    public void worksWithArgumentMatchers() throws Exception {
        final ArrayList<String> strings = new ArrayList<String>();
        final ArgumentMatcherDemo tested = mock(ArgumentMatcherDemo.class);
        doReturn(strings).when(tested, "findByNamedQuery", ArgumentMatchers.eq("AbstractPTVTicket.ticketSeatIds"), ArgumentMatchers.anyList());
        final List<String> stringList = tested.findByNamedQuery("something", strings);
        Assert.assertTrue(stringList.isEmpty());
    }
}

