import base_structure.PersistentBTree;
import base_structure.PersistentTree;

public class Main {
    public static void main(String[] args) {
        PersistentTree<Integer, String> tree = new PersistentBTree<>(2);
        for (int i = 0; i < 20; i++) {
            tree = tree.put(i, String.valueOf(i));
        }
        System.out.println(tree.get(15));
    }
}
