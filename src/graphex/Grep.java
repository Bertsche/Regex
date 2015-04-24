package graphex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by ryan on 4/21/15.
 */
public class Grep
{

    private static HashSet<Character> language = new HashSet<>();

    public static void main(String args[])
    {

        language.addAll(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g'));
        Parser ps = new Parser("(a|b)");



        ps.printTester();
    }

    public static HashSet<Character> getLanguage()
    {
        return language;
    }

}
