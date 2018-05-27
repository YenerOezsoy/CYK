import java.util.ArrayList;
import java.util.HashMap;

public class CYK {

    private String[] word;
    private int size;
    private HashMap<Integer, ArrayList<String>[]> pyramid;
    private HashMap<String, Element> map;


    public void initialize(String w) {
        pyramid = new HashMap<>();
        size = w.length();
        word = new String[size];
        for (int i = 0; i < w.length(); i++) {
            word[i] = String.valueOf(w.charAt(i));
        }
        buildPyramide(size);
    }

    public boolean isWordInGrammar() {
        int stage = size;
        while (stage != 0) {
            initializeStage(stage);
            stage--;
        }

        return false;
    }

    public void setMap(HashMap<String, Element> map) {
        this.map = map;
    }

    private void buildPyramide(int size) {
        while (size != 0) {
            ArrayList[] stage = new ArrayList[size];
            for (int i = 0; i < stage.length; i++) {
                stage[i] = new ArrayList<String>();
            }
            pyramid.put(size--, stage);
        }
    }

    private void initializeStage(int stage) {
        for (int i = 0; i < stage; i++) {
            if (stage == size) {
                checkColumn(stage, i);
            }
            else if (stage == size - 1){
                checkColumn(stage, stage + 1, i, i + 1);
            }
            else {
                /*checkColumn(stage, i ,stage + 1, i, size, i + size -1);
                checkColumn(stage, i ,size, i, stage + 1, i + 1);*/
                int stageCounter = stage + 1;
                int offset = stage;
                int stageCounter2 = size;
                while (stageCounter != size) {
                    checkColumn(stage,i, stageCounter, i, stageCounter2, i + offset - 1);
                    stageCounter++;
                    stageCounter2--;
                    offset--;
                }
            }
        }

    }

    private void checkColumn(int stage, int column) {
        //pyramid.get(stage)[column].add(map.get(word[column]).getString());
        copy(pyramid.get(stage)[column], getNonTerminals(word[column]));
    }

    private void checkColumn(int stage, int stage1, int column1, int column2) {
        checkCombinations(stage, column1, pyramid.get(stage1)[column1], pyramid.get(stage1)[column2]);
    }

    private void checkColumn(int stage, int column, int stage1, int column1, int stage2, int column2) {
        checkCombinations(stage, column, pyramid.get(stage1)[column1], pyramid.get(stage2)[column2]);
    }

    private ArrayList<String> getNonTerminals(String var1, String var2) {
        ArrayList<String> found = new ArrayList();
        ArrayList list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i)).hasChildren(var1, var2)) found.add(map.get(list.get(i)).getString());
        }
        return found;
    }

    private ArrayList<String> getNonTerminals(String var) {
        ArrayList<String> found = new ArrayList();
        ArrayList list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i)).hasOnlyChildren(var)) found.add(map.get(list.get(i)).getString());
        }
        return found;
    }

    private void checkCombinations(int stage, int column, ArrayList<String> list1, ArrayList<String> list2) {
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                copy(pyramid.get(stage)[column], getNonTerminals(list1.get(i), list2.get(j)));
            }
        }
    }

    private void copy(ArrayList<String> destination, ArrayList<String> source) {
        for (int i = 0; i < source.size(); i++) {
            if(!destination.contains(source.get(i))) destination.add(source.get(i));
        }
    }

    public void print() {
        for(int i = pyramid.size() ; i >= 0; i--) {
            ArrayList[] list = pyramid.get(i);
            if(list != null) {
                for(int j = 0; j < list.length; j++) {
                    ArrayList list2 = list[j];
                    for (int k = 0; k < list2.size(); k++) {
                        System.out.print(list2.get(k));
                        System.out.print(",");
                    }
                    System.out.print(" ; ");
                }
                System.out.println("");
            }

        }
    }


}
