import java.util.LinkedList;

public class Node {
	
    public String name;
    public boolean visited;	
    public LinkedList<Roads> roads;
    public double pathCost;
    
    public Node parent, child_fmin;
    public Roads parentRoad;
    
    double f , g , h , h_lrta;
    
    public Node(String name) {
    	this.name = name;
    	visited = false;
    	roads = new LinkedList<>();
    	pathCost = 0;
    	
    	parent = null;
    	parentRoad = null;
      child_fmin = null;

      f = 0.0;
      g = 0.0;
      h = Double.MIN_VALUE;
      h_lrta = Double.MIN_VALUE;
    }

    public Node getParent() {
      return this.parent;
     }

    public void setParent(Node parent) {
      this.parent = parent;
    }

    public double getH_lrta() {
      return this.h_lrta;
     }

    public void setH_lrta(double h_lrta) {
      this.h_lrta = h_lrta;
    }

    public double getH() {
      return this.h;
     }

    public void setH(double h) {
      this.h = h;
    }

    public double getG() {
      return this.g;
    }

    public void setG(double g) {
      this.g = g;
    }

    public double getF() {
      return this.f;
     }

    public void setF(double f) {
      this.f = f;
    }

    public Node getChild_fmin() {
      return this.child_fmin;
     }

    public void setChild_fmin(Node child_fmin) {
      this.child_fmin = child_fmin;
    }

    public boolean isVisited() {
        return visited;
    }

    public void visit() {
        visited = true;
    }

    public void unvisit() {
        visited = false;
    }

    public void init(){
        this.parent  = null;
        this.parentRoad = null;
        this.child_fmin = null;

        this.visited = false;
        this.pathCost = 0;
        this.f=0.0;
        this.h=0.0;
        this.g=0.0;
        this.h_lrta=0.0;
    }

    @Override
    public boolean equals(Object obj){
     
      if(!(obj instanceof Node)){
        return false;
      }

      Node toCompare = (Node) obj;
      
      return toCompare.name.equals(this.name);
    }

    public LinkedList<Node> getAdj(Node basic_node)
    {

        LinkedList<Node> adj = new LinkedList<>();

        for(Roads curr_road : basic_node.roads){
            
            if(curr_road.nodes[0].name.equals(basic_node.name)){
                adj.add(curr_road.nodes[1]);

            }else{
                adj.add(curr_road.nodes[0]);
            }
        }

        return adj;
    }    

   public Roads find_road(Node left , Node right)
    {
      LinkedList<Roads> roads = new LinkedList<>();
      Roads name_r = null;
      int min = Integer.MAX_VALUE;

      for(Roads curr_road : left.roads){

          if(curr_road.nodes[0].name.equals(left.name) && curr_road.nodes[1].name.equals(right.name)){ 
            roads.add(curr_road);
          }

          if(curr_road.nodes[1].name.equals(left.name) && curr_road.nodes[0].name.equals(right.name)){
            roads.add(curr_road);
          }
      }

      for(int i =0; i < roads.size(); i++){
    
        if(roads.get(i).cost < min ){
          min = roads.get(i).cost;
          name_r = roads.get(i);
        }
      }

      return name_r;
  }


}
