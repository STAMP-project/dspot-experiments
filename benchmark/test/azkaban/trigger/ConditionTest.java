/**
 * Copyright 2014 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.trigger;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ConditionTest {
    @Test
    public void conditionTest() {
        final Map<String, ConditionChecker> checkers = new HashMap<>();
        final ThresholdChecker fake1 = new ThresholdChecker("thresholdchecker1", 10);
        final ThresholdChecker fake2 = new ThresholdChecker("thresholdchecker2", 20);
        ThresholdChecker.setVal(15);
        checkers.put(fake1.getId(), fake1);
        checkers.put(fake2.getId(), fake2);
        final String expr1 = (((((((((((((("( " + (fake1.getId())) + ".eval()") + " && ") + (fake2.getId())) + ".eval()") + " )") + " || ") + "( ") + (fake1.getId())) + ".eval()") + " && ") + "!") + (fake2.getId())) + ".eval()") + " )";
        final String expr2 = ((((((((((((("( " + (fake1.getId())) + ".eval()") + " && ") + (fake2.getId())) + ".eval()") + " )") + " || ") + "( ") + (fake1.getId())) + ".eval()") + " && ") + (fake2.getId())) + ".eval()") + " )";
        final Condition cond = new Condition(checkers, expr1);
        System.out.println(("Setting expression " + expr1));
        Assert.assertTrue(cond.isMet());
        cond.setExpression(expr2);
        System.out.println(("Setting expression " + expr2));
        Assert.assertFalse(cond.isMet());
    }
}

