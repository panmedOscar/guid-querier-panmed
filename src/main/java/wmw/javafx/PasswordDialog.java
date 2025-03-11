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
package wmw.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.function.Consumer;

/**
 * 
 * PasswordDialog is designed for JavaFX application to pop up a password input
 * dialogue.
 * 
 */
public final class PasswordDialog {

  public PasswordDialog(String message, final Consumer<String> callback) {
    // 創建新的 Stage 作為對話框
    Stage dialog = new Stage();
    dialog.initStyle(StageStyle.UTILITY);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.setResizable(false);

    // 設置關閉請求的處理，使用 Lambda 表達式
    dialog.setOnCloseRequest((WindowEvent event) -> dialog.close());

    // 創建密碼輸入欄
    PasswordField password = new PasswordField();

    // 創建顯示消息的文本
    Text msg = new Text(message);
    msg.setFont(Font.font("Verdana", 16));

    // 創建確認按鈕，並設置事件處理器
    Button btn = new Button("Confirm");
    btn.setOnAction(event -> {
      callback.accept(password.getText());
      dialog.close();
    });

    // 布局設置
    HBox hbox = new HBox(10); // 設置間距為 10
    hbox.getChildren().addAll(msg, password, btn);
    hbox.setAlignment(Pos.CENTER);
    hbox.setPadding(new Insets(10));

    // 創建場景並顯示對話框
    Scene dialogScene = new Scene(hbox);
    dialog.setScene(dialogScene);
    dialog.showAndWait();
  }

}
