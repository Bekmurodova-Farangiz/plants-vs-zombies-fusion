import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
//timelien imports
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
// list of plants to GameBoard.
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBoard extends GridPane {

    private static final int ROWS = 5;
    private static final int COLUMNS = 9;
    private static final int CELL_WIDTH = 100;
    private static final int CELL_HEIGHT = 100;
    private List<Plant> plants = new ArrayList<>();    //plants stores all plant objects on the board
    private List<Zombie> zombies = new ArrayList<>(); //zombies stores all zombie objects on the board
    private List<Bullet> bullets = new ArrayList<>();  //bullets added to the storage
    private boolean gameOver = false; //Global state

    //Constructor
    public GameBoard() {
        createBoard();
        spawnZombie();
        startZombieSpawner();
    }

    private void createBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                final int currentRow = row;
                final int currentCol = col;
                Rectangle cellBackground = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
                cellBackground.setFill(Color.LIGHTGREEN);
                cellBackground.setStroke(Color.DARKGREEN);

                StackPane cell = new StackPane();
                cell.getChildren().add(cellBackground);
                final boolean[] hasPlant = {false};
                final Plant[] currentPlant = {null};

                cell.setOnMouseClicked(e -> {

                    // IF NO PLANT → PLACE ONE
                    if (!hasPlant[0]) {
                        Plant plant = new Plant(currentRow, currentCol);
                        currentPlant[0] = plant;
                        plants.add(plant); 
                        startShooting(plant);

                        cell.getChildren().add(plant.getView());
                        hasPlant[0] = true;

                        System.out.println("Plant placed at row " + currentRow + " col " + currentCol);
                    }

                    // IF PLANT EXISTS → DAMAGE IT
                    else {
                        Plant plant = currentPlant[0];

                        plant.takeDamage(20);

                        if (plant.isDead()) {
                            cell.getChildren().remove(plant.getView());
                            plants.remove(plant); //when it dies remove from list 
                            hasPlant[0] = false;
                            currentPlant[0] = null;

                            System.out.println("Plant died and removed.");
                        }
                    }
                });

                add(cell, col, row);
            }
        }
    }
    public void spawnZombie() {
        Random random = new Random();
        int row = random.nextInt(5); // 0 to 4 rows

        Zombie zombie = new Zombie(row);
        zombies.add(zombie); // add zombie to list

        double x = 800;
        double y = (row * 100) + 2;  //aligns zombie properly inside that row

        zombie.getView().setTranslateX(x);
        zombie.getView().setTranslateY(y);

        getChildren().add(zombie.getView());
        startGameLoop(zombie);
    }
    public void startGameLoop(Zombie zombie) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {
                if (gameOver) { //Guard clause
                    zombie.stopAllActions();
                    return;
                }
                boolean colliding = checkCollisions(zombie);

                if (colliding) {
                    zombie.stopMoving();  //	if touching a plant → no movement happens
                } else {
                    zombie.startMoving();  //	if not touching → movement happens
                    zombie.moveLeft();
                    checkGameOver(zombie);
                }
                for (int i = bullets.size() - 1; i >= 0; i--) {
                    Bullet bullet = bullets.get(i);
                    bullet.moveRight();
                    checkBulletHits();

                    if (bullet.isOffScreen()) {
                        getChildren().remove(bullet.getView());
                        bullets.remove(i);
                    }
                }
            })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        zombie.setMovementTimeline(timeline);
        timeline.play();
    }
    
    public boolean checkCollisions(Zombie zombie) {
    for (Plant plant : plants) {
        if (zombie.getView().localToScene(zombie.getView().getBoundsInLocal())
                .intersects(plant.getView().localToScene(plant.getView().getBoundsInLocal()))) {

            plant.takeDamage(1);
            System.out.println("Zombie is attacking a plant!");

            if (plant.isDead()) {
                plant.stopShooting();
                if (plant.getView().getParent() instanceof StackPane parentCell) {//if that parent is a StackPane, store it in a variable called parentCell
                    parentCell.getChildren().remove(plant.getView());//remove the plant from the cell that actually contains it
                }

                plants.remove(plant);
                zombie.startMoving();
                System.out.println("Plant destroyed by zombie!");
            }
            return true;
        }
    }

    return false;
    }
    public void shootFromPlant(Plant plant) {
        double bulletX = (plant.getCol() * 100) + 80;
        double bulletY = (plant.getRow() * 100) + 2;

        Bullet bullet = new Bullet(bulletX, bulletY);
        bullets.add(bullet);

        getChildren().add(bullet.getView());
    }
    // auto shooting system 
    public void startShooting(Plant plant) {
        Timeline shooter = new Timeline(
            new KeyFrame(Duration.seconds(2), e -> {
                if (plant.isDead() || !plants.contains(plant)) {
                    return;
                }

                if (hasZombieInLane(plant.getRow())) {
                    shootFromPlant(plant);
                }
            })
        );

        shooter.setCycleCount(Timeline.INDEFINITE);
        plant.setShootingTimeline(shooter);
        shooter.play();
    }
    public void checkBulletHits() {
        for (int i = bullets.size() - 1; i >= 0; i--) { //We loop backward through bullets so we can safely remove them.
            Bullet bullet = bullets.get(i);

            for (int j = zombies.size() - 1; j >= 0; j--) { //We also loop through zombies.
                Zombie zombie = zombies.get(j);

                if (bullet.getView().getBoundsInParent().intersects(zombie.getView().getBoundsInParent())) { //Checks whether bullet and zombie overlap visually.
                    zombie.takeDamage(bullet.getDamage());

                    getChildren().remove(bullet.getView());
                    bullets.remove(i);

                    System.out.println("Bullet hit zombie!");

                    if (zombie.isDead()) {
                        zombie.stopAllActions();
                        getChildren().remove(zombie.getView());
                        zombies.remove(j);
                        System.out.println("Zombie died!");
                    }
                    break;
                }
            }
        }
    }
    public void startZombieSpawner() {
        Timeline spawner = new Timeline(
            new KeyFrame(Duration.seconds(5), e -> {
                spawnZombie();
                System.out.println("New zombie spawned!");
            })
        );

        spawner.setCycleCount(Timeline.INDEFINITE);
        spawner.play();
    }
    public boolean hasZombieInLane(int row) {
        for (Zombie zombie : zombies) {
            if (zombie.getRow() == row) {
                return true;
            }
        }

        return false;
    }
    public void checkGameOver(Zombie zombie) {
        if (zombie.getView().getTranslateX() <= 0) {
            gameOver = true;
            zombie.stopAllActions();
            System.out.println("GAME OVER! A zombie reached the house.");
        }
    }
}
