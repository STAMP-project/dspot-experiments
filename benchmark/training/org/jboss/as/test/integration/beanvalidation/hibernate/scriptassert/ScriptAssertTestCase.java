/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.beanvalidation.hibernate.scriptassert;


import java.sql.SQLException;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * AS7-1110
 * <p/>
 * Tests that hibernate validator @ScriptAssert works correctly
 * <p/>
 * This is a non-standard extension provided by hibernate validator
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class ScriptAssertTestCase {
    private static final Logger logger = Logger.getLogger(ScriptAssertTestCase.class);

    @Test
    public void testScriptAssert() throws SQLException, NamingException {
        Validator validator = ((Validator) (new InitialContext().lookup("java:comp/Validator")));
        final Set<ConstraintViolation<ScriptAssertBean>> result = validator.validate(new ScriptAssertBean());
        Assert.assertEquals(1, result.size());
    }
}

