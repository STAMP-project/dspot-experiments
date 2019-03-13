/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.database.validators;


import org.graylog2.plugin.database.validators.Validator;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class FilledStringValidatorTest {
    @Test
    public void testValidate() throws Exception {
        Validator v = new FilledStringValidator();
        Assert.assertFalse(v.validate(null).passed());
        Assert.assertFalse(v.validate(534).passed());
        Assert.assertFalse(v.validate("").passed());
        Assert.assertFalse(v.validate(new String()).passed());
        Assert.assertTrue(v.validate("so valid").passed());
    }
}

