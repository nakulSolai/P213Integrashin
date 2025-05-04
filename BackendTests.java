import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BackendTests {

  protected BackendInterface backend;

  @BeforeEach
  public void createInstance() {
    // create a new backend for each test
    backend = new Backend(new Graph_Placeholder());
  }

  @Test
  public void roleTest1() throws IOException {
    // load the graph data
    backend.loadGraphData("campus.dot");

    // check if the graph contains the expected nodes
    assertTrue(backend.getListOfAllLocations().contains("Union South"));
    assertTrue(backend.getListOfAllLocations().contains("Computer Sciences and Statistics"));
    assertTrue(backend.getListOfAllLocations().contains("Weeks Hall for Geological Sciences"));
  }

  @Test
  public void roleTest2() throws IOException {
    // load the graph data
    backend.loadGraphData("campus.dot");

    // find the shortest path between two locations
    List<String> path = backend.findLocationsOnShortestPath("Union South", "Weeks Hall for Geological Sciences");

    // check if the path contains the expected locations
    assertTrue(path.contains("Union South"));
    assertTrue(path.contains("Computer Sciences and Statistics"));
    assertTrue(path.contains("Weeks Hall for Geological Sciences"));
  }

  @Test
  public void roleTest3() throws IOException {
    // load the graph data
    backend.loadGraphData("campus.dot");

    // find the reachable locations within a certain time
    List<String> reachable = backend.getReachableFromWithin("Union South", 5.0);

    // check if the reachable locations are correct
    assertTrue(reachable.contains("Computer Sciences and Statistics"));
    assertTrue(reachable.contains("Weeks Hall for Geological Sciences"));
    assertFalse(reachable.contains("Union South"));
  }

  @Test
  public void roleTest4() throws IOException {
    backend.loadGraphData("campus.dot");

    assertEquals(backend.findTimesOnShortestPath("Union South", "Computer Sciences and Statistics"), Arrays.asList(1.0));

    assertEquals(backend.findTimesOnShortestPath("Union South", "Weeks Hall for Geological Sciences"), Arrays.asList(1.0, 2.0));

    assertEquals(backend.findTimesOnShortestPath("Computer Sciences and Statistics", "Weeks Hall for Geological Sciences"), Arrays.asList(2.0));
  }
}
