/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.visitor;


import Operator.EQUAL;
import Operator.GREATER;
import Operator.LESS;
import Operator.NOT_EQUAL;
import VerifierComponentType.FIELD;
import VerifierComponentType.RESTRICTION;
import java.util.Collection;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.verifier.TestBase;
import org.junit.Assert;
import org.junit.Test;


public class ExprConstraintDescrVisitorTest extends TestBase {
    @Test
    public void testVisitPerson() throws Exception {
        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("ExprConstraintDescr1.drl"));
        Assert.assertNotNull(packageDescr);
        packageDescrVisitor.visitPackageDescr(packageDescr);
        Collection<StringRestriction> allRestrictions = verifierData.getAll(RESTRICTION);
        Collection<Field> allFields = verifierData.getAll(FIELD);
        Assert.assertEquals(3, allRestrictions.size());
        Assert.assertEquals(3, allFields.size());
        for (Field field : allFields) {
            Assert.assertNotNull(field.getFieldType());
        }
        assertContainsField("name");
        assertContainsField("lastName");
        assertContainsField("age");
        assertContainsStringRestriction(EQUAL, "toni");
        assertContainsStringRestriction(NOT_EQUAL, "Lake");
        assertContainsNumberRestriction(GREATER, 20);
        assertContainsEval("eval( true )");
    }

    @Test
    public void testVisitAnd() throws Exception {
        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("ExprConstraintDescr2.drl"));
        Assert.assertNotNull(packageDescr);
        packageDescrVisitor.visitPackageDescr(packageDescr);
        Collection<StringRestriction> allRestrictions = verifierData.getAll(RESTRICTION);
        Assert.assertEquals(2, allRestrictions.size());
        assertContainsFields(1);
        assertContainsField("age");
        assertContainsNumberRestriction(GREATER, 0);
        assertContainsNumberRestriction(LESS, 100);
    }

    @Test
    public void testVisitVariableRestriction() throws Exception {
        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("ExprConstraintDescr3.drl"));
        Assert.assertNotNull(packageDescr);
        packageDescrVisitor.visitPackageDescr(packageDescr);
        Collection<StringRestriction> allRestrictions = verifierData.getAll(RESTRICTION);
        Assert.assertEquals(1, allRestrictions.size());
        assertContainsFields(1);
        assertContainsField("age");
        assertContainsVariable("Test 1", "var");
        assertContainsVariableRestriction(EQUAL, "var");
    }
}

