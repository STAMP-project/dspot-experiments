/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.context.annotation;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Oliver Gierke
 */
public class Spr16179Tests {
    @Test
    public void repro() {
        AnnotationConfigApplicationContext bf = new AnnotationConfigApplicationContext(Spr16179Tests.AssemblerConfig.class, Spr16179Tests.AssemblerInjection.class);
        Assert.assertSame(bf.getBean("someAssembler"), bf.getBean(Spr16179Tests.AssemblerInjection.class).assembler0);
        // assertNull(bf.getBean(AssemblerInjection.class).assembler1);  TODO: accidental match
        // assertNull(bf.getBean(AssemblerInjection.class).assembler2);
        Assert.assertSame(bf.getBean("pageAssembler"), bf.getBean(Spr16179Tests.AssemblerInjection.class).assembler3);
        Assert.assertSame(bf.getBean("pageAssembler"), bf.getBean(Spr16179Tests.AssemblerInjection.class).assembler4);
        Assert.assertSame(bf.getBean("pageAssembler"), bf.getBean(Spr16179Tests.AssemblerInjection.class).assembler5);
        Assert.assertSame(bf.getBean("pageAssembler"), bf.getBean(Spr16179Tests.AssemblerInjection.class).assembler6);
    }

    @Configuration
    static class AssemblerConfig {
        @Bean
        Spr16179Tests.PageAssemblerImpl<?> pageAssembler() {
            return new Spr16179Tests.PageAssemblerImpl<>();
        }

        @Bean
        Spr16179Tests.Assembler<Spr16179Tests.SomeType> someAssembler() {
            return new Spr16179Tests.Assembler<Spr16179Tests.SomeType>() {};
        }
    }

    public static class AssemblerInjection {
        @Autowired(required = false)
        Spr16179Tests.Assembler<Spr16179Tests.SomeType> assembler0;

        @Autowired(required = false)
        Spr16179Tests.Assembler<Spr16179Tests.SomeOtherType> assembler1;

        @Autowired(required = false)
        Spr16179Tests.Assembler<Spr16179Tests.Page<String>> assembler2;

        @Autowired(required = false)
        Spr16179Tests.Assembler<Spr16179Tests.Page> assembler3;

        @Autowired(required = false)
        Spr16179Tests.Assembler<Spr16179Tests.Page<?>> assembler4;

        @Autowired(required = false)
        Spr16179Tests.PageAssembler<?> assembler5;

        @Autowired(required = false)
        Spr16179Tests.PageAssembler<String> assembler6;
    }

    interface Assembler<T> {}

    interface PageAssembler<T> extends Spr16179Tests.Assembler<Spr16179Tests.Page<T>> {}

    static class PageAssemblerImpl<T> implements Spr16179Tests.PageAssembler<T> {}

    interface Page<T> {}

    interface SomeType {}

    interface SomeOtherType {}
}

