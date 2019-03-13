/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.alarm;


import CheckerCategory.SLOW_COUNT;
import com.navercorp.pinpoint.web.alarm.checker.SlowCountChecker;
import com.navercorp.pinpoint.web.alarm.vo.Rule;
import org.junit.Assert;
import org.junit.Test;


public class CheckerCategoryTest {
    @Test
    public void createCheckerTest() {
        CheckerCategory slowCount = CheckerCategory.getValue("slow count");
        Rule rule = new Rule(null, "", SLOW_COUNT.getName(), 75, "testGroup", false, false, "");
        SlowCountChecker checker = ((SlowCountChecker) (slowCount.createChecker(null, rule)));
        rule = new Rule(null, "", SLOW_COUNT.getName(), 63, "testGroup", false, false, "");
        SlowCountChecker checker2 = ((SlowCountChecker) (slowCount.createChecker(null, rule)));
        Assert.assertNotSame(checker, checker2);
        Assert.assertNotNull(checker);
        Assert.assertEquals(75, ((int) (checker.getRule().getThreshold())));
        Assert.assertNotNull(checker2);
        Assert.assertEquals(63, ((int) (checker2.getRule().getThreshold())));
    }
}

