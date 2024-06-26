package seamcarving;

import graphs.Edge;
import graphs.Graph;
import graphs.shortestpaths.DijkstraShortestPathFinder;
import graphs.shortestpaths.ShortestPath;
import graphs.shortestpaths.ShortestPathFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DijkstraSeamFinder implements SeamFinder {
    private final ShortestPathFinder<Graph<Pixel, Edge<Pixel>>, Pixel, Edge<Pixel>> pathFinder;

    public DijkstraSeamFinder() {
        this.pathFinder = createPathFinder();
    }

    protected <G extends Graph<V, Edge<V>>, V> ShortestPathFinder<G, V, Edge<V>> createPathFinder() {
        /*
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
        */
        return new DijkstraShortestPathFinder<>();
    }

    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        double[][] rotatedGraph = new double[energies[0].length][energies.length];
        for (int i = 0; i < energies.length; i++) {
            for (int j = 0; j < energies[0].length; j++) {
                rotatedGraph[j][i] = energies[i][j];
            }
        }
        return findVerticalSeam(rotatedGraph);
    }

    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        PicGraph graph = new PicGraph();
        Pixel dummyStart = new Pixel(-1, -1);
        Pixel dummyEnd = new Pixel(-2, -2);

        for (int i = 0; i < energies.length; i++) {
            for (int j = 0; j < energies[0].length; j++) {
                // along the top of the picture
                if (i == 0 && j < energies[0].length - 1) {
                    Pixel pix = new Pixel(i, j);
                    if (j == 0) {
                        graph.addEdges(dummyStart, new Edge<>(dummyStart, pix, 0));
                    }
                    Pixel edge1 = new Pixel(i, j + 1);
                    Pixel edge2 = new Pixel(i + 1, j + 1);
                    graph.addEdges(pix, new Edge<>(pix, edge1, energies[i][j]));
                    graph.addEdges(pix, new Edge<>(pix, edge2, energies[i][j]));

                }
                // when it gets to the right of the picture, no edges going out
                else if (j == energies[0].length - 1) {
                    Pixel pix = new Pixel(i, j);
                    graph.addEdges(pix, new Edge<>(pix, dummyEnd, energies[i][j]));
                } else {
                    Pixel pix = new Pixel(i, j);
                    if (j == 0) {
                        graph.addEdges(dummyStart, new Edge<>(dummyStart, pix, 0));
                    }
                    Pixel edge1 = new Pixel(i, j + 1);
                    Pixel edge3 = new Pixel(i - 1, j + 1);
                    if (i < energies.length - 1) {
                        Pixel edge2 = new Pixel(i + 1, j + 1);
                        graph.addEdges(pix, new Edge<>(pix, edge2, energies[i][j]));
                    }
                    graph.addEdges(pix, new Edge<>(pix, edge1, energies[i][j]));
                    graph.addEdges(pix, new Edge<>(pix, edge3, energies[i][j]));
                }
            }
        }

        ShortestPath<Pixel, Edge<Pixel>> spt = pathFinder.findShortestPath(graph, dummyStart, dummyEnd);
        List<Integer> verticals = new ArrayList<>();
        if (spt != null) {
            for (Pixel vert : spt.vertices()) {
                verticals.add(vert.getVertical());
            }
        }
        verticals.remove(0);
        verticals.remove(verticals.size() - 1);
        return verticals;
    }

    private class PicGraph implements Graph<Pixel, Edge<Pixel>> {
        private Map<Pixel, List<Edge<Pixel>>> graphMap;

        public PicGraph() {
            graphMap = new HashMap<>();
        }

        public void addVertex(Pixel vertex) {
            graphMap.put(vertex, new ArrayList<>());
        }

        public void addEdges(Pixel vertex, Edge<Pixel> edge) {
            if (!graphMap.containsKey(vertex)) {
                addVertex(vertex);
            }
            graphMap.get(vertex).add(edge);
        }
        public Pixel get(int vertical, int horizontal) {
            for (Pixel vertex : graphMap.keySet()) {
                if (vertex.getHorizontal() == horizontal && vertex.getVertical() == vertical) {
                    return vertex;
                }
            }
            return null;
        }

        public Collection<Edge<Pixel>> outgoingEdgesFrom(Pixel vertex) {
            Collection<Edge<Pixel>> outEdges = new ArrayList<>(graphMap.get(vertex));
            return outEdges;
        }
    }

    private static class Pixel {
        private int row; //up/down
        private int col; //right/left

        public Pixel(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getHorizontal() {
            return this.col;
        }
        public int getVertical() {
            return this.row;
        }
        public boolean equals(Object other) {
            if (!(other instanceof Pixel)) {
                return false;
            }
            Pixel o = (Pixel) other;
            return o.row == row && o.col == col;
        }
        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

}
