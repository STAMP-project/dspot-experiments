package com.termux.terminal;


import KeyEvent.KEYCODE_DEL;
import KeyEvent.KEYCODE_DPAD_DOWN;
import KeyEvent.KEYCODE_DPAD_LEFT;
import KeyEvent.KEYCODE_DPAD_RIGHT;
import KeyEvent.KEYCODE_DPAD_UP;
import KeyEvent.KEYCODE_ENTER;
import KeyEvent.KEYCODE_F1;
import KeyEvent.KEYCODE_F10;
import KeyEvent.KEYCODE_F11;
import KeyEvent.KEYCODE_F12;
import KeyEvent.KEYCODE_F2;
import KeyEvent.KEYCODE_F3;
import KeyEvent.KEYCODE_F4;
import KeyEvent.KEYCODE_F5;
import KeyEvent.KEYCODE_F6;
import KeyEvent.KEYCODE_F7;
import KeyEvent.KEYCODE_F8;
import KeyEvent.KEYCODE_F9;
import KeyEvent.KEYCODE_MOVE_END;
import KeyEvent.KEYCODE_MOVE_HOME;
import KeyEvent.KEYCODE_NUMPAD_0;
import KeyEvent.KEYCODE_NUMPAD_1;
import KeyEvent.KEYCODE_NUMPAD_2;
import KeyEvent.KEYCODE_NUMPAD_3;
import KeyEvent.KEYCODE_NUMPAD_4;
import KeyEvent.KEYCODE_NUMPAD_5;
import KeyEvent.KEYCODE_NUMPAD_6;
import KeyEvent.KEYCODE_NUMPAD_7;
import KeyEvent.KEYCODE_NUMPAD_8;
import KeyEvent.KEYCODE_NUMPAD_9;
import KeyEvent.KEYCODE_NUMPAD_COMMA;
import KeyEvent.KEYCODE_NUMPAD_DOT;
import KeyEvent.KEYCODE_SPACE;
import KeyEvent.KEYCODE_TAB;
import KeyHandler.KEYMOD_CTRL;
import KeyHandler.KEYMOD_SHIFT;
import junit.framework.TestCase;

import static KeyHandler.KEYMOD_CTRL;
import static KeyHandler.KEYMOD_SHIFT;


