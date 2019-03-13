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
package org.drools.verifier.jarloader;


import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarInputStream;
import org.junit.Assert;
import org.junit.Test;


public class PackageHeaderLoaderTest {
    private ArrayList<JarInputStream> jarInputStreams;

    @Test
    public void testListAddressAndPetImport() throws Exception {
        PackageHeaderLoader packageHeaderLoader = getPackageHeaderLoader("org.test.Rambo", "org.test.Pet");
        Collection<String> classNames = packageHeaderLoader.getClassNames();
        Assert.assertEquals(2, classNames.size());
        Assert.assertTrue(classNames.contains("org.test.Rambo"));
        Assert.assertTrue(classNames.contains("org.test.Pet"));
    }

    @Test
    public void testListFewClassesThatDoNotExist() throws Exception {
        PackageHeaderLoader packageHeaderLoader = getPackageHeaderLoader("org.test.Rambo", "i.do.not.Exist", "me.Neither");
        Collection<String> classNames = packageHeaderLoader.getClassNames();
        Collection<String> missingClasses = packageHeaderLoader.getMissingClasses();
        Assert.assertEquals(3, classNames.size());
        Assert.assertEquals(2, missingClasses.size());
    }

    @Test
    public void testListFields() throws Exception {
        PackageHeaderLoader packageHeaderLoader = getPackageHeaderLoader("org.test.Person");
        Collection<String> fieldNames = packageHeaderLoader.getFieldNames("org.test.Person");
        Assert.assertTrue(fieldNames.contains("birhtday"));// Yes it is a typo -Rikkola-

        Assert.assertTrue(fieldNames.contains("firstName"));
        Assert.assertTrue(fieldNames.contains("lastName"));
        Assert.assertTrue(fieldNames.contains("pets"));
        Assert.assertTrue(fieldNames.contains("this"));
        Assert.assertEquals("java.lang.String", packageHeaderLoader.getFieldType("org.test.Person", "firstName"));
        Assert.assertEquals("java.lang.String", packageHeaderLoader.getFieldType("org.test.Person", "firstName"));
        Assert.assertEquals("java.util.List", packageHeaderLoader.getFieldType("org.test.Person", "pets"));
        Assert.assertEquals("java.util.Calendar", packageHeaderLoader.getFieldType("org.test.Person", "birhtday"));
        Assert.assertEquals("org.test.Person", packageHeaderLoader.getFieldType("org.test.Person", "this"));
        Assert.assertNull(packageHeaderLoader.getFieldType("org.test.Person", "toString"));
        Assert.assertNull(packageHeaderLoader.getFieldType("org.test.Person", "class"));
        Assert.assertNull(packageHeaderLoader.getFieldType("org.test.Person", "hashCode"));
    }
}

