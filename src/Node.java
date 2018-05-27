import java.util.ArrayList;
import java.util.HashMap;

public class Node {

    private ArrayList<Element> list;

    public Node() {
        list = new ArrayList<>();
    }

    public Node(Node node) {
        list = new ArrayList<>(node.getNodeList());
    }

    public void addTerminalToNode(String element) {
        Element elem = new Element(element, Type.Terminal);
        list.add(elem);
    }

    public Element addNonTerminalToNode(String element) {
        Element elem = new Element(element, Type.NonTerminal);
        list.add(elem);
        return elem;
    }

    /*
    private void createNewElement(String split) {
        if(input.getType(split) == Type.NonTerminal) {
            //wenn Element schon vorhanden, nur linken
            if (temp.containsKey(split)) {
                list.add(temp.get(split));
                System.out.println("Vorhandenes Nicht-Terminal: " + split);
            }
            else {
                System.out.println("Neues Nicht-Terminal: " + split);
                Element element = new Element(split, input.getType(split));
                list.add(element);
                temp.put(split, element);
                toProcess.add(split);
            }
        }
        else {
            System.out.println("Terminal: " + split);
            Element element = new Element(split, input.getType(split));
            list.add(element);
        }
    }
    */

    public ArrayList<Element> getNodeList() {
        return list;
    }

    public void addElement(Element element) {
        list.add(element);
    }

    public boolean hasElement(Element element) {
        return(list.contains(element));
    }
}