public class KeyHandlerTest extends TestCase {
    /**
     * See http://pubs.opengroup.org/onlinepubs/7990989799/xcurses/terminfo.html
     */
    public void testTermCaps() {
        // Backspace.
        KeyHandlerTest.assertKeysEquals("\u007f", KeyHandler.getCodeFromTermcap("kb", false, false));
        // Back tab.
        KeyHandlerTest.assertKeysEquals("\u001b[Z", KeyHandler.getCodeFromTermcap("kB", false, false));
        // Arrow keys (up/down/right/left):
        KeyHandlerTest.assertKeysEquals("\u001b[A", KeyHandler.getCodeFromTermcap("ku", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[B", KeyHandler.getCodeFromTermcap("kd", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[C", KeyHandler.getCodeFromTermcap("kr", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[D", KeyHandler.getCodeFromTermcap("kl", false, false));
        // .. shifted:
        KeyHandlerTest.assertKeysEquals("\u001b[1;2A", KeyHandler.getCodeFromTermcap("kUP", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2B", KeyHandler.getCodeFromTermcap("kDN", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2C", KeyHandler.getCodeFromTermcap("%i", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2D", KeyHandler.getCodeFromTermcap("#4", false, false));
        // Home/end keys:
        KeyHandlerTest.assertKeysEquals("\u001b[H", KeyHandler.getCodeFromTermcap("kh", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[F", KeyHandler.getCodeFromTermcap("@7", false, false));
        // ... shifted:
        KeyHandlerTest.assertKeysEquals("\u001b[1;2H", KeyHandler.getCodeFromTermcap("#2", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2F", KeyHandler.getCodeFromTermcap("*7", false, false));
        // The traditional keyboard keypad:
        // [Insert] [Home] [Page Up ]
        // [Delete] [End] [Page Down]
        // 
        // Termcap names (with xterm response in parenthesis):
        // K1=Upper left of keypad (xterm sends same "<ESC>[H" = Home).
        // K2=Center of keypad (xterm sends invalid response).
        // K3=Upper right of keypad (xterm sends "<ESC>[5~" = Page Up).
        // K4=Lower left of keypad (xterm sends "<ESC>[F" = End key).
        // K5=Lower right of keypad (xterm sends "<ESC>[6~" = Page Down).
        // 
        // vim/neovim (runtime/doc/term.txt):
        // t_K1 <kHome> keypad home key
        // t_K3 <kPageUp> keypad page-up key
        // t_K4 <kEnd> keypad end key
        // t_K5 <kPageDown> keypad page-down key
        // 
        KeyHandlerTest.assertKeysEquals("\u001b[H", KeyHandler.getCodeFromTermcap("K1", false, false));
        KeyHandlerTest.assertKeysEquals("\u001bOH", KeyHandler.getCodeFromTermcap("K1", true, false));
        KeyHandlerTest.assertKeysEquals("\u001b[5~", KeyHandler.getCodeFromTermcap("K3", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[F", KeyHandler.getCodeFromTermcap("K4", false, false));
        KeyHandlerTest.assertKeysEquals("\u001bOF", KeyHandler.getCodeFromTermcap("K4", true, false));
        KeyHandlerTest.assertKeysEquals("\u001b[6~", KeyHandler.getCodeFromTermcap("K5", false, false));
        // Function keys F1-F12:
        KeyHandlerTest.assertKeysEquals("\u001bOP", KeyHandler.getCodeFromTermcap("k1", false, false));
        KeyHandlerTest.assertKeysEquals("\u001bOQ", KeyHandler.getCodeFromTermcap("k2", false, false));
        KeyHandlerTest.assertKeysEquals("\u001bOR", KeyHandler.getCodeFromTermcap("k3", false, false));
        KeyHandlerTest.assertKeysEquals("\u001bOS", KeyHandler.getCodeFromTermcap("k4", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[15~", KeyHandler.getCodeFromTermcap("k5", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[17~", KeyHandler.getCodeFromTermcap("k6", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[18~", KeyHandler.getCodeFromTermcap("k7", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[19~", KeyHandler.getCodeFromTermcap("k8", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[20~", KeyHandler.getCodeFromTermcap("k9", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[21~", KeyHandler.getCodeFromTermcap("k;", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[23~", KeyHandler.getCodeFromTermcap("F1", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[24~", KeyHandler.getCodeFromTermcap("F2", false, false));
        // Function keys F13-F24 (same as shifted F1-F12):
        KeyHandlerTest.assertKeysEquals("\u001b[1;2P", KeyHandler.getCodeFromTermcap("F3", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2Q", KeyHandler.getCodeFromTermcap("F4", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2R", KeyHandler.getCodeFromTermcap("F5", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2S", KeyHandler.getCodeFromTermcap("F6", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[15;2~", KeyHandler.getCodeFromTermcap("F7", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[17;2~", KeyHandler.getCodeFromTermcap("F8", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[18;2~", KeyHandler.getCodeFromTermcap("F9", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[19;2~", KeyHandler.getCodeFromTermcap("FA", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[20;2~", KeyHandler.getCodeFromTermcap("FB", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[21;2~", KeyHandler.getCodeFromTermcap("FC", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[23;2~", KeyHandler.getCodeFromTermcap("FD", false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[24;2~", KeyHandler.getCodeFromTermcap("FE", false, false));
    }

    public void testKeyCodes() {
        // Return sends carriage return (\r), which normally gets translated by the device driver to newline (\n) unless the ICRNL termios
        // flag has been set.
        KeyHandlerTest.assertKeysEquals("\r", KeyHandler.getCode(KEYCODE_ENTER, 0, false, false));
        // Backspace.
        KeyHandlerTest.assertKeysEquals("\u007f", KeyHandler.getCode(KEYCODE_DEL, 0, false, false));
        // Space.
        TestCase.assertNull(KeyHandler.getCode(KEYCODE_SPACE, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u0000", KeyHandler.getCode(KEYCODE_SPACE, KEYMOD_CTRL, false, false));
        // Back tab.
        KeyHandlerTest.assertKeysEquals("\u001b[Z", KeyHandler.getCode(KEYCODE_TAB, KEYMOD_SHIFT, false, false));
        // Arrow keys (up/down/right/left):
        KeyHandlerTest.assertKeysEquals("\u001b[A", KeyHandler.getCode(KEYCODE_DPAD_UP, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[B", KeyHandler.getCode(KEYCODE_DPAD_DOWN, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[C", KeyHandler.getCode(KEYCODE_DPAD_RIGHT, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[D", KeyHandler.getCode(KEYCODE_DPAD_LEFT, 0, false, false));
        // .. shifted:
        KeyHandlerTest.assertKeysEquals("\u001b[1;2A", KeyHandler.getCode(KEYCODE_DPAD_UP, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2B", KeyHandler.getCode(KEYCODE_DPAD_DOWN, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2C", KeyHandler.getCode(KEYCODE_DPAD_RIGHT, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2D", KeyHandler.getCode(KEYCODE_DPAD_LEFT, KEYMOD_SHIFT, false, false));
        // .. ctrl:ed:
        KeyHandlerTest.assertKeysEquals("\u001b[1;5A", KeyHandler.getCode(KEYCODE_DPAD_UP, KEYMOD_CTRL, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;5B", KeyHandler.getCode(KEYCODE_DPAD_DOWN, KEYMOD_CTRL, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;5C", KeyHandler.getCode(KEYCODE_DPAD_RIGHT, KEYMOD_CTRL, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;5D", KeyHandler.getCode(KEYCODE_DPAD_LEFT, KEYMOD_CTRL, false, false));
        // .. ctrl:ed and shifted:
        int mod = (KEYMOD_CTRL) | (KEYMOD_SHIFT);
        KeyHandlerTest.assertKeysEquals("\u001b[1;6A", KeyHandler.getCode(KEYCODE_DPAD_UP, mod, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;6B", KeyHandler.getCode(KEYCODE_DPAD_DOWN, mod, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;6C", KeyHandler.getCode(KEYCODE_DPAD_RIGHT, mod, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;6D", KeyHandler.getCode(KEYCODE_DPAD_LEFT, mod, false, false));
        // Home/end keys:
        KeyHandlerTest.assertKeysEquals("\u001b[H", KeyHandler.getCode(KEYCODE_MOVE_HOME, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[F", KeyHandler.getCode(KEYCODE_MOVE_END, 0, false, false));
        // ... shifted:
        KeyHandlerTest.assertKeysEquals("\u001b[1;2H", KeyHandler.getCode(KEYCODE_MOVE_HOME, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2F", KeyHandler.getCode(KEYCODE_MOVE_END, KEYMOD_SHIFT, false, false));
        // Function keys F1-F12:
        KeyHandlerTest.assertKeysEquals("\u001bOP", KeyHandler.getCode(KEYCODE_F1, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001bOQ", KeyHandler.getCode(KEYCODE_F2, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001bOR", KeyHandler.getCode(KEYCODE_F3, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001bOS", KeyHandler.getCode(KEYCODE_F4, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[15~", KeyHandler.getCode(KEYCODE_F5, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[17~", KeyHandler.getCode(KEYCODE_F6, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[18~", KeyHandler.getCode(KEYCODE_F7, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[19~", KeyHandler.getCode(KEYCODE_F8, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[20~", KeyHandler.getCode(KEYCODE_F9, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[21~", KeyHandler.getCode(KEYCODE_F10, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[23~", KeyHandler.getCode(KEYCODE_F11, 0, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[24~", KeyHandler.getCode(KEYCODE_F12, 0, false, false));
        // Function keys F13-F24 (same as shifted F1-F12):
        KeyHandlerTest.assertKeysEquals("\u001b[1;2P", KeyHandler.getCode(KEYCODE_F1, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2Q", KeyHandler.getCode(KEYCODE_F2, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2R", KeyHandler.getCode(KEYCODE_F3, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[1;2S", KeyHandler.getCode(KEYCODE_F4, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[15;2~", KeyHandler.getCode(KEYCODE_F5, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[17;2~", KeyHandler.getCode(KEYCODE_F6, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[18;2~", KeyHandler.getCode(KEYCODE_F7, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[19;2~", KeyHandler.getCode(KEYCODE_F8, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[20;2~", KeyHandler.getCode(KEYCODE_F9, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[21;2~", KeyHandler.getCode(KEYCODE_F10, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[23;2~", KeyHandler.getCode(KEYCODE_F11, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("\u001b[24;2~", KeyHandler.getCode(KEYCODE_F12, KEYMOD_SHIFT, false, false));
        KeyHandlerTest.assertKeysEquals("0", KeyHandler.getCode(KEYCODE_NUMPAD_0, 0, false, false));
        KeyHandlerTest.assertKeysEquals("1", KeyHandler.getCode(KEYCODE_NUMPAD_1, 0, false, false));
        KeyHandlerTest.assertKeysEquals("2", KeyHandler.getCode(KEYCODE_NUMPAD_2, 0, false, false));
        KeyHandlerTest.assertKeysEquals("3", KeyHandler.getCode(KEYCODE_NUMPAD_3, 0, false, false));
        KeyHandlerTest.assertKeysEquals("4", KeyHandler.getCode(KEYCODE_NUMPAD_4, 0, false, false));
        KeyHandlerTest.assertKeysEquals("5", KeyHandler.getCode(KEYCODE_NUMPAD_5, 0, false, false));
        KeyHandlerTest.assertKeysEquals("6", KeyHandler.getCode(KEYCODE_NUMPAD_6, 0, false, false));
        KeyHandlerTest.assertKeysEquals("7", KeyHandler.getCode(KEYCODE_NUMPAD_7, 0, false, false));
        KeyHandlerTest.assertKeysEquals("8", KeyHandler.getCode(KEYCODE_NUMPAD_8, 0, false, false));
        KeyHandlerTest.assertKeysEquals("9", KeyHandler.getCode(KEYCODE_NUMPAD_9, 0, false, false));
        KeyHandlerTest.assertKeysEquals(",", KeyHandler.getCode(KEYCODE_NUMPAD_COMMA, 0, false, false));
        KeyHandlerTest.assertKeysEquals(".", KeyHandler.getCode(KEYCODE_NUMPAD_DOT, 0, false, false));
    }
}

