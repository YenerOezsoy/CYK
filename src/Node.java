import java.util.ArrayList;
import java.util.HashMap;

public class Node {

    private Input input;
    private ArrayList<Element> list;
    private HashMap<String, Element> temp;
    private ArrayList<String> toProcess;
    private int length;

    public Node(Input input, HashMap<String, Element> temp, ArrayList<String> toProcess) {
        this.input = input;
        list = new ArrayList<>();
        this.temp = temp;
        this.toProcess = toProcess;
        length = 0;
    }

    public Node() {
        list = new ArrayList<>();
    }

    public void addToNode(String s) {
        //System.out.println("To split: " + s);
        for (int i = 0; i < s.length(); i++) {
            String split;
            if (s.charAt(i) == '\"') {
                i += 4;
                split = "Epsilon";
            }
            else {
                split = String.valueOf(s.charAt(i));
            }
            System.out.println("Splitted: " + split);
            createNewElement(split);
        }
    }

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

    public ArrayList<Element> getNodeList() {
        return list;
    }

    public void addElement(Element element) {
        list.add(element);
    }

    public Element getNextElement() {
        if(list.size() > length) {
            return list.get(length++);
        }
        return null;
    }

    public int getNextElementIterator() {
        return length;
    }
}
