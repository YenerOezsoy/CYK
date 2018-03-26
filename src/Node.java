import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    private ArrayList<Element> list;
    private Element element;
    private Input input;
    HashMap<String, Element> temp;

    public Node(HashMap<String, Element> temp) {
        list = new ArrayList<Element>();
        this.temp = temp;
    }

    public void addToNode(String s, Input input, ArrayList<String> touched) {
        if (s.equals("\"Eps\"")) addTerminal("Epsilon");
        else {
            for (int i = 0; i < s.length(); i++) {
                String split = String.valueOf(s.charAt(i));
                if (input.getType(split) == Type.NonTerminal) {
                    addNonTerminal(split,input, touched);
                } else if (input.getType(split) == Type.Terminal) {
                    addTerminal(split);
                }
            }
        }
    }

    private void addTerminal(String split) {
        element = new Element(split);
        list.add(element);
    }

    private void addNonTerminal(String split,Input input, ArrayList<String> touched) {
        if (!touched.contains(split)) {
            touched.add(split);
            temp.put(split, element = new Element(split, input, touched, temp));
            list.add(element);
        }
        else {
            list.add(temp.get(split));
        }
    }

    public ArrayList<Element> getList() {
        return list;
    }
}
