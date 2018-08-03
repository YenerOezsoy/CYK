import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Output {

    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document doc;
    private Element element;
    private Element rootElement;


    public Output() {
        dbFactory = DocumentBuilderFactory.newInstance();
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse("grammatik.xml");
            rootElement = (Element) doc.getElementsByTagName("grammatik").item(0);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    private void createTerminals(HashMap<String, Elem> map) {
        element = doc.createElement("t");
        String terminals = "";
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            if (!map.get(list.get(i)).getString().equals(list.get(i))) terminals += list.get(i) + ";";
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
            production += " " + node.getNodeList().get(i).getString();
        }
        addChange(column, production);
    }

    private void write() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;

                transformer = transformerFactory.newTransformer();

            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            //write to console or file
            StreamResult console = new StreamResult(System.out);
            StreamResult file = new StreamResult(new File("grammatik.xml"));

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
