/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.example.cdi.scopes;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ApplicationScopedRulesJUnitTest {
    public ApplicationScopedRulesJUnitTest() {
    }

    @Inject
    private MyApplicationScopedBean myApplicationBean;

    private ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

    @Test
    public void helloApplicationScoped() {
        Assert.assertNotNull(myApplicationBean);
        ctxCtrl.startContext(ApplicationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        String myBeanId = myApplicationBean.getMyBean().getId();
        long myKieSessionId = myApplicationBean.getkSession().getIdentifier();
        int result = myApplicationBean.doSomething("hello 0");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        Assert.assertEquals(myBeanId, myApplicationBean.getMyBean().getId());
        Assert.assertEquals(myKieSessionId, myApplicationBean.getkSession().getIdentifier());
        myBeanId = myApplicationBean.getMyBean().getId();
        myKieSessionId = myApplicationBean.getkSession().getIdentifier();
        result = myApplicationBean.doSomething("hello 1");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        Assert.assertEquals(myBeanId, myApplicationBean.getMyBean().getId());
        Assert.assertEquals(myKieSessionId, myApplicationBean.getkSession().getIdentifier());
        result = myApplicationBean.doSomething("hello 2");
        Assert.assertEquals(1, result);
    }
}

