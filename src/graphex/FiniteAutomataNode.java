package graphex;

import java.util.*;

/**This is the data structure that hold the nodes/states for the NFA and DFA. Character transitions are stored in a hashmap
 * and epsilon transitions are held in a sperate set. Even though NFAs technically allow multiple transtions on the same character
 * from one node, with this implemntation it does not happen, so a unique hashmap can be used for characters. Also having nodes that
 * are epsilon transitioned to in a separate set helps with unique sets and reducing complexity
 * @author Ryan Bertsche
 */
public class FiniteAutomataNode implements Comparable<FiniteAutomataNode>
{
    //True if state is ann accept state
    private boolean accept;

    //hashmap of character transitions keyed on character and mapped to nodes transitioned to
    private HashMap<Character, FiniteAutomataNode> characterTransitions;

    //Set of nodes reachable from this node via epsilon traansitions
    private HashSet<FiniteAutomataNode> epsilonTransitions;

    //name of this node
    private String name;

    //boolean for DFAs set to true if all character transitions leaving this node have been mapped
    private boolean dfaChecked;

    //Set of all NFA nodes that a DFA node is made up of and contains
    private HashSet<FiniteAutomataNode> dfaContains;


    /**
     * Constructor for making a node while setting accept state and adding all character transitions
     * @param accept true if accepting
     * @param characterTransitions hashmap of transitions
     */
    public FiniteAutomataNode(boolean accept, HashMap<Character, FiniteAutomataNode> characterTransitions)
    {
        this.accept = accept;
        this.characterTransitions = new HashMap<Character, FiniteAutomataNode>(characterTransitions);
        this.epsilonTransitions = new HashSet<FiniteAutomataNode>();
        name = "";
        dfaChecked = false;
        this.dfaContains = new HashSet<FiniteAutomataNode>();
    }

    /**
     * Constructor used specifically for creating dfa nodes.  You feed it the list of contained dfa nodes
     * @param containedNodes set of NFA nodes contained by dfa node
     */
    public FiniteAutomataNode(HashSet<FiniteAutomataNode> containedNodes)
    {
        this.epsilonTransitions = new HashSet<>();
        this.characterTransitions = new HashMap<>();
        this.dfaChecked = false;
        this.dfaContains = new HashSet<>(containedNodes);
        this.accept = this.checkAccept();
        //Special method call that sets the name of a dfa based on the names of the contained nfas, based on alphabetic sorted order
        this.name = dfaNameMaker();

    }

    /**
     * Constructor for making new nfa giving it an accept boolean and a set of epsilon transitions
     * @param accept true if accepting
     * @param epsilonTransitions set of nodes transtiotned to by epsilon transitions
     */
    public FiniteAutomataNode(boolean accept,HashSet<FiniteAutomataNode> epsilonTransitions)
    {
        this.accept = accept;
        this.characterTransitions = new HashMap<Character, FiniteAutomataNode>();
        this.epsilonTransitions = new HashSet<FiniteAutomataNode>(epsilonTransitions);
        name = "";
        dfaChecked = false;
        this.dfaContains = new HashSet<FiniteAutomataNode>();
    }

    /**
     * Constructor for making a new node giving it just the parameter if accept or not accept. Most common constructor used
     * for making new NFA nodes during union concat and kleene closures
     * @param accept true if accepting
     */
    public FiniteAutomataNode(boolean accept)
    {
        this.accept = accept;
        this.characterTransitions = new HashMap<Character, FiniteAutomataNode>();
        this.epsilonTransitions = new HashSet<FiniteAutomataNode>();
        name = "";
        dfaChecked = false;
        this.dfaContains = new HashSet<FiniteAutomataNode>();
    }

    /**
     * Constructor used for making a new NFA node witht the ability to to set a transition right away, and set the accept.
     * This is used to add NFA Nodes when creating a new tree with just 2 nodes and 1 char transition
     * @param accept true if accepting
     * @param k key character
     * @param v value, node
     */
    public FiniteAutomataNode(boolean accept, Character k, FiniteAutomataNode v)
    {
        this.accept = accept;
        this.characterTransitions = new HashMap<Character, FiniteAutomataNode>();
        this.addCharactertransition(k, v);
        this.epsilonTransitions = new HashSet<FiniteAutomataNode>();
        name = "";
        dfaChecked = false;
        this.dfaContains = new HashSet<FiniteAutomataNode>();
    }

    /**
     * Method used by DFA nodes that tells if DFA is accepting by returning true if any contained NFA nodes are accepting
     * @return true if any contained NFA nodes are accepting
     */
    private boolean checkAccept()
    {
        boolean acceptHolder = false;
        for(FiniteAutomataNode fan : this.dfaContains)
        {
            acceptHolder = acceptHolder || fan.getAccept();
        }
        return acceptHolder;
    }

