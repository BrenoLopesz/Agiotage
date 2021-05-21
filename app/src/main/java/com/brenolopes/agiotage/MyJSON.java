package com.brenolopes.agiotage;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyJSON {
    static String fileName = "loans.json";

    private static void createFileIfDoesntExist(Context context) {
        try {
            new File(context.getFilesDir().getPath() + "/" + fileName).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveData(Context context, String data) {
        try {
            FileWriter writer = new FileWriter(context.getFilesDir().getPath() + "/" + fileName);
            writer.write(data);
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static BufferedReader getData(Context context) {
        createFileIfDoesntExist(context);
        try {
            BufferedReader br = new BufferedReader(new FileReader(context.getFilesDir().getPath() + "/" + fileName));
            return br;
        } catch (IOException e) {
            return null;
        }
    }

    public static void addDebt(float value, String name, String description, Context context) {
        // Pega a lista de Debtor atual e adiciona o novo Debtor
        Debtor[] debtors = getDebtors(context);
        Debtor[] new_debtors;

        Gson gson = new Gson();
        Debtor debtor = getDebtorByName(name, debtors);

        // Verifica se Debtor já existe
        if(debtor != null) {
            debtor.addDebt(new Debt(value, description));
            // Converte Debtor[] para JSON
            String json = gson.toJson(debtors);
            saveData(context, json);
            return;
        }

        debtor = new Debtor(name, new Debt[] { new Debt(value, description) });

        // Verifica se o array de debtors é vazia
        if(debtors == null) {
            new_debtors = new Debtor[] { debtor };
        } else {
            new_debtors = new Debtor[debtors.length + 1];
            for (int i = 0; i < debtors.length; i++) {
                Log.i("i", Integer.toString(i));
                new_debtors[i] = debtors[i];
            }
            new_debtors[debtors.length] = debtor;
        }

        // Converte Debtor[] para JSON
        String json = gson.toJson(new_debtors);

        saveData(context, json);
    }

    public static Debtor[] getDebtors(Context context) {
        Gson gson = new Gson();

        if(!new File(context.getFilesDir().getPath() + "/" + fileName).exists())
            return new Debtor[0];

        BufferedReader br = getData(context);
        Debtor[] debtors = gson.fromJson(br, Debtor[].class);

        return debtors;
    }

    private static Debtor getDebtorByName(String name, Debtor[] debtors) {
        for(Debtor debtor : debtors)
            if(debtor.getName().equals(name)) return debtor;

        return null;
    }
}