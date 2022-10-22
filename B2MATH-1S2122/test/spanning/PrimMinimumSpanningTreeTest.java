package spanning;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PrimMinimumSpanningTreeTest {

	// for each graph build a case for each vertex as start vertex

	private static final Supplier<Stream<Arguments>> simpleWeightedGraphCases = () -> IntStream.range(0, 7).boxed()
			.map(operand -> Arguments.of(createSimpleWeightedGraph(), 7, 20.0, operand));

	private static final Supplier<Stream<Arguments>> simpleWeightedGraph_2Cases = () -> IntStream.range(1, 6).boxed()
			.map(operand -> Arguments.of(createSimpleWeightedGraph_2(), 5, 10.0, operand));

	private static final Supplier<Stream<Arguments>> simpleWeightedGraphWithTwoComponentsAllComponentCases = () -> IntStream.range(1, 8).boxed()
			.map(operand -> Arguments.of(createSimpleWeightedGraphWithTwoComponents(), 6, 12.0, operand));

	private static final Supplier<Stream<Arguments>> simpleWeightedGraphWithTwoComponentsFirstComponentCases = () -> IntStream.range(1, 6).boxed()
			.map(operand -> Arguments.of(createSimpleWeightedGraphWithTwoComponents(), 5, 10.0, operand));

	private static final Supplier<Stream<Arguments>> simpleWeightedGraphWithTwoComponentsSecondCases = () -> IntStream.range(7, 8).boxed()
			.map(operand -> Arguments.of(createSimpleWeightedGraphWithTwoComponents(), 1, 2.0, operand));

	private static final Supplier<Stream<Arguments>> simpleWeightedGraphWithNegativeWeightCases = () -> IntStream.range(0, 7).boxed()
			.map(operand -> Arguments.of(createSimpleWeightedGraphWithNegativeWeight(), 7, -58.0, operand));

	/*
	 * CONSTRUCTOR TESTS
	 */
	@Test
	void testPrimMinimumSpanningTree() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createSimpleWeightedGraphWithTwoComponents();
		assertNotNull(new PrimMinimumSpanningTree<>(g));
	}

	/*
	 * SPANNING TREE/FOREST TESTS
	 */
	@ParameterizedTest
	@MethodSource("provideCaseForSpanningTree")
	void testGetSpanningTree(SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph, int expectedEdgeCount, double expectedTreeWeight) {
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(graph);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree();
		assertSpanningTree(expectedEdgeCount, expectedTreeWeight, spt);
	}

	private static Stream<Arguments> provideCaseForSpanningTree() {

		final List<Stream<Arguments>> cases = List.of(simpleWeightedGraphCases.get(),
				simpleWeightedGraph_2Cases.get(),
				simpleWeightedGraphWithTwoComponentsAllComponentCases.get(),
				simpleWeightedGraphWithNegativeWeightCases.get());
		return toStream(cases);
	}

	/*
	 * SPANNING TREE FROM SPECIFIC NODE TESTS
	 */
	@ParameterizedTest
	@MethodSource("provideCaseForSpanningTreeV")
	void testGetSpanningTreeV(SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph, int expectedEdgeCount, double expectedTreeWeight, int start) {
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(graph);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree(start);
		assertSpanningTree(expectedEdgeCount, expectedTreeWeight, spt);
	}

	private static Stream<Arguments> provideCaseForSpanningTreeV() {
		final List<Stream<Arguments>> cases = List.of(simpleWeightedGraphCases.get(),
				simpleWeightedGraph_2Cases.get(),
				simpleWeightedGraphWithTwoComponentsFirstComponentCases.get(),
				simpleWeightedGraphWithTwoComponentsSecondCases.get(),
				simpleWeightedGraphWithNegativeWeightCases.get());
		return toStream(cases);
	}

	@ParameterizedTest
	@MethodSource("provideCaseForSpanningTree")
	void testGetSpanningTreeMultipleTime(SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph, int expectedEdgeCount, double expectedTreeWeight) {

		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(graph);

		for (int i = 0; i < 10; i++) {
			SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree();
			assertSpanningTree(expectedEdgeCount, expectedTreeWeight, spt);
		}
	}

	@ParameterizedTest
	@MethodSource("provideCaseForSpanningTreeV")
	void testGetSpanningTreeVMultipleTime(SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph, int expectedEdgeCount, double expectedTreeWeight, int start) {

		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(graph);

		for (int i = 0; i < 10; i++) {
			SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree(start);
			assertSpanningTree(expectedEdgeCount, expectedTreeWeight, spt);
		}
	}

	@Test
	void testGetSpanningTreeWithEmptyGraph() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(graph);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree();
		assertSpanningTree(0, 0, spt);
	}

	private void assertSpanningTree(int expectedEdgeCount, double expectedTreeWeight, SpanningTree<DefaultWeightedEdge> spt) {
		assertNotNull(spt);
		assertEquals(expectedEdgeCount, spt.getEdges().size());
		assertEquals(expectedTreeWeight, spt.getWeight(), 0.001);
	}

	private static Stream<Arguments> toStream(List<Stream<Arguments>> streams) {
		return reduce(new ArrayList<>(streams), Stream.of(), Stream::concat);
	}

	private static <T, U> T reduce(List<U> source, T start, BiFunction<T, U, T> accumulator) {
		T result = start;
		for (U element : source)
			result = accumulator.apply(result, element);
		return result;
	}

	/**
	 * Creates a sample connected undirected weighted graph [MULLER example p.20]
	 * 
	 * @return connected undirected graph
	 */
	private static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createSimpleWeightedGraphWithTwoComponents() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// Création des sommets
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addVertex(4);
		g.addVertex(5);
		g.addVertex(6);
		
		// Création des arêtes pondérées
		g.setEdgeWeight(g.addEdge(1, 2), 5);
		g.setEdgeWeight(g.addEdge(1, 4), 3);
		g.setEdgeWeight(g.addEdge(1, 5), 2);
		g.setEdgeWeight(g.addEdge(2, 3), 5);
		g.setEdgeWeight(g.addEdge(2, 5), 4);
		g.setEdgeWeight(g.addEdge(3, 5), 2);
		g.setEdgeWeight(g.addEdge(3, 6), 1);
		g.setEdgeWeight(g.addEdge(4, 5), 1);
		g.setEdgeWeight(g.addEdge(5, 6), 3);

		// Ajout d'une deuxième composante connexe
		g.addVertex(7);
		g.addVertex(8);
		g.setEdgeWeight(g.addEdge(7, 8), 2);
		
		return g;
	}

	private static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createSimpleWeightedGraph() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// Création des sommets
		g.addVertex(0);
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addVertex(4);
		g.addVertex(5);
		g.addVertex(6);
		g.addVertex(7);

		// Création des arêtes pondérées
		g.setEdgeWeight(g.addEdge(0, 1), 10);
		g.setEdgeWeight(g.addEdge(0, 2), 1);
		g.setEdgeWeight(g.addEdge(0, 3), 4);

		g.setEdgeWeight(g.addEdge(1, 4), 0);
		g.setEdgeWeight(g.addEdge(1, 2), 3);

		g.setEdgeWeight(g.addEdge(2, 5), 8);
		g.setEdgeWeight(g.addEdge(2, 3), 2);

		g.setEdgeWeight(g.addEdge(3, 5), 2);
		g.setEdgeWeight(g.addEdge(3, 6), 7);

		g.setEdgeWeight(g.addEdge(4, 5), 1);
		g.setEdgeWeight(g.addEdge(4, 7), 8);

		g.setEdgeWeight(g.addEdge(5, 7), 9);
		g.setEdgeWeight(g.addEdge(5, 6), 6);

		g.setEdgeWeight(g.addEdge(6, 7), 12);

		return g;
	}

	private static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createSimpleWeightedGraph_2() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// Création des sommets
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addVertex(4);
		g.addVertex(5);
		g.addVertex(6);

		// Création des arêtes pondérées
		g.setEdgeWeight(g.addEdge(1, 2), 5);
		g.setEdgeWeight(g.addEdge(1, 4), 3);
		g.setEdgeWeight(g.addEdge(1, 5), 2);
		g.setEdgeWeight(g.addEdge(2, 3), 5);
		g.setEdgeWeight(g.addEdge(2, 5), 4);
		g.setEdgeWeight(g.addEdge(3, 5), 2);
		g.setEdgeWeight(g.addEdge(3, 6), 1);
		g.setEdgeWeight(g.addEdge(4, 5), 1);
		g.setEdgeWeight(g.addEdge(5, 6), 3);

		return g;
	}

	private static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createSimpleWeightedGraphWithNegativeWeight() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);


		// Création des sommets
		g.addVertex(0);
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addVertex(4);
		g.addVertex(5);
		g.addVertex(6);
		g.addVertex(7);

		// Création des arêtes pondérées
		g.setEdgeWeight(g.addEdge(0, 1), -10);
		g.setEdgeWeight(g.addEdge(0, 2), -1);
		g.setEdgeWeight(g.addEdge(0, 3), -4);

		g.setEdgeWeight(g.addEdge(1, 4), -0);
		g.setEdgeWeight(g.addEdge(1, 2), -3);

		g.setEdgeWeight(g.addEdge(2, 5), -8);
		g.setEdgeWeight(g.addEdge(2, 3), -2);

		g.setEdgeWeight(g.addEdge(3, 5), -2);
		g.setEdgeWeight(g.addEdge(3, 6), -7);

		g.setEdgeWeight(g.addEdge(4, 5), -1);
		g.setEdgeWeight(g.addEdge(4, 7), -8);

		g.setEdgeWeight(g.addEdge(5, 7), -9);
		g.setEdgeWeight(g.addEdge(5, 6), -6);

		g.setEdgeWeight(g.addEdge(6, 7), -12);

		return g;
	}
}