    /**
     * This helper method makes a name for the dfa by sorting all the contained nfa nodes by name and combining them to a build
     * a name that shows all the nfa nodes a dfa node contains
     * @return name of dfa as String
     */
    private String dfaNameMaker()
    {
        String dfaName = "";
        ArrayList<FiniteAutomataNode> tempForSort = new ArrayList<>(this.dfaContains);
        Collections.sort(tempForSort);

        for(FiniteAutomataNode fan: tempForSort) {
            dfaName += fan.getName() + ", ";
        }
        //gets rid of trailing comma
        dfaName = dfaName.replaceAll(", $", "");
        return dfaName;
    }

    /**
     * Adds a transition to the transition table based on the two parameters
     * @param k key Character
     * @param v Value node
     */
    public void addCharactertransition(Character k, FiniteAutomataNode v)
    {
        this.characterTransitions.put(k, v);
    }

    /**
     * Adds epsilon transition to given node
     * @param newEpsilon Node being epsilon transitioned to
     */
    public void addEpsilonTransition(FiniteAutomataNode newEpsilon)
    {
        this.epsilonTransitions.add(newEpsilon);
    }

    /**
     * Getter for if node is accept node
     * @return true if it is accepting node
     */
    public boolean getAccept()
    {
        return accept;
    }

    /**
     * Setter for accpepting state of node
     * @param b true to set node to accepting, false to make it not accepting
     */
    public void setAccept(boolean b)
    {
        accept = b;
    }

    /**
     * Setter for name of node
     * @param name String name to be given to node
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Getter for name of node
     * @return String name of node
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Getter for whether DFA node has been fully checked
     * @return true if all transitions for language have been added to node
     */
    public boolean isDfaChecked()
    {
        return dfaChecked;
    }

    /**
     * Setter for DFA being fully checked, and all transitions added to it
     * @param t boolean true if DFA has been fully checked
     */
    public void setDfaChecked(boolean t)
    {
        dfaChecked = t;
    }

    /**
     * Getter method for a set of All NFA nodes a DFA node contains
     * @return set of NFA nodes contained in this DFA node
     */
    public HashSet<FiniteAutomataNode> getDfaContains()
    {
        return dfaContains;
    }

    /**
     * Method for adding a single NFA to node to the contains of a DFA Node
     * @param fan NFA node to be added to conatins of this DFA Node
     */
    public void addDfaContains(FiniteAutomataNode fan)
    {
        dfaContains.add(fan);
    }

    /**
     * Gets all the nodes that are transitioned to in the transition hashmap for this node(not including epsilon transitions)
     * @return collection of all nodes that are transitioned to from this node
     */
    public Collection<FiniteAutomataNode> getValues()
    {
        return this.characterTransitions.values();

    }

    /**
     * For getting all the characters transitioned to by all the NFA nodes contained within a DFA node
     * @return Set of characters transitioned to by contained NFA nodes inside of DFA
     */
    public HashSet<Character> getContainedKeys()
    {
        HashSet<Character> all = new HashSet<>();
        for(FiniteAutomataNode fan: this.dfaContains)
        {
            all.addAll(fan.getKeys());
        }
        return all;
    }

    /**
     * Getter for all keys in the transition table of this particular node
     * @return set of all key sin transition table
     */
    public HashSet<Character> getKeys()
    {
        HashSet<Character> a = new HashSet<>(this.characterTransitions.keySet());
        return a;
    }

    /**
     * Checks to see if transition on particular character key exists in this node
     * @param c Character to be transitioned on
     * @return boolean true if transition exists for that character
     */
    public boolean hasKey(Character c)
    {
        return characterTransitions.containsKey(c);
    }

    /**
     * Gets the node being transitioned to for a particular character
     * @param c key to be transitioned on
     * @return node that is result of transition
     */
    public FiniteAutomataNode getMappedValue(Character c)
    {
        return characterTransitions.get(c);
    }

    /**
     * method that takes in a set of NFA nodes to check to see if it the same set of nodes that is contained in this DFA node.
     * Using set logic, we have to check if this contains other, and other contains this, otherwise if one was the subset of other it might
     * inaccurately return true. This is handled by the method
     * @param other is the set of NFA nodes that would be contained being checked against
     * @return boolean true if sets are exactly the same, false otherwise
     */
    public boolean dfaEquals(HashSet<FiniteAutomataNode> other)
    {

        return (this.dfaContains.containsAll(other) && other.containsAll(this.dfaContains));
    }


    /**
     * Getter method for all nodes transitioned to via epsilon transition
     * @return Set of all nodes transitioned to on epsilon transition
     */
    public HashSet<FiniteAutomataNode> getEpsilonTransitions()
    {
        return this.epsilonTransitions;
    }

    //This is an overide method that compare two nodes based on their names.  This is only used when a set of nodes is
    //Put in a list and sorted to name other DFA's, so NFA contained nodes are sorted in alphabetical order when naming
    //DFA nodes
    @Override
    public int compareTo(FiniteAutomataNode other) {
        return this.name.compareTo(other.getName());
    }
}


