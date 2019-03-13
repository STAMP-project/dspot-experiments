/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.project.server.type;


import BaseProjectType.ID;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.api.core.NotFoundException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author gazarenkov
 */
public class ProjectTypeTest {
    @Test
    public void testInvalidPTDefinition() throws Exception {
        ProjectTypeDef pt = new ProjectTypeDef("my", "second", true, false) {};
        Set<ProjectTypeDef> pts = new HashSet<>();
        pts.add(new ProjectTypeTest.MyProjectType(null));
        pts.add(pt);
        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);
        // BASE and MY (
        Assert.assertEquals(2, reg.getProjectTypes().size());
        // Invalid names
        pts.clear();
        pts.add(new ProjectTypeDef(null, "null id", true, false) {});
        pts.add(new ProjectTypeDef("", "empty id", true, false) {});
        pts.add(new ProjectTypeDef("invalid id", "invalid id", true, false) {});
        pts.add(new ProjectTypeDef("id1", null, true, false) {});
        pts.add(new ProjectTypeDef("id2", "", true, false) {});
        reg = new ProjectTypeRegistry(pts);
        // BASE only
        Assert.assertEquals(1, reg.getProjectTypes().size());
        // Invalid parent
        final ProjectTypeDef invalidParent = new ProjectTypeDef("i-parent", "parent", true, false) {};
        pts.add(new ProjectTypeDef("notRegParent", "not reg parent", true, false) {
            {
                addParent("i-parent");
            }
        });
        reg = new ProjectTypeRegistry(pts);
        // BASE only
        Assert.assertEquals(1, reg.getProjectTypes().size());
    }

    @Test
    public void testPTInheritance() throws Exception {
        Set<ProjectTypeDef> pts = new HashSet<>();
        final ProjectTypeDef parent = new ProjectTypeDef("parent", "parent", true, false) {
            {
                addConstantDefinition("parent_const", "Constant", "const_value");
            }
        };
        final ProjectTypeDef child = new ProjectTypeDef("child", "child", true, false) {
            {
                addParent("parent");
                addConstantDefinition("child_const", "Constant", "const_value");
            }
        };
        pts.add(child);
        pts.add(parent);
        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);
        Assert.assertEquals(3, reg.getProjectTypes().size());
        Assert.assertEquals(1, child.getParents().size());
        Assert.assertEquals(2, child.getAncestors().size());
        Assert.assertEquals((2 + (reg.getProjectType(ID).getAttributes().size())), reg.getProjectType("child").getAttributes().size());
        Assert.assertEquals((1 + (reg.getProjectType(ID).getAttributes().size())), reg.getProjectType("parent").getAttributes().size());
        Assert.assertTrue(reg.getProjectType("child").isTypeOf("parent"));
    }

    @Test
    public void testAttributeNameConflict() throws Exception {
        Set<ProjectTypeDef> pts = new HashSet<>();
        final ProjectTypeDef parent = new ProjectTypeDef("parent", "parent", true, false) {
            {
                addConstantDefinition("parent_const", "Constant", "const_value");
            }
        };
        final ProjectTypeDef child = new ProjectTypeDef("child", "child", true, false) {
            {
                addParent("parent");
                addConstantDefinition("parent_const", "Constant", "const_value");
            }
        };
        pts.add(child);
        pts.add(parent);
        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);
        Assert.assertNotNull(reg.getProjectType("parent"));
        // Assert.assertNull(reg.getProjectType("child"));
        try {
            ProjectTypeDef projectTypeDef = reg.getProjectType("child");
            Assert.assertThat(projectTypeDef, CoreMatchers.is(CoreMatchers.nullValue()));
        } catch (NotFoundException e) {
        }
        Assert.assertEquals(2, reg.getProjectTypes().size());
    }

    @Test
    public void testMultiInheritance() throws Exception {
        Set<ProjectTypeDef> pts = new HashSet<>();
        final ProjectTypeDef parent1 = new ProjectTypeDef("parent1", "parent", true, false) {
            {
                addConstantDefinition("parent1_const", "Constant", "const_value");
            }
        };
        final ProjectTypeDef parent2 = new ProjectTypeDef("parent2", "parent", true, false) {
            {
                addConstantDefinition("parent2_const", "Constant", "const_value");
            }
        };
        final ProjectTypeDef child = new ProjectTypeDef("child", "child", true, false) {
            {
                addParent("parent1");
                addParent("parent2");
                addConstantDefinition("child_const", "Constant", "const_value");
            }
        };
        pts.add(child);
        pts.add(parent1);
        pts.add(parent2);
        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);
        Assert.assertEquals(2, child.getParents().size());
        Assert.assertEquals((3 + (reg.getProjectType(ID).getAttributes().size())), reg.getProjectType("child").getAttributes().size());
    }

    @Test
    public void testMultiInheritanceAttributeConflict() throws Exception {
        Set<ProjectTypeDef> pts = new HashSet<>();
        final ProjectTypeDef parent1 = new ProjectTypeDef("parent1", "parent", true, false) {
            {
                addConstantDefinition("parent_const", "Constant", "const_value");
            }
        };
        final ProjectTypeDef parent2 = new ProjectTypeDef("parent2", "parent", true, false) {
            {
                addConstantDefinition("parent_const", "Constant", "const_value");
            }
        };
        final ProjectTypeDef child = new ProjectTypeDef("child", "child", true, false) {
            {
                addParent("parent1");
                addParent("parent2");
                addConstantDefinition("child_const", "Constant", "const_value");
            }
        };
        pts.add(child);
        pts.add(parent1);
        pts.add(parent2);
        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);
        Assert.assertNotNull(reg.getProjectType("parent1"));
        Assert.assertNotNull(reg.getProjectType("parent2"));
        try {
            ProjectTypeDef projectTypeDef = reg.getProjectType("child");
            Assert.assertThat(projectTypeDef, CoreMatchers.is(CoreMatchers.nullValue()));
        } catch (NotFoundException e) {
        }
        // Assert.assertNull(reg.getProjectType("child"));
    }

    @Test
    public void testTypeOf() throws Exception {
        Set<ProjectTypeDef> pts = new HashSet<>();
        final ProjectTypeDef parent = new ProjectTypeDef("parent", "parent", true, false) {};
        final ProjectTypeDef parent1 = new ProjectTypeDef("parent1", "parent", true, false) {};
        final ProjectTypeDef parent2 = new ProjectTypeDef("parent2", "parent", true, false) {};
        final ProjectTypeDef child = new ProjectTypeDef("child", "child", true, false) {
            {
                addParent("parent");
                addParent("parent2");
            }
        };
        final ProjectTypeDef child2 = new ProjectTypeDef("child2", "child2", true, false) {
            {
                addParent("child");
            }
        };
        pts.add(child);
        pts.add(parent);
        pts.add(child2);
        pts.add(parent1);
        pts.add(parent2);
        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);
        ProjectTypeDef t1 = reg.getProjectType("child2");
        Assert.assertTrue(t1.isTypeOf("parent"));
        Assert.assertTrue(t1.isTypeOf("parent2"));
        Assert.assertTrue(t1.isTypeOf("blank"));
        Assert.assertFalse(t1.isTypeOf("parent1"));
    }

    @Test
    public void testSortPTs() throws Exception {
        Set<ProjectTypeDef> pts = new HashSet<>();
        final ProjectTypeDef parent = new ProjectTypeDef("parent", "parent", true, false) {};
        final ProjectTypeDef child = new ProjectTypeDef("child", "child", true, false) {
            {
                addParent("parent");
            }
        };
        final ProjectTypeDef child2 = new ProjectTypeDef("child2", "child2", true, false) {
            {
                addParent("child");
            }
        };
        pts.add(child);
        pts.add(parent);
        pts.add(child2);
        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);
        List<ProjectTypeDef> list = reg.getProjectTypes(new ProjectTypeRegistry.ChildToParentComparator());
        Assert.assertEquals(list.get(0).getId(), "child2");
        Assert.assertEquals(list.get(1).getId(), "child");
        Assert.assertEquals(list.get(2).getId(), "parent");
        Assert.assertEquals(list.get(3).getId(), "blank");
    }

    @Singleton
    public static class MyVPFactory implements ValueProviderFactory {
        @Override
        public ValueProvider newInstance(String wsPath) {
            return new ProjectTypeTest.MyVPFactory.MyValueProvider();
        }

        public static class MyValueProvider extends ReadonlyValueProvider {
            @Override
            public List<String> getValues(String attributeName) throws ValueStorageException {
                return Arrays.asList("gena");
            }
        }
    }

    @Singleton
    public static class MyProjectType extends ProjectTypeDef {
        @Inject
        public MyProjectType(ProjectTypeTest.MyVPFactory myVPFactory) {
            super("my", "my type", true, false);
            addConstantDefinition("const", "Constant", "const_value");
            addVariableDefinition("var", "Variable", false, myVPFactory);
            addVariableDefinition("var1", "var", true, new AttributeValue("value"));
        }
    }
}

