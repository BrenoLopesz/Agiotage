package com.brenolopes.agiotage;

import android.content.Context;

import java.io.IOException;

public class NameHandler {
    private static String name;
    private static boolean initialized = false;
    private static Context context;

    public static void initialize(Context _context) {
        if(initialized) return;

        context = _context;
        FileHandler.setUp(context, "name.txt");
        FileHandler.createFileIfDoesntExist();

        try {
            name = FileHandler.getData().readLine();
        } catch (IOException e) {}
        initialized = true;
    }

    public static String getName() {
        return initialized? name : null;
    }

    public static void setName(String _name) {
        FileHandler.setUp(context, "name.txt");
        FileHandler.saveData(_name);
        name = _name;
    }

    public static boolean hasName() {
        return name != null;
    }
}
