public class Roads {
   
    int cost;
    String roadName;
    Node[] nodes = new Node[2];
    boolean visited; // for traversal

    public Roads(int cost, String roadName, Node nodeL, Node nodeR) {
      this.cost = cost;
      this.roadName = roadName;
      nodes[0] = nodeL;
      nodes[1] = nodeR;
      this.visited = false;
    }

    public Node getTarget(Roads road, String start) {
    	if(start.equals(road.nodes[0].name)) 
    		return road.nodes[1];
    	else if (start.equals(road.nodes[1].name)) 
    		return road.nodes[0];
    	else
    		return null;
    	
    }

}
