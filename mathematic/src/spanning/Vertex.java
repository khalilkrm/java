package spanning;

public class Vertex extends Point implements Comparable<Vertex> {

    public Vertex(int x, int y) {
        super(x, y);
    }

    @Override
    public int compareTo(Vertex o) {
        return 0;
    }
}
