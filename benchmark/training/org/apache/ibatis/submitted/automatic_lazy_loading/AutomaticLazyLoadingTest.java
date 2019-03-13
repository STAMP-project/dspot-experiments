/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.automatic_lazy_loading;


import java.lang.reflect.Method;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;


public class AutomaticLazyLoadingTest {
    private SqlSession sqlSession;

    private static List<Method> noMethods;

    private static List<Method> objectMethods;

    /**
     * Load an element with 'default' configuration.
     * Expect an exception when none of the 'object methods' is called.
     */
    @Test
    public void selectElementValue_default_nomethods() throws Exception {
        testScenario("default", true, AutomaticLazyLoadingTest.noMethods);
    }

    /**
     * Load an element with 'default' configuration.
     * Expect no exception when one of the 'object methods' is called.
     */
    @Test
    public void selectElementValue_default_objectmethods() throws Exception {
        testScenario("default", false, AutomaticLazyLoadingTest.objectMethods);
    }

    /**
     * Load an element with 'disabled' configuration.
     * Expect an exception when none of the 'object methods' is called.
     */
    @Test
    public void selectElementValue_disabled_nomethods() throws Exception {
        testScenario("disabled", true, AutomaticLazyLoadingTest.noMethods);
    }

    /**
     * Load an element with 'disabled' configuration.
     * Expect an exception when one of the 'object methods' is called.
     * (because calling object methods should not trigger lazy loading)
     */
    @Test
    public void selectElementValue_disabled_objectmethods() throws Exception {
        testScenario("disabled", true, AutomaticLazyLoadingTest.objectMethods);
    }

    /**
     * Load an element with 'enabled' configuration.
     * Expect an exception when none of the 'object methods' is called.
     */
    @Test
    public void selectElementValue_enabled_nomethods() throws Exception {
        testScenario("enabled", true, AutomaticLazyLoadingTest.noMethods);
    }

    /**
     * Load an element with 'enabled' configuration.
     * Expect no exception when one of the 'object methods' is called.
     */
    @Test
    public void selectElementValue_enabled_objectmethods() throws Exception {
        testScenario("enabled", false, AutomaticLazyLoadingTest.objectMethods);
    }
}

