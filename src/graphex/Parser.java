package graphex;

import java.util.*;

/**
 * This class handles the total transformation of a regex string to a nfa nad dfa tree
 * @author Ryan Bertsche
 *
 */
public class Parser
{
    //List of Characters version of the string regex
    ArrayList<Character> listRegex;
    //An iterator object for the list of chars of the string
    Iterator<Character> iteratorRegex;
    //Current character is the current character to be matched and parsed
    Character currentChar;
    //Holder for the trees that will be created
    FiniteAutomataTree nfaTree;
    FiniteAutomataTree dfaTree;

    /**
     * Constructor that takes in the in the raw regex, and calls all the necessary helper method to build the nfa nad dfa to completion
     * @param rawRegex String is the regex as grabbed by the string argumetn
     */
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

    /**
     * Testing method that is used to print out all nodes for verification, but not used at all in standard use of program
     */
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

    /**
     * Getter for dfa tree
     * @return dfa
     */
    public FiniteAutomataTree getDfaTree()
    {
        return dfaTree;
    }


    /**
     * getter for nfa tree
     * @return nfa
     */
    public FiniteAutomataTree getNfaTree()
    {
        return nfaTree;
    }


    /**
     * First method that starts the recursive decent parse of the regex string.
     * Parser works by recursively returning subtrees, and adding them together based on union, concat, or star operators.
     * Once the tree returns to this method, it is the full nfa tree, and is stored in the global variable
     * @throws Exception
     */
    public void parseStarter() throws Exception {

        nfaTree = parseRegex();
        //If there are mre charadters left after parse is completed, then the regex was invalid
        if(iteratorRegex.hasNext())
        {
            throw new Error("There entered regex was invalid.");
        }

    }

    /**
     * Parse regex level, always calls parse term, and then unions together other parse regex segment if '|' symbol
     * is next symbol
     * @return subtree
     * @throws Exception
     */
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

    /**
     * always calls parsefactor, and if the next char is not another reserved symbol, it calls parseterm again and concats
     * the two peieces together, Smiley face char is a bit of a hack that symbolizes the end of legitimate chars, to help end recursion.
     * It causes no errors because it is not a valid inputtable char in regex
     * @return NFA subtree
     * @throws Exception
     */
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


    /**
     * Always calls parse base, and then if after the next char is *, it stars the returned subtree, and consumes all
     * consecutive '*' because they are valid but do not change tree in any way
     * @return NFA subtree
     * @throws Exception
     */
    private FiniteAutomataTree parseFactor() throws Exception {
        FiniteAutomataTree t;
        t = parseBase();
        if(currentChar == '*')
        {
            t.star();
            do {
                consumeInput();
            } while(currentChar == '*');
        }
        return t;
    }

    /**
     * Parse base is the only method that makes new subtrees.  it first checks to see if it is dealing with a
     * '(' to call parse regex instead. If not i makes a new tree with just 2 nodes for the individual character.  Also catches some regex errors
     * @return NFA subtree
     * @throws Exception
     */
    private FiniteAutomataTree parseBase() throws Exception {
        FiniteAutomataTree t;
        if(currentChar == '(')
        {
            consumeInput();
            t = parseRegex();
            if(currentChar ==')')
                consumeInput();
            else
                throw new Error("Invalid Regex, parenthesis mismatch");

        }
        else if(currentChar == '|' || currentChar == ')' || currentChar == null)
        {
          throw new Error("There is an error in your regex.");
        }
        else
        {
            t = new FiniteAutomataTree(currentChar);
            consumeInput();
        }
        return t;
    }


    /**
     * This method traverses the stored NFA and names all the nodes, which is helpful when you have to output them to dot,
     * so they are nicely labeled, and you can see which NFA nodes are combined in a DFA
     */
    private void nameNFA()
    {
        int x = 0;
        for(FiniteAutomataNode fan: nfaTree.getAllNodes())
        {
            fan.setName(Integer.toString(x));
            x++;
        }
    }
//When all dfas are built, the boolean will be false, and the loop will be broken

    /**
     * This method is called when consuming a char in parsing, and it stores the current char in the iterator for easy peeking.
     * the smiley hack is used again, if there are no ore chars, to help prevent out of bounds errors
     */
    private void consumeInput()
    {
        //System.out.println("Consuming character: " + currentChar);
        if(iteratorRegex.hasNext())
            currentChar = iteratorRegex.next();
        else
            currentChar = '☺';
    }


