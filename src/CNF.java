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

    public void chainRule() {
        findCycle();
        startIteration(4);
        //cleanUp();
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
            else if (rule == 4) {
               // i = inspectForDependency(element, nodes.get(i), i);
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
                    actualRoot.getList().remove(node);
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

    /*private int inspectForDependency(Element root, Node node, int iterator) {

    }*/

    private void findCycle() {
        ArrayList<Element> touched = new ArrayList<>();
        touched.add(tree.getRootElement());
        //iteriere ueber Elemente die nach Breitensuche hinzukommen
        for (int i = 0; i < touched.size(); i++) {
            elementIterator(0, touched.get(i), touched);
        }
        System.out.println();
    }

    private void elementIterator(int rule, Element element, ArrayList<Element> touched) {
        for (int i = 0; i < element.getList().size(); i++) {
            nodeListIterator(element.getList().get(i), touched);
        }
    }

    private void nodeListIterator(Node node,ArrayList<Element> touched) {
        for (int i = 0; i < node.getNodeList().size(); i++) {
            ArrayList<Element> cycle = new ArrayList<>();
            Element cycleRoot = node.getNodeList().get(i);
            if (!touched.contains(node.getNodeList().get(i))) touched.add(node.getNodeList().get(i));
            else isCycle(cycleRoot, node.getNodeList().get(i), cycle);

        }
    }

    private boolean isCycle(Element cycleRoot, Element element, ArrayList<Element> cycle) {
        ArrayList<Element> touched = new ArrayList<>();
        boolean test = false;
        for (int i = 0; i < element.getList().size(); i++) {
            test = false;
            if (element.getList().get(i).getNodeList().size() == 1) {
                Element elem = element.getList().get(i).getNodeList().get(0);
                if (touched.contains(elem)) return false;
                touched.add(elem);
                if (elem.getType() == Type.NonTerminal) {
                    if (elem == cycleRoot) {
                        cycle.add(element);
                        return true;
                    }
                    else {
                        if (isCycle(cycleRoot, elem, cycle)) {
                            if (!cycle.contains(element)) cycle.add(element);
                            test = true;
                        }
                    }
                }

            }

        }
        return test;
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
                if (nonTerminal.hasOnlyChildren(elem.getElement()) && !epsilonStern.contains(nonTerminal)) epsilonStern.add(nonTerminal);
            }
        }
    }

    private void copyNodes(Element root, Node node) {
        //Es wurde ein Node mit nur einem Element uebergeben -> kopiere alle Abbildungen dieses Elements
        Element elem = node.getNodeList().get(0);
        for (int i = 0; i < elem.getList().size(); i++) {
            if (!checkDouble(root, elem.getList().get(i)) /*&& !checkForIdentity(root, elem.getList().get(i))*/) root.addNode(elem.getList().get(i));
        }

    }

    private boolean checkForIdentity(Element root, Node node) {
        if (node.getNodeList().size() == 1 && node.getNodeList().get(0) == root) return true;
        return false;
    }

    private boolean checkDouble(Element root, Node node) {
        boolean isEqual;
        boolean isSet;
        for (int i = 0; i < root.getList().size(); i++) {
            isEqual = false;
            isSet = false;
            if (root.getList().get(i).getNodeList().size() == node.getNodeList().size()) {
                for (int j = 0; j < node.getNodeList().size(); j++) {
                    if (node.getNodeList().get(j) == root.getList().get(i).getNodeList().get(j) && !isSet) isEqual = isSet = true;
                    else if (node.getNodeList().get(j) != root.getList().get(i).getNodeList().get(j)) isEqual = false;
                }
                if(isEqual) return true;
            }
        }
        return false;
    }

    //nicht benoetigte Uebergaenge loeschen
    /*private void cleanUp() {
        ArrayList<Element> touched = new ArrayList<>();
        touched.add(tree.getRootElement());
        for (int i = 0; i < touched.size(); i++) {
            for (int j = 0; j < touched.get(i).getList().size(); j++) {
                ArrayList<Element> nodelist = touched.get(i).getList().get(j).getNodeList();
                for (int k = 0; k < nodelist.size(); k++) {
                    if (nodelist.get(k).getType() == Type.NonTerminal && !touched.contains(nodelist.get(k))) touched.add(nodelist.get(k));
                }
            }
        }
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            if (!touched.contains(map.get(list.get(i)))) map.remove(list.get(i));
        }
    }*/
}
