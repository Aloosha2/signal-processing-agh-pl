package graphs.shortestpaths;

import priorityqueues.ExtrinsicMinPQ;
import priorityqueues.NaiveMinPQ;
import graphs.BaseEdge;
import graphs.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {


    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new NaiveMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */
        // return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        Set<V> known = new HashSet<>();
        Map<V, Double> distTo = new HashMap<>();
        Map<V, E> edgeTo = new HashMap<>();
        ExtrinsicMinPQ<V> pq = createMinPQ();
        pq.add(start, 0.0);
        distTo.put(start, 0.0);
        //edgeTo.put(start, null);

        while (!pq.isEmpty()) {
            V vertex = pq.removeMin();
            known.add(vertex);
            if (Objects.equals(vertex, end)) {
                return edgeTo;
            }
            for (E edge : graph.outgoingEdgesFrom(vertex)) {
                if (!distTo.containsKey(edge.to())) {
                    distTo.put(edge.to(), Double.POSITIVE_INFINITY);
                    //edgeTo.put(edge.to(), edge);
                    pq.add(edge.to(), edge.weight());
                }
                double oldDist = distTo.get(edge.to());
                double newDist = distTo.get(vertex) + edge.weight();
                if (newDist < oldDist) {
                    distTo.put(edge.to(), newDist);
                    edgeTo.put(edge.to(), edge);
                    pq.changePriority(edge.to(), newDist);
                }
            }
        }
        return edgeTo;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        if (Objects.equals(start, end)) {
            return new ShortestPath.SingleVertex<>(start);
        }
        List<E> shortestPath = new ArrayList<>();
        E edge = spt.get(end);
        if (edge == null) {
            return new ShortestPath.Failure<>();
        }
        // basically runs until start where edge.from() will be null
        while (edge != null) {
            shortestPath.add(edge);
            edge = spt.get(edge.from());
        }
        Collections.reverse(shortestPath);
        return new ShortestPath.Success<>(shortestPath);
    }
}


