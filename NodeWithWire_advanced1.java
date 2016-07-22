
import java.lang.Math; 
import java.util.HashMap;
import java.util.ArrayList;
import java.util.function.Function;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


public class HelloWorld {
   public static void main(String[] args) {
      System.out.println("Testing Boxes With Wires ...");

      Wire inputToA = new Wire();
      Wire linkAToC12G = new Wire();
      Wire linkC1ToG = new Wire();
      Wire linkC2ToG = new Wire();
        
      Wire outputFromG = new Wire();

      Box boxA = new Box(inputToA, "A", 
         FHelper.fHelper1("A", 10), linkAToC12G);
      Box boxC1 = new Box(linkAToC12G, "C1", 
         FHelper.fHelper1("C1", 10), linkC1ToG);
      Box boxC2 = new Box(linkAToC12G, "C2", 
         FHelper.fHelper1("C2", 20), linkC2ToG);
      Box boxG = new Box(new Wire[]{linkAToC12G, linkC1ToG, linkC2ToG}, "G", 
         FHelper.fHelper3("G", 1000, 2011, 3000), outputFromG);

      inputToA.addDownstream(boxA);

      linkAToC12G.setUpstream(boxA);
      linkAToC12G.addDownstream(boxC1);
      linkAToC12G.addDownstream(boxC2);
      linkAToC12G.addDownstream(boxG);

      linkC1ToG.setUpstream(boxC1);
      linkC1ToG.addDownstream(boxG);

      linkC2ToG.setUpstream(boxC2);
      linkC2ToG.addDownstream(boxG);

      // Test the setup
      inputToA.feed(new Signal(new Integer[]{1}));
   }
}


public class Signal {
   private Object[] data;
   public Object[] getData() {
      return data;
   }

   public Signal() {}

   public Signal(Object[] data) {
      this.data = data;
   }

   public static Signal compose(Signal[] data) {
      Signal composed = new Signal();
      composed.data = new Object[data.length];

      int index = 0;
      for (Signal s : data) {
         composed.data[index++] = (s.data.length == 1) 
                                       ? s.data[0] : s.data;
      }

      return composed;
   }
}

public class Wire {
   private Box upstreamBox;
   private ArrayList<Box> downstreamBoxes;

   public void setUpstream(Box upstreamBox) {
      this.upstreamBox = upstreamBox;
   }

   public String getUpstreamID(){
      return (upstreamBox == null) ? "no upstream" : upstreamBox.getID();
   }

   public void addDownstream(Box downstreamBox) {
      this.downstreamBoxes.add(downstreamBox);
   }

   public void addDownstream(Box downstreamBox, Signal pullUp) {
      this.downstreamBoxes.add(downstreamBox);
      downstreamBox.process(pullUp, this);
   }

   public Wire() {
      this.downstreamBoxes = new ArrayList<Box>();
   }

   public Wire(Box upstreamBox, Box[] downstreamBoxes) {
      this.upstreamBox = upstreamBox;
      this.downstreamBoxes = new ArrayList<Box>();

      if (downstreamBoxes != null) {
         for (Box downstreamBox : downstreamBoxes) {
            this.downstreamBoxes.add(downstreamBox);
         }
      }
   }

   public void feed(Signal input) {
      for (Box downstreamBox : downstreamBoxes) {
         // System.out.println("\n   "+downstreamBox.getID()+" fired!");
         downstreamBox.process(input, this);
      }
   }
}

public class Box {
   private String boxID;
   public String getID() { return boxID; }
   public Box setID(String id) { 
      this.boxID = id; 
      return this; 
   }

   private Wire[] inputs;
   private Wire output;
   public Wire getOutput() {
      return output;
   }

   private Signal[] arrivedSignals;
   private int numOfArrivedSignals = 0;
   private HashMap<Wire, Integer> pinOut;

   private Function<Signal, Signal> action;

   public Box(Wire[] inputs, String id, 
              Function<Signal, Signal> action, Wire output) {   
      boxID = id;

      this.inputs = inputs;
      this.action = action;
      this.output = output;

      arrivedSignals = new Signal[inputs.length];
      pinOut = new HashMap<Wire, Integer>();
      int pinIndex = 0;
      for (Wire in : inputs) {
         pinOut.put(in, pinIndex++);
      }
   }

   public void checkPinOut() {
      for (Wire w : pinOut.keySet()) {
         System.out.println("  -> "+w.getUpstreamID()
            +" @ "+ pinOut.get(w));
      }
   }

   public Box(Wire input, String id, Function<Signal, Signal> action, Wire output) {   
      this(new Wire[]{input}, id, action, output);
   }

   public void process(Signal input, Wire source) {
         if (this.getID() == "G") {
            System.out.println(" |-> "+input.getData()[0]+
               " @PIN "+pinOut.get(source));
         }
      arrivedSignals[pinOut.get(source)] = input;
      numOfArrivedSignals++;
         // System.out.println("   "+this.getID()+" # of arrived signals: "
         //          +this.arrivedSignals.size());
      if (numOfArrivedSignals == inputs.length) {    
         Signal composedInput = Signal.compose(arrivedSignals),
                actionResult = action.apply(composedInput);

         numOfArrivedSignals = 0;

         output.feed(actionResult);
      } 
   }
}

public class FHelper {
   public static Function<Signal, Signal> fHelper1(String id, int param) {
      return (i) -> {
         int inp = (int)i.getData()[0];
            
         System.out.print("\n"+id+" engaged with "+inp);
         System.out.print(" | param is "+param);
         int res = inp*param;
         System.out.print(" | result "+res);

         return new Signal(new Integer[]{res});
      };
   }

   public static Function<Signal, Signal> fHelper3(String id, 
                  int param1, int param2, int param3) {
      return (i) -> {
         int[] inp = new int[3];
         for (int v = 0; v < 3; v++) {
            inp[v] = (int)i.getData()[v];
         }
         
         System.out.print(id+" engaged with "+inp[0]+" "+inp[1]+" "+inp[2]);
         System.out.println(" | params are "+param1+" "+param2+" "+param3);
         int res = inp[0]*param1+inp[1]*param2+inp[2]*param3;
         System.out.println(id+" result "+res);

         return new Signal(new Integer[]{res});
      };
   }
}
