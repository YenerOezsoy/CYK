import java.util.ArrayList;

public class Node {

    private ArrayList<Elem> list;

    public Node() {
        list = new ArrayList<>();
    }

    public Node(Node node) {
        list = new ArrayList<>(node.getNodeList());
    }

    public void addTerminalToNode(String element) {
        Elem elem = new Elem(element, Type.Terminal);
        list.add(elem);
    }

    public Elem addNonTerminalToNode(String element) {
        Elem elem = new Elem(element, Type.NonTerminal);
        list.add(elem);
        return elem;
    }

    /*
    private void createNewElement(String split) {
        if(input.getType(split) == Type.NonTerminal) {
            //wenn Elem schon vorhanden, nur linken
            if (temp.containsKey(split)) {
                list.add(temp.get(split));
                System.out.println("Vorhandenes Nicht-Terminal: " + split);
            }
            else {
                System.out.println("Neues Nicht-Terminal: " + split);
                Elem element = new Elem(split, input.getType(split));
                list.add(element);
                temp.put(split, element);
                toProcess.add(split);
            }
        }
        else {
            System.out.println("Terminal: " + split);
            Elem element = new Elem(split, input.getType(split));
            list.add(element);
        }
    }
    */

    public ArrayList<Elem> getNodeList() {
        return list;
    }

    public void addElement(Elem elem) {
        list.add(elem);
    }

    public boolean hasElement(Elem elem) {
        return(list.contains(elem));
    }
}
