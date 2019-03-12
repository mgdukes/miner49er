/**
 * Author: Meghan Dukes
 * Last modified: 3/11/19
 * A program to play the game minesweeper
 */
package miner49er;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Miner49erMain extends Application{
	
	public void start(Stage stage) throws Exception
	   {  
	      // Load the FXML file.
	      Parent parent = FXMLLoader.load(
	               getClass().getResource("miner49erGUI.fxml")); 
	         
	      // Build the scene graph.
	      Scene scene = new Scene(parent);
	      // Display the window, using the scene graph.
	      stage.setTitle("Miner 49er"); 
	      stage.setScene(scene);
	      stage.show(); 
	   }
	
	public static void main(String[] args) {
		launch(args);
		// Launch the application
	}

}
