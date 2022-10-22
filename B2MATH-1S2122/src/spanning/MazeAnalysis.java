package spanning;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import graphics.Image;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.graph.SimpleWeightedGraph;

public class MazeAnalysis {


    private final int VERTEX_COLOR_R = 255;
    private final int VERTEX_COLOR_G = 255;
    private final int VERTEX_COLOR_B = 255;

	private final boolean isPerfect;
	private final boolean isConnected;
	private final boolean isConnectedWithCycles;
	private final boolean isExitReachable;

	// Work
	private final Predicate<Point> isVertex;
	private final Set<Edge> edgesToExit;

	/**
	 * Constructor
	 * 
	 * @param image The bitmap image to analyze
	 */
	public MazeAnalysis(final BufferedImage image) {
		isVertex = (point) -> PixelColor.from(image).at(point).hasRgb(VERTEX_COLOR_R, VERTEX_COLOR_G, VERTEX_COLOR_B);
		int squareRoomSize = getVerticalPixelCountUntilReachDoor(image, 0);
		int exitY = getVerticalPixelCountUntilReachDoor(image, image.getWidth() - squareRoomSize);
		edgesToExit = getIncidentVertex(image, new Vertex(image.getWidth() - squareRoomSize, exitY) , squareRoomSize, isVertex);
		final SimpleWeightedGraph<Vertex, Edge> graph = buildGraphFromMazeImage(image, squareRoomSize);
		final PrimMinimumSpanningTree<Vertex, Edge> prim = new PrimMinimumSpanningTree<>(graph);
		isConnected = isConnected(prim, graph);
		isExitReachable = isExitReachable(prim, graph.vertexSet().iterator().next());
		isConnectedWithCycles = isConnectedWithCycles(prim, graph);
		isPerfect = isPerfect(prim, graph);
	}

	public SimpleWeightedGraph<Vertex, Edge> buildGraphFromMazeImage(final BufferedImage image, final int squareRoomSize) {

		// Work variables
		final SimpleWeightedGraph<Vertex, Edge> graph = new SimpleWeightedGraph<>(Edge.class);

		Vertex current;
		Vertex top;
		Vertex left;

		for (int x = 0; x < image.getWidth(); x += squareRoomSize) {
			for (int y = 0; y < image.getHeight(); y += squareRoomSize) {
                current = new Vertex(x, y);

				// handle vertices
				if(isVertex.test(current)) {
					graph.addVertex(current);

					final int leftX = current.getX() - squareRoomSize;
					left = new Vertex(leftX < 0 ? current.getX() : leftX, current.getY());
					final Edge edgeFromLeftToCurrent = new Edge(left, current);

					final int topY = current.getY() - squareRoomSize;
					top = new Vertex(current.getX(), topY < 0 ? current.getY() : topY);
					final Edge edgeFromTopToCurrent = new Edge(top, current);

					if(!top.equals(current) && isVertex.test(top))
						graph.addEdge(top, current, edgeFromTopToCurrent);

					if(!left.equals(current) && isVertex.test(left))
						graph.addEdge(left, current, edgeFromLeftToCurrent);
				}
			}
		}
		return graph;
	}

	private Set<Edge> getIncidentVertex(final BufferedImage image, final Vertex current, final int squareRoomSize, final Predicate<Point> isVertex) {
		final Set<Edge> set = new HashSet<>();

		final int leftX = current.getX() - squareRoomSize;
		final Vertex left = new Vertex(leftX < 0 ? current.getX() : leftX, current.getY());
		if(!left.equals(current) && isVertex.test(left))
			set.add(new Edge(left, current));

		final int topY = current.getY() - squareRoomSize;
		final Vertex top = new Vertex(current.getX(), topY < 0 ? current.getY() : topY);
		if(!top.equals(current) && isVertex.test(top))
			set.add(new Edge(top, current));

		final int bottomY = current.getY() + squareRoomSize;
		final Vertex bottom = new Vertex(current.getX(), bottomY >= image.getHeight() ? current.getY() : bottomY);
		if(!bottom.equals(current) && isVertex.test(bottom))
			set.add(new Edge(bottom, current));

		final int rightX = current.getX() + squareRoomSize;
		final Vertex right = new Vertex(rightX >= image.getWidth() ? current.getX() : rightX, current.getY());
		if(!right.equals(current) && isVertex.test(right))
			set.add(new Edge(right, current));

		return set;
	}

	public int getVerticalPixelCountUntilReachDoor(final BufferedImage image, final int x) {
		int squaredRoomSize = 0;
		for(int y = 0; y < image.getHeight(); y++) {
			if(PixelColor.from(image).at(new Point(x, y)).hasRgb(VERTEX_COLOR_R, VERTEX_COLOR_G, VERTEX_COLOR_B)) break;
			else squaredRoomSize++;
		}
		return squaredRoomSize;
	}

