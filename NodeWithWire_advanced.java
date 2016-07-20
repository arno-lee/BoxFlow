
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
      Wire wireAtoB = new Wire();
      Wire wireBtoC123 = new Wire();
      Wire wireC1toG = new Wire();
      Wire wireC2toG = new Wire();
      Wire wireC3toG = new Wire();
      Wire outputFromG = new Wire();

      Box boxA = new Box(inputToA, "A", 
         FHelper.fHelper1("A", 10), wireAtoB);
      Box boxB = new Box(boxA.getOutput(), "B", 
         FHelper.fHelper1("B", 1000), wireBtoC123);
      Box boxC1 = new Box(boxB.getOutput(), "C1", 
         FHelper.fHelper1("C1", 2), wireC1toG);
      Box boxC2 = new Box(boxB.getOutput(), "C2", 
         FHelper.fHelper1("C2", 3), wireC2toG);
      Box boxC3 = new Box(boxB.getOutput(), "C3", 
         FHelper.fHelper1("C3", 5), wireC3toG);
      Box boxG = new Box(new Wire[]{wireC1toG, wireC2toG, wireC3toG}, "G", 
         FHelper.fHelper3("G", 7, 11, 17), outputFromG);

      inputToA.addDownstream(boxA);

      wireAtoB.setUpstream(boxA);
      wireAtoB.addDownstream(boxB);

      wireBtoC123.setUpstream(boxB);
      wireBtoC123.addDownstream(boxC1);
      wireBtoC123.addDownstream(boxC2);
      wireBtoC123.addDownstream(boxC3);

      wireC1toG.setUpstream(boxC1);
      wireC1toG.addDownstream(boxG);

      wireC2toG.setUpstream(boxC2);
      wireC2toG.addDownstream(boxG);

      wireC3toG.setUpstream(boxC3);
      wireC3toG.addDownstream(boxG);


      // Test the setup
      inputToA.feed(new Signal(new Integer[]{5}));
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

   public static Signal compose(ArrayList<Signal> data) {
      Signal composed = new Signal();
      composed.data = new Object[data.size()];

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

   public void addDownstream(Box downstreamBox) {
      this.downstreamBoxes.add(downstreamBox);
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
         downstreamBox.process(input);
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

   private ArrayList<Signal> arrivedSignals;

   private Function<Signal, Signal> action;

   public Box(Wire[] inputs, String id, Function<Signal, Signal> action, Wire output) {   
      boxID = id;

      this.inputs = inputs;
      this.action = action;
      this.output = output;

      arrivedSignals = new ArrayList<Signal>();
   }

   public Box(Wire input, String id, Function<Signal, Signal> action, Wire output) {   
      this(new Wire[]{input}, id, action, output);
   }

   public void process(Signal input) {
      arrivedSignals.add(input);
      if (inputs.length == arrivedSignals.size()) {
         Signal composedInput = Signal.compose(arrivedSignals);

         Signal actionResult = action.apply(composedInput);
         output.feed(actionResult);
      } 
   }
}

public class FHelper {
   public static Function<Signal, Signal> fHelper1(String id, int param) {
      return (i) -> {
         int inp = (int)i.getData()[0];
            
         System.out.print(id+" engaged with "+inp);
         System.out.println(" | param is "+param);
         int res = inp*param;
         System.out.println(id+" result "+res);

         return new Signal(new Integer[]{res});
      };
   }

   public static Function<Signal, Signal> fHelper3(String id, int param1,
                     int param2, int param3) {
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
