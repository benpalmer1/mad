package au.edu.curtin.gridgame.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.edu.curtin.gridgame.R;
import au.edu.curtin.gridgame.model.Equipment;
import au.edu.curtin.gridgame.model.GameMap;
import au.edu.curtin.gridgame.model.Item;
import au.edu.curtin.gridgame.model.Player;

public class ActivityNavigationMain extends AppCompatActivity
{
    /*Game Data*/
    private GameMap gameMap;
    private Player player;

    private static final int X_DIMENSION = 3;
    private static final int Y_DIMENSION = 5;
    private static final int STARTING_CASH = 25;
    public static final List<String> WINNING_ITEMS = new ArrayList<String>(Arrays.asList("Jade monkey", "Roadmap", "Ice scraper"));

    /*UI Labels*/
    private TextView labelLocation;
    private TextView labelStatus;
    private TextView labelCash;
    private TextView labelHealth;
    private TextView labelMass;

    /*UI Buttons*/
    private Button buttonOptions;
    private Button buttonRestart;
    private Button buttonNorth;
    private Button buttonEast;
    private Button buttonSouth;
    private Button buttonWest;

    /*Background*/
    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        getSupportActionBar().setTitle("Navigation");

        // Check if initial, or return from a market/wilderness.
        if (getIntent().getSerializableExtra("player") != null && getIntent().getSerializableExtra("gameMap") != null) // a return from market/wilderness
        {
            this.player = (Player)getIntent().getSerializableExtra("player");
            this.gameMap = (GameMap)getIntent().getSerializableExtra("gameMap");

            // Check if the player has lost.
            if(Math.round(this.player.getHealth()) <= 0)
            {
                gameLost();
            }

            setupViews();
            reStart();

            // Check if the player has won.
            List<String> itemsByName = new ArrayList<>();
            List<String> winningItems = WINNING_ITEMS;
            for(Item item : player.getEquipment())
            {
                itemsByName.add(item.getDescription());
            }

            if(itemsByName.containsAll(winningItems))
            {
                gameWon();
            }
        }
        else // Initial app startup.
        {
            // Show instruction dialog.
            showHelpDialog();
            // view related actions
            setupViews();
            // Start the game, randomly generate map.
            startInitial();
        }
    }

    // Retrieve the intent necessary to return to the navigation activity.
    // public static void getIntent() ...

    // Reset the game to previous values
    private void reStart()
    {
        enablePlayDirections();

        // Set up UI labels and graphics
        setBackground(player.getColLocation(),player.getRowLocation());
        labelHealth.setText(Double.valueOf(player.getHealth()).toString());
        labelMass.setText(Double.valueOf(player.getEquipmentMass()).toString());
        labelCash.setText(Double.valueOf(player.getCash()).toString());
        setLocationLabel(player.getColLocation(),player.getRowLocation());
        setupOptionsButton();
    }

    // Created to allow the game to be easily reset back to starting values.
    private void startInitial()
    {
        try
        {
            this.gameMap = new GameMap(getString(R.string.item_list), X_DIMENSION,Y_DIMENSION);
        }
        catch(IOException e) {
            ActivityNavigationMain.toastShort(this, "Error: " + e.getMessage());
            this.finishAffinity(); // Error loading the game map, quit immediately.
        }

        int randomX = Double.valueOf(Math.random()*X_DIMENSION).intValue();
        int randomY = Double.valueOf(Math.random()*Y_DIMENSION).intValue();
        this.player = new Player(randomX, randomY, STARTING_CASH, 100, 0, new ArrayList<Equipment>());

        enablePlayDirections();

        // Set up UI labels and graphics
        setBackground(randomX,randomY);
        labelHealth.setText(Double.valueOf(player.getHealth()).toString());
        labelMass.setText(Double.valueOf(player.getEquipmentMass()).toString());
        labelCash.setText(Double.valueOf(player.getCash()).toString());
        setLocationLabel(randomX, randomY);
        setupOptionsButton();
    }

    public void showHelpDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Y'all need some help?");
        builder.setMessage("Press continue if you need a quick guide to play the game.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showGameInstructions();
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showGameInstructions()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Guide");
        builder.setMessage("To play, move around the map with the north/south/east/west buttons.\nMoving will take a small amount of health on every step, and more will be taken depending on how much equipment you have.\nOn a town area, you can buy or sell your items, and on a wilderness area you can pick up and drop items.\nFood will be consumed automatically and cannot be stored in your inventory. Poison refers to the chance of losing health after eating the food.\n\nTo win, you must find the Jade monkey, Roadmap and Ice scraper.");

        // add a button
        builder.setPositiveButton("OK", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void optionsClicked()
    {
        Intent intentItems = new Intent(ActivityNavigationMain.this, ActivityItems.class);
        intentItems.putExtra("player", player);
        intentItems.putExtra("gameMap", gameMap);
        startActivity(intentItems);
    }

    private void setupViews()
    {
        this.labelLocation   = (TextView) findViewById(R.id.labelLocation);
        this.labelStatus     = (TextView) findViewById(R.id.labelStatus);
        this.labelCash       = (TextView) findViewById(R.id.labelCash);
        this.labelHealth     = (TextView) findViewById(R.id.labelHealth);
        this.labelMass       = (TextView) findViewById(R.id.labelMass);

        this.buttonOptions   = (Button) findViewById(R.id.buttonOptions);
        this.buttonRestart   = (Button) findViewById(R.id.buttonRestart);
        this.buttonNorth     = (Button) findViewById(R.id.buttonNorth);
        this.buttonEast      = (Button) findViewById(R.id.buttonEast);
        this.buttonSouth     = (Button) findViewById(R.id.buttonSouth);
        this.buttonWest      = (Button) findViewById(R.id.buttonWest);

        this.background      = (ImageView) findViewById(R.id.background);

        buttonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Open market or wilderness activity depending on the location.
                optionsClicked();
            }
        });

        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Restart the game to defaults and reset the map.
                labelStatus.clearAnimation();
                labelStatus.setText("");
                buttonOptions.setEnabled(true);
                startInitial();
            }
        });

        buttonNorth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                travel(player.getColLocation(), player.getRowLocation() - 1);
            }
        });

        buttonEast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                travel(player.getColLocation() + 1, player.getRowLocation());
            }
        });

        buttonSouth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                travel(player.getColLocation(), player.getRowLocation() + 1);
            }
        });

        buttonWest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                travel(player.getColLocation() - 1, player.getRowLocation());
            }
        });
    }

    private void setupOptionsButton()
    {
        if(gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].isTown())
        {
            buttonOptions.setText("Market Items");
        } else {
            buttonOptions.setText("Wilderness Items");
        }
    }

    // Travel function. Moves the player between origin and destination.
    // Deducts health from the player.
    private void travel(int destX, int destY)
    {
        //Check desired grid square is valid
        if(gameMap.getGrid()[destX][destY] != null)
        {
            //Deduct health from the player
            double resultHealth = Math.max(0.0, player.getHealth() - 5.0 - (player.getEquipmentMass() / 2.0));
            player.setHealth(resultHealth);

            //Change health label - rounded to 2 dp
            labelHealth.setText(Double.toString(Math.round(resultHealth * 100)/100));

            if(resultHealth > 0.0)
            {
                player.setColLocation(destX);
                player.setRowLocation(destY);

                //Disable buttons not available due to player location
                enablePlayDirections();

                //Change background to next one
                setBackground(destX, destY);

                //Change location label
                setLocationLabel(destX, destY);
            }
            else // Player lost
            {
                gameLost();
            }
        }
        else
        {
            throw new IllegalArgumentException("Error: Grid cell requested outside of available range.");
        }
    }

    /* From the player's current location, disable buttons that aren't available for the player to travel to.
    * Mapped from column X0, row Y0 - the top left of the map.*/
    private void enablePlayDirections()
    {
        //north
        if(player.getRowLocation() > 0)
        {
            buttonNorth.setEnabled(true);
        }
        else
        {
            buttonNorth.setEnabled(false);
        }

        //east
        if(player.getColLocation() < (gameMap.getGrid().length - 1))
        {
            buttonEast.setEnabled(true);
        }
        else
        {
            buttonEast.setEnabled(false);
        }

        //south
        if(player.getRowLocation() < (gameMap.getGrid()[0].length - 1))
        {
            buttonSouth.setEnabled(true);
        }
        else
        {
            buttonSouth.setEnabled(false);
        }

        //west
        if(player.getColLocation() > 0)
        {
            buttonWest.setEnabled(true);
        }
        else
        {
            buttonWest.setEnabled(false);
        }
    }

    private void setBackground(int x, int y)
    {
        if(gameMap.getGrid()[x][y].isTown())
        {
            switch (gameMap.getGrid()[x][y].getTerrain())
            {
                case 1:
                    background.setImageResource(R.mipmap.town1);
                    break;
                case 2:
                    background.setImageResource(R.mipmap.town2);
                    break;
                case 3:
                    background.setImageResource(R.mipmap.town3);
                    break;
                case 4:
                    background.setImageResource(R.mipmap.town4);
                    break;
                case 5:
                    background.setImageResource(R.mipmap.town5);
                    break;
            }
        }
        else {
            switch (gameMap.getGrid()[x][y].getTerrain())
            {
                case 1:
                    background.setImageResource(R.mipmap.wilderness1);
                    break;
                case 2:
                    background.setImageResource(R.mipmap.wilderness2);
                    break;
                case 3:
                    background.setImageResource(R.mipmap.wilderness3);
                    break;
                case 4:
                    background.setImageResource(R.mipmap.wilderness4);
                    break;
                case 5:
                    background.setImageResource(R.mipmap.wilderness5);
                    break;
            }
        }
    }

    private void setLocationLabel(int x, int y)
    {
        //Change location label
        if(gameMap.getGrid()[x][y].isTown())
        {
            labelLocation.setText("town");
        }
        else
        {
            labelLocation.setText("wilderness");
        }
        setupOptionsButton();
    }

    // Display a toast in the specified context.
    public static void toastShort(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void toastLong(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public GameMap getGameMap()
    {
        return gameMap;
    }

    public Player getPlayer()
    {
        return player;
    }

    private void gameWon()
    {
        labelStatus.setText("YOU WON!");
        labelStatus.setTextColor(Color.GREEN);
        labelStatus.setAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));
        disableTravel();
    }

    private void gameLost()
    {
        labelStatus.setText("YOU LOST!");
        labelStatus.setTextColor(Color.RED);
        labelStatus.setAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));
        disableTravel();
    }

    private void disableTravel()
    {
        buttonOptions.setEnabled(false);
        buttonNorth.setEnabled(false);
        buttonEast.setEnabled(false);
        buttonSouth.setEnabled(false);
        buttonWest.setEnabled(false);
    }
}