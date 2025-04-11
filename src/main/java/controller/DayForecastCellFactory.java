package controller;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import model.DayForecast;

public class DayForecastCellFactory implements Callback<ListView<DayForecast>, ListCell<DayForecast>> {
    @Override
    public ListCell<DayForecast> call(ListView<DayForecast> param) {
        return new DayForecastCellController();
    }
}