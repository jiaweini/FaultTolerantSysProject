public class Edge{
    public int cityA;
    public int cityB;
    public double reliability;
    public double cost;
    public Edge(int cityA, int cityB){
        this.cityA = cityA;
        this.cityB = cityB;
    }

    public int getcityA(){
        return cityA;
    }
    public int getcityB(){
        return cityB;
    }

    public double getR(){
        return reliability;
    }

    public double setR(double re){
        this.reliability=re;
        return reliability;
    }
    public double getCost() {
        return cost;
    }

    public double setCost(double cost) {
        this.cost = cost;
        return cost;
    }

}
