import java.io.*;
import java.util.*; 
import java.text.DecimalFormat;

public class Graph {
    
   public HashMap<String,Node> nodes;
   public HashMap<Integer , HashMap<String,String>> predicted_traffic;
   public HashMap<Integer , HashMap<String,String>> actual_traffic;
   
   public String start = "";
   public String goal = "";

   public double[] p ;
    
   public Graph() {
        this.nodes = new HashMap<>();
        this.predicted_traffic = new HashMap<>();
     	this.actual_traffic = new HashMap<>();
		
		this.p = new double[3];
     	this.p[0] = 0.6; //right
     	this.p[1] = 0.2; //heavier
     	this.p[2] = 0.2; //lighter
   }

    public double probConstrain () {
	   if(p[0]+p[1]+p[2] >1 || p[0]>1 || p[1]>1 || p[2]>1 || p[0]<0 || p[1]<0 || p[2]<0)
		   return -1;
	   else return p[0]+p[1]+p[2];
   }
   
   
   public void addRoad(String left_node, String  right_node, String name ,int cost) {
	    	  
	   if (!nodes.containsKey(left_node)) {
		   Node n1 = new Node(left_node);
		   nodes.put(left_node, n1);
	   }
	   
	   if (!nodes.containsKey(right_node)) {
		   Node n2 = new Node(right_node);
		   nodes.put(right_node, n2);
	   }
	   
	   Roads road = new Roads(cost, name, nodes.get(left_node), nodes.get(right_node) );
	   
	   nodes.get(left_node).roads.add(road);
	   nodes.get(right_node).roads.add(road);
   }
   
   public void printRoads() {
   
	   for (Node node : nodes.values()) {
	   
	        LinkedList<Roads> road = node.roads;

	        if (road.isEmpty()) {
	            System.out.println("Node " + node.name + " has no roads.");
	            continue;
	        }
	        
	        System.out.print("Node " + node.name + " has roads to: ");

	        for (Roads currRoad : road) {
	            System.out.print(currRoad.roadName + "(" + currRoad.cost + ") ");
	        }
	        
	        System.out.println();
	    } 
   }
   
   public void add_traffic_graph(int day, String road, String traffic, String traffic_flow){

	    if(traffic_flow=="predicted"){

	      if(!predicted_traffic.containsKey(day)){
			predicted_traffic.put(day , new HashMap<String,String>());
	      }
	      predicted_traffic.get(day).put(road,traffic);

	    }else if(traffic_flow=="actual"){

	      if(!actual_traffic.containsKey(day)){
			actual_traffic.put(day , new HashMap<String,String>());
	      }
	      actual_traffic.get(day).put(road,traffic);
	    }     
   }
   
   public void print_hashMap(int choice){
    
       if(choice==1){
         predicted_traffic.forEach((k, v) -> System.out.println(k + " : " + (v) + "\n")); 
       }else{
         actual_traffic.forEach((k, v) -> System.out.println(k + " : " + (v) + "\n")); 
       }
    }
   
    public void readFile(String filename) throws IOException {
	    
	    Scanner reader = new Scanner(new FileInputStream(filename));
	    int count_days =0;
	    boolean road_graph=true, pred=false, actual=false;

	    while (reader.hasNextLine()) {
	        String data = reader.nextLine();
	        
	        if(data.substring(0,4).equals("<Sou")){
				String[] tokens = data.split("[><]");
	            start = tokens[2]; 
	        }
	        
	        if(data.substring(0,4).equals("<Des")){
				String[] tokens = data.split("[><]");
	            goal = tokens[2]; 
	        }

	        if(data.equals("</Roads>")){
	         
	          road_graph=false;
	          pred=true;
	        }
	 
	        if(road_graph==true && data.substring(0,4).equals("Road") ){
	        
	          String[] splitted = data.split("; ");
	          addRoad(splitted[1],splitted[2],splitted[0],Integer.parseInt(splitted[3]));
	        } 

	        if(pred==true && data.equals("<Day>")){
	          count_days++;
	        }

	        if(pred==true && data.substring(0,4).equals("Road") ){
	        
	          String[] splitted = data.split("; ");
	          add_traffic_graph(count_days, splitted[0], splitted[1], "predicted");
	        }  
	       
	        if(data.equals("</Predictions>")){
	          count_days =0;
	          pred=false;
	          actual=true;
	        } 

	        if(actual==true && data.equals("<Day>")){
	          count_days++;
	        }

	        if(actual==true && data.substring(0,4).equals("Road") ){
	        
	          String[] splitted = data.split("; ");
	          add_traffic_graph(count_days, splitted[0], splitted[1], "actual");
	        }  
	    }
	        
	    reader.close();
	    return;
	}

	public double check_traffic(HashMap<Integer , HashMap<String,String>> traffic, int day, Roads road){

		for (Map.Entry<Integer , HashMap<String,String>> entry : traffic.entrySet())
		{    
		  if(day == entry.getKey()){

			  for (Map.Entry<String,String> entry2 : entry.getValue().entrySet())
			  {	  		
				  		if(road==null)
							return 0;

					  	if(entry2.getKey().equals(road.roadName)){

						  if(entry2.getValue().equals("normal")){
							double c = (p[0]*road.cost) + (p[1]*road.cost*1.25) + (p[2]*0.9*road.cost);
							return Double.parseDouble(new DecimalFormat("#.000").format(c));

						  }else if(entry2.getValue().equals("heavy")){
							double c = road.cost*1.25;
							double cc = (p[0]*c) + (p[1]*c*1.25) + (p[2]*0.9*c);
							return Double.parseDouble(new DecimalFormat("#.000").format(cc));

						  }else{
							double c = road.cost*0.9;
							double cc = (p[0]*c) + (p[1]*c*1.25) + (p[2]*0.9*c);
							return Double.parseDouble(new DecimalFormat("#.000").format(cc));
						  }

					  }
			  }
		  }
        }
		return -1;
	}

