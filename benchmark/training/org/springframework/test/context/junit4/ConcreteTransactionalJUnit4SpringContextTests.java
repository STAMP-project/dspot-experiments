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
package org.springframework.test.context.junit4;


import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.transaction.TransactionTestUtils;
import org.springframework.tests.sample.beans.Employee;
import org.springframework.tests.sample.beans.Pet;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Combined integration test for {@link AbstractJUnit4SpringContextTests} and
 * {@link AbstractTransactionalJUnit4SpringContextTests}.
 *
 * @author Sam Brannen
 * @since 2.5
 */
@ContextConfiguration
public class ConcreteTransactionalJUnit4SpringContextTests extends AbstractTransactionalJUnit4SpringContextTests implements BeanNameAware , InitializingBean {
    private static final String JANE = "jane";

    private static final String SUE = "sue";

    private static final String YODA = "yoda";

    private Employee employee;

    @Autowired
    private Pet pet;

    @Autowired(required = false)
    private Long nonrequiredLong;

    @Resource
    private String foo;

    private String bar;

    private String beanName;

    private boolean beanInitialized = false;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void verifyBeanNameSet() {
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertTrue(("The bean name of this test instance should have been set to the fully qualified class name " + "due to BeanNameAware semantics."), this.beanName.startsWith(getClass().getName()));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void verifyApplicationContext() {
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertNotNull("The application context should have been set due to ApplicationContextAware semantics.", super.applicationContext);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void verifyBeanInitialized() {
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertTrue("This test bean should have been initialized due to InitializingBean semantics.", this.beanInitialized);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void verifyAnnotationAutowiredFields() {
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertNull("The nonrequiredLong property should NOT have been autowired.", this.nonrequiredLong);
        Assert.assertNotNull("The pet field should have been autowired.", this.pet);
        Assert.assertEquals("Fido", this.pet.getName());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void verifyAnnotationAutowiredMethods() {
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertNotNull("The employee setter method should have been autowired.", this.employee);
        Assert.assertEquals("John Smith", this.employee.getName());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void verifyResourceAnnotationWiredFields() {
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertEquals("The foo field should have been wired via @Resource.", "Foo", this.foo);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void verifyResourceAnnotationWiredMethods() {
        TransactionTestUtils.assertInTransaction(false);
        Assert.assertEquals("The bar method should have been wired via @Resource.", "Bar", this.bar);
    }

    @Test
    public void modifyTestDataWithinTransaction() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertEquals("Adding jane", 1, addPerson(ConcreteTransactionalJUnit4SpringContextTests.JANE));
        Assert.assertEquals("Adding sue", 1, addPerson(ConcreteTransactionalJUnit4SpringContextTests.SUE));
        Assert.assertEquals("Verifying the number of rows in the person table in modifyTestDataWithinTransaction().", 4, countRowsInPersonTable());
    }
}

