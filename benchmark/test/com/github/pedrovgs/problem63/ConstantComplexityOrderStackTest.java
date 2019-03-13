/**
 * Copyright (C) 2014 Pedro Vicente G?mez S?nchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs.problem63;


import com.github.pedrovgs.problem63.ConstantComplexityOrderStack.Stack1;
import com.github.pedrovgs.problem63.ConstantComplexityOrderStack.Stack2;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class ConstantComplexityOrderStackTest {
    private Stack1 stack1;

    private Stack2 stack2;

    @Test
    public void shouldReturnMinElementInTheStack1() {
        stack1.push(1);
        stack1.push(2);
        stack1.push(3);
        stack1.push((-1));
        Assert.assertEquals((-1), stack1.getMin());
    }

    @Test
    public void shouldReturnMinElementInTheStack1EvenAfterModifyStackContent() {
        stack1.push(1);
        stack1.push(2);
        stack1.push(3);
        stack1.push((-1));
        stack1.pop();
        Assert.assertEquals(1, stack1.getMin());
    }

    @Test
    public void shouldReturnMinElementInTheStack2() {
        stack2.push(1);
        stack2.push(2);
        stack2.push(3);
        stack2.push((-1));
        Assert.assertEquals((-1), stack2.getMin());
    }

    @Test
    public void shouldReturnMinElementInTheStack2EvenAfterModifyStackContent() {
        stack2.push(1);
        stack2.push(2);
        stack2.push(3);
        stack2.push((-1));
        stack2.pop();
        Assert.assertEquals(1, stack2.getMin());
    }
}

