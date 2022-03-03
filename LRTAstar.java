import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;

public class LRTAstar{

    public Node prev_node=null;
    public Roads prev_road=null;

    public HashMap<String, HashMap<String,String>> nodes_connections=null;

    public Roads LRTA_Agent(Node curr_node, Graph graph, int day){

        if(curr_node.equals(graph.nodes.get(graph.goal))){
            curr_node.parent=prev_node;
            System.out.println(curr_node.name);
            return new Roads(-1 , "final_road", curr_node, graph.nodes.get(graph.goal)); //stop 
        }

        if (curr_node.getH_lrta()==0){
            
             UCS ucs = new UCS();
             ucs.ucs_route(graph, curr_node, graph.nodes.get(graph.goal) , day);
		     LinkedList<Node> path2 = ucs.getPath(graph.nodes.get(graph.goal));
             double h_tmp = path2.get(path2.size()-1).pathCost;
		     curr_node.setH_lrta(h_tmp);
		}

        /***previus***/
        if(prev_node != null){
            
           if(!nodes_connections.containsKey(prev_node.name)){
                nodes_connections.put(prev_node.name, new HashMap<String,String>());
            }
            nodes_connections.get(prev_node.name).put(prev_road.roadName, curr_node.name);
          
            double min = Double.MAX_VALUE;
            LinkedList<Node> children = prev_node.getAdj(prev_node);
         
            for (Node child : children) {

                Roads r_help = child.find_road(prev_node, child);

                double tmp = 0;
                if(nodes_connections.get(prev_node.name).containsKey(r_help.roadName)){
                     tmp = LRTA_star_cost(graph, prev_node, nodes_connections.get(prev_node.name).get(r_help.roadName), day);
                }
                else{
                    tmp = LRTA_star_cost(graph, prev_node, null, day);
                }

                if(tmp < min){
                    min = tmp;
                }	
            }

            prev_node.setH_lrta(min);
        }

        /***current***/
        if(!nodes_connections.containsKey(curr_node.name)){
                nodes_connections.put(curr_node.name, new HashMap<String,String>());
        }

        double min = Double.MAX_VALUE;

        LinkedList<Node> children = curr_node.getAdj(curr_node);
        for (Node child : children) {

            Roads r_help = child.find_road(curr_node , child );

            double tmp = 0;
           
            if(nodes_connections.get(curr_node.name).containsKey(r_help.roadName)){
            	tmp = LRTA_star_cost(graph, curr_node, nodes_connections.get(curr_node.name).get(r_help.roadName), day);
            }
            else{
            	tmp = LRTA_star_cost(graph, curr_node, null, day); 
            }

            if(tmp <= min){
                
				min = tmp;
                prev_road=r_help;
            }   
        }

        curr_node.parent = prev_node;
		prev_node = curr_node;
        return prev_road;
    }

    public double LRTA_star_cost(Graph graph, Node s_prev, String s_new, int day){

        if(s_new==null){
            UCS ucs = new UCS();
            ucs.ucs_route(graph, s_prev, graph.nodes.get(graph.goal) , day);
		    LinkedList<Node> path2 = ucs.getPath(graph.nodes.get(graph.goal));
           double h_tmp = path2.get(path2.size()-1).pathCost;
            
            return h_tmp;
        }else{
            Node s_new_node = graph.nodes.get(s_new);
            Roads r = s_new_node.find_road(s_prev, s_new_node);
            double c = graph.check_traffic(graph.actual_traffic, day, r);
            return s_new_node.getH_lrta() + c;
        }
    }

    public void init_lrtastar(Graph graph,int day){

        for (Map.Entry<String,Node> entry : graph.nodes.entrySet()){
          entry.getValue().init();
      
        }

        nodes_connections = new HashMap<>();

        LinkedList<Node> steps = new LinkedList<>(); 
        Node curr_node = graph.nodes.get(graph.start);
        
        while(true){

            if(!steps.contains(curr_node))
				steps.add(curr_node);

            Roads tmp = LRTA_Agent(curr_node,graph,day);

            if(tmp.roadName.equals("final_road"))
				break;
            
            Node tmp_node = tmp.getTarget(tmp,curr_node.name);
            curr_node = tmp_node;
        }   

        for(int i=0 ; i<steps.size();i++){   
            System.out.println(steps.get(i).name);
        }
    }

}
