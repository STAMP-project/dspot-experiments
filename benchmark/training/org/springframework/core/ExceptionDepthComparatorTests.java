/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.core;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Shepperd
 */
@SuppressWarnings("unchecked")
public class ExceptionDepthComparatorTests {
    @Test
    public void targetBeforeSameDepth() throws Exception {
        Class<? extends Throwable> foundClass = findClosestMatch(ExceptionDepthComparatorTests.TargetException.class, ExceptionDepthComparatorTests.SameDepthException.class);
        Assert.assertEquals(ExceptionDepthComparatorTests.TargetException.class, foundClass);
    }

    @Test
    public void sameDepthBeforeTarget() throws Exception {
        Class<? extends Throwable> foundClass = findClosestMatch(ExceptionDepthComparatorTests.SameDepthException.class, ExceptionDepthComparatorTests.TargetException.class);
        Assert.assertEquals(ExceptionDepthComparatorTests.TargetException.class, foundClass);
    }

    @Test
    public void lowestDepthBeforeTarget() throws Exception {
        Class<? extends Throwable> foundClass = findClosestMatch(ExceptionDepthComparatorTests.LowestDepthException.class, ExceptionDepthComparatorTests.TargetException.class);
        Assert.assertEquals(ExceptionDepthComparatorTests.TargetException.class, foundClass);
    }

    @Test
    public void targetBeforeLowestDepth() throws Exception {
        Class<? extends Throwable> foundClass = findClosestMatch(ExceptionDepthComparatorTests.TargetException.class, ExceptionDepthComparatorTests.LowestDepthException.class);
        Assert.assertEquals(ExceptionDepthComparatorTests.TargetException.class, foundClass);
    }

    @Test
    public void noDepthBeforeTarget() throws Exception {
        Class<? extends Throwable> foundClass = findClosestMatch(ExceptionDepthComparatorTests.NoDepthException.class, ExceptionDepthComparatorTests.TargetException.class);
        Assert.assertEquals(ExceptionDepthComparatorTests.TargetException.class, foundClass);
    }

    @Test
    public void noDepthBeforeHighestDepth() throws Exception {
        Class<? extends Throwable> foundClass = findClosestMatch(ExceptionDepthComparatorTests.NoDepthException.class, ExceptionDepthComparatorTests.HighestDepthException.class);
        Assert.assertEquals(ExceptionDepthComparatorTests.HighestDepthException.class, foundClass);
    }

    @Test
    public void highestDepthBeforeNoDepth() throws Exception {
        Class<? extends Throwable> foundClass = findClosestMatch(ExceptionDepthComparatorTests.HighestDepthException.class, ExceptionDepthComparatorTests.NoDepthException.class);
        Assert.assertEquals(ExceptionDepthComparatorTests.HighestDepthException.class, foundClass);
    }

    @Test
    public void highestDepthBeforeLowestDepth() throws Exception {
        Class<? extends Throwable> foundClass = findClosestMatch(ExceptionDepthComparatorTests.HighestDepthException.class, ExceptionDepthComparatorTests.LowestDepthException.class);
        Assert.assertEquals(ExceptionDepthComparatorTests.LowestDepthException.class, foundClass);
    }

    @Test
    public void lowestDepthBeforeHighestDepth() throws Exception {
        Class<? extends Throwable> foundClass = findClosestMatch(ExceptionDepthComparatorTests.LowestDepthException.class, ExceptionDepthComparatorTests.HighestDepthException.class);
        Assert.assertEquals(ExceptionDepthComparatorTests.LowestDepthException.class, foundClass);
    }

    @SuppressWarnings("serial")
    public class HighestDepthException extends Throwable {}

    @SuppressWarnings("serial")
    public class LowestDepthException extends ExceptionDepthComparatorTests.HighestDepthException {}

    @SuppressWarnings("serial")
    public class TargetException extends ExceptionDepthComparatorTests.LowestDepthException {}

    @SuppressWarnings("serial")
    public class SameDepthException extends ExceptionDepthComparatorTests.LowestDepthException {}

    @SuppressWarnings("serial")
    public class NoDepthException extends ExceptionDepthComparatorTests.TargetException {}
}

