import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class Output {

    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document doc;
    private Element element;
    private Element rootElement;
    private String path;


    public Output(String path) {
        this.path = path;
    }

    public void addToFile() {
        dbFactory = DocumentBuilderFactory.newInstance();
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(path);
            rootElement = (Element) doc.getElementsByTagName("grammatik").item(0);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newFile() {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path), "utf-8"));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
            writer.write("\n<grammatik>");
            writer.write("\n</grammatik>");
        } catch (IOException ex) {
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
    }


    public Element createChangeTag() {
        element = doc.createElement("change");
        rootElement.appendChild(element);
        return element;
    }


    public void  addChange(Element element, String change) {
        element.appendChild(doc.createTextNode(change));
    }

    public void writeTree(HashMap<String, Elem> map, String root, String name) {
        writeName(name);
        writeSign(map, root);
        writeTree(map);
        write();
    }


    public void initName(String name) {
        writeName(name);
    }

    private void append(Element element, String value) {
        rootElement.appendChild(element);
        addChange(element, value);
    }

    public void initNonterminal(String nonterminal) {
        element = doc.createElement("n");
        append(element, nonterminal);
    }

    public void initTerminal(String terminal) {
        element = doc.createElement("t");
        append(element, terminal);
    }

    public void initStartsymbol(String start) {
        element = doc.createElement("s");
        append(element, start);
    }

    public void initProduction() {
        element = doc.createElement("p");
        rootElement.appendChild(element);
    }

    public void initLine(String start, String[] productions) {
        Element line = doc.createElement("zeile");
        element.appendChild(line);
        for (int i = -1; i < productions.length; i++) {
            Element cell = doc.createElement("zelle");
            if (i == -1) addChange(cell, start);
            else addChange(cell, productions[i]);
            line.appendChild(cell);
        }
    }

    private void writeName(String name) {
        element = doc.createElement("name");
        rootElement.appendChild(element);
        addChange(element, name);
    }

    private void writeSign(HashMap<String, Elem> map, String root) {
        createNonTerminals(map);
        createTerminals(map);
        createStartElement(root);
    }

    private void createNonTerminals(HashMap<String, Elem> map) {
        element = doc.createElement("n");
        String nonTerminals = "";
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i)).getType() == Type.NonTerminal) nonTerminals += map.get(list.get(i)).getString() + ";" ;
        }
        nonTerminals = nonTerminals.substring(0, nonTerminals.length() - 1);
        rootElement.appendChild(element);
        addChange(element, nonTerminals);
    }

    //Fehler in Matrix
    private void createTerminals(HashMap<String, Elem> map) {
        element = doc.createElement("t");
        String terminals = "";
        HashSet<String> set = new HashSet<>();
        for (String elem : map.keySet()) {
            for(Node node : map.get(elem).getList()) {
                for(Elem child : node.getNodeList()) {
                    if (child.getType() == Type.Terminal) set.add(child.getString());
                }
            }
        }
        for(String terminal : set) {
            terminals += terminal + ";";
        }
        terminals = terminals.substring(0, terminals.length() - 1);
        rootElement.appendChild(element);
        addChange(element, terminals);
    }

    private void createStartElement(String root) {
        element = doc.createElement("s");
        rootElement.appendChild(element);
        addChange(element, root);
    }

    private void writeTree(HashMap<String, Elem> map) {
        element = doc.createElement("p");
        rootElement.appendChild(element);
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            writeProduction(map.get(list.get(i)));
        }
    }

    private void writeProduction(Elem element) {
        Element row = doc.createElement("zeile");
        this.element.appendChild(row);
        Element column = doc.createElement("zelle");
        row.appendChild(column);
        addChange(column, element.getString());
        for (int i = 0; i < element.getList().size(); i++) {
            writeNode(row, element.getList().get(i));
        }
    }

    private void writeNode(Element row, Node node) {
        String production = "";
        Element column = doc.createElement("zelle");
        row.appendChild(column);
        for (int i = 0; i < node.getNodeList().size(); i++) {
            if (i != 0) production += " ";
            production += node.getNodeList().get(i).getString();
        }
        addChange(column, production);
    }

    public void finishInit() {
        write();
    }

    private void write() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            //write to console or file
            StreamResult console = new StreamResult(System.out);
            StreamResult file = new StreamResult(new File(path));

            //write data
            transformer.transform(source, console);
            transformer.transform(source, file);
            System.out.println("DONE");
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
