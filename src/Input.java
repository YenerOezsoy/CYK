import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;


/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Input {

    private final QName nodesetType = XPathConstants.NODESET;
    private final QName stringType = XPathConstants.STRING;

    private String path = "grammatik";
    private String rootElement;
    private String[] nonterminals;
    private String[] terminals;

    private Document doc;
    private XPath xpath;
    private XPathExpression expr;

    public Input () {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse("grammatik.xml");
            XPathFactory xPathfactory = XPathFactory.newInstance();
            xpath = xPathfactory.newXPath();
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        setActiveStep(0);
    }

    public void setActiveStep(int i) {
        switch(i) {
            case 0: path = "grammatik";
                    break;
            case 1: path = "firstRule";
                    break;
            case 2: path = "secondRule";
                    break;
            case 3: path = "thirdRule";
                    break;
            case 4: path = "fourthRule";
                    break;
        }
        initializeNames();
    }


    private void initializeNames() {
        String nonterminal = (String) read("/" + path + "/n", stringType);
        String terminal = (String) read("/" + path + "/t", stringType);
        rootElement = (String) read("/" + path + "/s", stringType);
        nonterminals = nonterminal.split(";");
        terminals = terminal.split(";");
    }


    public int getColumnSize() {
        NodeList columns = (NodeList) read("/" + path + "/p/zeile", nodesetType);
        return columns.getLength();
    }

    public boolean isNonTerminal(String s) {
        for (int i = 0; i < nonterminals.length; i++) {
            if (nonterminals[i].equals(s)) return true;
        }
        return false;
    }

    public boolean isTerminal(String s) {
        for (int i = 0; i < terminals.length; i++) {
            if (terminals[i].equals(s)) return true;
        }
        return false;
    }

    public String[] getNonterminals() {
        return nonterminals;
    }

    public String[] getTerminals() {
        return terminals;
    }

    public String getRootElement() {
        return rootElement;
    }

    public NodeList getProduction(int i) {
        String expression = "/" + path + "/p/zeile[" + i + "]/zelle";
        return (NodeList) read(expression, nodesetType);
    }

    public NodeList getProduction(String root) {
        String expression = "/" + path + "/p/zeile/zelle[text()='" + root + "' and position()=1]/../zelle";
        return (NodeList) read(expression, nodesetType);
    }



    private Object read(String expression, QName type) {
        Object result;
        try {
            expr = xpath.compile(expression);
            result = expr.evaluate(doc, type);

        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }







    /*
    private void initializeArray() {
        size = new int[doc.getElementsByTagName("zeile").getLength()];
        for (int i = 0; i < size.length; i++) {
            size[i] = 1;
        }
    }

    public String getRootElement() {
        return production.item(3).getTextContent();
    }

    public int getProductionSize(String s) {
        ArrayList<Node> list = getTags("zelleStart");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTextContent().equals(s)) {
                return getTags("zeile").get(i).getChildNodes().getLength();
            }
        }
        return 0;
    }

    public Type getType(String s) {
        ArrayList<Node> terminal = getTags("t");
        if (terminal.get(0).getTextContent().contains(s)) return Type.Terminal;
        ArrayList<Node> nonterminal = getTags("n");
        if (nonterminal.get(0).getTextContent().contains(s)) return Type.NonTerminal;
        if(s.equals("Epsilon")) return Type.Terminal;
        return null;
    }

    public String getProduction(String s, int column) {
        ArrayList<Node> productions = getTags("zelleStart");
        for (int i = 0; i < productions.size(); i++) {
            if (productions.get(i).getTextContent().equals(s)) return getTags("zeile").get(i).getChildNodes().item(column).getTextContent();
        }
        return null;
    }

    public void setActiveStep(int step) {
        if (step == 0) production = doc.getElementsByTagName("grammatik").item(0).getChildNodes();
        else production = doc.getElementsByTagName("produktion").item(step).getChildNodes();
    }

    public ArrayList<Node> getTags(String tag) {
       ArrayList<Node> toReturn = new ArrayList<>();
       addChilds(toReturn, production, tag);
       return toReturn;
    }

    private void addChilds(ArrayList<Node> list,NodeList node, String tag) {
        for (int j = 0; j < node.getLength(); j++) {
            if (node.item(j).getNodeName().equals(tag)) list.add(node.item(j));
            if (node.item(j).hasChildNodes()) addChilds(list, node.item(j).getChildNodes(), tag);
        }
    }

    public String[] getNonTerminal() {
        String[] split = getTags("n").get(0).getTextContent().split(";");
        return split;
    }*/

}
