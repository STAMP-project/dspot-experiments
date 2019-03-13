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
package org.springframework.web.jsf;


import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.lang.Nullable;


/**
 *
 *
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 */
public class DelegatingNavigationHandlerTests {
    private final MockFacesContext facesContext = new MockFacesContext();

    private final StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();

    private final DelegatingNavigationHandlerTests.TestNavigationHandler origNavHandler = new DelegatingNavigationHandlerTests.TestNavigationHandler();

    private final DelegatingNavigationHandlerProxy delNavHandler = new DelegatingNavigationHandlerProxy(origNavHandler) {
        @Override
        protected BeanFactory getBeanFactory(FacesContext facesContext) {
            return beanFactory;
        }
    };

    @Test
    public void handleNavigationWithoutDecoration() {
        DelegatingNavigationHandlerTests.TestNavigationHandler targetHandler = new DelegatingNavigationHandlerTests.TestNavigationHandler();
        beanFactory.addBean("jsfNavigationHandler", targetHandler);
        delNavHandler.handleNavigation(facesContext, "fromAction", "myViewId");
        Assert.assertEquals("fromAction", targetHandler.lastFromAction);
        Assert.assertEquals("myViewId", targetHandler.lastOutcome);
    }

    @Test
    public void handleNavigationWithDecoration() {
        DelegatingNavigationHandlerTests.TestDecoratingNavigationHandler targetHandler = new DelegatingNavigationHandlerTests.TestDecoratingNavigationHandler();
        beanFactory.addBean("jsfNavigationHandler", targetHandler);
        delNavHandler.handleNavigation(facesContext, "fromAction", "myViewId");
        Assert.assertEquals("fromAction", targetHandler.lastFromAction);
        Assert.assertEquals("myViewId", targetHandler.lastOutcome);
        // Original handler must have been invoked as well...
        Assert.assertEquals("fromAction", origNavHandler.lastFromAction);
        Assert.assertEquals("myViewId", origNavHandler.lastOutcome);
    }

    static class TestNavigationHandler extends NavigationHandler {
        private String lastFromAction;

        private String lastOutcome;

        @Override
        public void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
            lastFromAction = fromAction;
            lastOutcome = outcome;
        }
    }

    static class TestDecoratingNavigationHandler extends DecoratingNavigationHandler {
        private String lastFromAction;

        private String lastOutcome;

        @Override
        public void handleNavigation(FacesContext facesContext, @Nullable
        String fromAction, @Nullable
        String outcome, @Nullable
        NavigationHandler originalNavigationHandler) {
            lastFromAction = fromAction;
            lastOutcome = outcome;
            if (originalNavigationHandler != null) {
                originalNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
            }
        }
    }
}

