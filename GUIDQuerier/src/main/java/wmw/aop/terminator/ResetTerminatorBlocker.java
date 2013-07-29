package wmw.aop.terminator;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public abstract class ResetTerminatorBlocker implements MethodInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    resetTimer();
    return invocation.proceed();
  }

  protected abstract void resetTimer();

}
