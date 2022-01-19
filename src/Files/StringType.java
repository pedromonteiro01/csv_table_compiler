package Files;
public class StringType extends Type {
    public StringType() {
       super("string");
    }
    
    @Override public boolean isNumeric() {
       return false;
    }
    @Override public boolean conformsTo(Type other) {
      Type ti = new BooleanType();
      return name.equals(other.name()) || other.name().equals(ti.name());
   }
}