package spanning;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Objects;

public class Edge extends DefaultWeightedEdge {

    private final Vertex source;
    private final Vertex target;

    public Edge(final Vertex source, final Vertex target) {
        this.source = source;
        this.target = target;
    }

    public Vertex getTarget() {
        return target;
    }

    public Vertex getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return source.equals(edge.source) && target.equals(edge.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
