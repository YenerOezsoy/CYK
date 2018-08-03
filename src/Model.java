/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Model {

    /*private Tree tree;
    private CNF cnf;
    private CYK cyk;

    public Model() {
        tree = new Tree();
        cyk = new CYK();
    }*/

    private CNF cnf;
    private Input input;
    private Output output;
    private Tree tree;

    public Model() {

    }

    public void readFile(String path) {
        tree = new Tree(path);
        cnf = new CNF(tree);
    }

    public void start() {
        tree.buildTree();
        System.out.println("===========");
        System.out.println("===========");
        System.out.println("FIRST RULE");
        System.out.println("___________");
        cnf = new CNF(tree);
        cnf.terminalRule();
        System.out.println("===========");
        System.out.println("===========");
        System.out.println("SECOND RULE");
        cnf.lengthRule();
        System.out.println("===========");
        System.out.println("===========");
        System.out.println("THIRD RULE");
        cnf.epsilonRule();
        System.out.println("===========");
        System.out.println("===========");
        System.out.println("LAST RULE");
        cnf.chainRule();

        /*cyk.setMap(tree.getMap());
        cyk.initialize("aba");
        //System.out.println(cyk.isWordInGrammar());
        int[] arr = cyk.nextStep();
        while(arr != null) {
            cyk.printArray(arr);
            arr = cyk.nextStep();
        }
        cyk.print();*/

    }

    public Tree getStep(int step) {
        tree.setActiveStep(step);
        tree.printTree();
        return tree;
    }

}
