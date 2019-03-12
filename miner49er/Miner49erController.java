/*
 * Controller class
 * Contains main code for game
 */
package miner49er;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

public class Miner49erController {
	//TODO: make so grid formats correctly for sizes larger than 10x10
	private static final int ROWS = 10;
	private static final int COLS = 10;
	private static final int FIELDSIZE = ROWS * COLS;
	private static final int NUM_MINES = 10; // modify to change number of mines
	private int[][] minefield;
	private int mineCount = NUM_MINES;
	private int flagCount = 0;
	private int clickedSquares = 0;
	private Task<Void> task;
    @FXML
    private AnchorPane anchorPane_main;
    @FXML
    private Button button_newGame;
    @FXML
    private Label label_title;
    @FXML
    private GridPane grid;
    @FXML
    private Label label_timer;
    @FXML
    private ImageView image_smiley;
    @FXML
    private Label label_mines;
    @FXML
    private Label label_flags;


    /**
     * Reset board and start new game
     */
    public void newGame() {
    	//(re)initialize board
    	Image img = new Image(this.getClass().getResourceAsStream("smiley.png"));
    	image_smiley.setImage(img);
    	minefield = new int[ROWS][COLS];
    	flagCount = 0;
    	mineCount = NUM_MINES;
    	clickedSquares = 0;
    	label_flags.setText(String.valueOf(flagCount));
    	label_mines.setText(String.valueOf(mineCount));
    	for(int[] row : minefield)
			for(int cell : row)
				cell = 0;
    	
    	//set up board
    	setMinefield();
    	for(int i = 0; i < ROWS; i++){
    		  for(int j = 0; j < COLS; j++){
    			Button newBtn = new Button();
    			newBtn.setPadding(Insets.EMPTY);
    			newBtn.setOnMouseClicked(e -> {
    				if(e.getButton() == MouseButton.SECONDARY) {
    					addFlag(e);
    				}
    				else {
    					squareClicked(e);
    				}
    			});
    			newBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    			grid.add(newBtn, j, i);
    		}
    	}
    	
    	//start timer
    	startTask();
    }
    
    
    /**
     * Randomly select squares and add mines to them
     */
    private void setMinefield() {
    	Random rand = new Random(); 
    	int i = 0;
    	while(i < NUM_MINES) { //add given number of mines
    		int randRow = rand.nextInt(ROWS);
    		int randCol = rand.nextInt(COLS);
    		if(minefield[randRow][randCol] != 1) { //if this is not already a mine
    			minefield[randRow][randCol] = 1;
    			//for debugging purposes
    			System.out.println("random index: [" + randRow + "][" + randCol + "]\n");
    		}
    		else { //redo the square selection if the current spot is taken
    			continue;
    		}
    		i++;
    	}
    	label_mines.setText(String.valueOf(NUM_MINES));
    }
       
    
    /**
     * If a square is clicked, clear the square (may be a mine or a number)
     */
	public void squareClicked(MouseEvent e) {
    	Button btn = ((Button) e.getSource());
    	btn.setStyle("-fx-background-color: #d5d5d5;");
    	//freeze this button so you can't click it again
    	btn.setMouseTransparent(true);
    	btn.setFocusTraversable(false);
    	
    	//determine if this square is a mine
    	int row = GridPane.getRowIndex(btn);
    	int col = GridPane.getColumnIndex(btn);
    	if(minefield[row][col] == 1) { //this is a mine, you lose
    		gameLost();
    		Image boomImg = new Image(this.getClass().getResourceAsStream("kaboom.png"),
					btn.getWidth(), btn.getHeight(), true, true);
			btn.setGraphic(new ImageView(boomImg));
    	}
    	else { //set the number of surrounding mines
    		String numberVal = String.valueOf(countSurroundingMines(row, col));
    		if(numberVal.equals("0")) {
    			btn.setText(" ");
    			clearMyNeighbors(btn);
    		}
    		else {
    			btn.setText(numberVal);
    			btn.setStyle("-fx-text-fill: " + numColor(numberVal) + ";" +
    			"-fx-font-weight: bold;" +
    			"-fx-background-color: #d5d5d5;");
    		}
    		clickedSquares++;
    	}
    	//stop when you have clicked all non-mine squares
    	if(clickedSquares == FIELDSIZE - NUM_MINES) {
    		gameWon();
    	}
    }
	
	
	/**
	 * Color-coding the number of mines indicator
	 */
    private String numColor(String number) {
    	if(number.equals("1"))
    		return "darkgreen";
    	else if(number.equals("2"))
    		return "blue";
    	else if(number.equals("3"))
    		return "red";
    	else if(number.equals("4"))
    		return "blueviolet";
    	else if(number.equals("5"))
    		return "magenta";
    	else if(number.equals("6"))
    		return "saddlebrown";
    	else if(number.equals("7"))
    		return "gray";
    	else
    		return "black";
    }
    
	
	/**
     * Add a flag to this square (on right-click)
     */
    private void addFlag(MouseEvent e) {
    	//TODO: prohibit adding flag to clear square or another flag
    	Button btn = ((Button) e.getSource());
    	Image flagImg = new Image(this.getClass().getResourceAsStream("flag.png"),
				btn.getWidth(), btn.getHeight(), true, true);
		btn.setGraphic(new ImageView(flagImg));
		btn.setStyle("-fx-background-color: #d5d5d5;");
    	flagCount++;
    	mineCount--;
    	label_flags.setText(String.valueOf(flagCount));
    	label_mines.setText(String.valueOf(mineCount));
    }
    
	
    /**
     * Determine how many of the 8 squares surrounding this one are mines
     * @param rw row index for this square
     * @param cl column index for this square
     */
    private int countSurroundingMines(int rw, int cl) {
    	int numMines = 0;
    	for(int i = rw-1; i <= rw+1; i++) {
    		for(int j = cl-1; j <= cl+1; j++) {
    			if(isInRange(i, j)) {
    				if(minefield[i][j] == 1)
    					numMines++;
    			}
    		}
    	}
    	return numMines;
	}
    
    
    /**
     * Determine if a pair of indices is within range of the board
     */
    private boolean isInRange(int i, int j) {
    	if(i < 0 || i > ROWS-1)
    		return false;
    	else if(j < 0 || j > COLS-1)
    		return false;
    	else 
    		return true;
    }


