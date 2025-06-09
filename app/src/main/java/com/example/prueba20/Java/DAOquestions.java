/*package com.example.prueba20.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public abstract class DAOquestions {
    public static String readQuestion(File questions, int opt) {
        try (Scanner reader = new Scanner(new FileReader(questions)).useDelimiter("\n")) {
            String line;
            for (int i = 0; i <= opt && reader.hasNext(); i++) {
                line = reader.next();
                if (i == opt) return line;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
} */