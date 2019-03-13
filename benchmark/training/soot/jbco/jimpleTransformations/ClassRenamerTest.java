/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot.jbco.jimpleTransformations;


import ClassRenamer.name;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static ClassRenamer.name;


public class ClassRenamerTest {
    @Test
    public void getName() {
        Assert.assertThat(ClassRenamer.v().getName(), Matchers.equalTo(name));
    }

    @Test
    public void getDependencies() {
        Assert.assertThat(ClassRenamer.v().getDependencies(), Matchers.equalTo(new String[]{ name }));
    }

    @Test
    public void getPackageName() {
        Assert.assertNull(ClassRenamer.getPackageName(""));
        Assert.assertNull(ClassRenamer.getPackageName(null));
        Assert.assertNull(ClassRenamer.getPackageName("."));
        Assert.assertNull(ClassRenamer.getPackageName("ClassName"));
        Assert.assertEquals("com.sable", ClassRenamer.getPackageName("com.sable.Soot"));
    }

    @Test
    public void getClassName() {
        Assert.assertNull(ClassRenamer.getClassName(""));
        Assert.assertNull(ClassRenamer.getClassName(null));
        Assert.assertNull(ClassRenamer.getClassName("."));
        Assert.assertEquals("ClassName", ClassRenamer.getClassName("ClassName"));
        Assert.assertEquals("Soot", ClassRenamer.getClassName("com.sable.Soot"));
        Assert.assertNull(ClassRenamer.getClassName("com.sable."));
    }

    @Test
    public void getOrAddNewName_cachingName() {
        ClassRenamer.v().setRemovePackages(false);
        ClassRenamer.v().setRenamePackages(false);
        final String newName = ClassRenamer.v().getOrAddNewName(null, "ClassName");
        Assert.assertThat(newName, Matchers.not(Matchers.containsString(".")));
        Map<String, String> mapping = ClassRenamer.v().getClassNameMapping(( pOldName, pNewName) -> pOldName.equals("ClassName"));
        Assert.assertThat(mapping, Matchers.hasEntry("ClassName", newName));
        Assert.assertThat(mapping.size(), Matchers.equalTo(1));
        Assert.assertThat(ClassRenamer.v().getOrAddNewName(null, "ClassName"), Matchers.equalTo(newName));
        mapping = ClassRenamer.v().getClassNameMapping(( pOldName, pNewName) -> pOldName.equals("ClassName"));
        Assert.assertThat(mapping, Matchers.hasEntry("ClassName", newName));
        Assert.assertThat(mapping.size(), Matchers.equalTo(1));
    }

    @Test
    public void getOrAddNewName_cachingPackage() {
        ClassRenamer.v().setRemovePackages(false);
        ClassRenamer.v().setRenamePackages(false);
        final String newName = ClassRenamer.v().getOrAddNewName("pac.age", "ClassName");
        Assert.assertThat(newName, Matchers.allOf(Matchers.startsWith("pac.age."), Matchers.not(Matchers.endsWith("ClassName"))));
        Assert.assertThat(newName.split("\\.").length, Matchers.equalTo(3));
        Assert.assertThat(ClassRenamer.v().getOrAddNewName("pac.age", "ClassName"), Matchers.equalTo(newName));
    }

    @Test
    public void getOrAddNewName_nullClassName() {
        ClassRenamer.v().setRemovePackages(false);
        ClassRenamer.v().setRenamePackages(false);
        final String newName = ClassRenamer.v().getOrAddNewName("pac.age", null);
        Assert.assertThat(newName, Matchers.startsWith("pac.age."));
        Assert.assertThat(newName.split("\\.").length, Matchers.equalTo(3));
        Assert.assertThat(ClassRenamer.v().getOrAddNewName("pac.age", null), Matchers.not(Matchers.equalTo(newName)));
    }

    @Test
    public void getOrAddNewName_renamePackage() {
        ClassRenamer.v().setRemovePackages(false);
        ClassRenamer.v().setRenamePackages(true);
        final String newName = ClassRenamer.v().getOrAddNewName("pac.age.getOrAddNewName_renamePackage", "ClassName");
        Assert.assertThat(newName, Matchers.allOf(Matchers.not(Matchers.startsWith("pac.age.getOrAddNewName_renamePackage.")), Matchers.not(Matchers.endsWith("ClassName"))));
        Assert.assertThat(newName.split("\\.").length, Matchers.equalTo(4));
        Assert.assertThat(ClassRenamer.v().getOrAddNewName("pac.age.getOrAddNewName_renamePackage", "ClassName"), Matchers.equalTo(newName));
    }

    @Test
    public void getOrAddNewName_renamePackage_nullPackage() {
        ClassRenamer.v().setRemovePackages(false);
        ClassRenamer.v().setRenamePackages(true);
        final String newName = ClassRenamer.v().getOrAddNewName(null, "ClassName");
        Assert.assertThat(newName, Matchers.allOf(Matchers.not(Matchers.endsWith("ClassName")), Matchers.not(Matchers.containsString("."))));
        final String newName0 = ClassRenamer.v().getOrAddNewName(null, "ClassName");
        Assert.assertThat(newName0, Matchers.equalTo(newName));// package names and class names are equal

        final String newName1 = ClassRenamer.v().getOrAddNewName(null, "ClassName1");
        Assert.assertThat(newName1, Matchers.not(Matchers.equalTo(newName)));
        Assert.assertThat(newName1.split("\\.").length, Matchers.equalTo(2));
        Assert.assertThat(newName.split("\\.")[0], Matchers.equalTo(newName.split("\\.")[0]));// package names are equal

    }

    @Test
    public void getOrAddNewName_removePackage() {
        ClassRenamer.v().setRemovePackages(true);
        String newName = ClassRenamer.v().getOrAddNewName("a.b.c", "ClassName");
        Assert.assertThat(newName, Matchers.allOf(Matchers.not(Matchers.endsWith("ClassName")), Matchers.not(Matchers.containsString("."))));
        String packageName = "a.b.c";
        for (int i = 0; i < 100; i++) {
            packageName = (packageName + ".p") + i;
            newName = ClassRenamer.v().getOrAddNewName(packageName, "ClassName");
            Assert.assertThat(newName, Matchers.allOf(Matchers.not(Matchers.endsWith("ClassName")), Matchers.not(Matchers.containsString("."))));
        }
    }
}

