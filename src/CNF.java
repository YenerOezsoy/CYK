import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CNF {

    private Element foundRootOfCycle;
    private boolean recursive;
    private Tree tree;
    private HashMap<String, Element> map;
    private Element actualRoot;
    private ArrayList<Element> epsilonStern = new ArrayList<>();
    ArrayList<Element> touched;
    ArrayList<Element> cycle;
    ArrayList<Element> visited;
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
        //startIteration(4);
        //cleanUp();
        tree.printTree();
    }

    private void startIteration(int rule) {
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            inspectNonTerminal(map.get(list.get(i)), rule);
            nameSuffix = 2;
        }
    }

    private void inspectNonTerminal(Element element, int rule) {
        //jeder Node bildet eine Ableitungsregel
        ArrayList<Node> nodes = element.getList();
        for (int i = 0; i < nodes.size(); i++) {
            //einzelnes Symbol von Regel nicht betroffen
            if (rule == 0 && nodes.get(i).getNodeList().size() > 1) {
                inspectChildren(nodes.get(i), rule);
            } else if (rule == 1 && nodes.get(i).getNodeList().size() > 2) {
                shortenNode(element, nodes.get(i));
            } else if (rule == 2) {
                actualRoot = element;
                inspectChildren(nodes.get(i), rule);
            }
            //gehoert zu Epsilon-Regel
            else if (rule == 3) {
                actualRoot = element;
                inspectChildren(nodes.get(i), rule);
            } else if (rule == 5) {
                if (nodes.get(i).getNodeList().size() == 1) inspectChildren(nodes.get(i), rule);
            }
            //replaceElement
            else if (rule == 7) {
                inspectChildren(nodes.get(i), rule);
                if (nodes.get(i).getNodeList().size() == 0) nodes.remove(i);
            }
        }
    }

    private void inspectChildren(Node node, int rule) {
        recursive = false;
        ArrayList<Element> nodeList = node.getNodeList();
        for (int i = 0; i < nodeList.size(); i++) {
            if (rule == 0 && nodeList.get(i).getType() == Type.Terminal) {
                replaceNonTerminal(nodeList.get(i), nodeList, i);
            } else if (rule == 2) {
                if (nodeList.get(i).getElement().equals("Epsilon")) {
                    actualRoot.getList().remove(node);
                    if (!epsilonStern.contains(actualRoot)) epsilonStern.add(actualRoot);
                }
            }
            //gehoert zu Epsilon-Regel
            else if (rule == 3) {
                if (actualRoot.getList().isEmpty()) map.remove(actualRoot.getElement());
                else replaceEpsilon(node, nodeList.get(i));
            }
            //findCycle
            else if (rule == 4) {
                if (nodeList.get(i).getType() == Type.NonTerminal && !touched.contains(nodeList.get(i)))
                    touched.add(nodeList.get(i));
                else if (touched.contains(nodeList.get(i))) checkCycle(nodeList.get(i));
            }
            //isCycle
            else if (rule == 5) {
                if (isCycle(nodeList.get(i))) {
                }
            }
            else if (rule == 6) {
                if (cycle.contains(nodeList.get(i))) {
                    foundRootOfCycle = nodeList.get(i);
                    break;
                }
            }
            //replaceElement
            else if (rule == 7) {
                if (cycle.contains(nodeList.get(i))) {
                    if (nodeList.size() == 1) nodeList.remove(i);
                    else {
                        nodeList.add(i, foundRootOfCycle);
                        nodeList.remove(i + 1);
                    }
                }
            }

        }
    }

    private void replaceNonTerminal(Element element, ArrayList<Element> list, int j) {
        if (map.containsKey(element.getElement())) list.set(j, map.get(element.getElement()));
        else {
            String newChar = element.getElement().toUpperCase() + "1";
            String prev = element.getElement();
            element.setType(Type.NonTerminal);
            element.setCharacter(newChar);
            element.addNode(tree.newNode(prev));
            map.put(newChar, element);
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
            if (element == epsilonStern.get(i) && node.getNodeList().size() > 1) {
                actualRoot.addNode(copyWithoutEpsilonTrans(node, element));
            }
        }
    }

    private Node copyWithoutEpsilonTrans(Node node, Element element) {
        Node copy = new Node();
        for (int i = 0; i < node.getNodeList().size(); i++) {
            if (node.getNodeList().get(i) != element) copy.addElement(node.getNodeList().get(i));
        }
        return copy;
    }

    private void searchBackwards() {
        for (int i = 0; i < epsilonStern.size(); i++) {
            Element elem = epsilonStern.get(i);
            List<String> list = new ArrayList<>(map.keySet());
            for (int j = 0; j < list.size(); j++) {
                Element nonTerminal = map.get(list.get(j));
                if (nonTerminal.hasOnlyChildren(elem.getElement()) && !epsilonStern.contains(nonTerminal))
                    epsilonStern.add(nonTerminal);
            }
        }
    }

    private void findCycle() {
        breadthFirstSearch(tree.getRootElement(), 4);
    }

    private void breadthFirstSearch(Element root, int rule) {
        foundRootOfCycle = null;
        touched = new ArrayList<>();
        touched.add(root);
        for (int i = 0; i < touched.size(); i++) {
            elementIterator(touched.get(i), rule);
            if (foundRootOfCycle != null) break;
        }
    }

    private void elementIterator(Element element, int rule) {
        for (int i = 0; i < element.getList().size(); i++) {
            inspectChildren(element.getList().get(i), rule);
            if (foundRootOfCycle != null) break;
        }
    }

    private void checkCycle(Element element) {
        cycle = new ArrayList<>();
        cycle.add(element);
        visited = new ArrayList<>();
        if (isCycle(element)) {
            Element root = findRootOfCycle();
            replaceElement(root);
        }
    }

    private boolean isCycle(Element element) {
        boolean foundCycle = false;
        for (int i = 0; i < element.getList().size(); i ++) {
            Node node = element.getList().get(i);
            if (node.getNodeList().size() == 1 && recursive(node.getNodeList().get(0))) {
                if (!cycle.contains(node.getNodeList().get(0))) cycle.add(node.getNodeList().get(0));
                foundCycle = true;
            }
        }
        return foundCycle;
    }

    private boolean recursive(Element element) {
        if (element.getType() == Type.NonTerminal) {
            if (element == cycle.get(0)) return true;
            else {
                if (isCycle(element))  return true;
            }
        }
        return false;
    }

    private Element findRootOfCycle() {
        breadthFirstSearch(tree.getRootElement(), 6);
        cycle.remove(foundRootOfCycle);
        return foundRootOfCycle;
    }

    private void replaceElement(Element root) {
        for (int i = 0; i < cycle.size(); i++) {
            copyNodes(root, cycle.get(i));
        }
        startIteration(7);
    }

    private void copyNodes(Element root, Element element) {
        for (int i = 0; i < element.getList().size(); i++) {
            /*if(!isNodeEqual(root, element.getList().get(i)))*/ root.addNode(element.getList().get(i));
        }
        map.remove(element);
    }

    /*private boolean isNodeEqual(Element root, Node node) {
        if (node.getNodeList().size() == 1) {
            if (node.getNodeList().get(0) == root) return false;
        }
        for (int i = 0; i < root.getList().size(); i++) {
            Node rootNode = root.getList().get(i);
            for (int j = 0; j < rootNode.getNodeList().size(); j++) {

            }
        }
    }*/
}