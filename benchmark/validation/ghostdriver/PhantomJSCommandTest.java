/**
 * This file is part of the GhostDriver by Ivan De Marino <http://ivandemarino.me>.
 *
 * Copyright (c) 2012-2014, Ivan De Marino <http://ivandemarino.me>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ghostdriver;


import junit.framework.TestCase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;


public class PhantomJSCommandTest extends BaseTest {
    @Test
    public void executePhantomJS() {
        WebDriver d = getDriver();
        if (!(d instanceof PhantomJSDriver)) {
            // Skip this test if not using PhantomJS.
            // The command under test is only available when using PhantomJS
            return;
        }
        PhantomJSDriver phantom = ((PhantomJSDriver) (d));
        // Do we get results back?
        Object result = phantom.executePhantomJS("return 1 + 1");
        TestCase.assertEquals(new Long(2), ((Long) (result)));
        // Can we read arguments?
        result = phantom.executePhantomJS("return arguments[0] + arguments[0]", new Long(1));
        TestCase.assertEquals(new Long(2), ((Long) (result)));
        // Can we override some browser JavaScript functions in the page context?
        result = phantom.executePhantomJS(("var page = this;" + (((("page.onInitialized = function () { " + "page.evaluate(function () { ") + "Math.random = function() { return 42 / 100 } ") + "})") + "}")));
        phantom.get("http://ariya.github.com/js/random/");
        WebElement numbers = phantom.findElement(By.id("numbers"));
        boolean foundAtLeastOne = false;
        for (String number : numbers.getText().split(" ")) {
            foundAtLeastOne = true;
            TestCase.assertEquals("42", number);
        }
        assert foundAtLeastOne;
    }
}

