import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions .*;

/**
 * Class to test the Frontend class for P209 in CS400 using the placeholder Backend and Graph
 */
public class FrontendTests {
    
    /**
     *Test that the generateShortestPathPromptHTML and generateReachableFromWithinPromptHTML return strings with HTML code with the required content
     */
    @Test
    public void frontendTest1(){
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend_Placeholder backend = new Backend_Placeholder(graph);
        Frontend testFront = new Frontend(backend);

        // ensure that both methods have an input tag since an input tag is needed to input text
        String shortestHTML = testFront.generateShortestPathPromptHTML();
        assertTrue(shortestHTML.strip().contains("<input"));
        
        // also check for some keywords that are required 
        assertTrue(shortestHTML.contains("Find Shortest Path"));
        assertTrue(shortestHTML.strip().contains("id=\"start\""));
        assertTrue(shortestHTML.strip().contains("id=\"end\""));

        String reachableHTML = testFront.generateReachableFromWithinPromptHTML();
        assertTrue(reachableHTML.strip().contains("<input"));
        assertTrue(reachableHTML.contains("Reachable From Within"));
        assertTrue(reachableHTML.strip().contains("id=\"from\""));
        assertTrue(reachableHTML.strip().contains("id=\"time\""));
    }

    /**
     *Test that generateShortestPathResponseHTML returns what is expected given the placeholders
     */
    @Test
    public void frontendTest2(){
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend_Placeholder backend = new Backend_Placeholder(graph);
        Frontend testFront = new Frontend(backend);
        
        // Using the placeholder methods, go from start to finish. This should give us a list of all 3 locations
        String pathResponse = testFront.generateShortestPathResponseHTML("Union South", "Weeks Hall for Geological Sciences");
        assertTrue(pathResponse.contains("Union South"));
        assertTrue(pathResponse.contains("Computer Sciences and Statistics"));
        assertTrue(pathResponse.contains("Weeks Hall for Geological Sciences"));
        
        // the response should have the appropriate HTML tags
        assertTrue(pathResponse.contains("ol") & pathResponse.strip().contains("</ol>"));
        assertTrue(pathResponse.contains("li") & pathResponse.strip().contains("</li>"));

        // Test when there is an empty list returned from the backend (based on the placeholder methods)
        String nullPathResponse = testFront.generateShortestPathResponseHTML("utter", "nonsense");
        // there is no specified behavior other than to describe the problem with HTML code
        assertTrue(nullPathResponse != null);
    }

    /**
     * Test that the generateReachableFromWithinResponseHTML method returns what is expected given the placeholders
     */
    @Test
    public void frontendTest3(){
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend_Placeholder backend = new Backend_Placeholder(graph);
        Frontend testFront = new Frontend(backend);

        // parameters don't matter for the placeholder implementation
        String reachResp = testFront.generateReachableFromWithinResponseHTML("lalala", 16);
        
        // the string should contain an unordered list, paragraph, and the location names
        assertTrue(reachResp.contains("ul") & reachResp.strip().contains("</ul>"));
        assertTrue(reachResp.contains("p") & reachResp.strip().contains("</p>"));
        assertTrue(reachResp.contains("Union South") & reachResp.contains("Computer Sciences and Statistics") & reachResp.contains("Weeks Hall for Geological Sciences"));
        assertTrue(reachResp.contains("lalala") & reachResp.contains("16"));
    }
}
