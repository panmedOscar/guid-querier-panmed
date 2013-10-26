package tw.edu.ym.guid.querier;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RubyObject {

  private RubyObject() {}

  public static <E> E send(Object o, String methodName, Object... args) {
    Class<?>[] classes = new Class[args.length];
    for (int i = 0; i < args.length; i++) {
      if (args[i] == null)
        classes[i] = null;
      else
        classes[i] = args[i].getClass();
    }
    Method method;
    try {
      method = o.getClass().getDeclaredMethod(methodName, classes);
      @SuppressWarnings("unchecked")
      E invoke = (E) method.invoke(o, args);
      return invoke;
    } catch (NoSuchMethodException ex) {
      Logger.getLogger(RubyObject.class.getName()).log(Level.SEVERE, null, ex);
      throw new IllegalArgumentException("NoMethodError: undefined method `"
          + methodName + "' for " + o);
    } catch (Exception ex) {
      Logger.getLogger(RubyObject.class.getName()).log(Level.SEVERE, null, ex);
      throw new RuntimeException(ex);
    }
  }

}
