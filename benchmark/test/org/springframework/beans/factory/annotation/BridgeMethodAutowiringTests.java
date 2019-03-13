/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.beans.factory.annotation;


import javax.inject.Inject;
import javax.inject.Named;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;


public class BridgeMethodAutowiringTests {
    @Test
    public void SPR8434() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(BridgeMethodAutowiringTests.UserServiceImpl.class, BridgeMethodAutowiringTests.Foo.class);
        ctx.refresh();
        Assert.assertNotNull(ctx.getBean(BridgeMethodAutowiringTests.UserServiceImpl.class).object);
    }

    abstract static class GenericServiceImpl<D> {
        public abstract void setObject(D object);
    }

    public static class UserServiceImpl extends BridgeMethodAutowiringTests.GenericServiceImpl<BridgeMethodAutowiringTests.Foo> {
        protected BridgeMethodAutowiringTests.Foo object;

        @Override
        @Inject
        @Named("userObject")
        public void setObject(BridgeMethodAutowiringTests.Foo object) {
            if ((this.object) != null) {
                throw new IllegalStateException("Already called");
            }
            this.object = object;
        }
    }

    @Component("userObject")
    public static class Foo {}
}

