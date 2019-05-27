package au.edu.curtin.gridgame.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.edu.curtin.gridgame.R;
import au.edu.curtin.gridgame.model.Equipment;
import au.edu.curtin.gridgame.model.Food;
import au.edu.curtin.gridgame.model.GameMap;
import au.edu.curtin.gridgame.model.Item;
import au.edu.curtin.gridgame.model.ListItem;
import au.edu.curtin.gridgame.model.Player;

public class ActivityItems extends AppCompatActivity
{
    private Player player;
    private GameMap gameMap;

    private TextView labelStatus;
    private TextView labelCash;
    private TextView labelHealth;
    private TextView labelMass;
    private Button buttonLeave;
    private Button buttonInv;

    private ImageView background;

    private ListView itemsViewList;
    private ArrayList<ListItem> itemsValList;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_interface);

        this.player = (Player)getIntent().getSerializableExtra("player");
        this.gameMap = (GameMap)getIntent().getSerializableExtra("gameMap");

        //Set health/weights/mass and activate buttons.
        setupViews();

        //Update List with the current area's items
        updateArea();
    }

    private void setupViews()
    {
        this.labelCash    = (TextView) findViewById(R.id.labelCash);
        this.labelHealth  = (TextView) findViewById(R.id.labelHealth);
        this.labelMass    = (TextView) findViewById(R.id.labelMass);
        this.buttonLeave  = (Button) findViewById(R.id.buttonLeave);
        this.buttonInv    = (Button) findViewById(R.id.buttonInventory);
        this.background   = (ImageView) findViewById(R.id.background);
        this.labelStatus  = (TextView) findViewById(R.id.labelStatus);
        this.itemsViewList = (ListView) findViewById(R.id.listItems);

        if(gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].isTown()) {
            getSupportActionBar().setTitle("Market");
            this.background.setImageResource(R.drawable.markettileres);
            labelStatus.setText("Tap item to buy");
        } else {
            getSupportActionBar().setTitle("Wilderness");
            this.background.setImageResource(R.drawable.wildernesstileres);
            labelStatus.setText("Tap item to pick up");
        }

        this.labelCash.setText(Integer.toString(player.getCash()));
        this.labelHealth.setText(Double.toString(player.getHealth()));
        this.labelMass.setText(Double.toString(player.getEquipmentMass()));

        //Setup list of items
        this.itemsValList = new ArrayList<ListItem>();
        this.listAdapter = new ListAdapter(this, itemsValList);
        itemsViewList.setAdapter(this.listAdapter);

        itemsViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final Item item = gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].getItems().get(position-1);

                if(gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].isTown()) //market
                {
                    if(player.getCash() >  item.getValue()) // buy item logic
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityItems.this);
                        builder.setTitle("Confirm Purchase");
                        builder.setMessage("Please confirm if you wish to purchase " + item.getDescription() + " for $" + item.getValue());

                        builder.setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                player.setCash(player.getCash() - item.getValue()); // deduct Cash

                                if(item instanceof Food) // Food
                                {
                                    player.eatFood((Food)item);
                                    gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].removeItem(item);
                                    if(player.getHealth() <= 0)
                                    {
                                        back();
                                    }
                                }
                                else // Equipment
                                {
                                    player.addEquipment((Equipment)item);
                                    gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].removeItem(item);
                                }

                                updateArea();
                            }
                        });
                        builder.setNegativeButton("Cancel", null);

                        // create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else
                    {
                        ActivityNavigationMain.toastShort(ActivityItems.this, "Sorry, not enough cash.");
                    }
                }
                else // wilderness
                {
                    if(item instanceof Food) { // Food
                        player.eatFood((Food)item);
                        gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].removeItem(item);
                    } else { // Equipment
                        player.addEquipment((Equipment)item);
                        gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].removeItem(item);
                    }
                }
                updateArea();
            }
        });

        updateArea();


        buttonLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Leave the areas
                back();
            }
        });

        buttonInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Open the activity_inventory
                openInventory();
            }
        });
    }

    private void openInventory()
    {
        Intent intentInventory = new Intent(ActivityItems.this, ActivityInventory.class);
        intentInventory.putExtra("player", this.player);
        intentInventory.putExtra("gameMap", this.gameMap);
        startActivity(intentInventory);
    }

    private void updateArea()
    {
        // Check if the player won
        List<String> itemsByName = new ArrayList<>();
        List<String> winningItems = ActivityNavigationMain.WINNING_ITEMS;
        for(Item playerItem : player.getEquipment())
        {
            itemsByName.add(playerItem.getDescription());
        }

        if(itemsByName.containsAll(winningItems))
        {
            back();
        }

        // Player hasn't won. Continue.
        itemsValList.clear();
        itemsValList.add(new ListItem(null,"Description","Health\n/Poison","Mass", "Value")); // Bit of a hack
        for(Item areaItem : gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].getItems())
        {
            if(areaItem instanceof Equipment) {
                ListItem newItem = new ListItem(areaItem,
                        areaItem.getDescription(),
                        "NA",
                        Double.toString(((Equipment)areaItem).getMass()),
                        Integer.toString(areaItem.getValue()));
                itemsValList.add(newItem);
            } else { // food
                ListItem newItem = new ListItem(areaItem,
                        areaItem.getDescription(),
                        ((Food)areaItem).getHealth()+"/"+((Food)areaItem).getPoison(),
                        "0",
                        Integer.toString(areaItem.getValue()));
                itemsValList.add(newItem);
            }
        }
        listAdapter.notifyDataSetChanged();

        this.labelCash.setText(Integer.toString(player.getCash()));
        this.labelHealth.setText(Double.toString(player.getHealth()));
        this.labelMass.setText(Double.toString(player.getEquipmentMass()));
    }

    // Make sure the hardware back button performs the correct action.
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            back();
            return true;
        }
        return super.onKeyDown( keyCode, event );
    }

    private void back()
    {
        Intent intentMain = new Intent(ActivityItems.this, ActivityNavigationMain.class);
        intentMain.putExtra("player", player);
        intentMain.putExtra("gameMap", gameMap);
        startActivity(intentMain);
    }
}