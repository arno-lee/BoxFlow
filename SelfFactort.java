
import java.lang.Math; 
import java.util.HashMap;

public class HelloWorld {
   public static void main(String[] args) {
      System.out.println("HELLO!");
    
      SelfFactory inst = SelfFactory.f("myInstance");
      System.out.println(inst.getName());
    
      SelfFactory anotherInst = SelfFactory.f("anotherInstance");
      SelfFactory yetAnotherInst = SelfFactory.f("yetAnotherInstance")
        .f("andMore").f("andEvenMore!").to("someModule");
    
      anotherInst.listInstances();  
   }
}

public class SelfFactory {
   private String mName;
   public void name(String name) { this.mName = name; }
   public String getName() { return mName; }
  
   private static HashMap<String, SelfFactory> instances;
   static {
      instances = new HashMap<String, SelfFactory>();
   }

   public SelfFactory() { }
  
   protected static void addInstance(SelfFactory instance) {
      instances.put(instance.mName, instance);
   }
  
   public static SelfFactory f(String name) {
      SelfFactory instance = new SelfFactory();
      instance.mName = name;
      
      SelfFactory.addInstance(instance);
    
      return instance;
   }

   public static SelfFactory to(String name) {
      return f(name);
   }
  
   public static void listInstances() {
      for (String key : instances.keySet()) {
         System.out.println(": " + key + " => " 
            + instances.get(key).mName);
      }
   }
}