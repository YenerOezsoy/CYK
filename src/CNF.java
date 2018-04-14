import java.util.ArrayList;
import java.util.HashMap;

public class CNF {

    private Tree tree;
    private HashMap<String, Element> temp = new HashMap<>();


    public CNF(Tree tree) {
        this.tree = tree;
    }

    public void terminalRule() {
        String[] list = tree.getNonTerminalList();
        HashMap<String, Element> map = tree.getMap();
        for (int i = 0; i < list.length; i++) {
            inspectNonTerminal(map.get(list[i]));
        }
        tree.printTree();
    }

    private void inspectNonTerminal(Element element) {
        //jeder Node bildet eine Ableitungsregel
        ArrayList<Node> nodes = element.getList();
        for (int i = 0; i < nodes.size(); i++) {
            //einzelnes Symbol von Regel nicht betroffen
            if (nodes.get(i).getNodeList().size() > 1) {
                for (int j = 0; j < nodes.get(i).getNodeList().size(); j++) {
                    if (nodes.get(i).getNodeList().get(j).getType() == Type.Terminal) {
                        replaceNonTerminal(nodes.get(i).getNodeList().get(j), nodes.get(i).getNodeList(), j);
                    }
                }
            }
        }
    }

    private void replaceNonTerminal(Element element, ArrayList<Element> list , int j) {
        if (temp.containsKey(element.getElement())) list.set(j, temp.get(element.getElement()));
        else {
            String newChar = element.getElement().toUpperCase() + "1";
            String prev = element.getElement();
            element.setType(Type.NonTerminal);
            element.setCharacter(newChar);
            element.addNode(tree.newNode(prev));
            temp.put(prev, element);
        }

    }

}
