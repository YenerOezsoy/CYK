import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CNF {

    private Tree tree;
    private HashMap<String, Element> map;
    private Element actualRoot;
    private ArrayList<Element> epsilonStern = new ArrayList<>();
    private int nameSuffix;


    public CNF(Tree tree) {
        this.tree = tree;
        map = tree.getMap();
    }

    public void terminalRule() {
        startIteration(0);
        tree.printTree();
    }

    public void lengthRule() {
        startIteration(1);
        tree.printTree();
    }

    public void epsilonRule() {
        startIteration(2);
        searchBackwards();
        startIteration(3);
        tree.printTree();
    }

    private void startIteration(int rule) {
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            inspectNonTerminal(map.get(list.get(i)),rule);
            nameSuffix = 2;
        }
    }

    private void inspectNonTerminal(Element element, int rule) {
        //jeder Node bildet eine Ableitungsregel
        ArrayList<Node> nodes = element.getList();
        for (int i = 0; i < nodes.size(); i++) {
            //einzelnes Symbol von Regel nicht betroffen
            if (rule == 0 && nodes.get(i).getNodeList().size() > 1) {
                inspectChildren(nodes.get(i), 0);
            }
            else if (rule == 1 && nodes.get(i).getNodeList().size() > 2) {
                shortenNode(element, nodes.get(i));
            }
            else if (rule == 2) {
                actualRoot = element;
                inspectChildren(nodes.get(i), 2);
            }
            //gehoert zu Epsilon-Regel
            else if (rule == 3) {
                actualRoot = element;
                inspectChildren(nodes.get(i), 3);
            }
        }
    }

    private void inspectChildren(Node node, int rule) {
        for (int j = 0; j < node.getNodeList().size(); j++) {
            if (rule == 0 && node.getNodeList().get(j).getType() == Type.Terminal) {
                replaceNonTerminal(node.getNodeList().get(j), node.getNodeList(), j);
            }
            else if(rule == 2) {
                if (node.getNodeList().get(j).getElement().equals("Epsilon")) {
                    node.getNodeList().remove(j);
                    if(!epsilonStern.contains(actualRoot)) epsilonStern.add(actualRoot);
                }
            }
            //gehoert zu Epsilon-Regel
            else if (rule == 3) {
                if (actualRoot.getList().isEmpty()) map.remove(actualRoot.getElement());
                else replaceEpsilon(node, node.getNodeList().get(j));
            }
        }
    }

    private void replaceNonTerminal(Element element, ArrayList<Element> list , int j) {
        if (map.containsKey(element.getElement())) list.set(j, map.get(element.getElement()));
        else {
            String newChar = element.getElement().toUpperCase() + "1";
            String prev = element.getElement();
            element.setType(Type.NonTerminal);
            element.setCharacter(newChar);
            element.addNode(tree.newNode(prev));
            map.put(prev, element);
        }

    }



    private void shortenNode(Element element, Node node) {
        String name;
        ArrayList<Element> list = new ArrayList<>(node.getNodeList());
        int size = list.size();
        Node actualNode = node;
        node.getNodeList().clear();
        for (int i = 0; i < size - 2; i++) {
            name = element.getElement() + nameSuffix++;
            Element newElement = new Element(name, Type.NonTerminal);
            map.put(name, newElement);
            actualNode.addElement(list.get(i));
            actualNode.addElement(newElement);
            actualNode = new Node();
            newElement.addNode(actualNode);
        }
        actualNode.addElement(list.get(size - 2));
        actualNode.addElement(list.get(size - 1));
    }



    private void replaceEpsilon(Node node, Element element) {
        for (int i = 0; i < epsilonStern.size(); i++) {
            //System.out.println("Epsilon-Stern: " + epsilonStern.get(i).getElement());
            if(element == epsilonStern.get(i) && node.getNodeList().size() > 1) {
                actualRoot.addNode(copyWithoutEpsilonTrans(node, element));
            }
        }
    }

    private Node copyWithoutEpsilonTrans(Node node, Element element) {
        Node copy = new Node();
        for(int i = 0; i < node.getNodeList().size(); i++) {
            if(node.getNodeList().get(i) != element) copy.addElement(node.getNodeList().get(i));
        }
        return copy;
    }

    private void searchBackwards() {
        for (int i = 0; i < epsilonStern.size(); i++) {
            Element elem = epsilonStern.get(i);
            List<String> list = new ArrayList<>(map.keySet());
            for (int j = 0; j < list.size(); j++) {
                Element nonTerminal = map.get(list.get(j));
                if (nonTerminal.hasChildren(elem.getElement()) && !epsilonStern.contains(nonTerminal)) epsilonStern.add(nonTerminal);
            }
        }
    }

}
