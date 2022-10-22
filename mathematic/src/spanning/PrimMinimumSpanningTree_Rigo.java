package spanning;

//org.jgrapht.Graph.*, 
//org.jgrapht.Graphs.*, 
//org.jgrapht.graph.* 
//et org.jgrapht.alg.interfaces.*.

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/*
 * Implémentation naïve et très inefficace de l'algorithme de PRIM
 * en suivant le pseudo-code à la lettre (ou presque).
 * 
 * ==> L'EXEMPLE PARFAIT A NE PAS SUIVRE !!! 
 */
public class PrimMinimumSpanningTree_Rigo<V, E> implements SpanningTreeAlgorithm<E>{

	private final Graph<V, E> G;
	private final Set<V> Vp = new HashSet<>();
	private final Set<E> A = new HashSet<>();
	private final Map<V, E> L = new HashMap<>();
	private double sptWeight;
	
	public PrimMinimumSpanningTree_Rigo(Graph<V, E> graph) {
		this.G = graph;
	}
	
	@Override
	public SpanningTree<E> getSpanningTree() {
		return getSpanningTree(G.vertexSet().iterator().next());
	}
	
	public SpanningTree<E> getSpanningTree(V startVertex) {
		init(startVertex);
		execute();
        return new SpanningTreeImpl<>(A, sptWeight);
	}
	
	/**
	 * Phase d'initialisation.
	 * 
	 * @param startVertex Sommet de départ
	 */
	private void init(V startVertex) {
		Vp.clear();
		Vp.add(startVertex);

		A.clear();

		L.clear();
		Set<V> VmoinsVp = new HashSet<>(G.vertexSet());
		VmoinsVp.removeAll(Vp);
		for (V v : VmoinsVp) {
			L.put(v, G.getEdge(startVertex, v));
		}
		sptWeight = 0d;
	}
	
	/**
	 * Boucle principale de l'algorithme de Prim
	 */
	private void execute() {
		while (Vp.size() != G.vertexSet().size()) {
			Set<V> VmoinsVp = new HashSet<>(G.vertexSet());
			VmoinsVp.removeAll(Vp);
			V minVertex = selectMinVertex(VmoinsVp);
			update(minVertex);
		}	
	}
	
	/**
	 * Sélection du sommet 'u' à ajouter à l'arbre couvrant,
	 * qui est joignable avec l'arête de poids minimal.
	 * 
	 * @param VmoinsVp Ensemble des sommets non visités
	 * @return Sommet sélectionné
	 */
	private V selectMinVertex(Set<V> VmoinsVp) {
		V uMin = null;
		double wMin = Double.POSITIVE_INFINITY;
		E eMin = null;
		
		for(V u : VmoinsVp) {
			E edge = L.get(u);
			if (edge != null) {
				double weight = G.getEdgeWeight(edge);
				if (wMin == Double.POSITIVE_INFINITY || weight < wMin) {
					uMin = u;
					wMin = weight;
					eMin = edge;
				}
			}
		}
		
		Vp.add(uMin);
		A.add(eMin);
		sptWeight += wMin;
		return uMin;
	}
	
	/**
	 * Mise à jour des sommets atteignables depuis le sommet 'u'
	 * et des poids minimaux associés.
	 * 
	 * @param u Le dernier sommet ajouté à l'arbre couvrant.
	 */
	private void update(V u) {
		Set<V> VmoinsVp = new HashSet<>(G.vertexSet());
		VmoinsVp.removeAll(Vp);
		for (V v : VmoinsVp) {
			E edge = G.getEdge(u, v);
			if (edge != null) {
				double weight = G.getEdgeWeight(edge);
				if (L.get(v) == null || weight < G.getEdgeWeight(L.get(v))) {
					L.put(v, edge);
				}
			}
		}
	}
	
