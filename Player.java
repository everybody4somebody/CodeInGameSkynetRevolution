import java.util.*;
import java.io.*;
import java.math.*;
import java.awt.Point;

class QueueNode implements Comparable<QueueNode>{
    
    private int distanceFromGate;
    private int distanceTravelled;
    private int index;
    
    public QueueNode(int index, int distanceFromGate, int distanceTravelled){
        this.distanceFromGate = distanceFromGate;
        this.distanceTravelled = distanceTravelled;
        this.index = index;
    }
    
    public int getIndex(){
        return index;
    }
    
    public int getDistanceTravelled(){
        return distanceTravelled;
    }

    public Integer getPriority(){
        return distanceFromGate + distanceTravelled;
    }
    
    public int compareTo(QueueNode queueNode){
        return this.getPriority().compareTo(queueNode.getPriority());
    }
}
 
class Player {

    public static void main(String args[]) 
    {
        Scanner in = new Scanner(System.in);
        int numNodes = in.nextInt(); // the total number of nodes in the level, including the gateways
        int numLinks = in.nextInt(); // the number of links
        int numExits = in.nextInt(); // the number of exit gateways        
        //generate a list of neighbors
        LinkedList<Integer>[] neighbors = new LinkedList[numNodes];
        generateNeighbors(in, neighbors, numLinks);
        //designate nodes as gateways
        boolean[] exits = new boolean[numNodes];
        generateExits(in, exits, numExits);
        //create an array of distance from gateway values
        int[] distanceFromGate = new int[numNodes];
        generateDistances(distanceFromGate, numNodes, exits, neighbors);
        
        // game loop
        while (true) 
        {
            int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn
            if(distanceFromGate[SI] == 1)
            {
                System.err.println("DISTANCE = 1");
                Point cut = gatewaySearch(neighbors, exits, distanceFromGate, SI, 1);
                System.err.println(cut);
                cutNeighbors(neighbors, cut);
                generateDistances(distanceFromGate, numNodes, exits, neighbors);
                System.out.println((int)cut.getX() + " " + (int)cut.getY());
            } 
            else
            {
                System.err.println("DISTANCE != 1");
                Point cut = gatewaySearch(neighbors, exits, distanceFromGate, SI, 2);
                System.err.println(cut);
                cutNeighbors(neighbors, cut);
                generateDistances(distanceFromGate, numNodes, exits, neighbors);
                System.out.println((int)cut.getX() + " " + (int)cut.getY());
            }
        }
    }
    
    public static Point gatewaySearch(LinkedList<Integer>[] neighbors, boolean[] exits, int[] distanceFromGate, int startNode, int connections)
    {
        int destination = startNode;
        int gate = startNode;
        int weight = 10;
        PriorityQueue<QueueNode> searchQueue = new PriorityQueue<>();
        searchQueue.add(new QueueNode(startNode, distanceFromGate[startNode] * weight, 0));
        LinkedList<Integer> visited = new LinkedList<>();

        while(!searchQueue.isEmpty())
        {
            QueueNode queueNode = searchQueue.poll();
            int currentNode = queueNode.getIndex();
            int currentDistanceTravelled = queueNode.getDistanceTravelled();
            Integer[] currentNeighbors = neighbors[currentNode].toArray(new Integer[0]);
            int exitCount = 0;
            for(int i = 0; i < currentNeighbors.length; i++)
            {
                if(!visited.contains(currentNeighbors[i]))
                {
                    if(exits[currentNeighbors[i]])
                    {
                        exitCount++;
                        destination = currentNode;
                        gate = currentNeighbors[i];
                    }
                    else
                    {
                        searchQueue.add(new QueueNode(currentNeighbors[i], distanceFromGate[currentNeighbors[i]] * weight, currentDistanceTravelled + 1));
                        visited.add(currentNeighbors[i]);
                    }
                }
            }
            if(exitCount > connections - 1)
            {
                searchQueue.clear();
                destination = currentNode;
            }
        }
        return new Point(destination, gate);
    }
    
    public static boolean cutNeighbors(LinkedList<Integer>[] neighbors, Point cut)
    {
        neighbors[(int)cut.getX()].remove((Object)(int)cut.getY());
        neighbors[(int)cut.getY()].remove((Object)(int)cut.getX());
        return true;
    }
    
    public static boolean generateNeighbors(Scanner in, LinkedList<Integer>[] neighbors, int numLinks)
    {
        for (int i = 0; i < numLinks; i++) 
        {
            int N1 = in.nextInt(); // N1 and N2 defines a link between these nodes
            int N2 = in.nextInt();
            if(neighbors[N1] == null){
                neighbors[N1] = new LinkedList();
            }
            neighbors[N1].add(N2);
            if(neighbors[N2] == null){
                neighbors[N2] = new LinkedList();
            }
            neighbors[N2].add(N1);
        }
        return true;
    }
    
    public static boolean generateExits(Scanner in, boolean[] exits, int numExits)
    {
        for (int i = 0; i < numExits; i++) 
        {
            int EI = in.nextInt(); // the index of a gateway node    
            exits[EI] = true;
        }
        return true;
    }
    
    public static boolean generateDistances(int[] distanceFromGate, int numNodes, boolean[] exits, LinkedList<Integer>[] neighbors)
    {
        boolean[] visited = new boolean[numNodes];
        Queue<Integer> toVisit = new LinkedList<>();
        for(int i = 0; i < exits.length; i++)
        {
            if(exits[i])
            {
                toVisit.add(i);
                distanceFromGate[i] = 0;
                visited[i] = true;
            }
        }
        while(!toVisit.isEmpty())
        {
            int currentNode = toVisit.remove();
            Integer[] currentNeighbors = neighbors[currentNode].toArray(new Integer[0]);
            for(int i = 0; i < currentNeighbors.length; i++)
            {
                int neighborNode = currentNeighbors[i];
                if(!visited[neighborNode])
                {
                    toVisit.add(neighborNode);
                    distanceFromGate[neighborNode] = distanceFromGate[currentNode] + 1;
                    visited[neighborNode] = true;
                }
            }
        }
        return true;
    }
}
