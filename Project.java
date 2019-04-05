
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Project{
	public static double expectedReliability = 0.95; //This is the desired probability
	public static String fileURL="/Users/sentinel/Desktop/git/Network-Design/src/input.txt";
	
	
	public static double[] costs;
	public static double[] reliabilities;
	public static ArrayList<Edge> stem = new ArrayList<Edge>();
	public static ArrayList<Edge> unAdded = new ArrayList<Edge>();
	public static int NodeNum;
	public static Edge[] sorted;
	public static double Rmax;
	public static int Rindex;
	public static double currentR=1; //current reliability
	public static int mstEdgeN; //number of edges for MinSpanTree
	public static ArrayList<Edge> currentEdge; //Edges currently selected

	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		ReadFile(fileURL);
		sorted = SortData(reliabilities,costs);
		stem = FindStem(NodeNum,sorted); // Minimum spanning tree: Edges
		currentR=Probability(stem);
		System.out.println("Minimum Spanning Tree Reliability: "+currentR);
		unAdded = FindUnAddEdge(stem,sorted);

		currentEdge = new ArrayList<Edge>();
		currentEdge = (ArrayList<Edge>) stem.clone();
		mstEdgeN=stem.size();

		while(currentR<expectedReliability) {

			double[] rUnadded=new double[unAdded.size()];

			for(int i =0; i<unAdded.size();i++) {

				Edge e = unAdded.get(i);

				ArrayList<Edge> cloneList = new ArrayList<Edge>();
				cloneList = (ArrayList<Edge>) currentEdge.clone();
				cloneList.add(e);

				ArrayList<Edge> emptyL = new ArrayList<Edge>();

				rUnadded[i]=findR(cloneList,emptyL);


			}
			Rindex=0;
			Rmax=rUnadded[0];
			for(int i = 1; i<rUnadded.length;i++) {
				//System.out.println(rUnadded[i]);
				if(Rmax<rUnadded[i]) {

					Rmax = rUnadded[i];
					Rindex = i;
				}
			}
			currentEdge.add(unAdded.get(Rindex));
			currentR=Rmax;
			System.out.println("an Edge has been added. Current Reliablility: "+currentR);
			unAdded.remove(unAdded.get(Rindex));
		}
		for(int i = 0; i<currentEdge.size();i++) {
			System.out.print("Edge " +i+ ": node " + currentEdge.get(i).cityA);
			System.out.println(" - node " + currentEdge.get(i).cityB);
			//System.out.println("reliable "+ currentEdge.get(i).reliability);
		}
		System.out.println("achieved reliability: " + Rmax);
	}
	
	/** reads input file
	 * 
	 * @param fileName URL
	 */
	public static void ReadFile(String fileName){
		String line = null;
		String[] splited;
		try{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine())!= null){
				if(line.contains("#")&&line.contains("nodes")){
					line = bufferedReader.readLine();
					NodeNums = Integer.parseInt(line);
				}
				else if(line.contains("#")&&line.contains("reliability")){
					line = bufferedReader.readLine();
					line = bufferedReader.readLine();
					splited = line.split("\\s+");
					reliabilities = new double[splited.length];
					for(int i =0; i<splited.length; i++){
						reliabilities[i] = Double.valueOf(splited[i]);;
					}

				}
				else if(line.contains("#")&&line.contains("cost")){
					line = bufferedReader.readLine();
					line = bufferedReader.readLine();
					splited = line.split("\\s+");
					costs = new double[splited.length];
					for(int i =0; i<splited.length; i++){
						costs[i] = Double.valueOf(splited[i]);;
					}
				
				}
			}
			bufferedReader.close();
		}catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" + fileName + "'");                
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public static Edge[] SortData (double[] reliabilities, double[] costs){
		Edge[] combined = new Edge [reliabilities.length];
		int abc=0;
		for(int i = 0; i< NodeNums;i++) {
			for(int j =i+1; j<NodeNums; j++) {
				Edge temp = new Edge(i,j);
				temp.setR(reliabilities[abc]);
				temp.setCost(costs[abc]);
				combined[abc]=temp;
				abc++;
			}
		}
		for (int i = 0; i < reliabilities.length; i++){     
			for (int j = 0; j < reliabilities.length-1; j++){ 
				if (combined[j].reliability < combined[j+1].reliability){
					Edge temp = combined[j]; 
					combined[j] = combined[j+1]; 
					combined[j+1] = temp;
				}
			}
		}
		return combined;
	}
	
	/**
	 * find MST
	 * @param numOfNodes number of nodes
	 * @param sortedEdges all possible edges
	 * @return edges that create MSTs
	 */
	public static ArrayList<Edge> FindStem(int numOfNodes, Edge[] sortedEdges){
		ArrayList<Integer> nodes = new ArrayList<>();
		nodes.add(sortedEdges[0].getcityA());
		nodes.add(sortedEdges[0].getcityB());
		ArrayList<Edge> stem = new ArrayList<>();
		stem.add(sortedEdges[0]);
		for(int i = 1; i<sortedEdges.length; i++){
			if(nodes.size() == NodeNums) break;
			int x = sortedEdges[i].getcityA();
			int y = sortedEdges[i].getcityB();
			if(nodes.contains(x) && nodes.contains(y)){
				continue;
			} else{
				if(nodes.contains(x)){
					nodes.add(y);
				}else if(nodes.contains(y)){
					nodes.add(x);
				}else{
					nodes.add(y);
					nodes.add(x);
				}
				stem.add(sortedEdges[i]);
			}
		}
		return stem;
	}
	
	/** gets edges that aren't in MST
	 * 
	 * @param stem MST edges
	 * @param sortedEdges all edges
	 * @return unadded edges
	 */
	public static ArrayList<Edge> FindUnAddEdge(ArrayList<Edge> stem, Edge[] sortedEdges) {
		ArrayList<Edge> unAddedEdge = new ArrayList<Edge>(); 
		for(Edge e:sortedEdges){
			if(!stem.contains(e)){
				unAddedEdge.add(e);
			}
		}
		return unAddedEdge;
	}
	
	/**
	 * get reliability that all given edges works
	 * @param workedEdges given edges
	 * @return probability
	 */
	public static double Probability(ArrayList<Edge> workedEdges) {
		double probability =1;

		for(Edge e:workedEdges) {
			double temp =e.getR();
			probability = temp* probability;
		}

		return probability;
	}

	
	public static double TotalCosts(ArrayList<Edge> Edges) {
		double totalCosts= 0;
		for(int i=0;i<Edges.size();i++) {
			totalCosts = totalCosts+Edges.get(i).getCost();
		}
		return totalCosts;
	}

	/**
 * find the reliability for given edges
 * @param edges edges given
 * @param additional reliable edges
 * @return reliability
 */
	@SuppressWarnings("unchecked")
	public static double findR(ArrayList<Edge> edges,ArrayList<Edge> additional){
		//System.out.println("edgesize "+edges.size()+" unaddedsize "+unAddedEdge.size());

		double rTotal=0;
		if((edges.size()+additional.size())==mstEdgeN && isConnect(edges,additional)){
			//System.out.print("MST, prob: "+Probability(edges));
			return Probability(edges);
		}else{
			if(!isConnect(edges,additional)){
				return 0;
			}
			if(edges.size()>0){
				Edge e=edges.get(0);
				ArrayList<Edge> cloneEdge= (ArrayList<Edge>) edges.clone();

				cloneEdge.remove(e);

				rTotal+= (1-e.getR())*findR(cloneEdge,additional);
				additional.add(e);
				rTotal+=e.getR()*findR(cloneEdge,additional);
				//System.out.println("rTotal"+rTotal);

				return rTotal;
			}else{
				if(isConnect(edges,additional)){
					return 1;
				}else{
					return 0;
				}
			}

		}

	}
	
/**
 * check if given edges connects all nodes
 * @param edges removeable edges
 * @param additional edges that are 100% reliable
 * @return true=connected; false=not connected
 */
	public static boolean isConnect(ArrayList<Edge> edges,ArrayList<Edge> additional){
		ArrayList<Integer> nodeConnected=new ArrayList<Integer>(); // 1=true, 0=false
		Boolean change=true;
		for(int aa=0;aa<NodeNums;aa++){
			nodeConnected.add(0);
		}
		nodeConnected.set(0,1);

		while(change){
			change=false;
			for(Edge e:edges){
				if( nodeConnected.get(e.getcityA())!= nodeConnected.get(e.getcityB()) ){
					nodeConnected.set(e.getcityA(),1);
					nodeConnected.set(e.getcityB(),1);
					change=true;
				}
			}
			for(Edge e:additional){
				if( nodeConnected.get(e.getcityA())!= nodeConnected.get(e.getcityB()) ){
					nodeConnected.set(e.getcityA(),1);
					nodeConnected.set(e.getcityB(),1);
					change=true;
				}
			}
		}
		if (nodeConnected.contains(0)){
			return false;
		}
		return true;
	}

}
