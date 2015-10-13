package es.us.lsi.fogallego;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class GraphTechnologiesBenchmark {

    public static final int INIT_NUM_VERTICES = 100;
    public static final int MAX_NUM_VERTICES = 100000;
    public static final int STEP = 10;
    public static final int NUM_TEST_REPETITIONS = 5;

    static VertexFactory<Object> vFactory =
            new ClassBasedVertexFactory<Object>(Object.class);

    public static void main(String[] args) {

        Random rdm = new Random();

        System.out.println("Starting benchmark...");

        for (int numVertices= INIT_NUM_VERTICES;numVertices<= MAX_NUM_VERTICES;numVertices*= STEP) {
            int maxLimitEdges = numVertices * (numVertices - 1)/2;

            System.out.println("Testing graphs with "+numVertices+" number of vertices");
            double timeCreatingJGrapht = 0;
            double timeCreatingNeo4j = 0;
            double timeExecutionJGrapht = 0;
            double timeExecutionNeo4j = 0;
            for (int j = 0;j< NUM_TEST_REPETITIONS;j++) {
                int numEdgesTest = rdm.nextInt(maxLimitEdges);
                System.out.println("Random number of edges: " + numEdgesTest);

                for (int k = 0; k < NUM_TEST_REPETITIONS; k++) {

                    // JGraphT
                    long tInit = System.currentTimeMillis();
                    Graph<Object, DefaultEdge> graphJGraphT = generateRandomJGraphtGraph(numVertices, numEdgesTest);
                    long tExecution = System.currentTimeMillis() - tInit;
                    timeCreatingJGrapht += tExecution;
                    //System.out.println("Time creating JGraphT graph: " + tExecution);

                    tInit = System.currentTimeMillis();
                    int numJGraphtConnectedComponents = getJGraphtNumConnectedComponents(graphJGraphT);
                    tExecution = System.currentTimeMillis() - tInit;
                    timeExecutionJGrapht += tExecution;
                   // System.out.println("Time calculating connected components: " + tExecution);

                    //Neo4j
                    tInit = System.currentTimeMillis();
//                    Graph<Object, DefaultEdge> graphNeo4j = generateRandomNeo4jGraph(numVertices, numEdgesTest);
                    tExecution = System.currentTimeMillis() - tInit;
                    timeCreatingNeo4j += tExecution;
                    //System.out.println("Time creating Neo4j graph: " + tExecution);

                    tInit = System.currentTimeMillis();
//                    int numNeo4jConnectedComponents = getNeo4jNumConnectedComponents(graphNeo4j);
                    tExecution = System.currentTimeMillis() - tInit;
                    timeExecutionNeo4j += tExecution;
                   // System.out.println("Time calculating connected components: " + tExecution);

//                    if (numJGraphtConnectedComponents != numNeo4jConnectedComponents) {
//                        System.err.println("Different number of calculated connected components");
//                    }

                }

                timeCreatingJGrapht /= NUM_TEST_REPETITIONS;
                timeCreatingNeo4j /= NUM_TEST_REPETITIONS;
                timeExecutionJGrapht /= NUM_TEST_REPETITIONS;
                timeExecutionNeo4j /= NUM_TEST_REPETITIONS;

                System.out.println("Mean values: ");
                System.out.println("JGrapht: "+timeCreatingJGrapht+" creating graph, "+timeExecutionJGrapht+
                        " calculating connected components");
                System.out.println("Neo4j: "+timeCreatingNeo4j+" creating graph, "+timeExecutionNeo4j+
                        " calculating connected components");
            }

            System.out.println();
        }

        System.out.println("Finishing benchmark");

    }

    private static Graph<Object, DefaultEdge> generateRandomJGraphtGraph(int numVertices, int numEdges) {

        Graph<Object, DefaultEdge> graph = new SimpleGraph<Object, DefaultEdge>(DefaultEdge.class);

        RandomGraphGenerator<Object, DefaultEdge> randomGraphGenerator = new RandomGraphGenerator<>(numVertices, numEdges);

        randomGraphGenerator.generateGraph(graph, vFactory, null);

        return graph;
    }

    private static Graph<Object, DefaultEdge> generateRandomNeo4jGraph(int numVertices, int numEdges) {
        //TODO


        return null;
    }

    private static int getJGraphtNumConnectedComponents(Graph<Object, DefaultEdge> graphJGraphT) {

        ConnectivityInspector<Object,DefaultEdge> connectivityInspector =
                new ConnectivityInspector<Object, DefaultEdge>((UndirectedGraph<Object, DefaultEdge>) graphJGraphT);
        List<Set<Object>> connectedSets = connectivityInspector.connectedSets();

        return connectedSets.size();
    }

    private static int getNeo4jNumConnectedComponents(Graph<Object, DefaultEdge> graphJGraphT) {
        return 0;
    }

    private static Graph<Object, DefaultEdge> generateCompleteJGraphtGraph(int size) {

        Graph<Object, DefaultEdge> completeGraph = new SimpleGraph<Object, DefaultEdge>(DefaultEdge.class);

        //Create the CompleteGraphGenerator object
        CompleteGraphGenerator<Object, DefaultEdge> completeGenerator =
                new CompleteGraphGenerator<Object, DefaultEdge>(size);

        //Create the VertexFactory so the generator can create vertices
        VertexFactory<Object> vFactory =
                new ClassBasedVertexFactory<Object>(Object.class);

        //Use the CompleteGraphGenerator object to make completeGraph a
        //complete graph with [size] number of vertices
        completeGenerator.generateGraph(completeGraph, vFactory, null);

        return completeGraph;
    }

}
