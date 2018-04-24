import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CNF {

    private boolean startedSearch = false;
    private Tree tree;
    private HashMap<String, Element> map;
    private Element actualRoot;
    private Element rootOfCycle;
    private ArrayList<Element> epsilonStern = new ArrayList<>();
    private ArrayList<Element> visited = new ArrayList<>();
    private ArrayList<Element> possibleCycle = new ArrayList<>();
    private int nameSuffix;


    public CNF(Tree tree) {
        this.tree = tree;
        map = tree.getMap();
        visited.add(tree.getRootElement());
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
        tree.printTree();
    }

    public void chainRule() {
        startIteration(3);
        startIteration(5);
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
                inspectChildren(nodes.get(i), rule);
            }
            else if (rule == 1 && nodes.get(i).getNodeList().size() > 2) {
                shortenNode(element, nodes.get(i));
            }
            else if (rule == 2) {
                actualRoot = element;
                inspectChildren(nodes.get(i), rule);
            }
            //Kettenregel Zyklus
            else if (rule == 3) {
                inspectChildren(nodes.get(i), rule);
            }
            else if (rule == 4) {
                actualRoot = element;
                inspectChildren(nodes.get(i), rule);
                cleanUp(element);
            }
            //Kettenregel
            else if (rule == 5) {
                if (nodes.get(i).getNodeList().size() == 1 && nodes.get(i).getNodeList().get(0).getType() == Type.NonTerminal) {
                    Element elem = nodes.get(i).getNodeList().get(0);
                    for (int j = 0; j < elem.getList().size(); j++) {
                        element.addNode(elem.getList().get(j));
                    }
                    nodes.remove(i);
                }
                cleanUp(element);
            }
        }
    }

    private void inspectChildren(Node node, int rule) {
        for (int i = 0; i < node.getNodeList().size(); i++) {
            if (rule == 0 && node.getNodeList().get(i).getType() == Type.Terminal) {
                replaceNonTerminal(node.getNodeList().get(i), node.getNodeList(), i);
            }
            else if(rule == 2) {
                if (node.getNodeList().get(i).getElement().equals("Epsilon")) {
                    epsilonStern.clear();
                    removeEpsilon(node, i);
                }
            }
            //Kettenregel Zyklus
            else if (rule == 3) {
                actualRoot = node.getNodeList().get(i);
                if (visited.contains(node.getNodeList().get(i))) checkCycle(node.getNodeList().get(i));
                else visited.add(node.getNodeList().get(i));
            }
            //gehoert zu chainRule -> Methode replaceElement
            else if (rule == 4) {
                if (possibleCycle.contains(node.getNodeList().get(i))) {
                    node.getNodeList().add(i, rootOfCycle);
                    node.getNodeList().remove(i + 1);
                }
            }
        }
    }

    // 1. Regel _____________

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


    // 2. Regel _____________

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


    // 3. Regel _____________

    private void removeEpsilon(Node node, int i) {
        node.getNodeList().remove(i);
        epsilonStern.add(actualRoot);
        searchBackwards();
        if (node.getNodeList().isEmpty()) actualRoot.getList().remove(node);
        if (actualRoot.getList().isEmpty()) map.remove(actualRoot.getElement());
        else replaceEpsilon();

    }

    //Suche nach Elementen, die Wurzel von Element aus Liste epsilonStern sind
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

    private void replaceEpsilon() {
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            Element root = map.get(list.get(i));
            for (int j = 0; j < epsilonStern.size(); j++) {
                if (root.hasChildrenWSize(epsilonStern.get(j).getElement())) addNodes(root, epsilonStern.get(j));
            }
        }
    }

    private void addNodes(Element root, Element element) {
        for (int i = 0; i < root.getList().size(); i++) {
            if (root.getList().get(i).hasElement(element)) root.addNode(copyWithoutEpsilonTrans(root.getList().get(i), element));
        }

    }

    private Node copyWithoutEpsilonTrans(Node node, Element element) {
        Node copy = new Node();
        for(int i = 0; i < node.getNodeList().size(); i++) {
            if(node.getNodeList().get(i) != element) copy.addElement(node.getNodeList().get(i));
        }
        return copy;
    }


    // 4. Regel _____________

    private void checkCycle(Element element) {
        startedSearch = false;
        epsilonStern.clear();
        if (isCycle(element)) copyNodes(findRootOfCycle());
    }

    private boolean isCycle(Element element) {
        if (element.getType() == Type.NonTerminal) {
            if (element == actualRoot && startedSearch) return true;
            else {
                startedSearch = true;
                boolean result = elementIterator(element);
                if (result) possibleCycle.add(element);
                return result;
            }
        }
        else return false;
    }

    private boolean elementIterator(Element element) {
        boolean foundElement = false;
        for (int i = 0; i < element.getList().size(); i++) {
            Node nodeList = element.getList().get(i);
            //nur Abbildungen der Laenge sind von der Regel betroffen
            if (nodeList.getNodeList().size() == 1 && nodeList.getNodeList().get(0).getType() == Type.NonTerminal) {
                if (!epsilonStern.contains(nodeList.getNodeList().get(0))) {
                    epsilonStern.add(nodeList.getNodeList().get(0));
                    if (isCycle(nodeList.getNodeList().get(0)) && !foundElement) foundElement = true;
                }
            }
        }
        return foundElement;
    }

    private Element findRootOfCycle() {
        ArrayList<Element>  touched = new ArrayList<>();
        touched.add(tree.getRootElement());
        return breadthFirstSearch(touched);
    }

    private Element breadthFirstSearch(ArrayList<Element> touched) {
        for (int i = 0; i < touched.size(); i++) {
            for (int j = 0; j < touched.get(i).getList().size(); j++) {
                Node node = touched.get(i).getList().get(j);
                Element foundRoot = cycleIterator(node);
                if (foundRoot != null) return foundRoot;
            }
        }
        return null;
    }

    private Element cycleIterator(Node node) {
        for (int i = 0; i < possibleCycle.size(); i++) {
            if (node.getNodeList().contains(possibleCycle.get(i))) return possibleCycle.get(i);
        }
        return null;
    }

    private void copyNodes(Element root) {
        possibleCycle.remove(root);
        for (int i = 0; i < possibleCycle.size(); i++) {
            for (int j = 0; j < possibleCycle.get(i).getList().size(); j++) {
                root.addNode(possibleCycle.get(i).getList().get(j));
            }
            map.remove(possibleCycle.get(i));
        }
        replaceElement(root);
        possibleCycle.clear();
    }

    private void replaceElement(Element root) {
        rootOfCycle = root;
        startIteration(4);
    }

    private void cleanUp(Element root) {
        for (int i = 0; i < root.getList().size(); i++) {
            if (root.getList().get(i).getNodeList().size() == 1) checkIdentity(root, root.getList().get(i));
            else checkDouble(root, root.getList().get(i));
        }
    }

    private void checkIdentity(Element root, Node node) {
        if (node.getNodeList().get(0) == root) root.getList().remove(node);
        else checkDouble(root, node);
    }

    private void checkDouble(Element root, Node node) {
        for (int i = root.getList().indexOf(node) + 1; i < root.getList().size(); i++) {
            if (node.getNodeList().size() == root.getList().get(i).getNodeList().size()) {
                if (isNodeEqual(root.getList().get(i), node)) root.getList().remove(node);
            }

        }
    }

    private boolean isNodeEqual(Node node1, Node node2) {
        for (int i = 0; i < node1.getNodeList().size(); i++) {
            if (node1.getNodeList().get(i) != node2.getNodeList().get(i)) return false;
        }
        return true;
    }

    //CHECKE OB ES GLEICHE ABBILDUNGEN GIBT -> IDENTISCHE NONTERMINALE
}