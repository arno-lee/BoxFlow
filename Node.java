
import java.lang.Math; 
import java.util.HashMap;
import java.util.ArrayList;
import java.util.function.Function;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


public class HelloWorld {
   public static void main(String[] args) {
      System.out.println("Testing Node...");
      
      Node model = Node.in(msg -> { 
         System.out.println("InputNode"+" got "+msg); 
         return 10*(int)msg; 
      }).id("InputNode")
      .to(i ->  {
         System.out.println("AnotherNode"+" got "+i); 
         return (int)i*50;
      }).id("AnotherNode")
      .to(i ->  {
         System.out.println("SomeMoreNode"+" got "+i); 
         return (int)i*70;
      }).id("SomeMoreNode")
      .out(i -> {
         System.out.println("OutNode"+" got "+i); 
         return (int)i*11;
      }).id("OutNode");

      Object res = model.feed(7);

      System.out.println("\nFinal result: " + (int)res);
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

   private Function<Object, Object> action;

   private HashMap<String, Object> inputs;

   private Node upstreamNode;
   private ArrayList<Node> downstreamNodes;

   private Node modelHead;
   boolean chainInitiated;
   private Object finalResult;

   private Node() {
      instances = new ArrayList<Node>();
      inputs = new HashMap<String, Object>();
      downstreamNodes = new ArrayList<Node>();

      chainInitiated = false;
   }
  
   protected void addInstance(Node instance) {
      instances.add(instance);
   }
  
   // Node factory constructor
   public static Node in(Function<Object, Object> action) {
      Node instance = new Node();  // TO DO: change to getClass.getInstance

      instance.upstreamNode = null;
      instance.action = action;
      instance.addInstance(instance);
      /*for (Parameter input : action.getParameters()) {
         instance.inputs.put(input.getName(), input);
      }*/

      return instance;
   }

   public Node to(Function<Object, Object> action) {
      Node instance = new Node();  // TO DO: change to getClass.getInstance

      instance.addInstance(instance);
      instance.upstreamNode = this;
      instance.action = action;

      /*for (Parameter input : action.getParameters()) {
         instance.inputs.put(input.getName(), input);
      }*/

      downstreamNodes.add(instance);

      return instance;
   }

   public Node out(Function<Object, Object> action) { 
      Node instance = new Node();  // TO DO: change to getClass.getInstance

      instance.addInstance(instance);
      instance.upstreamNode = this;
      instance.action = action;

      Node headNode = this;
      do  {  // TODO: test this more
         headNode = headNode.upstreamNode;
      } while (headNode.upstreamNode != null);
      instance.modelHead = headNode;

      downstreamNodes.add(instance);
      return instance;
   }

   public Node toPin(Function<Object, Object> action, String pinID) {
      Node instance = this.to(action);
      
      return instance;
   }

   public Object feed(Object modelInput) {
      // output node logic

      if (modelHead != null) {
         if (!chainInitiated) {  
            chainInitiated = true; 
            modelHead.feed(modelInput);
         } else {
            chainInitiated = false;
            finalResult = action.apply(modelInput);
         }
      // regular node logic
      } else {
         Object yield = action.apply(modelInput);

         //for (Node sink : downstreamNodes) {
            downstreamNodes.get(0).feed(yield);
         //}
      }
      return finalResult;   // stub
   }

   public void listInstances() {
      for (Node i : instances) {
         System.out.println("> " + i.nodeID);
      }
   }
}

