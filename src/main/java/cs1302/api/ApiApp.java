package cs1302.api;

import java.net.http.HttpClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.geometry.Orientation;
import javafx.event.ActionEvent;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import java.lang.Throwable;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.ArrayList;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.lang.Math;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {

    private static final String SNAPI = "https://api.spaceflightnewsapi." +
         "net/v4/articles/?format=json";

    private static final String TLE_API = "https://tle.ivanstanojevic.me/api/tle/?search=";

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    //Scene Graph Components
    Stage stage;
    Scene scene;
    VBox root;

    Label articleLabel;

    HBox searchBox;
    TextField searchBar;
    Button loadArticle;

    HBox articleBox;
    VBox articleInfoBox;
    Label articleInfo;
    Label articleSummary;
    VBox articleImageBox;
    ImageView articleImage;

    Label satelliteLabel;

    HBox satelliteBox;
    Label satelliteInfo;

    //Instance Variables
    String uri;
    ArticleResponse articleResponse;
    SatelliteResponse satelliteResponse;
    ArrayList<ArticleResult> articleList;
    ArrayList<SatelliteResult> satelliteList;
    ArticleResult displayedArticle;
    SatelliteResult displayedSatellite;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        articleLabel = new Label("Enter a query for a spaceflight article.");
        searchBox = new HBox();
        searchBar = new TextField("Topic/Title");
        loadArticle = new Button("Load Article");
        articleBox = new HBox();
        articleInfoBox = new VBox();
        articleInfo = new Label("Title: " + "\n\nAuthor(s): " + "\n\nDate: ");
        articleSummary = new Label("\nSummary: ");
        articleImageBox = new VBox();
        articleImage = new ImageView(
            new Image("file:resources/readme-banner.png", 100, 100, false, false));
        articleImage.setPreserveRatio(true);
        articleImage.setFitWidth(200);
        satelliteLabel = new Label("Info on Sattelite launched in Article Release Year");
        satelliteBox = new HBox();
        satelliteInfo = new Label("Satellite: " + "\nLaunch Year: " + "\nSatellite Number: "
            + "\nRevolutions around Earth: ");

    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void init () {
        //Connecting Components
        root.getChildren().addAll(articleLabel, searchBox,
            articleBox, satelliteLabel, satelliteBox);
        searchBox.getChildren().addAll(searchBar, loadArticle);
        articleBox.getChildren().addAll(articleInfoBox, articleImageBox);
        articleInfoBox.getChildren().addAll(articleInfo, articleSummary);
        articleImageBox.getChildren().addAll(articleImage);
        satelliteBox.getChildren().addAll(satelliteInfo);

    } // init


    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        scene = new Scene(root);
        Platform.runLater(() -> this.stage.setResizable(false));
        //Setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

        //Load Article Button
        EventHandler<ActionEvent> loadArticleHandler = (ActionEvent e) -> {
            loadArticle.setDisable(true);

            //Form SNAPI URI Response
            runNow(() -> {
                getArticleResponse(jsonArticleResponse());

                //Randomly select an article from SNAPI response to display and use for the TLE API
                int randIndexArticle = (int)(Math.random() * articleList.size() + 1);
                displayedArticle = articleList.get(randIndexArticle);

                //Display Article Info
                //Platform.runLater(() -> displayArticle());

                //Form TLE URI Response
                getSatelliteResponse(jsonSatelliteResponse());

                //Randomly select an article from SNAPI response to display and use for the TLE API
                int randIndexSatellite = (int)(Math.random() * satelliteList.size() + 1);
                displayedSatellite = satelliteList.get(randIndexSatellite);


                //Display Satellite Info
                //Platform.runLater(() -> displaySatellite());

                Platform.runLater(() -> loadArticle.setDisable(false));
            });
        };
        loadArticle.setOnAction(loadArticleHandler);

    } // start


    /**
     * Method to retrieve JSON response String for an SNAPI query.
     * Pulled from the HTTP Textbook Reading.
     * @return ArticleResponse the response from the SNAPI.
     */
    public ArticleResponse jsonArticleResponse() {
        try {
            // form URI
            String title = URLEncoder.encode(searchBar.getText(), StandardCharsets.UTF_8);
            System.out.println(title);
            String limit = URLEncoder.encode("50", StandardCharsets.UTF_8);
            String query = String.format("&title_contains=%s&limit=%s", title, limit);
            uri = SNAPI + query;

            // build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
            // send request / receive response in the form of a String
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
            // ensure the request is okay
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            } // if
            // get request body (the content we requested)
            String jsonString = response.body();
            // parse the JSON-formatted string using GSON
            articleResponse = GSON
                .fromJson(jsonString, ArticleResponse.class);
        } catch (IOException | InterruptedException ioe) {
            Platform.runLater(() -> alertError(ioe, "URI:" + uri + "\n\nException: "));
            System.err.println(ioe);
            ioe.printStackTrace();
        } // try
        return articleResponse;
    } // jsonArticleResponse

    /**
     * Method to retrieve JSON response String for an TLE API query.
     * Pulled from the HTTP Textbook Reading.
     * @return SatelliteResponse the response from the TLE API.
     */
    public SatelliteResponse jsonSatelliteResponse() {
        try {
            // form URI
            String query = URLEncoder.encode(
                displayedArticle.published_at.substring(0, 4), StandardCharsets.UTF_8);
            uri = TLE_API + query;
            System.out.println(uri);
            // build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
            // send request / receive response in the form of a String
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
            // ensure the request is okay
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            } // if
            // get request body (the content we requested)
            String jsonString = response.body();
            // parse the JSON-formatted string using GSON
            satelliteResponse = GSON
                .fromJson(jsonString, SatelliteResponse.class);
        } catch (IOException | InterruptedException ioe) {
            Platform.runLater(() -> alertError(ioe, "URI:" + uri + "\n\nException: "));
            System.err.println(ioe);
            ioe.printStackTrace();
        } // try
        return satelliteResponse;
    } // jsonSatelliteResponse

    /**
     * Returns list of URI Strings based on an {@code articleResponse}.
     * Pulled from the HTTP Textbook Reading.
     * @param articleResponse the response object
     * @return ArticleResult[] the array of results
     */
    private ArrayList<ArticleResult> getArticleResponse(ArticleResponse articleResponse) {
        //Stores URLs in a list.
        articleList = new ArrayList<ArticleResult>(articleResponse.results.length);
        for (int i = 0; i < articleResponse.results.length; i++) {
            ArticleResult result = articleResponse.results[i];
            articleList.add(i, result);
        } // for

        return articleList;
    } // getArticleResponse

    /**
     * Returns list of URI Strings based on an {@code satelliteResponse}.
     * Pulled from the HTTP Textbook Reading.
     * @param satelliteResponse the response object
     * @return String[] the array of results
     */
    private ArrayList<SatelliteResult> getSatelliteResponse(SatelliteResponse satelliteResponse) {
        System.out.println(GSON.toJson(satelliteResponse));
        //Stores URLs in a list.
        satelliteList = new ArrayList<SatelliteResult>(satelliteResponse.member.length);
        for (int i = 0; i < satelliteResponse.member.length; i++) {
            SatelliteResult result = satelliteResponse.member[i];
            if (result.name.substring(0, 4).equals
                (displayedArticle.published_at.substring(0, 4))) {
                satelliteList.add(i, result);
            } // if
        } // for

        return satelliteList;
    } // getSatelliteResponse

    /**
     * Takes the SNAPI Article contents and adds their data to the scene graph components.
     */
    public void displayArticle() {
        throw new UnsupportedOperationException("not yet implemented");
    } // displayArticle

    /**
     * Takes the TLE Satellite contents and adds their data to the scene graph components.
     */
    public void displaySatellite() {
        throw new UnsupportedOperationException("not yet implemented");
    } // displaySatellite

    /**
     * Show a modal error alert based on {@code cause}.
     * Pulled from Homework 7.6.
     * @param cause a {@link java.lang.Throwable Throwable} that caused the alert
     * @param uriString displays URI that caused error
     */
    public void alertError(Throwable cause, String uriString) {
        TextArea text = new TextArea(uriString + cause.toString());
        text.setEditable(false);
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().setContent(text);
        alert.setResizable(true);
        alert.showAndWait();
    } // alertError

    /**
     * Creates and immediately starts a new daemon thread that executes
     * {@code target.run()}. This method, which may be called from any thread,
     * will return immediately its the caller.
     * Pulled from the CS1302 Threads textbook reading.
     * @param target the object whose {@code run} method is invoked when this
     *     thread is started
     */
    public static void runNow(Runnable target) {
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    } // runNow



} // ApiApp