	public void display(final SpanningTreeAlgorithm.SpanningTree<Edge> graph, final int roomSize, final int width) {
		final Set<Edge> vertices = graph.getEdges();
		final Function<Vertex, Integer> indice = (vertex) -> ((vertex.getY() / roomSize) * (width / 50)) + (vertex.getX() / roomSize);
		for (final Edge edge : vertices) {
			System.out.printf(" %d -> %d\n", indice.apply(edge.getSource()), indice.apply(edge.getTarget()));
		}
	}

	/**
	 * Determines if the maze is perfect.
	 * 
	 * @return true if the maze is perfect, false otherwise
	 */
	public boolean isPerfect() {
		return isPerfect;
	}

	public boolean isPerfect(PrimMinimumSpanningTree<Vertex, Edge> prim, final SimpleWeightedGraph<Vertex, Edge> graph) {
		final SpanningTreeAlgorithm.SpanningTree<Edge> sp = prim.getSpanningTree();
		final Set<Edge> edgesInSp = sp.getEdges();
		return isConnected && isExitReachable && edgesInSp.containsAll(graph.edgeSet());
	}

	/**
	 * Determines if all the rooms in the maze are interconnected.
	 *  
	 * @return true if connected, false otherwise
	 */
	public boolean isConnected() {
		return isConnected;
	}

	private boolean isConnected(final PrimMinimumSpanningTree<Vertex, Edge> prim, final SimpleWeightedGraph<Vertex, Edge> graph) {
		// Connected if the spanning tree have n - 1 edges where n is the count of vertices in the original graph
		final SpanningTreeAlgorithm.SpanningTree<Edge> sp = prim.getSpanningTree();
		return sp.getEdges().size() == graph.vertexSet().size() - 1;
	}
	
	/**
	 * Determines if all the rooms in the maze are interconnected,
	 * but with the possibility of going in circles.
	 *
	 * @return true if connected + cycles, false otherwise
	 */
	public boolean isConnectedWithCycles() {
		return isConnectedWithCycles;
	}

	private boolean isConnectedWithCycles(final PrimMinimumSpanningTree<Vertex, Edge> prim, final SimpleWeightedGraph<Vertex, Edge> graph) {
		final SpanningTreeAlgorithm.SpanningTree<Edge> sp = prim.getSpanningTree();
		final Set<Edge> edgesInSp = sp.getEdges();
		return isConnected && !edgesInSp.containsAll(graph.edgeSet());
	}

	/**
	 * Determines if the exit is reachable from the entry.
	 * 
	 * @return true if exit is reachable, false otherwise
	 */
	public boolean isExitReachable() {
		return isExitReachable;
	}

	private boolean isExitReachable(final PrimMinimumSpanningTree<Vertex, Edge> prim, final Vertex start) {
		final SpanningTreeAlgorithm.SpanningTree<Edge> sp = prim.getSpanningTree(start);
		final Set<Edge> edgesInSp = sp.getEdges();
		return edgesInSp.stream().anyMatch(edgesToExit::contains);
	}
	
	/**
	 * MAIN - Examples
	 *
	 */
	public static void main(String[] args) {
		System.out.println("\n>>> MAZE ANALYSIS <<<\n");
		
		BufferedImage image;
		
		image = Image.loadImage("img/mazes/maze_perfect.png");
		System.out.println("maze_perfect : "+image.getWidth()+"x"+image.getHeight());
		AnalyzeMaze(image);
		System.out.println();

		image = Image.loadImage("img/mazes/maze_with_cycles.png");
		System.out.println("maze_with_cycles : "+image.getWidth()+"x"+image.getHeight());
		AnalyzeMaze(image);
		System.out.println();

		image = Image.loadImage("img/mazes/maze_separate_zones_exit_reachable.png");
		System.out.println("maze_with_separate_zones : "+image.getWidth()+"x"+image.getHeight());
		AnalyzeMaze(image);
		System.out.println();

		image = Image.loadImage("img/mazes/maze_separate_zones_no_exit.png");
		System.out.println("maze_with_separate_zones_no_exit : "+image.getWidth()+"x"+image.getHeight());
		AnalyzeMaze(image);
		System.out.println();
	}
	
	/**
	 * Checks the characteristics of a maze.
	 * 
	 * @param image A bitmap image of a maze.
	 */
	private static void AnalyzeMaze(BufferedImage image) {
		MazeAnalysis analysis = new MazeAnalysis(image);
		if (analysis.isPerfect()) {
			System.out.println("The maze is perfect !");
		}
		if (analysis.isConnectedWithCycles()) {
			System.out.println("The maze is connected but contains cycles.");
		}
		if (!analysis.isConnected()) {
			System.out.println("The maze contains disconnected areas.");
		}
		if (analysis.isExitReachable()) {
			System.out.println("-> The exit is reachable from the entry.");
		} else {
			System.out.println("-> The exit is not reachable.");
		}
	}
}
