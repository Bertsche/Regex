package graphex;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by ryan on 4/21/15.
 */
public class FiniteAutomataTree
{
    private HashSet<FiniteAutomataNode> allNodes;
    private FiniteAutomataNode startNode;
    private FiniteAutomataNode nullState;

    public FiniteAutomataTree()
    {
        allNodes = new HashSet<FiniteAutomataNode>();
        startNode = null;
    }        ArrayList<Character> language = new ArrayList<>();

    public FiniteAutomataTree(Character c)
    {
        allNodes = new HashSet<FiniteAutomataNode>();
        FiniteAutomataNode accept = new FiniteAutomataNode(true);
        FiniteAutomataNode start = new FiniteAutomataNode(false, c, accept);
        allNodes.add(accept);
        allNodes.add(start);
        this.startNode = start;
    }
    public FiniteAutomataNode getStartNode()
    {
        return startNode;
    }

    public HashSet<FiniteAutomataNode> getAllNodes()
    {
        return allNodes;
    }

    public void setAcceptToFalse()
    {
        for(FiniteAutomataNode fan : this.getAllAccept())
            fan.setAccept(false);
    }

    public void epsilonToCurrentStart(FiniteAutomataNode start)
    {
        for (FiniteAutomataNode fan : this.getAllAccept())
            fan.addEpsilonTransition(start);
    }

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



    public void union(FiniteAutomataTree t)
    {
        FiniteAutomataNode newStart = new FiniteAutomataNode(false);

        newStart.addEpsilonTransition(this.startNode);
        newStart.addEpsilonTransition(t.getStartNode());
        this.allNodes.addAll(t.getAllNodes());
        this.allNodes.add(newStart);
        this.startNode = newStart;
    }

    public void concat(FiniteAutomataTree t)
    {
        this.epsilonToCurrentStart(t.getStartNode());
        this.setAcceptToFalse();
        this.allNodes.addAll(t.getAllNodes());
    }

    public void star()
    {
        FiniteAutomataNode newStart = new FiniteAutomataNode(true);
        this.allNodes.add(newStart);
        this.epsilonToCurrentStart(this.startNode);
        this.startNode = newStart;
    }

    public void setStartNode(FiniteAutomataNode node)
    {
        startNode = node;
    }

    public void addNode(FiniteAutomataNode node)
    {
        allNodes.add(node);
    }

    public boolean containsNode(HashSet<FiniteAutomataNode> other)
    {
        boolean contains = false;
        for(FiniteAutomataNode fan : this.allNodes)
        {
            contains = contains || fan.dfaEquals(other);
        }
        return contains;
    }

    public FiniteAutomataNode getEquivalentDfa(HashSet<FiniteAutomataNode> matcher)
    {
        for(FiniteAutomataNode fan: this.allNodes) {
            if (fan.dfaEquals(matcher))
                return fan;
        }
        return null;
    }

    public void setNullState(FiniteAutomataNode nullState)
    {
        this.nullState = nullState;
    }

    public FiniteAutomataNode getNullState()
    {
        return nullState;
    }


}