	/*
	 * MAIN - Exemple d'utilisation 
	 */
	public static void main(String[] args) {
		long time;

		System.out.println("ARBRE COUVRANT DE POIDS MINIMUM");

		/*
		 * Exemple 1 - MULLER [page 20] 
		 */
		System.out.println("\n>>> MULLER [page 20]");
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g1 = createSimpleWeightedGraph();
		System.out.println("\nGraphe généré");
		System.out.printf("  sommets : %d / arêtes : %d\n", g1.vertexSet().size(), g1.edgeSet().size());
		
		time = System.currentTimeMillis();
		org.jgrapht.alg.spanning.PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> jgtPrim1 = new org.jgrapht.alg.spanning.PrimMinimumSpanningTree<>(g1);
		SpanningTree<DefaultWeightedEdge> jgtSpt1 = jgtPrim1.getSpanningTree();	
		System.out.println("\nArbre couvrant de poids minimal (JGraphT)");
		System.out.println("  poids total : "+(int)(jgtSpt1.getWeight()*10)/10.0);
		System.out.println("  arêtes : "+jgtSpt1.getEdges().size());
		System.out.printf("  temps écoulé = %.2f seconds\n", (System.currentTimeMillis() - time) / 1000.0);

		time = System.currentTimeMillis();
		PrimMinimumSpanningTree_Rigo<Integer, DefaultWeightedEdge> prim1 = new PrimMinimumSpanningTree_Rigo<>(g1);
		SpanningTree<DefaultWeightedEdge> spt1 = prim1.getSpanningTree();
		System.out.println("\nArbre couvrant de poids minimal");
		System.out.println("  poids total : "+(int)(spt1.getWeight()*10)/10.0);
		System.out.println("  arêtes : "+spt1.getEdges().size());
		System.out.printf("  temps écoulé = %.2f secondes\n", (System.currentTimeMillis() - time) / 1000.0);
		
		/*
		 * Exemple 2 - Graphe aléatoire
		 */
		System.out.println("\n>>> Graphe pondéré connexe aléatoire");
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(5000, 10000);
//		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(10000, 20000);
//		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(20000, 40000);
//		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(100000, 200000);
		System.out.println("\nGraphe généré");
		System.out.printf("  sommets : %d / arêtes : %d\n", g2.vertexSet().size(), g2.edgeSet().size());
		
		time = System.currentTimeMillis();
		org.jgrapht.alg.spanning.PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> jgtPrim2 = new org.jgrapht.alg.spanning.PrimMinimumSpanningTree<>(g2);
		SpanningTree<DefaultWeightedEdge> jgtSpt2 = jgtPrim2.getSpanningTree();	
		long jgtDuration = System.currentTimeMillis() - time;
		System.out.println("\nArbre couvrant de poids minimal (JGraphT)");
		System.out.println("  poids total : "+(int)(jgtSpt2.getWeight()*10)/10.0);
		System.out.println("  arêtes : "+jgtSpt2.getEdges().size());
		System.out.printf("  temps écoulé = %.2f seconds\n", jgtDuration / 1000.0);
		
		time = System.currentTimeMillis();
		PrimMinimumSpanningTree_Rigo<Integer, DefaultWeightedEdge> prim2 = new PrimMinimumSpanningTree_Rigo<>(g2);
		SpanningTree<DefaultWeightedEdge> spt2 = prim2.getSpanningTree();
		long customDuration = System.currentTimeMillis() - time;
		System.out.println("\nArbre couvrant de poids minimal");
		System.out.println("  poids total : "+(int)(spt2.getWeight()*10)/10.0);
		System.out.println("  arêtes : "+spt2.getEdges().size());
		System.out.printf("  temps écoulé = %.2f secondes\n", customDuration / 1000.0);	
		
		float performanceFactor = (float)customDuration / jgtDuration;
		if (performanceFactor >= 1.0f) {
			System.out.printf("\n==> %.1f fois plus lent", performanceFactor);
		} else {
			System.out.printf("\n==> %.1f fois plus rapide", 1.0f/performanceFactor);
		}
	}

	/**
	 * Creates a sample connected undirected weighted graph [MULLER example p.20]
	 * 
	 * @return connected undirected graph
	 */
	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createSimpleWeightedGraph() {
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
//		g.addVertex(7);
//		g.addVertex(8);
//		g.setEdgeWeight(g.addEdge(7, 8), 2);
		
		return g;
	}

	/**
	 * Creates a random connected undirected weighted graph
	 * 
	 * @param vertexCount number of vertices
	 * @param edgeCount number of edges to target, must be >= (vertexCount-1) 
	 * @return random undirected weighted graph
	 */
	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createUndirectedWeightedGraph(int vertexCount, int edgeCount) {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		/* Ajout de <size> sommets au graphe. Chaque nouveau sommet est
		 * connecté à un sommet précédent choisi au hasard, ce qui
		 * garantit que le graphe est connexe (c'est également un arbre).
		 */
		for (int i=0; i<vertexCount; i++) {
			g.addVertex(i);
			if (i>0) g.setEdgeWeight(g.addEdge(i, (int)(Math.random()*i)), (int)(Math.random()*100)/10.0 + 0.1);
		}
		
		/*
		 * Ajout de maximum <additionalEdges> supplémentaires au graphe
		 */
		int additionalEdges = edgeCount - vertexCount + 1;
		for (int i=0; i<additionalEdges; i++) {
			int src = (int)(Math.random()*vertexCount);
			int dst = (int)(Math.random()*vertexCount);
			if (src != dst && g.getEdge(src, dst) == null) {
				g.setEdgeWeight(g.addEdge(src, dst), (int)(Math.random()*100)/10.0 + 0.1);
			}
		}
		return g;
	}
}

