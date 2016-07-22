
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
      Wire outputFromB = new Wire();

      Box boxA = new Box(inputToA, "A", FHelper.fHelper1("A", 10), wireAtoB);
      Box boxB = new Box(boxA.getOutput(), "B", 
         FHelper.fHelper1("B", 1000), outputFromB);
     

      inputToA.addDownstream(boxA);

      wireAtoB.setUpstream(boxA);
      wireAtoB.addDownstream(boxB);

      outputFromB.setUpstream(boxB);

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

      for (Signal s : data) {
        composed.data = s.data;
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
      } else {
         arrivedSignals.add(input);
      }
   }
}

public class FHelper {
   public static Function<Signal, Signal> fHelper1(String id, int param) {
      return (i) -> {
         int inp = (int)i.getData()[0];
            
         System.out.println(id+" engaged with "+inp);
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
            
         System.out.println(id+" engaged with "+param1+" "+param2+" "+param3);
         int[] res = new int[]{inp[0]*param1,inp[1]*param2, inp[2]*param3};
         System.out.println(id+" result "+res[0]+" "+res[1]+" "+res[2]);

         return new Signal(new Integer[]{res[0], res[1], res[2]});
      };
   }
}
