// === CS400 File Header Information ===
// Name: Nakul Solai
// Email: solai@wisc.edu
// Group and Team: P2.1915

import java.util.PriorityQueue;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
      extends BaseGraph<NodeType, EdgeType>
      implements GraphADT<NodeType, EdgeType> {

  /**
   * While searching for the shortest path between two nodes, a SearchNode
   * contains data about one specific path between the start node and another
   * node in the graph. The final node in this path is stored in its node
   * field. The total cost of this path is stored in its cost field. And the
   * predecessor SearchNode within this path is referened by the predecessor
   * field (this field is null within the SearchNode containing the starting
   * node in its node field).
   * 
   * SearchNodes are Comparable and are sorted by cost so that the lowest cost
   * SearchNode has the highest priority within a java.util.PriorityQueue.
   */
  protected class SearchNode implements Comparable<SearchNode> {
    public Node node;
    public double cost;
    public SearchNode predecessor;

    public SearchNode(Node node, double cost, SearchNode predecessor) {
      this.node = node;
      this.cost = cost;
      this.predecessor = predecessor;
    }

    public int compareTo(SearchNode other) {
      if (cost > other.cost)
        return +1;
      if (cost < other.cost)
        return -1;
      return 0;
    }
  }

  /**
   * Constructor that sets the map that the graph uses.
   */
  public DijkstraGraph() {
    super(new HashtableMap<>());
  }

  /**
   * This helper method creates a network of SearchNodes while computing the
   * shortest path between the provided start and end locations. The
   * SearchNode that is returned by this method is represents the end of the
   * shortest path that is found: it's cost is the cost of that shortest path,
   * and the nodes linked together through predecessor references represent
   * all of the nodes along that shortest path (ordered from end to start).
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return SearchNode for the final end node within the shortest path
   * @throws NoSuchElementException when no path from start to end is found
   *                                or when either start or end data do not
   *                                correspond to a graph node
   */
  protected SearchNode computeShortestPath(NodeType start, NodeType end) {
    // implement in step 5.3
    
    // if the nodes are not in the graph, throw the error
    if (!this.nodes.containsKey(start) || !this.nodes.containsKey(end))
      throw new NoSuchElementException("One or both of the nodes are not in the graph.");

    // searchPaths is a priority queue for storing the shortest paths to connected nodes
    // SearchNode stores a node, the cost from start to this node, and the predecessor SearchNode
    PriorityQueue<SearchNode> searchPaths = new PriorityQueue<>();
    // visited is a set of hash set of nodes that are visited during Dijkstra's algorithm.
    // If a node is in the set, we know it's cost from start to this node is the smallest possible.
    HashtableMap<NodeType, SearchNode> visited = new HashtableMap<>();

    // curPath is the current path to visit in the algorithm.
    SearchNode curPath = new SearchNode(this.nodes.get(start), 0, null);
    // our fist path to add to the priority queue is start node
    searchPaths.add(curPath);

    // if the priority queue is empty, there are no more connected nodes left, and we're done.
    while (!searchPaths.isEmpty()) {

      // remove the least costly node from the priority queue
      curPath = searchPaths.remove();

      // if the last node in curPath is the end node, 
      // return curPath before potentially adding new paths to the priority queue.
      if (curPath.node.data.equals(end))
        // curPath contains the information of the shortest path from start to end
        return curPath;

      // if we already visited this node, we don't add it to the visited set because there is already a shorter path 
      if (!visited.containsKey(curPath.node.data)) {
        // else add it to the visited set because we determined it was the shortest remaining path.
        visited.put(curPath.node.data, curPath);
      
        // for each edge that we can get to from our current node
        for (Edge nextEdge : curPath.node.edgesLeaving) {

          // if the edge's successor node is in the visited set, don't add it to searchPaths.
          if (!visited.containsKey(nextEdge.successor.data)) {

            // if the edge isn't in the visited set, add it to the priority queue.
            // SearchNode's cost = the cost of curPath (the cost we just added to the visited set)
            //  plus the weight of this edge of this edge.
            searchPaths.add(new SearchNode(
                  nextEdge.successor, curPath.cost + nextEdge.data.doubleValue(), curPath));
            
            // for SearchNodes that have the same successor, 
            //  the least costly one will be added to the visited set, & the other one won't.
          }
        }
      }
    }

    // if the end node is never found in the loop, start and end aren't connected
    throw new NoSuchElementException("No path from start to end was found.");

  }

  /**
   * Returns the list of data values from nodes along the shortest path
   * from the node with the provided start value through the node with the
   * provided end value. This list of data values starts with the start
   * value, ends with the end value, and contains intermediary values in the
   * order they are encountered while traversing this shorteset path. This
   * method uses Dijkstra's shortest path algorithm to find this solution.
   * 
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return list of data item from node along this shortest path
   */
  public List<NodeType> shortestPathData(NodeType start, NodeType end) {
    // implement in step 5.4

    // this is the list to return
    LinkedList<NodeType> pathNodes = new LinkedList<>();
    // this is the last node not pushed onto the linked list
    SearchNode reversed = this.computeShortestPath(start, end);

    // we keep adding nodes until start, which doesn't have a predeccesor
    while(reversed != null) {
      // add the node to the beginning of the list
      pathNodes.add(0, reversed.node.data);
      // go to the previous node, closer to start
      reversed = reversed.predecessor;
    }

    return pathNodes;
	}

  /**
   * Returns the cost of the path (sum over edge weights) of the shortest
   * path freom the node containing the start data to the node containing the
   * end data. This method uses Dijkstra's shortest path algorithm to find
   * this solution.
   * 
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return the cost of the shortest path between these nodes
   */
  public double shortestPathCost(NodeType start, NodeType end) {
    // implement in step 5.4
    // the cost of the path from the start to the edge
    return this.computeShortestPath(start, end).cost;
  }


  // implement 3+ tests in step 4.1

  private DijkstraGraph<Character, Integer> lectureGraph;

  @BeforeEach
  public void setBase() {
    // initialize graph from lecture to use in test methods
    lectureGraph = new DijkstraGraph<>();

    final char[] DEMONODES = {'A', 'B', 'D', 'E', 'F', 'G', 'H', 'I', 'L', 'M'};
    for (char dode : DEMONODES)
      lectureGraph.insertNode(dode);

    lectureGraph.insertEdge('A', 'B', 1);
    lectureGraph.insertEdge('A', 'M', 5);
    lectureGraph.insertEdge('A', 'H', 7);

    lectureGraph.insertEdge('B', 'M', 3);

    lectureGraph.insertEdge('M', 'E', 3);
    lectureGraph.insertEdge('M', 'F', 4);
    lectureGraph.insertEdge('M', 'I', 4);

    lectureGraph.insertEdge('I', 'H', 2);
    lectureGraph.insertEdge('I', 'D', 1);

    lectureGraph.insertEdge('F', 'G', 9);

    lectureGraph.insertEdge('D', 'F', 4);
    lectureGraph.insertEdge('D', 'G', 2);
    lectureGraph.insertEdge('D', 'A', 7);

    lectureGraph.insertEdge('H', 'L', 2);
    lectureGraph.insertEdge('H', 'I', 2);
    lectureGraph.insertEdge('H', 'B', 6);

    lectureGraph.insertEdge('G', 'H', 9);
    lectureGraph.insertEdge('G', 'L', 7);
    lectureGraph.insertEdge('G', 'A', 4);
  }

  @Test
  public void testDtoI() {
    // assert the shortest path from D to I is [D -> G -> H -> I]
    assertEquals(List.of('D', 'G', 'H', 'I'),
          lectureGraph.shortestPathData('D', 'I'));
    // the cost is 13
    assertEquals(13, lectureGraph.shortestPathCost('D', 'I'));
  }

  @Test
  public void testAtoG() {
    // assert the shortest path from A to G is [A -> B -> M -> I -> D -> G]
    assertEquals(List.of('A', 'B', 'M', 'I', 'D', 'G'),
          lectureGraph.shortestPathData('A', 'G'));
    // this path costs 11
    assertEquals(11, lectureGraph.shortestPathCost('A', 'G'));
  }

  @Test
  public void testLtoE() {
    // because L and E are not connected, shortestPathData and shortestPathCost should throw
    // NoSuchElementExceptions
    assertThrows(NoSuchElementException.class, () -> {
      lectureGraph.shortestPathData('L', 'E');
    } );
    assertThrows(NoSuchElementException.class, () -> {
      lectureGraph.shortestPathCost('L', 'E');
    } );
  }

}
