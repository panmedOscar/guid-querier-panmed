package wmw.aop.terminator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class CountdownTerminatorModule extends AbstractModule {

  private final long terminationTime;
  private long idleTime = System.nanoTime();

  public CountdownTerminatorModule(long terminationTime) {
    this.terminationTime = terminationTime;
    autoShutdown();
  }

  private void autoShutdown() {
    Timer timer = new Timer(2000, new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (System.nanoTime() - idleTime > terminationTime)
          System.exit(0);
      }

    });
    timer.start();
  }

  @Override
  protected void configure() {
    bindInterceptor(Matchers.any(),
        Matchers.annotatedWith(ResetTerminator.class),
        new ResetTerminatorBlocker() {

          @Override
          protected void resetTimer() {
            idleTime = System.nanoTime();
          }

        });
  }

}
