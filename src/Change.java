public class Change {

    private Output output;
    String changes;

    public Change() {
        output = new Output();
    }

    public void createNewChangeSet() {
        changes = "";
    }

    public void add(String change) {
        changes += "add(" + change + "); ";
    }

    public void addTo(String change) {
        changes += "addTo(" + change + "); ";
    }

    /*
    copy node from x to y
    Destination , Source , Node Identifier
     */
    public void addNode(String change) {
        changes += "addNode(" + change + "); ";
    }

    public void replace(String change) {
        changes += "replace(" + change + "); ";
    }

    public void delete(String change) {
        changes += "delete(" + change + "); ";
    }

    public void deleteNode(String change) {
        changes += "deleteNode(" + change + "); ";
    }

    /*
    change besteht aus:
        Root und Element
             oder
        Root
     */
    public void mark(String change) {
        changes += "mark(" + change + "); ";
    }

    public void highlight(String change) {
        changes += "highlight(" + change + "); ";
    }

    public void test () {
        System.out.println(changes);
    }

    /*public void write() {
        output.createChangeTag();
        output.addChange(changes);
    }*/

    public String getChanges() {
        return changes;
    }

}
