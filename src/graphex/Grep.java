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
    private static String inputFileName;
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
                inputFileName = args[3];
            }
            else {
                printDFA = false;
                dfaOutput = "";
                regexAsString = args[1];
                inputFileName = args[2];
            }
        }
        else if(option1.equals("-d"))
        {
            printNFA = false;
            printDFA = true;
            nfaOutput = "";
            dfaOutput = args[0].substring(2);
            regexAsString = args[1];
            inputFileName = args[2];
        }
        else{
            printDFA = false;
            printNFA = false;
            nfaOutput = "";
            dfaOutput = "";
            regexAsString = args[0];
            inputFileName = args[1];
        }
        setLanguage();
        inputParse = new Parser(regexAsString);
        nfa = inputParse.getNfaTree();
        dfa = inputParse.getDfaTree();
        //testAllPrinter();
        //inputParse.printTester();
        run();
        if(printDFA)
            writeToFile(dfaOutput, toDot(dfa));
        if(printNFA)
            writeToFile(nfaOutput, toDot(nfa));
/*
        System.out.println("NFA DOT FILE:");
        System.out.println(toDot(nfa));
        System.out.println("DFA DOT FILE:");
        System.out.println(toDot(dfa));



        File searchable = new File(args[])
        language.addAll(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g'));
        Parser ps = new Parser("(a|b)");
        */





    }

    public static HashSet<Character> getLanguage()
    {
        return language;
    }

    public static void setLanguage()
    {

        FileInputStream fileInput = null;
        try {
            fileInput = new FileInputStream(inputFileName);
            int r;
            while ((r = fileInput.read()) != -1)
            {
                if(!(r == 40 || r == 41 || r == 42 || r == 124 || r == 10))
                {
                    char c = (char) r;
                    language.add(c);
                }
                else if((r == 40 || r == 41 || r == 42 || r == 124))
                    throw new Error("FATAL ERROR, OPERATION ABORTED: The input file cannot contain the characters '(, ')' , '*' , or '|'.");



            }
            fileInput.close();

        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("There was an error trying to read the file input file");
        }

    }

    private static void run()
    {

        FileInputStream fileInput2 = null;
        BufferedReader buff = null;
        InputStreamReader sr = null;
        try {
            int x =1;
            fileInput2 = new FileInputStream(inputFileName);
            sr = new InputStreamReader(fileInput2);
            buff =  new BufferedReader(sr);
            String line =  null;
            System.out.println("The following lines from the input file match the regex: ");
            while ((line = buff.readLine()) != null)
            {
                if(match(line))
                    System.out.println(line);




            }
            fileInput2.close();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("There was an error trying to read the file input file");
        }
    }

    private static boolean match(String line)
    {
        FiniteAutomataNode current;
        current = dfa.getStartNode();

        for(Character c: line.toCharArray())
        {


                current = current.getMappedValue(c);



        }

            return current.getAccept();

    }

    private static void testAllPrinter()
    {
        for(FiniteAutomataNode fan : dfa.getAllNodes())
        {
            System.out.println("Node : " + fan.getName() + ":");
            System.out.println("The characters that transition out of this node are as follows: ");
            for (Character c: fan.getKeys())
            {
                System.out.print(c + ", ");
            }

            System.out.print("\n Start State: " + (fan.getName().equals(dfa.getStartNode().getName())) + "\n");


        }
    }

    private static String toDot(FiniteAutomataTree tree)
    {

        String fullString = "digraph finite_automata{\nrankdir=LR;\nvirtualStart [style = invisible ];\n";
        String subAccept = "";
        String subNotAccept = "";
        String subTransitions = "";
        subAccept += "node [shape = doublecircle]; ";
        subNotAccept += "node [shape = circle]; ";
        for(FiniteAutomataNode fan : tree.getAllNodes())
        {
            String name = fan.getName();
            if (fan.getAccept())
                subAccept += "\"" + name + "\" ";
            else
                subNotAccept += "\"" + name + "\" ";

            for(FiniteAutomataNode destinationNode: new HashSet<>(fan.getValues()))
            {
                String transChars = "";
                for(Character c: fan.getKeys())
                {
                    if(fan.getMappedValue(c).equals(destinationNode))
                        transChars += "'" + c + "', ";
                }
                subTransitions += "\"" + name + "\" -> \""  + destinationNode.getName() + "\" [ label = \"" + transChars + "\" ];\n";
            }

            for(FiniteAutomataNode epsilons : fan.getEpsilonTransitions())
            {
                subTransitions += "\"" + name + "\" -> \""  + epsilons.getName() + "\" [ label = \"ε\" ];\n";
            }


        }
        subAccept += ";\n";
        subNotAccept += ";\n";

        fullString += subAccept + subNotAccept + subTransitions + "virtualStart -> \"" + tree.getStartNode().getName() + "\"\n}";
        return fullString;

    }

    private static void writeToFile(String destination, String toBeWritten)
    {
        System.out.println("writing to destination: " + destination);
        try {
            FileWriter writer = new FileWriter(destination);
            writer.write(toBeWritten);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
