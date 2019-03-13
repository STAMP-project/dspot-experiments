/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.osgitests;


import com.github.dozermapper.osgitests.support.PaxExamTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class SpringKarafContainerTest extends CoreKarafContainerTest {
    @Test
    @Override
    public void canGetBundleFromDozerCore() {
        super.canGetBundleFromDozerCore();
        Bundle spring = getBundle(bundleContext, "com.github.dozermapper.dozer-spring4");
        Assert.assertNotNull(spring);
        Assert.assertEquals(Bundle.ACTIVE, spring.getState());
    }
}

