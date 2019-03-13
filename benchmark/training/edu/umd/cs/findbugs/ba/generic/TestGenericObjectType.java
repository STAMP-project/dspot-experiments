/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2006, University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.umd.cs.findbugs.ba.generic;


import GenericUtilities.TypeCategory.PARAMETERIZED;
import edu.umd.cs.findbugs.ba.generic.GenericUtilities.TypeCategory;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;
import org.junit.Test;


/**
 *
 *
 * @author Nat Ayewah
 */
public class TestGenericObjectType {
    GenericObjectType obj;

    String javaSignature;

    String underlyingClass;

    TypeCategory typeCategory;

    @Nullable
    String variable;

    @Nullable
    Type extension;

    List<ReferenceType> parameters;

    @Test
    public void testParameterizedList() {
        initTest("Ljava/util/List<Ljava/lang/Comparable;>;", "java.util.List<java.lang.Comparable>", "java.util.List", PARAMETERIZED, null, null, GenericUtilities.getTypeParameters("Ljava/lang/Comparable;"));
        processTest();
    }
}

