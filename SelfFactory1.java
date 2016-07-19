
import java.lang.Math; 
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


public class HelloWorld {
   public static void main(String[] args) {
      System.out.println("HELLO!");
    
      /*Node inst = Node.f("myInstance");
      System.out.println(inst.getID());
    
      Node anotherInst = Node.f("anotherInstance");
      Node yetAnotherInst = Node.f("yetAnotherInstance")
        .f("andMore").f("andEvenMore!").to("someModule");
    
      anotherInst.listInstances();  */
   }
}

public class Node {
   private String nodeID;

   public String getID() { return nodeID; }
   public Node id(String id) { 
      this.nodeID = id; 
      return this; 
   }

   private ArrayList<Node> instances;

   private Method actionOnInput;

   private HashMap<String, Object> inputs;

   private Node upstreamNode;
   private ArrayList<Node> downstreamNodes;

   private Node() {
      instances = new ArrayList<Node>();
      inputs = new HashMap<String, Object>();
      downstreamNodes = new ArrayList<Node>();
   }
  
   protected void addInstance(Node instance) {
      instances.add(instance);
   }
  
   // Node factory constructor
   public static Node in(Method action) {
      Node instance = new Node();  // TO DO: change to getClass.getInstance

      instance.upstreamNode = null;
      instance.actionOnInput = action;
      instance.addInstance(instance);
      for (Parameter input : action.getParameters()) {
         instance.inputs.put(input.getName(), input);
      }

      return instance;
   }

   public Node to(Method action) {
      Node instance = new Node();  // TO DO: change to getClass.getInstance

      instance.addInstance(instance);
      instance.upstreamNode = this;
      instance.actionOnInput = action;

      for (Parameter input : action.getParameters()) {
         instance.inputs.put(input.getName(), input);
      }

      downstreamNodes.add(instance);
      return instance;
   }

   public Node toPin(Method action, String pinID) {
      Node instance = this.to(action);
      
      return instance;
   }

   public void listInstances() {
      for (Node i : instances) {
         System.out.println("> " + i.nodeID);
      }
   }
}