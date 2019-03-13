/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.aop.aspectj;


import org.junit.Test;


/**
 * Check that an aspect that depends on another bean, where the referenced bean
 * itself is advised by the same aspect, works correctly.
 *
 * @author Ramnivas Laddad
 * @author Juergen Hoeller
 * @author Chris Beams
 */
@SuppressWarnings("resource")
public class PropertyDependentAspectTests {
    @Test
    public void propertyDependentAspectWithPropertyDeclaredBeforeAdvice() throws Exception {
        checkXmlAspect(((getClass().getSimpleName()) + "-before.xml"));
    }

    @Test
    public void propertyDependentAspectWithPropertyDeclaredAfterAdvice() throws Exception {
        checkXmlAspect(((getClass().getSimpleName()) + "-after.xml"));
    }

    @Test
    public void propertyDependentAtAspectJAspectWithPropertyDeclaredBeforeAdvice() throws Exception {
        checkAtAspectJAspect(((getClass().getSimpleName()) + "-atAspectJ-before.xml"));
    }

    @Test
    public void propertyDependentAtAspectJAspectWithPropertyDeclaredAfterAdvice() throws Exception {
        checkAtAspectJAspect(((getClass().getSimpleName()) + "-atAspectJ-after.xml"));
    }
}

