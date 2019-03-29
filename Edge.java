public class Edge{
    public int x;
    public int y;
    public double reliability;
    public double cost;
    public Edge(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean bigger(Edge a1, Edge a2){
        if(a1.reliability<a2.reliability){
            return true;
        }
        else{
            return false;
        }
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
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