	public double check_traffic_real(HashMap<Integer , HashMap<String,String>> traffic, int day, Roads road){

		for (Map.Entry<Integer , HashMap<String,String>> entry : traffic.entrySet())
		{    
		  if(day == entry.getKey()){

			  for (Map.Entry<String,String> entry2 : entry.getValue().entrySet())
			  {	  		
				  		if(road==null)
							return 0;

					  	if(entry2.getKey().equals(road.roadName)){

						  if(entry2.getValue().equals("normal")){
							double c = road.cost;
							return Double.parseDouble(new DecimalFormat("#.000").format(c));

						  }else if(entry2.getValue().equals("heavy")){
							double c = 1.25*road.cost;
							return Double.parseDouble(new DecimalFormat("#.000").format(c));

						  }else{
							double c = 0.9*road.cost;
							return Double.parseDouble(new DecimalFormat("#.000").format(c));
						  }

					  }
			  }
		  }
        }
		return -1;
	}

public void checkProb(double d) {
		if(this.probConstrain()==-1)
			return;
		if(d>1 && p[1]<=1) {  		//real cost is greater than pred -> heavier than we thought
			if(p[2]-0.01<0 && p[0]-0.01>=0) {
			p[0]=p[0]-0.01;
			p[1]=p[1]+0.01;
			
			}else if(p[2]-0.005>0 && p[0]-0.005>0) {
				p[1]=p[1]+0.01;
				p[2]=p[2]-0.005;
				p[0]-=0.005;
			}else if(p[0]-0.01<0 && p[2]-0.01>=0) {
				p[2]=p[2]-0.01;
				p[1]=p[1]+0.01;
			}
			return;
		}else if (d<-1 && p[2]<=1 ) {	//real cost is smaller than pred ->lower than we thought
			if(p[1]-0.01<0 && p[0]-0.01>=0) {
				p[0]=p[0]-0.01;
				p[2]=p[2]+0.01;
				}else if(p[1]-0.005>0 && p[0]-0.005>0) {
					p[2]=p[2]+0.01;
					p[1]=p[1]-0.005;
					p[0]-=0.005;
				}else if(p[0]-0.01<0 && p[1]-0.01>=0) {
					p[1]=p[1]-0.01;
					p[2]=p[2]+0.01;
				}
			return;
		}else if (d>=-1 && d<=1 && p[0]<=1 && p[2]-0.005>=0 && p[1]-0.005>=0){ //real cost is just right
			p[0]=p[0]+0.01;
			p[1]=p[1]-0.005;
			p[2]=p[2]-0.005;
			return;
		}
		
	}
   
    public static void main(String[] args) throws IOException{
	    
	    Graph graph = new Graph();

	    if(args.length < 1) {
	      System.out.println("Error, usage: java ClassName inputfile");
		    System.exit(1);
	    }
	   
	    graph.readFile(args[0]); 

		double ida_cost=0, ucs_cost=0, realNum[]= {0,0};
		FileWriter myWriter = new FileWriter("results.txt");

					  
		for(int i =1; i <=80; i++){

			System.out.println("Day "+i);
			myWriter.write("\n\nDay "+i);

			/*********UCS********/
			long startTime = System.nanoTime();
			UCS ucs = new UCS();
			ucs.ucs_route(graph, graph.nodes.get(graph.start), graph.nodes.get(graph.goal) , i);
			LinkedList<Node> path = ucs.getPath(graph.nodes.get(graph.goal));
			double pred_ucs = path.getLast().pathCost;
			long stopTime = System.nanoTime();

			realNum[0] = ucs.printPath(myWriter, graph, path, i, (stopTime - startTime));
			ucs_cost = ucs_cost + realNum[0]; //average

			/*********ida_star********/
			startTime = System.nanoTime();
			IDAstar a = new IDAstar();
			Node destination = a.idastar_search(graph, i);
			LinkedList<Node> path2 = a.getPath(graph, graph.nodes.get(graph.start), i);
			stopTime = System.nanoTime();

			realNum[1]= a.print_ida_star(myWriter, graph, stopTime-startTime , path2 );
			ida_cost = ida_cost +realNum[1]; //average


			/*		 	LRTAstar l = new LRTAstar();
						l.init_lrtastar(graph,day);  */

			/* After running the algorithms we compare the real and the prediction costs */
			/* in order to chage the probabilities p1, p2, p3 to achieve greater similarity*/
			/* real cost - perdicted cost */
			graph.checkProb(realNum[0] - pred_ucs);
			graph.checkProb(realNum[1] - path2.getLast().pathCost);
		}

		ida_cost = Double.parseDouble(new DecimalFormat("#.000").format(ida_cost/80));
		ucs_cost = Double.parseDouble(new DecimalFormat("#.000").format(ucs_cost/80));
		
		System.out.println("Average daily real cost :");
		myWriter.write("\n\nAverage daily real cost :\n");

		System.out.println("	IDA*: " + ida_cost +"\n	UCS: " + ucs_cost);
		myWriter.write("	IDA*: " + ida_cost +"\n	UCS: " + ucs_cost);

		

		myWriter.close();

	}

}
