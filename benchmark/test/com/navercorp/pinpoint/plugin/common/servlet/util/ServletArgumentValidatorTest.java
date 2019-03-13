/**
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.plugin.common.servlet.util;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class ServletArgumentValidatorTest {
    private ArgumentValidator validator;

    @Test
    public void valid() {
        Object[] argument = new Object[2];
        argument[0] = Mockito.mock(ServletRequest.class);
        argument[1] = Mockito.mock(ServletResponse.class);
        Assert.assertTrue(validator.validate(argument));
    }

    @Test
    public void valid_boundary_check() {
        Assert.assertFalse(validator.validate(null));
        Assert.assertFalse(validator.validate(new Object[0]));
        Assert.assertFalse(validator.validate(new Object[1]));
        Object[] argument = new Object[10];
        argument[0] = Mockito.mock(ServletRequest.class);
        argument[1] = Mockito.mock(ServletResponse.class);
        Assert.assertTrue(validator.validate(argument));
    }

    @Test
    public void valid_fail1() {
        Object[] argument = new Object[2];
        argument[0] = Mockito.mock(ServletRequest.class);
        argument[1] = new Object();
        Assert.assertFalse(validator.validate(argument));
    }

    @Test
    public void valid_fail2() {
        Object[] argument = new Object[2];
        argument[0] = new Object();
        argument[1] = Mockito.mock(ServletResponse.class);
        Assert.assertFalse(validator.validate(argument));
    }
}

