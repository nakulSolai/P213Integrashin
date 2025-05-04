import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;


public class Backend implements BackendInterface {

  protected GraphADT<String, Double> graph;

  /**
   * Implementing classes should support the constructor below.
   * 
   * @param graph object to store the backend's graph data
   */
  public Backend(GraphADT<String,Double> graph) {
    this.graph = graph;
  }

  @Override
  public void loadGraphData(String filename) throws IOException {
    String edge;
    String[] nodes;

    // remove every node
    List<String> everyNode = this.graph.getAllNodes();
    for (String n : everyNode)
      this.graph.removeNode(n);

    try {
      File diagraph = new File(filename);
      Scanner userInterface = new Scanner(diagraph);
      userInterface.nextLine();

      // exract the first edge (one the second line)
      String line = userInterface.nextLine();

      // exit the loop at the last line
      while(!line.trim().equals("}") && userInterface.hasNextLine()) {
        // nodes should have 5 elements 
        nodes = line.split("\"");

        // nodes[1] is the first node, and if it's not in the graph, insert it
        if (!this.graph.containsNode(nodes[1])) this.graph.insertNode(nodes[1]);
        // nodes[2] is the second node, and if it's not if the graph, insert it
        if (!this.graph.containsNode(nodes[3])) this.graph.insertNode(nodes[3]);

        nodes[4] = nodes[4].trim();
        // the edge weight is between "[seconds=" and "];"
        edge = nodes[4].substring(9, nodes[4].length() - 2);

        // if the edge isn't in the graph, insert it
        if (!this.graph.containsEdge(nodes[1], nodes[3])) 
          this.graph.insertEdge(nodes[1], nodes[3], Double.valueOf(edge));

        // go to the next line
        line = userInterface.nextLine();
      }

      userInterface.close();
    }
    catch (IOException ioe) {
      // throw IOException if error is caught
      throw ioe;
    }
  }

  @Override
  public List<String> getListOfAllLocations() {
    // call getAllNodes(), which shouldn't contain duplicates
    return this.graph.getAllNodes();
  }

  @Override
  public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
    try {
      // call shortestPathData, which can throw a NoSuchElementException
      return this.graph.shortestPathData(startLocation, endLocation);
    } 
    catch (NoSuchElementException nsee) {

      // if the start and end locations are in the graph, and the error was thrown,
      // that means the nodes are not connected, so we return an empty list
      if (this.graph.containsNode(startLocation) && this.graph.containsNode(endLocation))
        return Collections.emptyList();
      // else we still return empty list
      else
        return Collections.emptyList();
    }
  }

  @Override
  public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
    // linked list containing every edge's weight
    List<Double> weightList = new LinkedList<>();
    // the list of nodes representing the shortest path from start to end
    List<String> shortestPath = this.findLocationsOnShortestPath(startLocation, endLocation);

    // for each pair of nodes, add the corresponding edge weight to weightList
    for (int i = 0; i < shortestPath.size() - 1; i++)
      weightList.add(this.graph.getEdge(shortestPath.get(i), shortestPath.get(i + 1)));

    // retun the list
    return weightList;
  }

  @Override
  public List<String> getReachableFromWithin(String startLocation, double travelTime) throws NoSuchElementException {
    // throw exception in this case
    if (!graph.containsNode(startLocation))
      throw new NoSuchElementException("Start location not in graph.");

    List<String> reachable = new ArrayList<>();
    List<String> allLocs = graph.getAllNodes();
    double cost;

    for (String curLoc : allLocs) {
      try {
        // if location is the same as startLocation, don't add it to the reachle list
        if (!curLoc.equals(startLocation)) {
          // the travel time is the cost of the shortest path from start to current location
          cost = this.graph.shortestPathCost(startLocation, curLoc);
          // if it's smaller than the given travelTime, add the location to the list
          if (cost <= travelTime) reachable.add(curLoc);
        }
      } 
      catch (NoSuchElementException nsee) {
        // Ignore locations that aren't reachable from start
      }
    }
    
    // return the list of reachable locations
    return reachable;
  }

}