    /**
     * This method is repsonsible for taking the complete NFA Tree and converting it into a DFA tree.  This is done by
     * starting at the NFA start and doing epsilon enclosure to find the dfa start node.  DFA nodes actually contain a set
     * of all nfa nodes that they are made up of, so they can.  You can then combine all transitions for each character
     * and do an epsilon closure on all of them, and you have a set of NFA nodes, which ends up being the dfa node that that character
     * transitions to. All characters that do not transition to any nodes for a dfa go to a Null/termination state.  This
     * method is slightly more complex, but leads to a DFA that almost always has less than 2^n nodes, as you would get with a
     * straight power set of nfa nodes.  You make a transition for every character in a dfa to another dfa node(If it exists, you
     * just transition to it, if not you make a new dfa node and transition to that).  You keep looping through list of all dfa nodes
     * until each dfa node has all it transitions done. This eventually converges, and the dfa is made.
     */
    private void nfaToDfa()
    {
        //Create new dfa tree
        dfaTree = new FiniteAutomataTree();
        //Create new Node, which will be start node, which is epsilon closure of the nfa start node
        FiniteAutomataNode dfaRoot = new FiniteAutomataNode(epsilonClosure(nfaTree.getStartNode()));
        //Create dfa null state, which is the garbage state for dfa
        FiniteAutomataNode dfaNullState = new FiniteAutomataNode(false);
        //This is the boolean that tracks whether all dfa nodes in the set of all dfa nodes has been checked.
        boolean dfaBuildComplete = true;

        //Adds the start state node to the dfa and sets it as start
        dfaTree.addNode(dfaRoot);
        dfaTree.setStartNode(dfaRoot);

        //Adds the null state to the dfa and sets it to the null state
        dfaNullState.setName("Termination State");
        dfaNullState.setDfaChecked(false);
        dfaTree.addNode(dfaNullState);
        dfaTree.setNullState(dfaNullState);

        /**
         * Loop that keeps whole process going until all dfa states have been made with all transitions.  will stop when
         * conversion converges.
         */
        do
        {
            //sets compete to true.  Will be && with individual node competion to see give acurate representaion of build complete
            dfaBuildComplete = true;
            //have to convert set to array list to cycle through by manual for loop with counter, because iterating through set while adding items to set is an error in java language
            ArrayList<FiniteAutomataNode> cycler = new ArrayList<>(dfaTree.getAllNodes());
            //current size of array when for loop starts
            int i = cycler.size();

            //Loop through all DFAs nodes in dfa tree
            for(int j = 0; j < i; j++)
            {
                //Boolean setter that is only true if all dfa nodes in dfa tree have been marked as completely set, which happens when convergence happens.
                //This happens before connections are added because new nodes could be added by this method, and if this happened after, all nodes might be completed, but new, unchecked nodes could have been added
                dfaBuildComplete = dfaBuildComplete && cycler.get(j).isDfaChecked();

                //If DFA node is not finished, make call helper on that node that makes all the connections
                if(! cycler.get(j).isDfaChecked())
                {
                    nfaToDfaConnectionMaker(cycler.get(j));

                }
            }


        }while(! dfaBuildComplete);

    }

    /**
     * This method takes in a dfa node, and makes all the connections for each character in the language to other dfa nodes.
     * It accomplishes this by doing epsilon enclosures on all transitions for all NFA nodes contained in the DFA node and
     * adding them to a set of NFA Nodes that either matches a contained set NFA nodes in an existing DFA, or a new DFA
     * node is created with the the contained NFA nodes being that set, and added to the DFA tree. The param DFA node
     * then adds a transition to that node.  This is done for all chars in a language.  If no contained NFA nodes transition to a
     * character, a traqnsition is added from the DFA node to the Null DFA state for that character.
     * @param dfaNode
     */
    private void nfaToDfaConnectionMaker(FiniteAutomataNode dfaNode)
    {
        //Character set of all Characters in the language
        HashSet<Character> languageLeftover = new HashSet<>(Grep.getLanguage());

        //Loop for each character that should transition out of the dfa node, based on all transitions out of the contained nfa nodes
        for(Character c : dfaNode.getContainedKeys())
        {
            //Removes the node from the language, because there is a transition for it, and transition to null state doesn;t need to happen for that character
            languageLeftover.remove(c);

            //This is the set of all nfa nodes that can be reached on from a transition on a particular character
            HashSet<FiniteAutomataNode> nodeReachedOnChar = new HashSet<>();

            //Loops through all NFA nodes contained within DFA node
            for(FiniteAutomataNode fan : dfaNode.getDfaContains())
            {
                //If this NFA node transitions to the current char c, add the epsilon enclosure of the destination node to the set of nodes that the dfa node transitions to
                if(fan.hasKey(c))
                    nodeReachedOnChar.addAll(epsilonClosure(fan.getMappedValue(c)));
            }

            //If the dfaTree already contains a DFA node with those contained NFA nodes, add transition from current DFA Node to that existing DFA node on currrent char
            if(dfaTree.containsNode(nodeReachedOnChar))
            {
                dfaNode.addCharactertransition(c, dfaTree.getEquivalentDfa(nodeReachedOnChar));
            }
            //Otherwise, make a new DFA Node with the contained NFA nodes being the reached on char nodes, and transition from current DFA node to newly created dfa node on current char
            else {
                FiniteAutomataNode newNode = new FiniteAutomataNode(nodeReachedOnChar);
                dfaTree.addNode(newNode);
                dfaNode.addCharactertransition(c, newNode);
            }

        }

        //For every character where there were no transitions to other DFA nodes, make transitions to the null state dfa node
        for(Character c: languageLeftover)
            dfaNode.addCharactertransition(c, dfaTree.getNullState());
        dfaNode.setDfaChecked(true);


    }


    /**
     * This is the ever important method that takes a destination nfa node as a parameter, and returns a set of all nfa nodes that
     * can be reached via epsilon closure from that node, which includes recursively finding the epsilon closure for all
     * nodes reached on the previous iteration of epsilon closure.  There can be no infinite loops in this implementation of
     * NFA nodes and trees because of the canonical way nfas are built and the fact that sets are used to store transitions
     * nodes
     * @param node is the node being epsilon closed
     * @return set of nodes that is reachable via epsilon closure fot the given node
     */
    private HashSet<FiniteAutomataNode> epsilonClosure(FiniteAutomataNode node)
    {
        //set of all nodes reached
        HashSet<FiniteAutomataNode> temp = new HashSet<>();

        //Add self node to group, because this method should always at least return set containing originally called node
        temp.add(node);

        //Loop through all nodes that can be reached b yepsilon transition from current node
        for(FiniteAutomataNode fan : node.getEpsilonTransitions())
        {

            //For every node reachable via epsilon, add epsilon closure of the themselves to current list
            temp.addAll(epsilonClosure(fan));
        }

        //return set of all reachable nodes
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
