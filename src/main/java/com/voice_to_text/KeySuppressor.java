package com.voice_to_text;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.voice_to_text.managers.SettingsManager;

public class KeySuppressor {
    private static HHOOK hhk;

    public static void startGlobalHook() 
    {
        WinUser.LowLevelKeyboardProc keyboardProc = new WinUser.LowLevelKeyboardProc() 
        {
            @Override
            public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) 
            {
                if (nCode >= 0) 
                {
                    int msg = wParam.intValue();
                    int vkCode = info.vkCode;

                    // Fetch the key to suppress
                    String pttKey = SettingsManager.getInstance().getSetting("pushToTalkKey");
                    int pttKeyCode = getVirtualKeyCode(pttKey);

                    if (vkCode == pttKeyCode) 
                    {
                        return new LRESULT(1); 
                    }
                }

                return User32.INSTANCE.CallNextHookEx(
                    hhk,
                    nCode,
                    wParam,
                    new LPARAM(Pointer.nativeValue(info.getPointer()))
                );
            }

        };

        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        hhk = User32.INSTANCE.SetWindowsHookEx(
            WinUser.WH_KEYBOARD_LL,
            keyboardProc,
            hMod,
            0
        );

        // Keep the hook alive
        new Thread(() -> {
            MSG msg = new MSG();
            while (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
                User32.INSTANCE.TranslateMessage(msg);
                User32.INSTANCE.DispatchMessage(msg);
            }
        }).start();
    }

    private static int getVirtualKeyCode(String keyName) 
    {
        if (keyName == null) return -1;
    
        // Map common keys from JNativeHook to Windows VK
        switch (keyName.toUpperCase()) {
            case "SHIFT": return 0xA0; // VK_LSHIFT
            case "CONTROL": return 0xA2; // VK_LCONTROL
            case "ALT": return 0xA4; // VK_LMENU
            case "ENTER": return 0x0D;
            case "SPACE": return 0x20;
            case "ESCAPE": return 0x1B;
            case "TAB": return 0x09;
            case "CAPS_LOCK": return 0x14;
            case "BACKSPACE": return 0x08;
            case "DELETE": return 0x2E;
            case "UP": return 0x26;
            case "DOWN": return 0x28;
            case "LEFT": return 0x25;
            case "RIGHT": return 0x27;
        }
    
        // Try single letter keys like A-Z, 0-9
        if (keyName.length() == 1) {
            char ch = keyName.toUpperCase().charAt(0);
            if (Character.isLetterOrDigit(ch)) {
                return (int) ch;
            }
        }
    
        System.err.println("Unknown key: " + keyName);
        return -1;
    }    
}
