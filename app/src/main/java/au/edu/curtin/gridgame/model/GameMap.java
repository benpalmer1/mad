package au.edu.curtin.gridgame.model;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class GameMap  implements Serializable
{
    private static final int TERRAIN_COUNT = 5; // Represents different backgrounds, for towns or wilderness.

    private Area[][] grid;
    private Player player;
    private static GameMap instance;

    private int xMax;
    private int yMax;
    private String itemList;

    /**
     * Build GameMap object as per given dimensions.
     * Calls generateArea & generateItems to randomly generate areas, items and terrain for each area.
     * @param xDimension
     * @param yDimension
     */
    public GameMap(String itemList, int xDimension, int yDimension) throws IOException
    {
        this.grid = new Area[xDimension][yDimension];
        this.itemList = itemList;
        this.xMax = xDimension;
        this.yMax = yDimension;

        for(int ii = 0; ii < xDimension; ii++)
        {
            for(int jj = 0; jj < yDimension; jj++)
            {
                this.grid[ii][jj] = generateArea();
            }
        }

        generateItems();
    }

    private Area generateArea()
    {
        int terrain = Double.valueOf(Math.random()*TERRAIN_COUNT+1).intValue(); // Generate random terrain value.

        if(Double.valueOf(Math.random()*2+1).intValue() == 1) // TOWN
        {
            return new Area(true, terrain);
        }
        else
        {
            return new Area(false, terrain);
        }
    }

    private void generateItems() throws IOException
    {
        BufferedReader br = null;
        String newLine;
        List<Item> foods = new ArrayList<>();
        List<Item> equipment = new ArrayList<>();
        List<Item> items = new ArrayList<>();

        try {

            br = new BufferedReader(new StringReader(itemList));

            while ((newLine = br.readLine()) != null) {
                newLine = newLine.trim();
                if (!newLine.startsWith("#")) //comment line
                {
                    String[] item = newLine.split(","); // split each field on a comma, file guaranteed to not contain commas elsewhere
                    if (item[0].equals("food")) {
                        foods.add(new Food(Double.parseDouble(item[2].trim()), Double.parseDouble(item[3].trim()), Integer.parseInt(item[4].trim()), item[1].trim()));
                    } else if (item[0].equals("equipment")) {
                        equipment.add(new Equipment(Double.parseDouble(item[3].trim()), Integer.parseInt(item[2].trim()), item[1].trim(), Boolean.parseBoolean(item[4].trim())));
                    } else {
                        throw new IOException("Incorrectly formatted CSV file.");
                    }
                }
            }

            //Fill the map with standard items
            for (int xx = 0; xx < xMax; xx++) {
                for (int yy = 0; yy < yMax; yy++) {
                    // Add up to ten random items to the area, that aren't the winning items
                    for (int ii = 0; ii < Double.valueOf(Math.random() * 10 + 1).intValue(); ii++) {
                        if (Double.valueOf(Math.random() * 2 + 1).intValue() == 1) // food
                        {
                            items.add(foods.get(Double.valueOf(Math.random() * (foods.size())).intValue()));
                        } else // equipment
                        {
                            // Random ID value for the equipment
                            int randomEquip = (Double.valueOf(Math.random() * (equipment.size()))).intValue();

                            // If required
                            if (!((Equipment) equipment.get(randomEquip)).isRequired()) {
                                items.add(equipment.get(randomEquip));
                            } else  // don't add - decrement loop to run again.
                            {
                                ii--;
                            }
                        }
                    }

                    this.grid[xx][yy].addItems(items);
                    items.clear();
                }
            }

            //Randomly add the winning items to the map
            for (int ii = 0; ii < equipment.size(); ii++) {
                if (((Equipment) equipment.get(ii)).isRequired()) {
                    this.grid[Double.valueOf(Math.random() * (xMax)).intValue()][Double.valueOf(Math.random() * (yMax)).intValue()].addItem(equipment.get(ii));
                }
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new IOException(e.getMessage());
                }
            }
        }
    }

    public Area[][] getGrid()
    {
        return this.grid;
    }

    public void setGrid(Area[][] newGrid)
    {
        this.grid = newGrid;
    }
}