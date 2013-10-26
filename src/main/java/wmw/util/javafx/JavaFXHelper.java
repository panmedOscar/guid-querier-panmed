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

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.DatePicker;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

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
   * @param filter
   *          an ExtensionFilter
   * @return the selected File
   */
  public static File FileSelector(String msg, ExtensionFilter filter) {
    Stage stage = new Stage();
    stage.initModality(Modality.WINDOW_MODAL);
    FileChooser chooser = new FileChooser();
    chooser.setTitle(msg);
    chooser.getExtensionFilters().add(filter);
    return chooser.showOpenDialog(stage);
  }

  public static File FolderSelector() {
    Stage stage = new Stage();
    stage.initModality(Modality.WINDOW_MODAL);
    DirectoryChooser chooser = new DirectoryChooser();
    return chooser.showDialog(stage);
  }

  /**
   * Sets up the format of a JavaFX DatePicker.
   * 
   * @param datePicker
   *          a DatePicker
   * @param pattern
   *          the date format
   */
  public static void setDatePickerFormat(final DatePicker datePicker,
      final String pattern) {
    datePicker.setConverter(new StringConverter<LocalDate>() {

      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

      {
        datePicker.setPromptText(pattern.toLowerCase());
      }

      @Override
      public String toString(LocalDate date) {
        return date == null ? "" : dateFormatter.format(date);
      }

      @Override
      public LocalDate fromString(String string) {
        if (string != null && !string.trim().isEmpty())
          return LocalDate.parse(string.trim(), dateFormatter);
        else
          return null;
      }

    });
  }

}
