package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.DayForecast;
import model.SavedCity;
import model.WeatherRecord;
import repository.CityRepository;
import repository.WeatherRepository;
import service.WeatherApiClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {
    @FXML private ListView<DayForecast> forecastList;
    @FXML private TextField locationField;
    @FXML private ListView<SavedCity> cityList;
    @FXML private TextField searchField;
    @FXML private Button prevPage;
    @FXML private Button nextPage;
    @FXML private Label pageInfo;

    private final WeatherRepository weatherRepo = new WeatherRepository();
    private final CityRepository cityRepo = new CityRepository();
    private final WeatherApiClient apiClient = new WeatherApiClient();
    private ObservableList<SavedCity> cities;
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 5;

    @FXML
    public void initialize() {
        setupCityList();
        setupSearch();
        setupPagination();
        loadPage(0);
        forecastList.setCellFactory(new DayForecastCellFactory());
        refreshWeatherDisplay();
        cityList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> handleCitySelection(newVal)
        );
    }

    private void setupPagination() {
        updatePaginationControls();

        prevPage.setOnAction(e -> {
            if (currentPage > 0) {
                currentPage--;
                loadPage(currentPage);
            }
        });

        nextPage.setOnAction(e -> {
            int totalPages = cityRepo.getTotalPages(ITEMS_PER_PAGE);
            if (currentPage < totalPages - 1) {
                currentPage++;
                loadPage(currentPage);
            }
        });
    }

    private void loadPage(int page) {
        cities = FXCollections.observableArrayList(cityRepo.findPage(page, ITEMS_PER_PAGE));
        cityList.setItems(cities);
        updatePaginationControls();
    }

    private void updatePaginationControls() {
        int totalPages = cityRepo.getTotalPages(ITEMS_PER_PAGE);
        pageInfo.setText(String.format("Page %d/%d", currentPage + 1, totalPages));
        prevPage.setDisable(currentPage == 0);
        nextPage.setDisable(currentPage >= totalPages - 1);
    }

    private void handleCitySelection(SavedCity selectedCity) {
        if (selectedCity != null) {
            locationField.setText(selectedCity.getName());
            handleFetchWeather();
        }
    }

    private void setupCityList() {
        cityList.setCellFactory(lv -> new ListCell<SavedCity>() {
            private final HBox box = new HBox(10);
            private final Label name = new Label();
            private final Label time = new Label();
            private final Button delete = new Button("Delete");
            private final Button edit = new Button("Edit");
            private final ContextMenu contextMenu = new ContextMenu();

            {
                box.getChildren().addAll(name, time, edit, delete);
                name.setStyle("-fx-font-weight: bold;");
                time.setStyle("-fx-text-fill: #666;");

                MenuItem editItem = new MenuItem("Edit");
                MenuItem deleteItem = new MenuItem("Delete");
                contextMenu.getItems().addAll(editItem, deleteItem);

                editItem.setOnAction(e -> editCity(getItem()));
                deleteItem.setOnAction(e -> deleteCity(getItem()));

                setContextMenu(contextMenu);
                setGraphic(box);
            }

            @Override
            protected void updateItem(SavedCity city, boolean empty) {
                super.updateItem(city, empty);
                if (empty || city == null) {
                    setGraphic(null);
                } else {
                    name.setText(city.getName());
                    time.setText(formatLastViewed(city.getLastViewed()));
                    delete.setOnAction(e -> deleteCity(city));
                    edit.setOnAction(e -> editCity(city));
                    setGraphic(box);
                }
            }

            private String formatLastViewed(LocalDateTime time) {
                return time.format(DateTimeFormatter.ofPattern("MMM dd HH:mm", Locale.ENGLISH));
            }
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 0;
            if (newVal.isEmpty()) {
                loadPage(0);
            } else {
                List<SavedCity> filtered = cityRepo.searchCities(newVal, currentPage, ITEMS_PER_PAGE);
                cities.setAll(filtered);
                updatePaginationControls();
            }
        });
    }

    @FXML
    private void handleAddCity() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add City");
        dialog.setHeaderText("Enter a city name");
        dialog.setContentText("City:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(cityName -> {
            String trimmedName = cityName.trim();
            if (trimmedName.isEmpty()) return;

            if (cityRepo.existsByName(trimmedName)) {
                showError("Duplicate City", "This city is already in your list!");
                return;
            }

            SavedCity city = new SavedCity();
            city.setName(trimmedName);
            city.setLastViewed(LocalDateTime.now());
            cityRepo.save(city);
            loadPage(currentPage);
        });
    }

    private void editCity(SavedCity city) {
        TextInputDialog dialog = new TextInputDialog(city.getName());
        dialog.setTitle("Edit City");
        dialog.setHeaderText("Modify city name");
        dialog.setContentText("New name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (!newName.trim().isEmpty() && !newName.equalsIgnoreCase(city.getName())) {
                if (cityRepo.existsByName(newName)) {
                    showError("Duplicate City", "This city name already exists!");
                    return;
                }
                city.setName(newName.trim());
                cityRepo.save(city);
                loadPage(currentPage);
            }
        });
    }

    private void deleteCity(SavedCity city) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete " + city.getName() + "?");
        confirm.setContentText("This will remove the city from your saved list.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cityRepo.delete(city.getId());
            loadPage(currentPage);
        }
    }

    @FXML
    private void handleFetchWeather() {
        String cityName = locationField.getText().trim();
        if (!cityName.isEmpty()) {
            forecastList.setDisable(true);
            new Thread(() -> {
                try {
                    List<WeatherRecord> records = apiClient.fetchWeatherData(cityName);
                    weatherRepo.deleteAll();
                    records.forEach(weatherRepo::save);

                    Platform.runLater(() -> {
                        refreshWeatherDisplay();
                        updateLastViewed(cityName);
                        forecastList.setDisable(false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showError("API Error", "Failed to fetch data: " + e.getMessage());
                        forecastList.setDisable(false);
                    });
                }
            }).start();
        }
    }

    private void refreshCityList() {
        cities.setAll(cityRepo.findAll());
        cityList.refresh();
    }

    private void updateLastViewed(String cityName) {
        cityRepo.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(cityName))
                .findFirst()
                .ifPresent(city -> {
                    city.setLastViewed(LocalDateTime.now());
                    cityRepo.save(city);
                    refreshCityList();
                });
    }

    private void refreshWeatherDisplay() {
        List<WeatherRecord> records = weatherRepo.findAll();
        ObservableList<DayForecast> items = groupByDay(records);

        Platform.runLater(() -> {
            forecastList.setItems(items);
            if (!items.isEmpty()) {
                forecastList.getSelectionModel().clearAndSelect(0);
                forecastList.scrollTo(0);
            } else {
                forecastList.getSelectionModel().clearSelection();
            }
        });
    }

    private ObservableList<DayForecast> groupByDay(List<WeatherRecord> records) {
        Map<LocalDate, List<WeatherRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getObservationDateTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.toList()
                ));

        return FXCollections.observableArrayList(
                grouped.entrySet().stream()
                        .map(entry -> new DayForecast(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList())
        );
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}