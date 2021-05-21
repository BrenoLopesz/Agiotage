package com.brenolopes.agiotage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
    private static Context context;
    private static String fileName;

    public static void setContext(Context _context) {
        context = _context;
    }

    public static void setFileName(String _fileName) {
        fileName = _fileName;
    }

    public static void setUp(Context _context, String _fileName) {
        setContext(_context);
        setFileName(_fileName);
    }

    public static void createFileIfDoesntExist() {
        if(isContextNull() || isFileNameEmpty())
            return;

        try {
            new File(context.getFilesDir().getPath() + "/" + fileName).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveData(String data) {
        if(isContextNull() || isFileNameEmpty())
            return false;

        try {
            FileWriter writer = new FileWriter(context.getFilesDir().getPath() + "/" + fileName);
            writer.write(data);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static BufferedReader getData() {
        if(isContextNull() || isFileNameEmpty())
            return null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(context.getFilesDir().getPath() + "/" + fileName));
            return br;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isContextNull() {
        if(context == null)
            Log.e("Context", "Context is null!");

        return context == null;
    }

    public static boolean isFileNameEmpty() {
        boolean isEmpty = fileName == null || fileName.isEmpty();
        if(isEmpty)
            Log.e("File Name", "File name is empty!");

        return isEmpty;
    }
}
