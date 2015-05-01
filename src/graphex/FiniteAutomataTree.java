package graphex;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is the data structure that holds the DFA and NFA Trees
 *@author Ryan Bertsche
 */
public class FiniteAutomataTree
{
    //Set of all nodes contained within tree, slight memory tradeoff for much greater ease when dealing with tree
    private HashSet<FiniteAutomataNode> allNodes;

    //Variable that holds start node.  Essentailly root of tree
    private FiniteAutomataNode startNode;

    //Node that holds null state, only used in DFA implementation
    private FiniteAutomataNode nullState;

    /**
     * Constructor that makes a new tree with no predifined values
     */
    public FiniteAutomataTree()
    {
        allNodes = new HashSet<FiniteAutomataNode>();
        startNode = null;
    }

    /**
     * Constructor that makes tree based on 1 character, used specifically when building nfa.  It make two new nodes
     * and links them together with a transition between on parameter char, sets the start state, and adds both to set of all nodes
     * @param c is the Character that is encountered when making a tree for a single character
     */
    public FiniteAutomataTree(Character c)
    {
        allNodes = new HashSet<FiniteAutomataNode>();
        FiniteAutomataNode accept = new FiniteAutomataNode(true);
        FiniteAutomataNode start = new FiniteAutomataNode(false, c, accept);
        allNodes.add(accept);
        allNodes.add(start);
        this.startNode = start;
    }

    /**
     * Getter method for startNode/Root of tree
     * @return start node
     */
    public FiniteAutomataNode getStartNode()
    {
        return startNode;
    }

    /**
     * Getter for allNodes in tree
     * @return set of all nodes in tree
     */
    public HashSet<FiniteAutomataNode> getAllNodes()
    {
        return allNodes;
    }

    /**
     * Setter method that cahnges all accept nodes in tree to non-accept nodes
     */
    public void setAcceptToFalse()
    {
        for(FiniteAutomataNode fan : this.getAllAccept())
            fan.setAccept(false);
    }

    /**
     * Setter that takes in a node, presumably the start node of a tree, and makes all the accept nodes in this tree transition to that start node
     * @param start node that will be the start node of the tree
     */
    public void epsilonToCurrentStart(FiniteAutomataNode start)
    {
        for (FiniteAutomataNode fan : this.getAllAccept())
            fan.addEpsilonTransition(start);
    }

    /**
     * getter that returns all nodes in this tree that are accept states, by getting the accept value from each node in tree
     * @return list of all accept nodes in the tree
     */
    private ArrayList<FiniteAutomataNode> getAllAccept()
    {
        ArrayList<FiniteAutomataNode> tempList = new ArrayList<FiniteAutomataNode>();
        for(FiniteAutomataNode fan: this.allNodes)
        {
            if(fan.getAccept())
                tempList.add(fan);
        }
        return tempList;
    }


    /**
     * This method takes in a tree as a parameter and unions it with this tree, using the canonical form for union
     * @param t Other tree to be unioned with
     */
    public void union(FiniteAutomataTree t)
    {
        //Create a new node for the start node
        FiniteAutomataNode newStart = new FiniteAutomataNode(false);

        //Epsilon transition from new start node to old start nodes of other 2 trees
        newStart.addEpsilonTransition(this.startNode);
        newStart.addEpsilonTransition(t.getStartNode());

        //Add all nodes from other tree to set of nodes in this tree
        this.allNodes.addAll(t.getAllNodes());

        //add new start node to set of all nodes in this tree
        this.allNodes.add(newStart);

        //set new start node to start node of this tree
        this.startNode = newStart;
    }

    /**
     * Concatenates this tree with other tree given in the parameter using the canonical concatenation form
     * @param t other tree to be concatenated with
     */
    public void concat(FiniteAutomataTree t)
    {
        //Epsilon accept states of this tree to start of other tree
        this.epsilonToCurrentStart(t.getStartNode());

        //Set all accepts states of this tree to non-accept states
        this.setAcceptToFalse();

        //Add all nodes of other tree to set of all nodes in this tree
        this.allNodes.addAll(t.getAllNodes());
    }

    /**
     * Does a Kleene closure on the current tree using the canonical form of Kleene closure
     */
    public void star()
    {
        //makes a new start node that is accepting
        FiniteAutomataNode newStart = new FiniteAutomataNode(true);

        //Adds new start to list of all nodes in this tree
        this.allNodes.add(newStart);

        //epsilon all accept states of this tree to old start state, (includes newly added start state)
        this.epsilonToCurrentStart(this.startNode);

        //Sets new start state to the start state of this tree
        this.startNode = newStart;
    }

    /**
     * Setter for start node
     * @param node Node that will be the new start node,Must be independently added to list of all nodes in tree, or already exist
     */
    public void setStartNode(FiniteAutomataNode node)
    {
        startNode = node;


    }

    /**
     * Setter that adds node to set of all nodes in the tree
     * @param node node to be added
     */
    public void addNode(FiniteAutomataNode node)
    {
        allNodes.add(node);
    }

    /**
     * Takes in a set of nfa nodes,and checks all the nodes in the tree to if there is a dfa node that contains that same set of NFA nodes
     * @param other is set of contained nfa nodes that would make up a dfa
     * @return true if there exists a single equivalent dfa node to the set of nfa nodes the dfa would contain in the tree
     */
    public boolean containsNode(HashSet<FiniteAutomataNode> other)
    {
        boolean contains = false;

        //Loop through all nodes in this tree
        for(FiniteAutomataNode fan : this.allNodes)
        {
            //boolean check to see if any node is equivalent
            contains = contains || fan.dfaEquals(other);
        }
        return contains;
    }

    /**
     * This method is called if there is a verified match, this method will find the matching dfa node and return it.
     * @param matcher set of contained NFA nodes
     * @return DFA node that matches
     */
    public FiniteAutomataNode getEquivalentDfa(HashSet<FiniteAutomataNode> matcher)
    {
        for(FiniteAutomataNode fan: this.allNodes) {
            if (fan.dfaEquals(matcher))
                return fan;
        }
        return null;
    }

    /**
     * Setter that sets the null state
     * @param nullState is Node to be designated as null state. need to be added to set of all nodes separately, or already be part of set
     */
    public void setNullState(FiniteAutomataNode nullState)
    {
        this.nullState = nullState;
    }

    /**
     * Getter that return the null state
     * @return null state node
     */
    public FiniteAutomataNode getNullState()
    {
        return nullState;
    }


}
