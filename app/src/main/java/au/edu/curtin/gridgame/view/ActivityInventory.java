package au.edu.curtin.gridgame.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import au.edu.curtin.gridgame.R;
import au.edu.curtin.gridgame.model.Equipment;
import au.edu.curtin.gridgame.model.Food;
import au.edu.curtin.gridgame.model.GameMap;
import au.edu.curtin.gridgame.model.Item;
import au.edu.curtin.gridgame.model.ListItem;
import au.edu.curtin.gridgame.model.Player;

public class ActivityInventory extends AppCompatActivity
{
    private Player player;
    private GameMap gameMap;

    private TextView labelStatus;
    private TextView labelCash;
    private TextView labelHealth;
    private TextView labelMass;

    private Button buttonBack;

    private ListView itemList;
    private ArrayList<ListItem> itemsValList;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Inventory");
        setContentView(R.layout.activity_inventory);

        this.player = (Player)getIntent().getSerializableExtra("player");
        this.gameMap = (GameMap)getIntent().getSerializableExtra("gameMap");

        this.labelCash    = (TextView) findViewById(R.id.labelCash);
        this.labelHealth  = (TextView) findViewById(R.id.labelHealth);
        this.labelMass    = (TextView) findViewById(R.id.labelMass);
        this.buttonBack   = (Button)   findViewById(R.id.buttonBack);
        this.labelStatus  = (TextView) findViewById(R.id.labelStatus);

        this.labelCash.setText(Integer.toString(player.getCash()));
        this.labelHealth.setText(Double.toString(player.getHealth()));
        this.labelMass.setText(Double.toString(player.getEquipmentMass()));

        this.itemList = (ListView) findViewById(R.id.listItems);

        //Setup list of items
        this.itemsValList = new ArrayList<ListItem>();
        this.listAdapter = new ListAdapter(this, itemsValList);
        this.itemList.setAdapter(listAdapter);

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final Item item = player.getEquipment().get(position-1);

                if(gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].isTown()) //market
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventory.this);
                    builder.setTitle("Confirm Sale");
                    builder.setMessage("Please confirm if you wish to sell " + item.getDescription() + " for $" + item.getValue()*0.75);

                    builder.setPositiveButton("Sell", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            player.setCash(player.getCash() + Double.valueOf(item.getValue()*0.75).intValue()); // add Cash
                            player.removeEquipment((Equipment)item);
                            gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].addItem(item);
                            updateInventory();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else // wilderness
                {
                    player.removeEquipment((Equipment)item);
                    gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].addItem(item);
                }
                updateInventory();
            }
        });

        updateInventory();

        if(gameMap.getGrid()[player.getColLocation()][player.getRowLocation()].isTown()) {
            labelStatus.setText("Tap items to sell at the market. (75% of value)");
        } else {
            labelStatus.setText("Tap items to drop. It doesn't appear that anyone is here to buy from you...");
        }

        this.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goBack();
            }
        });
    }

    private void updateInventory()
    {
        itemsValList.clear();
        itemsValList.add(new ListItem(null,"Description","Health\n/Poison","Mass", "Value"));

        for(Item playerItem : player.getEquipment())
        {
            ListItem newItem = new ListItem(playerItem,
                                            playerItem.getDescription(),
                                            "NA",
                                            Double.toString(((Equipment)playerItem).getMass()),
                                            Integer.toString(playerItem.getValue()));
            itemsValList.add(newItem);
        }
        listAdapter.notifyDataSetChanged();

        this.labelCash.setText(Integer.toString(player.getCash()));
        this.labelHealth.setText(Double.toString(player.getHealth()));
        this.labelMass.setText(Double.toString(player.getEquipmentMass()));
    }

    private void goBack()
    {
        Intent intentItems = new Intent(ActivityInventory.this, ActivityItems.class);
        intentItems.putExtra("player", player);
        intentItems.putExtra("gameMap", gameMap);
        startActivity(intentItems);
    }

    // Make sure the back button performs the correct action.
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            goBack();
            return true;
        }
        return super.onKeyDown( keyCode, event );
    }
}
