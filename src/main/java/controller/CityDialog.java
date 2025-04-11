package controller;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.SavedCity;

public class CityDialog extends Dialog<SavedCity> {
    private final TextField nameField = new TextField();

    public CityDialog() {
        setTitle("Add City");
        setHeaderText("Enter city details");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("City Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button == saveButton) {
                if (nameField.getText().isBlank()) {
                    showError("Validation Error", "City name cannot be empty!");
                    return null;
                }
                SavedCity city = new SavedCity();
                city.setName(nameField.getText().trim());
                return city;
            }
            return null;
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
