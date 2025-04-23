module guid.querier {
  requires com.github.wnameless.workbookaccessor;
  requires com.google.common;
  requires java.desktop;
  requires java.logging;
  requires java.smartcardio;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.graphics;
  requires net.sf.rubycollect4j;
  requires org.apache.commons.io;
  requires org.apache.commons.codec;
  requires org.slf4j;
  requires zip4j;
  requires persistence.api;
  requires ebean;
  requires java.sql;
  requires aopalliance;
  requires javax.inject;
  requires com.google.guice;
  requires com.h2database;

  exports app.models;
  opens app.models;
  // Export your package if other modules need to access its public types
  exports tw.edu.ym.guid.querier;
  // If you have controllers accessed via FXML, open them
  opens tw.edu.ym.guid.querier to javafx.fxml, javafx.base, javafx.graphics;


}