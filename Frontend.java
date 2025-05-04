import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of Frontend interface for P209. It will 
 * enable users to access the functionality exposed by the BackendInterface.
 *
 * Notice the organization of the methods below into pairs that generate HTML
 * strings that 1) prompt the user for input to perform a specific computation,
 * and then 2) make use of that input with the help of the backend, to compute
 * and then display the requested results.
 *
 * A webapp will be developed later in this project to integrate these html
 * snippets into a webpage that is returned custom build in response to each
 * user request.
 */
public class Frontend implements FrontendInterface{

    // backend for the Frontend to use
    private BackendInterface backend;

    /**
    * Implementing classes should support the constructor below.
    * @param backend is used for shortest path computations
    */
    public Frontend(BackendInterface backend){
        this.backend = backend;
        try {
            backend.loadGraphData("campus.dot");
        } catch (IOException e) {
            // there was a problem loading the graph data
            e.printStackTrace();
        }
    }
        
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a text input field with the id="start", for the start location
     * - a text input field with the id="end", for the destination
     * - a button labelled "Find Shortest Path" to request this computation
     * Ensure that these text fields are clearly labelled, so that the user
     * can understand how to use them.
     * @return an HTML string that contains input controls that the user can
     *         make use of to request a shortest path computation
     */
    public String generateShortestPathPromptHTML(){
        return "<input id=\"start\" placeholder=\"Start\">"
        + "<input id=\"end\" placeholder=\"End\">" +
        "<button>Find Shortest Path</button>";
    }
    
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a paragraph (p) that describes the path's start and end locations
     * - an ordered list (ol) of locations along that shortest path
     * - a paragraph (p) that includes the total travel time along this path
     * Or if there is no such path, the HTML returned should instead indicate 
     * the kind of problem encountered.
     * @param start is the starting location to find a shortest path from
     * @param end is the destination that this shortest path should end at
     * @return an HTML string that describes the shortest path between these
     *         two locations
     */
    public String generateShortestPathResponseHTML(String start, String end){
        // Get list of locations and add them to the HTML
        List<String> path = backend.findLocationsOnShortestPath(start, end);
        
        // Check if there was a path found
        if(path.isEmpty()){
            return "<p>There is no such path</p>";
        }

        // create an unordered list
        StringBuilder pathHTML = new StringBuilder("<ol>");

        // this loop adds each location found on the path with notation markers for a list item in between
        for(String location : path){
            pathHTML.append("\n<li>").append(location).append("</li>");
        }

        //add notation to end the list
        pathHTML.append("</ol>");

        // get the time for the last location
        List<Double> times = backend.findTimesOnShortestPath(start, end);
        
        // the total time needed is the sum of times between locations
        double totalTime = 0.0;

        for(double currTime : times){
            totalTime += currTime;
        }

        // return string with HTML using the previously made HTML code for the path and the descriptive paragraphs
        return "<p>The path starts at " + start + " and ends at " + end + "</p>" + pathHTML + "<p>The total travel time is " + String.valueOf(totalTime) + "</p>";
    }

    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a text input field with the id="from", for the start locations
     * - a text input field with the id="time", for the max time limit
     * - a button labelled "Reachable From Within" to submit this request
     * Ensure that these text fields are clearly labelled, so that the user
     * can understand how to use them.
     * @return an HTML string that contains input controls that the user can
     *         make use of to request a ten closest destinations calculation
     */
    public String generateReachableFromWithinPromptHTML(){
        return "<input id=\"from\" placeholder=\"From\">"
        + "<input id=\"time\" placeholder=\"Time\">" +
        "<button>Reachable From Within</button>";
    }
    
    /**
     * Returns an HTML fragment that can be embedded within the body of a
     * larger html page.  This HTML output should include:
     * - a paragraph (p) describing the start location and travel time allowed
     * - an unordered list (ul) of destinations that can be reached within
     *        that allowed travel time
     * Or if no such destinations can be found, the HTML returned should 
     * instead indicate the kind of problem encountered.
     * @param start is the starting location to search from
     * @param travelTime is the maximum number of seconds away from the start
     *        that will allow a destination to be reported
     * @return an HTML string that describes the closest destinations from the
     *         specified start location.
     */    
    public String generateReachableFromWithinResponseHTML(String start, double travelTime){
        try {
            // get list of reachable places and convert it into HTML
            List<String> places = backend.getReachableFromWithin(start, travelTime);

            // return html to report that no places were found if there are no places found
            if(places.isEmpty()){
                return "<p>No places could be found that could be reached within the time limit.</p>";
            }

            // create unordered list notation
            StringBuilder placesHTML = new StringBuilder("<ul>");

            // add each location found to be reachable and surround it with HTML that signifies a list item
            for(String place : places){
                placesHTML.append("<li>").append(place).append("</li>");
            }

            // close the list
            placesHTML.append("</ul>");

            // add in addtiional formating for explanations of the lists
            return "<p>Starting from " + start + " and within " + travelTime + " seconds, you can get to:</p>" + placesHTML;
        } catch (NoSuchElementException e) {
            return "<p>The chosen starting location was not found</p>";
        }   
    }
}