import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Project{
	public static double[] costs;
	public static double[] reliabilities;
	public static ArrayList<Edge> stem = new ArrayList<Edge>();
	public static ArrayList<Edge> unAdded = new ArrayList<Edge>();
	public static int numOfNodes;
	public static Edge[] sorted;
	//public static double reliability=1;
	public static double expectedReliability = 0.8;
	public static double Rmax;
	public static int Rindex;
	public static double currentR; //current reliablility
	public static int currentEn; //current edge count
	public static int mstEdgeN; //number of edges for mst
	public static ArrayList<Edge> currentEdge;

	public static void main(String[] args) {
		ReadFile("E:/git/FaultTolerantSysProject/input.txt");
		sorted = SortData(reliabilities,costs);

		currentEdge = FindStem(numOfNodes,sorted);

		for(int i =0 ; i<stem.size();i++) {
			double temp=stem.get(i).reliability;
			currentR = temp*currentR;
		}
		unAdded = FindUnAddEdge(stem,sorted);
		//System.out.println(unAdded.size());

		while(currentR<expectedReliability) {

			double[] rUnadded=new double[unAdded.size()];

			for(int i =0; i<unAdded.size();i++) {

				Edge e = unAdded.get(i);

				ArrayList<Edge> cloneList = new ArrayList<Edge>();
				cloneList = currentEdge;
				System.out.println(currentEdge.size());
				//ArrayList<Edge> cloneList =(ArrayList<Edge>)currentEdge.clone();
				cloneList.add(e);
				rUnadded[i]=findR(cloneList);
			}
			Rindex=0;
			Rmax=rUnadded[0];
			for(int i = 1; i<rUnadded.length;i++) {
				if(Rmax<rUnadded[i]) {
					Rmax = rUnadded[i];
					Rindex = i;
				}
			}
			currentEdge.add(unAdded.get(Rindex));
			currentR=Rmax;
		}
		for(int i = 0; i<currentEdge.size();i++) {
			System.out.print("x:" + currentEdge.get(i).x);
			System.out.println("  y:" + currentEdge.get(i).y);
		}
	}
	public static void ReadFile(String fileName){
		String line = null;
		String[] splited;
		try{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine())!= null){
				if(line.contains("#")&&line.contains("nodes")){
					line = bufferedReader.readLine();
					numOfNodes = Integer.parseInt(line);
					//					System.out.println(numOfNodes);
				}
				else if(line.contains("#")&&line.contains("reliability")){
					line = bufferedReader.readLine();
					line = bufferedReader.readLine();
					splited = line.split("\\s+");
					reliabilities = new double[splited.length];
					for(int i =0; i<splited.length; i++){
						reliabilities[i] = Double.valueOf(splited[i]);;
					}
					//					for(int i= 0;i<reliabilities.length;i++) {
					//					System.out.println(reliabilities[i]);
					//					}
				}
				else if(line.contains("#")&&line.contains("cost")){
					line = bufferedReader.readLine();
					line = bufferedReader.readLine();
					splited = line.split("\\s+");
					costs = new double[splited.length];
					for(int i =0; i<splited.length; i++){
						costs[i] = Double.valueOf(splited[i]);;
					}
					//					for(int i= 0;i<reliabilities.length;i++) {
					//					System.out.println(costs[i]);
					//					}
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
		for(int i = 0; i< numOfNodes;i++) {
			for(int j =i+1; j<numOfNodes; j++) {
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
	public static ArrayList<Edge> FindStem(int numOfNodes, Edge[] sortedEdges){
		ArrayList<Integer> nodes = new ArrayList<>();
		nodes.add(sortedEdges[0].getX());
		nodes.add(sortedEdges[0].getY());
		ArrayList<Edge> stem = new ArrayList<>();
		stem.add(sortedEdges[0]);
		for(int i = 1; i<sortedEdges.length; i++){
			if(nodes.size() == numOfNodes) break;
			int x = sortedEdges[i].getX();
			int y = sortedEdges[i].getY();
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
	public static ArrayList<Edge> FindUnAddEdge(ArrayList<Edge> stem, Edge[] sortedEdges) {
		ArrayList<Edge> unAddedEdge = new ArrayList<Edge>(); 
		ArrayList<Integer> containedIndex = new ArrayList<Integer>();
		for(int i = 0 ; i<sortedEdges.length;i++) {
			for(int j = 0; j<stem.size();j++) {
				if(sortedEdges[i].equals(stem.get(j))) {
					containedIndex.add(i);
				}
			}
		}
		for(int i = 0; i<sortedEdges.length;i++) {
			if(!containedIndex.contains(i)) {
				unAddedEdge.add(sortedEdges[i]);
			}
		}
		return unAddedEdge;
	}
	public static double Probability(ArrayList<Edge> workedEdges, Edge[] sortedEdges) {
		double probability =1;
		ArrayList<Edge> unWorked= new ArrayList<Edge>();
		unWorked = FindUnAddEdge(workedEdges,sortedEdges);
		for(int i =0; i<workedEdges.size();i++) {
			double temp =workedEdges.get(i).reliability;
			probability = temp* probability;
		}
		for(int i =0; i<unWorked.size();i++) {
			double temp =1 - workedEdges.get(i).reliability;
			probability = temp * probability;
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
	public static double findR(ArrayList<Edge> edges){
		double rTotal=0;
		if(edges.size()<mstEdgeN){
			return 0;
		}else{
			if(isConnect(edges)){
				rTotal+= Probability(edges,sorted); //TODO:
			}
			for(Edge e:unAdded){

				edges.remove(e);
				unAdded.add(e);
				rTotal+=findR(edges);
			}
			return rTotal;
		}

	}


	//TODO:
	public static boolean isConnect(ArrayList<Edge> edges){
		ArrayList<Integer> nodeConnected=new ArrayList<Integer>(); // 1=true, 0=false
		Boolean change=true;
		for(int aa=0;aa<numOfNodes;aa++){
			nodeConnected.add(0);
		}
		nodeConnected.set(0,1);

		while(change){
			change=false;
			for(Edge e:edges){
				if( nodeConnected.get(e.getX())!= nodeConnected.get(e.getY()) ){
					nodeConnected.set(e.getX(),1);
					nodeConnected.set(e.getY(),1);
					change=true;
				}
			}
		}
		if (nodeConnected.contains(false)){
			return false;
		}
		return true;
	}

}