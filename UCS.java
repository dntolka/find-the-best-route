import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.*;
import java.text.DecimalFormat;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class UCS {

	public void ucs_route(Graph graph, Node source, Node dest, int day) {

		for (Map.Entry<String,Node> entry : graph.nodes.entrySet()){
          entry.getValue().init();
      	}
		
		PriorityQueue<Node> queue = new PriorityQueue<>(new Comparator<Node>() {
			//overide compare method for priorityQ
	        public int compare(Node right, Node left) {
	        	if(right.pathCost > left.pathCost)
					return 1;
				else if (right.pathCost < left.pathCost)
					return -1;
				else
					return 0;
	        }
	    });
		
		queue.add(source);
		Set<Node> explored = new HashSet<>();
		boolean f = false;
		do {
		Node curr = queue.poll();
		explored.add(curr);
		
		if (curr.name == dest.name) {
			f = true;
		}
		
		for(Roads road : curr.roads) {
			Node temp = road.getTarget(road, curr.name);
			double cost = graph.check_traffic(graph.predicted_traffic, day, road);
			
			//add node to queue if node has not been explored
			if(!explored.contains(temp) && !queue.contains(temp)){
				temp.pathCost = curr.pathCost + cost;
				temp.parent = curr;
				queue.add(temp);
				temp.parentRoad =road;
			}
				
			//we found a shorter path
			else if((queue.contains(temp))&&(temp.pathCost>(curr.pathCost+cost))){
				temp.parent=curr;
				temp.pathCost = curr.pathCost + cost;
				queue.remove(temp);
				queue.add(temp);
				temp.parentRoad = road;
			}
			}
		
		}while(!queue.isEmpty() && f==false);
	}
	
	public LinkedList<Node> getPath(Node dest){
		LinkedList<Node> path = new LinkedList<Node>();
		for(Node node = dest; node!=null; node = node.parent)
			path.add(node);
		
		Collections.reverse(path);

		return path;
	}

	public double printPath(FileWriter myWriter, Graph graph, LinkedList<Node> path, int day, long time) {
		
		double realCost = 0;
		double nextCost = 0;
		double predCost =0;
		LinkedList<Node> node = path;
		
		try {
			System.out.println("UCS:");
			myWriter.write("\nUCS:\n");
			System.out.println("	Visited Nodes: "+ path.size());
			myWriter.write("	Visited Nodes: "+ path.size()+"\n");
			System.out.println("	Execution Time: "+time+" ns" ); 
			myWriter.write("	Execution Time: "+time+" ns" +"\n");

			System.out.print("	Path: ");
			myWriter.write("	Path: ");

			predCost = node.get(node.size()-1).pathCost;

			for (int i=0;i<node.size()-1;i++) {
				
				nextCost = node.get(i+1).pathCost - node.get(i).pathCost;
				nextCost = Double.parseDouble(new DecimalFormat("#.000").format(nextCost));
				System.out.print(node.get(i).name + "("+ nextCost +")" +" -> ");
				myWriter.write(node.get(i).name + "("+ nextCost +")" +" -> ");

				realCost = realCost +graph.check_traffic_real(graph.actual_traffic, day, node.get(i).parentRoad); 
			}
			
			System.out.print(node.get(node.size()-1).name);	
			myWriter.write(node.get(node.size()-1).name);	

			realCost = realCost +graph.check_traffic_real(graph.actual_traffic, day, node.get(node.size()-1).parentRoad); 
			
			System.out.println("\n	Predicted Cost: "+ Double.parseDouble(new DecimalFormat("#.000").format(predCost)));
			myWriter.write("\n	Predicted Cost: "+ Double.parseDouble(new DecimalFormat("#.000").format(predCost))+"\n");
			System.out.println("	Real Cost: "+ Double.parseDouble(new DecimalFormat("#.000").format(realCost)));
			myWriter.write("	Real Cost: "+ Double.parseDouble(new DecimalFormat("#.000").format(realCost))+"\n");
		
		}catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

		return realCost;
	}

}
