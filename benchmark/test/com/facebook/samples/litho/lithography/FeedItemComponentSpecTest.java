/**
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook.samples.litho.lithography;


import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.testing.ComponentsRule;
import com.facebook.litho.testing.helper.ComponentTestHelper;
import com.facebook.litho.testing.subcomponents.SubComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class FeedItemComponentSpecTest {
    @Rule
    public ComponentsRule mComponentsRule = new ComponentsRule();

    private Component mComponent;

    @Test
    public void testRecursiveSubComponentExists() {
        final ComponentContext c = mComponentsRule.getContext();
        assertThat(c, mComponent).extractingSubComponents(c).hasSize(2);
    }

    @Test
    public void testLithoViewSubComponentMatching() {
        final ComponentContext c = mComponentsRule.getContext();
        final LithoView lithoView = ComponentTestHelper.mountComponent(c, mComponent);
        assertThat(lithoView).has(deepSubComponentWith(textEquals("Sindre Sorhus")));
    }

    @Test
    public void testSubComponentLegacyBridge() {
        final ComponentContext c = mComponentsRule.getContext();
        assertThat(c, mComponent).has(subComponentWith(c, legacySubComponent(SubComponent.of(FooterComponent.create(c).text("Rockstar Developer").build()))));
    }
}

