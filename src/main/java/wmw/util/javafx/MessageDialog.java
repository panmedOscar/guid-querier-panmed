/**
 *
 * @author Wei-Ming Wu
 *
 *
 * Copyright 2013 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package wmw.util.javafx;

import static net.sf.rubycollect4j.RubyCollections.ra;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * 
 * MessageDialog is designed for JavaFX application to pop up a dialogue.
 * 
 */
public final class MessageDialog {

  /**
   * Pops up a dialogue and displays the messages.
   * 
   * @param msgs
   *          an String array of message
   */
  public void showMessages(String... msgs) {
    final Stage dialog = new Stage();
    dialog.initModality(Modality.WINDOW_MODAL);

    Button okButton = new Button("關閉");
    dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {

      public void handle(final WindowEvent event) {
        dialog.close();
      }

    });
    okButton.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        dialog.close();
      }

    });

    VBox vbox = new VBox();
    Text messages = new Text(ra(msgs).join("\n"));
    messages.setFont(Font.font("Verdana", 16));
    // messages.setId("message-dialog");
    vbox.getChildren().addAll(messages, okButton);
    vbox.setAlignment(Pos.CENTER);
    vbox.setPadding(new Insets(10));
    Scene dialogScene = new Scene(vbox);
    // myDialogScene.getStylesheets().add("/styles/Styles.css");

    dialog.setScene(dialogScene);
    dialog.show();
  }

}
