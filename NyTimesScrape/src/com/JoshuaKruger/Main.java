package com.JoshuaKruger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Enter Number of Counties to Parse /OR/ type 0 to take all");

        Scanner userInputScanner = new Scanner(System.in);
        int option;
        option = userInputScanner.nextInt();


        DownloadMe link = new DownloadMe();
        link.CompileLinksAndNames(option);


    }
}
