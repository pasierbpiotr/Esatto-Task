package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.DayForecast;
import model.WeatherRecord;
import util.IconCache;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DayForecastCellController extends ListCell<DayForecast> {
    @FXML private Label dateLabel;
    @FXML private HBox hourlyContainer;
    private VBox container;

    public DayForecastCellController() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dayForecastCell.fxml"));
            loader.setController(this);
            container = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(DayForecast item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            if (container == null) {
                loadFXML();
            }
            dateLabel.setText(item.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMM dd", Locale.ENGLISH)));
            hourlyContainer.getChildren().clear();

            for (WeatherRecord forecast : item.getHourlyForecasts()) {
                hourlyContainer.getChildren().add(createHourlyCard(forecast));
            }
            setGraphic(container);
        }
    }

    private VBox createHourlyCard(WeatherRecord forecast) {
        VBox card = new VBox(5);
        card.getStyleClass().add("hourly-card");
        card.setStyle("-fx-padding: 10; -fx-background-radius: 6;");

        ImageView iconView = new ImageView(IconCache.getIcon(forecast.getWeatherIcon()));
        iconView.setFitWidth(40);
        iconView.setFitHeight(40);

        Label timeLabel = new Label(forecast.getObservationDateTime()
                .format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.getStyleClass().add("time-label");

        Label tempLabel = new Label(String.format("%.1f°C", forecast.getTemperature()));
        tempLabel.getStyleClass().addAll(
                forecast.getTemperature() > 25 ? "temperature-positive" :
                        forecast.getTemperature() < 5 ? "temperature-negative" : ""
        );

        Label conditionLabel = new Label(forecast.getWeatherCondition());
        conditionLabel.getStyleClass().add("condition-label");

        card.getChildren().addAll(iconView, timeLabel, tempLabel, conditionLabel);
        return card;
    }
}