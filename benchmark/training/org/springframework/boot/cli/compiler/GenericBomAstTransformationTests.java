/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.cli.compiler;


import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.io.ReaderSource;
import org.codehaus.groovy.transform.ASTTransformation;
import org.junit.Test;
import org.springframework.boot.groovy.DependencyManagementBom;

import static DependencyManagementBomTransformation.ORDER;


/**
 * Tests for {@link ResolveDependencyCoordinatesTransformation}
 *
 * @author Andy Wilkinson
 * @author Dave Syer
 */
public final class GenericBomAstTransformationTests {
    private final SourceUnit sourceUnit = new SourceUnit(((String) (null)), ((ReaderSource) (null)), null, null, null);

    private final ModuleNode moduleNode = new ModuleNode(this.sourceUnit);

    private final ASTTransformation transformation = new GenericBomAstTransformation() {
        @Override
        public int getOrder() {
            return (ORDER) - 10;
        }

        @Override
        protected String getBomModule() {
            return "test:child:1.0.0";
        }
    };

    @Test
    public void transformationOfEmptyPackage() {
        this.moduleNode.setPackage(new PackageNode("foo"));
        this.transformation.visit(new ASTNode[]{ this.moduleNode }, this.sourceUnit);
        assertThat(getValue().toString()).isEqualTo("[test:child:1.0.0]");
    }

    @Test
    public void transformationOfClass() {
        this.moduleNode.addClass(ClassHelper.make("MyClass"));
        this.transformation.visit(new ASTNode[]{ this.moduleNode }, this.sourceUnit);
        assertThat(getValue().toString()).isEqualTo("[test:child:1.0.0]");
    }

    @Test
    public void transformationOfClassWithExistingManagedDependencies() {
        this.moduleNode.setPackage(new PackageNode("foo"));
        ClassNode cls = ClassHelper.make("MyClass");
        this.moduleNode.addClass(cls);
        AnnotationNode annotation = new AnnotationNode(ClassHelper.make(DependencyManagementBom.class));
        annotation.addMember("value", new ConstantExpression("test:parent:1.0.0"));
        cls.addAnnotation(annotation);
        this.transformation.visit(new ASTNode[]{ this.moduleNode }, this.sourceUnit);
        assertThat(getValue().toString()).isEqualTo("[test:parent:1.0.0, test:child:1.0.0]");
    }
}