    /**
     * If a square is not adjacent to any mines, clear out all surrounding
     * squares that are not adjacent to any mines
     * @param btn the button that was clicked
     */
    private void clearMyNeighbors(Button btn) {
    	//TODO: implement the clear function correctly
    	int btnX = GridPane.getRowIndex(btn);
		int btnY = GridPane.getColumnIndex(btn);
    	for(Node square : grid.getChildren()) {
    		int x = GridPane.getRowIndex(square);
    		int y = GridPane.getColumnIndex(square);
    		if((x == btnX+1 || x == btnX-1 || x == btnX) 
    				&& (y == btnY+1 || y == btnY-1 || y == btnY))
    			if(countSurroundingMines(x,y) == 0)
    				((Button)square).fire();
    		/*Event.fireEvent(MouseEvent.MOUSE_CLICKED,
    				   getSceneX(), sceneCoords.getY(), screenCoords.getX(), screenCoords.getY(), MouseButton.PRIMARY, 1,
    				   true, true, true, true, true, true, true, true, true, true, null));*/
    	}
    }
    
    
    /**
     * Perform end of game actions for win
     */
    private void gameWon() {
    	System.out.println("YOU WON!");
    	//change to glasses smiley
    	Image glassesImg = new Image(this.getClass().getResourceAsStream("labrinthSunglasses.png"));
		image_smiley.setImage(glassesImg);
		//lock all squares
		for(Node cell : grid.getChildren()) {
    		Button btn = ((Button)cell);
    		btn.setMouseTransparent(true);
        	btn.setFocusTraversable(false);	
		}
    	//stop timer
		if(task.isRunning()) 
    		task.cancel();
    }
    
    
    /**
     * Perform end of game actions for lose
     */
    private void gameLost() {
    	//show all mines
    	for(Node cell : grid.getChildren()) {
    		int x = GridPane.getRowIndex(cell);
    		int y = GridPane.getColumnIndex(cell);
    		Button btn = ((Button)cell);
    		if(minefield[x][y] == 1) {
    			Image mineImg = new Image(this.getClass().getResourceAsStream("bomb.png"),
    					btn.getWidth(), btn.getHeight(), true, true);
    			btn.setGraphic(new ImageView(mineImg));
    		}
    		
    		//set image to frowny
        	Image frownyImg = new Image(this.getClass().getResourceAsStream("deadFrowny.png"),
    				btn.getWidth(), btn.getHeight(), true, true);
    		image_smiley.setImage(frownyImg);
    		//lock all squares
    		btn.setMouseTransparent(true);
        	btn.setFocusTraversable(false);	
    		//stop timer
    		if(task.isRunning()) 
        		task.cancel();
    	}
    }

    
    /**
     * Start a new timer
     */
    private void startTask() {
    	//task to update the timer every second
        task = new Task<Void>() {
            public Void call() throws InterruptedException {
            	//TODO: change time to format "00:00"
            	//LocalTime time = LocalTime.MIN;
            	//String timeString;
            	//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
                int i = 0;
            	while(!isCancelled()) {
                    //timeString = time.getMinute() + ":" + time.getSecond();
                	//updateMessage(timeString);
                    updateMessage(String.valueOf(i));
                    i++;
					//LocalTime time = LocalTime.parse(timeString, formatter);
			    	//time.plusSeconds(1);
                    if (isCancelled()) 
                        break;
			    	Thread.sleep(1000);
                }
                return null;
            }
        };
        label_timer.textProperty().bind(task.messageProperty());
    	if(task.isRunning())  //kill the current task if there already is one
    		task.cancel();
    	Thread timerThread = new Thread(task);
    	timerThread.start();
    	//TODO: kill task on close button even if game is in progress
    }
    
}  
