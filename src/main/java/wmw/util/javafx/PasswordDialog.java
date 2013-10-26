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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.sf.rubycollect4j.block.Block;

/**
 * 
 * PasswordDialog is designed for JavaFX application to pop up a password input
 * dialogue.
 * 
 */
public final class PasswordDialog {

  public PasswordDialog(String message, final Block<String> block) {
    final Stage dialog = new Stage();
    dialog.initStyle(StageStyle.UTILITY);
    dialog.setResizable(false);
    dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {

      public void handle(final WindowEvent event) {
        dialog.close();
      }

    });

    final PasswordField password = new PasswordField();
    Text msg = new Text(message);
    msg.setFont(Font.font("Verdana", 16));
    Button btn = new Button("確認");
    btn.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent arg0) {
        block.yield(password.getText());
        dialog.close();
      }

    });

    HBox hbox = new HBox();
    hbox.getChildren().addAll(msg, password, btn);
    hbox.setAlignment(Pos.CENTER);
    hbox.setPadding(new Insets(10));
    hbox.setSpacing(5.0);

    Scene dialogScene = new Scene(hbox);
    dialogScene.getStylesheets().add("/styles/Styles.css");

    dialog.setScene(dialogScene);
    dialog.showAndWait();
  }

}
