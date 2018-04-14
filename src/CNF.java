import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CNF {

    private Tree tree;
    private HashMap<String, Element> map;


    public CNF(Tree tree) {
        this.tree = tree;
        map = tree.getMap();
    }

    public void terminalRule() {
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            inspectNonTerminal(map.get(list.get(i)));
        }
        tree.printTree();
    }

    private void inspectNonTerminal(Element element) {
        //jeder Node bildet eine Ableitungsregel
        ArrayList<Node> nodes = element.getList();
        for (int i = 0; i < nodes.size(); i++) {
            //einzelnes Symbol von Regel nicht betroffen
            if (nodes.get(i).getNodeList().size() > 1) {
                inspectChildren(nodes.get(i), 0);
            }
        }
    }

    private void inspectChildren(Node node, int rule) {
        for (int j = 0; j < node.getNodeList().size(); j++) {
            if (rule == 0 && node.getNodeList().get(j).getType() == Type.Terminal) {
                replaceNonTerminal(node.getNodeList().get(j), node.getNodeList(), j);
            }
            else if(rule == 1) {
                //shortenNode(node);
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

    public void lengthRule() {
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            Element element = map.get(list.get(i));
            ArrayList<Node> nodes = element.getList();
            for (int j = 0; j < nodes.size(); j++) {
                //Regel betrifft nur Nodes mit mehr als 2 Zeichen
                if (nodes.get(j).getNodeList().size() > 2) {
                    shortenNode(element, nodes.get(j));
                }
            }
        }
        tree.printTree();
    }

    private void shortenNode(Element element, Node node) {
        String name;
        int nameSuffix = 2;
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
        actualNode.addElement(list.get(size - 1));
        actualNode.addElement(list.get(size - 2));
    }
}
