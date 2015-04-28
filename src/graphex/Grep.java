package graphex;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by ryan on 4/21/15.
 */
public class Grep
{

    private static HashSet<Character> language = new HashSet<>();
    private static boolean printNFA;
    private static boolean printDFA;
    private static String dfaOutput;
    private static String nfaOutput;
    private static String regexAsString;
    private static String inputFileAsString;
    private static FiniteAutomataTree dfa;
    private static FiniteAutomataTree nfa;
    private static Parser inputParse;

    public static void main(String args[])
    {


        String option1 = args[0].substring(0, 2);
        String option2 = args[1].substring(0,2);


        //Analyze the arguments and pull all the data based on the options
        if(option1.equals("-n"))
        {
            printNFA = true;
            nfaOutput = args[0].substring(2);
            if(option2.equals("-d"))
            {
                printDFA = true;
                dfaOutput = args[1].substring(2);
                regexAsString = args[2];
                inputFileAsString = args[3];
            }
            else {
                printDFA = false;
                dfaOutput = "";
                regexAsString = args[1];
                inputFileAsString = args[2];
            }
        }
        else if(option1.equals("-d"))
        {
            printNFA = false;
            printDFA = true;
            nfaOutput = "";
            dfaOutput = args[0].substring(2);
            regexAsString = args[1];
            inputFileAsString = args[2];
        }
        else{
            printDFA = false;
            printNFA = false;
            nfaOutput = "";
            dfaOutput = "";
            regexAsString = args[0];
            inputFileAsString = args[1];
        }
        setLanguage();
        inputParse = new Parser(regexAsString);
        nfa = inputParse.getNfaTree();
        dfa = inputParse.getDfaTree();



        /*
        File searchable = new File(args[])
        language.addAll(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g'));
        Parser ps = new Parser("(a|b)");
        */


        inputParse.printTester();


    }

    public static HashSet<Character> getLanguage()
    {
        return language;
    }

    public static void setLanguage()
    {

        FileInputStream fileInput = null;
        try {
            fileInput = new FileInputStream(inputFileAsString);
            int r;
            while ((r = fileInput.read()) != -1)
            {
                if(!(r == 40 || r == 41 || r == 42 || r == 124 || r == 10))
                {
                    char c = (char) r;
                    language.add(c);
                }
                else
                    throw new Error("FATAL ERROR, OPERATION ABORTED: The input file cannot contain the characters '(, ')' , '*' , or '|'.");



            }
            fileInput.close();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("There was an arror trying to read the file input file");
        }
    }

    private static void run()
    {

        FileInputStream fileInput = null;
        BufferedReader buff = null;
        InputStreamReader sr = null;
        try {

            fileInput = new FileInputStream(inputFileAsString);
            sr = new InputStreamReader(fileInput);
            buff =  new BufferedReader(sr);
            String line =  null;
            System.out.println("The following fines from the input file match the regex: ");
            while ((line = buff.readLine()) != null)
            {
                if(match(line))
                    System.out.println(line);




            }
            fileInput.close();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("There was an arror trying to read the file input file");
        }
    }

    private static boolean match(String line)
    {
        FiniteAutomataNode current;
        current = dfa.getStartNode();
        boolean dead = false;

        for(Character c: line.toCharArray())
        {
            if(! dead)
            {
                current = current.getMappedValue(c);
                if (current.equals(dfa.getNullState()))
                    dead = true;
            }

        }
        return current.getAccept();

    }



}
