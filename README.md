## App explanation
It's a simple app that allows the user to check weather in his favourite cities. It relies on OpenWeatherAPI for weather data.
First the user adds a city to the list after clicking the Add city button, the user can search through the cities. When he clicks them on the right side of the app he gets a display of the weather in the city in 3 hour intervals.
## Running the application
### Prerequisites
- **JDK 17+** ([Eclipse Temurin](https://adoptium.net/) recommended)
- **Maven 3.8+** (included with most IDEs)
- **JavaFX SDK 20** ([Download](https://gluonhq.com/products/javafx/))
### Recommended method: Using Maven
```bash
# Clone repository
git clone https://github.com/pasierbpiotr/Esatto-Task.git
cd Esatto-Task

# Run application
mvn clean javafx:run
```
### Alternative method: Manual Command Line
1. Download **[JavaFX SDK 20](https://gluonhq.com/products/javafx/)**
2. Extract to a known location (e.g., ```C:\javafx-sdk-20```)
3. Run:
```bash
mvn clean package
java --module-path "path/to/javafx-sdk-20/lib" --add-modules javafx.controls,javafx.fxml -jar target/Esatto-Task-1.0-SNAPSHOT.jar
```
### Important
1. Replace "```path/to/javafx-sdk-20/lib```" with your actual JavaFX SDK path
2. Ensure Java 17+ is in your PATH (```java -version```)
3. First run may take longer due to Maven dependencies download
### IDE Setup
- **IntelliJ** - Enable "Maven Projects" tab > Plugins > javafx > Goals: javafx:run
