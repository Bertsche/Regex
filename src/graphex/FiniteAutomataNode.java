package graphex;

import java.util.*;

/**
 * Created by ryan on 4/21/15.
 */
public class FiniteAutomataNode implements Comparable<FiniteAutomataNode>
{
    private boolean accept;
    private HashMap<Character, FiniteAutomataNode> characterTransitions;
    private HashSet<FiniteAutomataNode> epsilonTransitions;
    private String name;
    private boolean dfaChecked;
    private HashSet<FiniteAutomataNode> dfaContains;


    public FiniteAutomataNode(boolean accept, HashMap<Character, FiniteAutomataNode> characterTransitions)
    {
        this.accept = accept;
        this.characterTransitions = new HashMap<Character, FiniteAutomataNode>(characterTransitions);
        this.epsilonTransitions = new HashSet<FiniteAutomataNode>();
        name = "";
        dfaChecked = false;
        this.dfaContains = new HashSet<FiniteAutomataNode>();
    }

    public FiniteAutomataNode(HashSet<FiniteAutomataNode> containedNodes)
    {
        this.epsilonTransitions = new HashSet<>();
        this.characterTransitions = new HashMap<>();
        this.dfaChecked = false;
        this.dfaContains = new HashSet<>(containedNodes);
        this.accept = this.checkAccept();
        this.name = dfaNameMaker();

    }


    public FiniteAutomataNode(boolean accept,HashSet<FiniteAutomataNode> epsilonTransitions)
    {
        this.accept = accept;
        this.characterTransitions = new HashMap<Character, FiniteAutomataNode>();
        this.epsilonTransitions = new HashSet<FiniteAutomataNode>(epsilonTransitions);
        name = "";
        dfaChecked = false;
        this.dfaContains = new HashSet<FiniteAutomataNode>();
    }

    public FiniteAutomataNode(boolean accept)
    {
        this.accept = accept;
        this.characterTransitions = new HashMap<Character, FiniteAutomataNode>();
        this.epsilonTransitions = new HashSet<FiniteAutomataNode>();
        name = "";
        dfaChecked = false;
        this.dfaContains = new HashSet<FiniteAutomataNode>();
    }

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

    private boolean checkAccept()
    {
        boolean acceptHolder = false;
        for(FiniteAutomataNode fan : this.dfaContains)
        {
            acceptHolder = acceptHolder || fan.getAccept();
        }
        return acceptHolder;
    }

    private String dfaNameMaker()
    {
        String dfaName = "";
        ArrayList<FiniteAutomataNode> tempForSort = new ArrayList<>(this.dfaContains);
        Collections.sort(tempForSort);

        for(FiniteAutomataNode fan: tempForSort) {
            dfaName += fan.getName() + ", ";
        }
        dfaName.replaceAll(", $", "");
        return dfaName;
    }


    public void addCharactertransition(Character k, FiniteAutomataNode v)
    {
        this.characterTransitions.put(k, v);
    }

    public void addEpsilonTransition(FiniteAutomataNode newEpsilon)
    {
        this.epsilonTransitions.add(newEpsilon);
    }

    public boolean getAccept()
    {
        return accept;
    }

    public void setAccept(boolean b)
    {
        accept = b;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isDfaChecked()
    {
        return dfaChecked;
    }

    public void setDfaChecked(boolean t)
    {
        dfaChecked = t;
    }

    public HashSet<FiniteAutomataNode> getDfaContains()
    {
        return dfaContains;
    }

    public void addDfaContains(FiniteAutomataNode fan)
    {
        dfaContains.add(fan);
    }

    public Collection<FiniteAutomataNode> getValues()
    {
        return this.characterTransitions.values();

    }


    public HashSet<Character> getContainedKeys()
    {
        HashSet<Character> all = new HashSet<>();
        for(FiniteAutomataNode fan: this.dfaContains)
        {
            all.addAll(fan.getKeys());
        }
        return all;
    }
    public HashSet<Character> getKeys()
    {
        HashSet<Character> a = new HashSet<>(this.characterTransitions.keySet());
        return a;
    }

    public boolean hasKey(Character c)
    {
        return characterTransitions.containsKey(c);
    }

    public FiniteAutomataNode getMappedValue(Character c)
    {
        return characterTransitions.get(c);
    }

    public boolean dfaEquals(HashSet<FiniteAutomataNode> other)
    {

        return (this.dfaContains.containsAll(other) && other.containsAll(this.dfaContains));
    }



    public HashSet<FiniteAutomataNode> getEpsilonTransitions()
    {
        return this.epsilonTransitions;
    }


    @Override
    public int compareTo(FiniteAutomataNode other) {
        return this.name.compareTo(other.getName());
    }
}


