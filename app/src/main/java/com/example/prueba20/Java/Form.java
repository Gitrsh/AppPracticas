/*package com.example.prueba20.data;

import java.io.File;
import java.util.Scanner;

public abstract class Form {
    private static File file = new File("questions.txt");

    public static String returnQuestion(int opt) {
        return DAOquestions.readQuestion(file,opt);
    }

    public static int selectAnswer() {
        Scanner kb = new Scanner(System.in).useDelimiter("\n");
        int num=0;
        do {
            System.out.println("Indique un número del 1 al 10"); // Esta va a ser la forma temporal de hacerlo hasta que sepamos como pasarlo a interfaz grafica
            System.out.println("1/MUY MALA  -  10/MUY BUENA");
            num = kb.nextInt();
        }while (num<1 && num>10);
        return num;
    }
} */
