package pizzaclicker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PizzaClickerApp extends Application {
    private PizzaGame game;
    private Label pizzaCountLabel;
    private VBox upgradesBox;
    private Label pizzaRateLabel;



    public static void main(String[] args) {
        // adds looping music
        String filepath = "loopingmusic.wav";
        musicStuff musicObject = new musicStuff();
        musicObject.playMusic(filepath);

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        
        
        // load image from resource folder
        Image backgroundImage = new Image(getClass().getResource("/background.jpg").toExternalForm());
    
        // creates background image
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, 
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true) 
        );
    
        Background background = new Background(bgImage);
    
        BorderPane root = new BorderPane();
        root.setBackground(background);
    
        game = new PizzaGame();
    
        VBox gameContent = new VBox(10);
        gameContent.setPadding(new Insets(15));
    
        // pizza count display
        pizzaCountLabel = new Label("Pizzas: 0");
        pizzaCountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");
    
        // main pizza button
        Button pizzaButton = new Button();
    
        // pizza button image
        ImageView pizzaImage = createPizzaImage();
        pizzaButton.setGraphic(pizzaImage);
        pizzaButton.setStyle("-fx-background-color: transparent;");
    
        pizzaButton.setOnAction(e -> {
            game.clickPizza();
            updateDisplay();
        });

        
        // creates boxes for upgrade buttons
        upgradesBox = new VBox(10);
        upgradesBox.setPadding(new Insets(50));
        createUpgradeButtons();
    
        // save load buttons
        HBox saveLoadBox = new HBox(10);
        Button saveButton = new Button("Save Progress");
        Button loadButton = new Button("Load Progress");
        root.setBottom(saveLoadBox);
        BorderPane.setAlignment(saveLoadBox, Pos.BOTTOM_LEFT);
    
        saveButton.setOnAction(e -> saveGame());
        loadButton.setOnAction(e -> loadGame());
    
        saveLoadBox.getChildren().addAll(saveButton, loadButton);
    
    
        root.setCenter(gameContent);
    
        root.setRight(upgradesBox);
        BorderPane.setAlignment(upgradesBox, Pos.TOP_CENTER);

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("Pizza Clicker");
        primaryStage.setScene(scene);
        primaryStage.show();
    
        startAutoGenerationTimer();

        pizzaRateLabel = new Label("Passive Pizzas/Second: 0");
        pizzaRateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");

        gameContent.getChildren().addAll(pizzaCountLabel, pizzaButton, pizzaRateLabel);


    }

  private ImageView createPizzaImage() {
    ImageView imageView = new ImageView();

    try {
        // load from resources
        InputStream imageStream = getClass().getResourceAsStream("/pizza.png");

        if (imageStream != null) {
            Image image = new Image(imageStream);
            imageView.setImage(image);
        } else {
            imageView.setStyle("-fx-background-color: orange; -fx-border-color: brown;");
        }
    } catch (Exception e) {
        imageView.setStyle("-fx-background-color: orange; -fx-border-color: brown;");
    }

    imageView.setFitWidth(300);
    imageView.setFitHeight(300);

    return imageView;
}



    private void createUpgradeButtons() {
        upgradesBox.getChildren().clear();
    
        int currentPizzaCount = game.getPizzaCount();
    
        for (Upgrade upgrade : game.getAvailableUpgrades()) {
            HBox upgradeBox = new HBox(10);
            upgradeBox.setAlignment(Pos.CENTER_LEFT);
    
            // create label to display the purchase count
            Label purchaseCountLabel = new Label("x" + upgrade.getPurchaseCount());
            purchaseCountLabel.setStyle("-fx-background-color: #000; -fx-text-fill: white;");
    
            Button upgradeButton = new Button(
                upgrade.getName() + " (Cost: " + upgrade.getCurrentCost() + " pizzas)"
            );
    
            if (currentPizzaCount >= upgrade.getCurrentCost()) {
                upgradeButton.setStyle("-fx-background-color: #55FF55; -fx-text-fill: black;");
            } else {
                upgradeButton.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
            }
    
            upgradeButton.setOnAction(e -> {
                if (game.purchaseUpgrade(upgrade)) {
                    updateDisplay();
                    createUpgradeButtons();
                }
            });
    
            upgradeBox.getChildren().addAll(upgradeButton, purchaseCountLabel);
            upgradesBox.getChildren().add(upgradeBox);
        }
    }
    

    private void updateDisplay() {
        Platform.runLater(() -> {
            if (game.isGameOver()) {
                showGameOverScreen();
            } else {
                pizzaCountLabel.setText("Pizzas: " + game.getPizzaCount());
                pizzaRateLabel.setText("Passive Pizzas/Second: " + game.getPassivePizzaRate());
                createUpgradeButtons();
            }
        });
    }
    
    private void showGameOverScreen() {
        Stage gameOverStage = new Stage();
        gameOverStage.setTitle("Game Over");
    
        ImageView gifView = new ImageView(new Image(getClass().getResource("/youhavewon.gif").toExternalForm()));
        gifView.setPreserveRatio(true);
        gifView.setFitWidth(800);
    
        Button closeButton = new Button("Close Game");
        closeButton.setOnAction(e -> {
            gameOverStage.close();
            Platform.exit();
        });
    
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(gifView, closeButton);
        vbox.setAlignment(Pos.CENTER);
    
        Scene gameOverScene = new Scene(vbox, 800, 800);
        gameOverStage.setScene(gameOverScene);
    
        gameOverStage.setOnCloseRequest(e -> {
            e.consume(); // Prevent default close
            gameOverStage.close();
            Platform.exit();
        });
    
        gameOverStage.show();
    }

    private void startAutoGenerationTimer() {
        Thread autoGeneratorThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        game.generatePassivePizzas();
                        updateDisplay();
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        autoGeneratorThread.setDaemon(true);
        autoGeneratorThread.start();
    }

    private void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("pizza_save.dat"))) {
            out.writeObject(game);
            showAlert("Game Saved", "Your progress has been saved successfully!");
        } catch (IOException e) {
            showAlert("Save Error", "Could not save the game: " + e.getMessage());
        }
    }

    private void loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("pizza_save.dat"))) {
            game = (PizzaGame) in.readObject();
            updateDisplay();
            showAlert("Game Loaded", "Your previous progress has been loaded!");
        } catch (IOException | ClassNotFoundException e) {
            showAlert("Load Error", "Could not load the game: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

// upgrade class showing inheritance and polymorphism
abstract class Upgrade implements Serializable {
    protected String name;
    protected int baseCost;
    protected int pizzasPerSecond;
    protected int purchaseCount;



    public Upgrade(String name, int baseCost, int pizzasPerSecond) {
        this.name = name;
        this.baseCost = baseCost;
        this.pizzasPerSecond = pizzasPerSecond;
        this.purchaseCount = 0;
    }

    public abstract void applyUpgrade(PizzaGame game);

    public String getName() { return name; }
    public int getBaseCost() { return baseCost; }
    public int getPizzasPerSecond() { return pizzasPerSecond; }
    public int getPurchaseCount() { return purchaseCount; }

    public void incrementPurchaseCount() {
        this.purchaseCount++;
    }

    public int getCurrentCost() {
        // increase the cost after each purchase
        return (int) (baseCost * Math.pow(1.25, purchaseCount));
    }
}



// all the diff upgrades
class ClickerUpgrade extends Upgrade {
    public ClickerUpgrade() {
        super("Clicker:                               +1 PER SECOND", 15, 1);
    }

    @Override
    public void applyUpgrade(PizzaGame game) {
        game.addPassivePizzaGenerator(pizzasPerSecond);
    }
}

class DominosWorkerUpgrade extends Upgrade {
    public DominosWorkerUpgrade() {
        super("Domino's Worker:              +5 PER SECOND", 100, 5);
    }

    @Override
    public void applyUpgrade(PizzaGame game) {
        game.addPassivePizzaGenerator(pizzasPerSecond);
    }
}
 class RandomItalianUpgrade extends Upgrade {
    public RandomItalianUpgrade() {
        super("Random Italian:                   +10 PER SECOND", 1000, 10);
    }
    @Override
    public void applyUpgrade(PizzaGame game) {
        game.addPassivePizzaGenerator(pizzasPerSecond);
    }
 }   

 class PapaJohnsOffspring extends Upgrade {
    public PapaJohnsOffspring() {
        super("Papa John's Offspring:        +50 PER SECOND", 5000, 50);
    }
    @Override
    public void applyUpgrade(PizzaGame game) {
        game.addPassivePizzaGenerator(pizzasPerSecond);
    }
 }

 class LittleCaesarsStore extends Upgrade {
    public LittleCaesarsStore() {
        super("Little Caesar's Store:           +100 PER SECOND", 50000, 100);
    }
    @Override
    public void applyUpgrade(PizzaGame game) {
        game.addPassivePizzaGenerator(pizzasPerSecond);
    }
 }

 class IndustrialPizzaFactory extends Upgrade {
    public IndustrialPizzaFactory() {
        super("Industrial Pizza Factory:         +900 PER SECOND", 100000, 900);
    }
    @Override
    public void applyUpgrade(PizzaGame game) {
        game.addPassivePizzaGenerator(pizzasPerSecond);
    }
 }

 class SecretGovernmentFacility extends Upgrade {
    public SecretGovernmentFacility() {
        super("Secret Government Facility:     +1500 PER SECOND", 1000000, 1500);
    }
    @Override
    public void applyUpgrade(PizzaGame game) {
        game.addPassivePizzaGenerator(pizzasPerSecond);
    }
 }


 class GlobalistPizzaLegislation extends Upgrade {
    public GlobalistPizzaLegislation() {
        super("Globalist Pizza Legislation:       +5000 PER SECOND", 10000000, 5000);
    }
    @Override
    public void applyUpgrade(PizzaGame game) {
        game.addPassivePizzaGenerator(pizzasPerSecond);
    }
 }


 class PapaJohn extends Upgrade {
    public PapaJohn() {
        super("Papa John:                                +15000 PER SECOND", 100000000, 15000);
    }
    @Override
    public void applyUpgrade(PizzaGame game) {
        game.addPassivePizzaGenerator(pizzasPerSecond);
    }
 }

// game logic
class PizzaGame implements Serializable {
    private int pizzaCount = 0;
    private List<Integer> passivePizzaGenerators = new ArrayList<>();
    private List<Upgrade> availableUpgrades = new ArrayList<>(); // Persist upgrades

    public boolean isGameOver() {
        return pizzaCount >= 2_000_000_000; // set a specific threshold
    }

    public PizzaGame() {
        // initialize upgrades when the game starts
        availableUpgrades = List.of(
            new ClickerUpgrade(),
            new DominosWorkerUpgrade(),
            new RandomItalianUpgrade(),
            new PapaJohnsOffspring(),
            new LittleCaesarsStore(),
            new IndustrialPizzaFactory(),
            new SecretGovernmentFacility(),
            new GlobalistPizzaLegislation(),
            new PapaJohn()
        );
    }

    // recursive method for passive pizza generation
    public int calculateTotalPassivePizzas() {
        return calculatePassivePizzasRecursive(0);
    }

    private int calculatePassivePizzasRecursive(int index) {
        if (index >= passivePizzaGenerators.size()) {
            return 0;
        }
        return passivePizzaGenerators.get(index) + calculatePassivePizzasRecursive(index + 1);
    }

    public int getPassivePizzaRate() {
        return calculateTotalPassivePizzas();
    }

    public void clickPizza() {
        if (getPizzaCount() % 10 == 0) {
            pizzaCount += 5;
        } else {
            pizzaCount +=1;
        }
    }

    public void generatePassivePizzas() {
        pizzaCount += calculateTotalPassivePizzas();
    }

    public void addPassivePizzaGenerator(int pizzasPerSecond) {
        passivePizzaGenerators.add(pizzasPerSecond);
    }

    public boolean purchaseUpgrade(Upgrade upgrade) {
        int currentCost = upgrade.getCurrentCost();
        if (pizzaCount >= currentCost) {
            pizzaCount -= currentCost;
            upgrade.applyUpgrade(this);
            upgrade.incrementPurchaseCount();
            return true;
        }
        return false;
    }

    public int getPizzaCount() {
        return pizzaCount;
    }

    public List<Upgrade> getAvailableUpgrades() {
        return availableUpgrades; // return persist list
    }


    


}
