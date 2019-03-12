# Miner 49er

My version of minesweeper

### How to run
Run the main class `Miner49erMain.java`


On Eclipse: if you get the error "Access restriction: the type 'Application' is not API" do the following steps:
- Go to 'Project' > 'Properties' > 'Java Build Path' 
- Click the 'Libraries' tab and expand 'JRE System Library'
- Click 'Access rules' > 'Edit...' > 'Add...' 
- Set Resolution to 'Accessible' and Rule Pattern to javafx/**

## Game options
Miner49er currently allows changing the number of mines by modifying the `NUM_MINES` constant in the controller class. The grid size may be changed, but does not format correctly for anything larger than 10x10 at this time. 
Flags are supported, but unflagging and question marks are not. Clearing adjacent squares is currently non-functional. 
