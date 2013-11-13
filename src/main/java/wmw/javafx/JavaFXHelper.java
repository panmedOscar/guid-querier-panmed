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

import java.io.File;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 
 * JavaFXHelper provides several methods to manipulate JavaFX application.
 * 
 */
public final class JavaFXHelper {

  private JavaFXHelper() {}

  /**
   * Pops up a FileChooser for JavaFX application.
   * 
   * @param msg
   *          of the FileChooser
   * @param filters
   *          an array of ExtensionFilter
   * @return the selected File
   */
  public static File FileSelector(String msg, ExtensionFilter... filters) {
    Stage stage = new Stage();
    stage.initModality(Modality.WINDOW_MODAL);
    FileChooser chooser = new FileChooser();
    chooser.setTitle(msg);
    chooser.getExtensionFilters().addAll(filters);
    return chooser.showOpenDialog(stage);
  }

  public static File FolderSelector() {
    Stage stage = new Stage();
    stage.initModality(Modality.WINDOW_MODAL);
    DirectoryChooser chooser = new DirectoryChooser();
    return chooser.showDialog(stage);
  }

}
