package graphex;

import java.util.*;

/**
 * Created by ryan on 4/21/15.
 */
public class Parser
{

    ArrayList<Character> listRegex;
    Iterator<Character> iteratorRegex;
    Character currentChar;
    FiniteAutomataTree nfaTree;
    FiniteAutomataTree dfaTree;

    public Parser(String rawRegex)
    {
        listRegex = new ArrayList<Character>(charToObject(rawRegex));
        iteratorRegex = listRegex.iterator();
        if(iteratorRegex.hasNext())
            currentChar = iteratorRegex.next();
        try {
            parseStarter();
            nameNFA();
            nfaToDfa();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void printTester()
    {
        System.out.println("NFA NODES!!!!!!!!!!!!!!!!!!!!!!!!!");
        for(FiniteAutomataNode fan : nfaTree.getAllNodes()) {
            System.out.println("Node: " + fan.getName());

        }
        System.out.println("DFA NODES!!!!!!!!!!!!!!!!!!!!!!!!!");
        for(FiniteAutomataNode fan : dfaTree.getAllNodes()) {
            System.out.println("Node: " + fan.getName());

        }

    }

    public FiniteAutomataTree getDfaTree()
    {
        return dfaTree;
    }


    public FiniteAutomataTree getNfaTree()
    {
        return nfaTree;
    }


    public void parseStarter() throws Exception {

        nfaTree = parseRegex();

    }

    private FiniteAutomataTree parseRegex() throws Exception {
        FiniteAutomataTree t;
        t = parseTerm();
        if(currentChar == '|')
        {
            consumeInput();
            FiniteAutomataTree t2;
            t2 = parseRegex();
            t.union(t2);
        }
        return t;

    }

    private FiniteAutomataTree parseTerm() throws Exception {
        FiniteAutomataTree t;
        t = parseFactor();
        if(currentChar != '|'  && currentChar != null && currentChar != ')' && currentChar != '☺')
        {
            FiniteAutomataTree t2;
            t2 = parseTerm();
            t.concat(t2);
        }

        return t;
    }



    private FiniteAutomataTree parseFactor() throws Exception {
        FiniteAutomataTree t = parseBase();
        if(currentChar == '*')
        {
            t.star();
            do {
                consumeInput();
            } while(currentChar == '*');
        }
        return t;
    }

    private FiniteAutomataTree parseBase() throws Exception {
        FiniteAutomataTree t;
        if(currentChar == '(')
        {
            consumeInput();
            t = parseRegex();
            consumeInput();
        }
        else if(currentChar == null)
        {
          throw new Exception("There is an error in your regex: Expected character, instead got end of regex!");
        }
        else
        {
            t = new FiniteAutomataTree(currentChar);
            consumeInput();
        }
        return t;
    }



    private void nameNFA()
    {
        int x = 0;
        for(FiniteAutomataNode fan: nfaTree.getAllNodes())
        {
            fan.setName(Integer.toString(x));
            x++;
        }
    }





    private void consumeInput()
    {
        System.out.println("Consuming character: " + currentChar);
        if(iteratorRegex.hasNext())
            currentChar = iteratorRegex.next();
        else
            currentChar = '☺';
    }






    private void nfaToDfa()
    {
        dfaTree = new FiniteAutomataTree();
        FiniteAutomataNode dfaRoot = new FiniteAutomataNode(epsilonClosure(nfaTree.getStartNode()));
        FiniteAutomataNode dfaNullState = new FiniteAutomataNode(false);
        boolean dfaBuildComplete = true;


        dfaTree.addNode(dfaRoot);
        dfaTree.setStartNode(dfaRoot);

        dfaNullState.setName("Termination State");
        dfaNullState.setDfaChecked(true);
        dfaTree.addNode(dfaNullState);
        dfaTree.setNullState(dfaNullState);


        do
        {
            dfaBuildComplete = true;
            ArrayList<FiniteAutomataNode> cycler = new ArrayList<>(dfaTree.getAllNodes());
            int i = cycler.size();

            for(int j = 0; j < i; j++)
            {
                System.out.println("j = " + j);
                dfaBuildComplete = dfaBuildComplete && cycler.get(j).isDfaChecked();
                if(! cycler.get(j).isDfaChecked())
                {
                    nfaToDfaConnectionMaker(cycler.get(j));

                }
            }

           /*
            for(FiniteAutomataNode fan :cycler)
            {
                dfaBuildComplete = dfaBuildComplete && fan.isDfaChecked();
                if(! fan.isDfaChecked())
                {
                    nfaToDfaConnectionMaker(fan);

                }

            }
            */

        }while(! dfaBuildComplete);

    }

    private void nfaToDfaConnectionMaker(FiniteAutomataNode dfaNode)
    {
        HashSet<Character> languageLeftover = Grep.getLanguage();

        for(Character c : dfaNode.getContainedKeys())
        {
            languageLeftover.remove(c);
            HashSet<FiniteAutomataNode> nodeReachedOnChar = new HashSet<>();
            for(FiniteAutomataNode fan : dfaNode.getDfaContains())
            {
                if(fan.hasKey(c))
                    nodeReachedOnChar.addAll(epsilonClosure(fan.getMappedValue(c)));
            }
            if(dfaTree.containsNode(nodeReachedOnChar))
            {
                dfaNode.addCharactertransition(c, dfaTree.getEquivalentDfa(nodeReachedOnChar));
            }
            else {
                FiniteAutomataNode newNode = new FiniteAutomataNode(nodeReachedOnChar);
                dfaTree.addNode(newNode);
                dfaNode.addCharactertransition(c, newNode);
            }

        }

        for(Character c: languageLeftover)
            dfaNode.addCharactertransition(c, dfaTree.getNullState());
        dfaNode.setDfaChecked(true);


    }







    private void addIfUnique(FiniteAutomataNode newNode, ArrayList<FiniteAutomataNode> nodeList)
    {
        boolean unique = true;
        for(FiniteAutomataNode fan: nodeList)
        {
            if(fan.compareTo(newNode) == 0)
                unique = false;
        }

        if(unique)
            nodeList.add(newNode);
    }


    private void addIfUnique(ArrayList<FiniteAutomataNode> newNodes, ArrayList<FiniteAutomataNode> nodeList)
    {
        for(FiniteAutomataNode nfan: newNodes)
            addIfUnique(nfan, nodeList);
    }



    private HashSet<FiniteAutomataNode> epsilonClosure(FiniteAutomataNode node)
    {
        HashSet<FiniteAutomataNode> temp = new HashSet<>();
        temp.add(node);
        for(FiniteAutomataNode fan : node.getEpsilonTransitions())
        {
            temp.addAll(epsilonClosure(fan));
        }
        return temp;
    }








    /**
     * Helper method to turn string into ArrayList of Characters for simpler parsing, and use of built in list iterators
     * @param s is the raw regex string
     * @return ArrayList of Characters in
     */
    private  ArrayList charToObject(String s)
    {
        char cArray[] = s.toCharArray();
        ArrayList<Character> cObjects = new ArrayList<>(cArray.length);
        for(char c : cArray)
        {
            cObjects.add(c);
        }
        return cObjects;
    }

}
