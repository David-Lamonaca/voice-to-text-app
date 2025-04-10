package com.voice_to_text;

import java.util.HashMap;
import java.util.Map;

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
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.voice_to_text.managers.SettingsManager;

public class KeySuppressor 
{
    private static HHOOK hhk;
    private static LowLevelKeyboardProc keyboardProc;
    private static Thread messagePumpThread;
    private static volatile boolean isRunning = false;
    private static int messageThreadId = -1;
    private static final Map<String, Integer> keyMap = new HashMap<>();

    static 
    {
        // Function Keys
        for (int i = 1; i <= 12; i++) {
            keyMap.put("F" + i, 0x6F + i); // F1 is 0x70
        }

        // Number Keys (top row)
        for (int i = 0; i <= 9; i++) {
            keyMap.put("DIGIT" + (i), 0x30 + i);
        }

        // Letter Keys
        for (char c = 'A'; c <= 'Z'; c++) {
            keyMap.put(String.valueOf(c), (int) c);
        }

        // Modifier Keys
        keyMap.put("SHIFT", 0xA0);
        keyMap.put("CONTROL", 0xA2);
        keyMap.put("ALT", 0xA4);

        // Other common keys
        keyMap.put("SPACE", 0x20);
        keyMap.put("ENTER", 0x0D);
        keyMap.put("TAB", 0x09);
        keyMap.put("ESCAPE", 0x1B);
        keyMap.put("BACKSPACE", 0x08);
        keyMap.put("DELETE", 0x2E);
        keyMap.put("UP", 0x26);
        keyMap.put("DOWN", 0x28);
        keyMap.put("LEFT", 0x25);
        keyMap.put("RIGHT", 0x27);
        keyMap.put("CAPS_LOCK", 0x14);
    }

    public static void startGlobalHook()
    {
        if (isRunning) return;

        keyboardProc = new LowLevelKeyboardProc() {
            @Override
            public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) 
            {
                if (nCode >= 0) 
                {
                    int msg = wParam.intValue();
                    int vkCode = info.vkCode;

                    String pttKey = SettingsManager.getInstance().getSetting("pushToTalkKey");
                    int pttKeyCode = getVirtualKeyCode(pttKey);

                    String kwaKey = SettingsManager.getInstance().getSetting("keywordActivationKey");
                    int kwaKeyCode = getVirtualKeyCode(kwaKey);

                    if (vkCode == pttKeyCode) 
                    {
                        if (msg == WinUser.WM_KEYDOWN || msg == WinUser.WM_SYSKEYDOWN ||
                            msg == WinUser.WM_KEYUP   || msg == WinUser.WM_SYSKEYUP) 
                        {
                           
                            return new LRESULT(1);
                        }
                    }

                    if (vkCode == kwaKeyCode) 
                    {
                        if (msg == WinUser.WM_KEYDOWN || msg == WinUser.WM_SYSKEYDOWN ||
                            msg == WinUser.WM_KEYUP   || msg == WinUser.WM_SYSKEYUP) 
                        {
                           
                            return new LRESULT(1);
                        }
                    }
                }

                return User32.INSTANCE.CallNextHookEx(hhk, nCode, wParam, new LPARAM(Pointer.nativeValue(info.getPointer())));
            }
        };

        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        hhk = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardProc, hMod, 0);

        messagePumpThread = new Thread(() -> {
            MSG msg = new MSG();
            messageThreadId = Kernel32.INSTANCE.GetCurrentThreadId();
            isRunning = true;

            while (User32.INSTANCE.GetMessage(msg, null, 0, 0) > 0) 
            {
                User32.INSTANCE.TranslateMessage(msg);
                User32.INSTANCE.DispatchMessage(msg);
            }
            isRunning = false;
        }, "KeySuppressor-PumpThread");

        messagePumpThread.start();
    }

    public static void stopGlobalHook() 
    {
        if (!isRunning) return;

        if (hhk != null) 
        {
            User32.INSTANCE.UnhookWindowsHookEx(hhk);
            hhk = null;
        }

        if (messageThreadId != -1) 
        {
            // Send a dummy quit message to stop GetMessage()
            User32.INSTANCE.PostThreadMessage(messageThreadId, WinUser.WM_QUIT, null, null);
        }

        try 
        {
            if (messagePumpThread != null) 
            {
                messagePumpThread.join(500); 
            }
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }

        messagePumpThread = null;
        messageThreadId = -1;
        isRunning = false;
    }

    public static void restartGlobalHook() 
    {
        stopGlobalHook();
        try 
        {
            Thread.sleep(100); 
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
        startGlobalHook();
    }

    public static int getVirtualKeyCode(String keyName) 
    {
        return keyMap.getOrDefault(keyName.toUpperCase(), -1);
    }
}
