package com.vaadin.tests.server.component.window;


import KeyCode.ARROW_LEFT;
import KeyCode.ARROW_RIGHT;
import KeyCode.ESCAPE;
import KeyCode.SPACEBAR;
import ModifierKey.ALT;
import ModifierKey.CTRL;
import WindowMode.MAXIMIZED;
import WindowRole.ALERTDIALOG;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.declarative.DesignException;
import org.junit.Test;


/**
 * Tests declarative support for implementations of {@link Window}.
 *
 * @author Vaadin Ltd
 */
public class WindowDeclarativeTest extends DeclarativeTestBase<Window> {
    @Test
    public void testDefault() {
        String design = "<vaadin-window>";
        Window expected = new Window();
        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testFeatures() {
        String design = "<vaadin-window position='100,100' window-mode='maximized' " + (((((("center modal resizable=false resize-lazy closable=false draggable=false " + "close-shortcut='ctrl-alt-escape' ") + "assistive-prefix='Hello' assistive-postfix='World' assistive-role='alertdialog' ") + "tab-stop-enabled ") + "tab-stop-top-assistive-text='Do not move above the window' ") + "tab-stop-bottom-assistive-text='End of window'>") + "</vaadin-window>");
        Window expected = new Window();
        expected.setPositionX(100);
        expected.setPositionY(100);
        expected.setWindowMode(MAXIMIZED);
        expected.center();
        expected.setModal((!(expected.isModal())));
        expected.setResizable((!(expected.isResizable())));
        expected.setResizeLazy((!(expected.isResizeLazy())));
        expected.setClosable((!(expected.isClosable())));
        expected.setDraggable((!(expected.isDraggable())));
        expected.removeAllCloseShortcuts();
        expected.addCloseShortcut(ESCAPE, ALT, CTRL);
        expected.setAssistivePrefix("Hello");
        expected.setAssistivePostfix("World");
        expected.setAssistiveRole(ALERTDIALOG);
        expected.setTabStopEnabled((!(expected.isTabStopEnabled())));
        expected.setTabStopTopAssistiveText("Do not move above the window");
        expected.setTabStopBottomAssistiveText("End of window");
        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testMultiCloseShortcuts() {
        Window expected = new Window();
        // Add two shortcuts - should now contain three (default escape + two
        // added)
        expected.addCloseShortcut(SPACEBAR);
        expected.addCloseShortcut(ARROW_LEFT, ALT, CTRL);
        // Try to add the same shortcut again, should be no-op
        expected.addCloseShortcut(ARROW_LEFT, CTRL, ALT);
        // Add a third shortcut, should total four (default escape + three
        // added)
        expected.addCloseShortcut(ARROW_RIGHT, CTRL);
        // Test validity
        String design = "<vaadin-window close-shortcut='escape spacebar ctrl-alt-left ctrl-right' />";
        testRead(design, expected);
        testWrite(design, expected);
        // Try removing the spacebar shortcut
        expected.removeCloseShortcut(SPACEBAR);
        // Test again
        design = "<vaadin-window close-shortcut='escape ctrl-alt-left ctrl-right' />";
        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testInvalidPosition() {
        assertInvalidPosition("");
        assertInvalidPosition("1");
        assertInvalidPosition("100,100.1");
        assertInvalidPosition("x");
        assertInvalidPosition("2,foo");
        // Should be invalid, not checked currently
        // assertInvalidPosition("1,2,3");
    }

    @Test
    public void testChildContent() {
        String design = ("<vaadin-window>" + (createElement(new Button("OK")))) + "</vaadin-window>";
        Window expected = new Window();
        expected.setContent(new Button("OK"));
        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test(expected = DesignException.class)
    public void testMultipleContentChildren() {
        String design = (("<vaadin-window>" + (createElement(new Label("Hello")))) + (createElement(new Button("OK")))) + "</vaadin-window>";
        read(design);
    }

    @Test
    public void testAssistiveDescription() {
        Label assistive1 = new Label("Assistive text");
        Label assistive2 = new Label("More assistive text");
        String design = (("<vaadin-window>" + (createElement(assistive1).attr(":assistive-description", true))) + (createElement(new Button("OK")))) + (createElement(assistive2).attr(":assistive-description", true));
        Window expected = new Window();
        expected.setContent(new Button("OK"));
        expected.setAssistiveDescription(assistive1, assistive2);
        testRead(design, expected);
        String written = (("<vaadin-window>" + (createElement(new Button("OK")))) + (createElement(assistive1).attr(":assistive-description", true))) + (createElement(assistive2).attr(":assistive-description", true));
        testWrite(written, expected);
    }
}

