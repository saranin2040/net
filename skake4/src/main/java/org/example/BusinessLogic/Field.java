package org.example.BusinessLogic;

import java.util.*;

public class Field
{
    Field (int width, int height,int maxFoods)
    {
        this.width = width;
        this.height = height;
        this.maxFoods=maxFoods;
        spawnFoods(new ArrayList<Coords>());
    }
    public void spawnFoods(ArrayList<Coords> busysCoords)
    {
        if(foods.size()<maxFoods)
        {
            busysCoords.addAll(foods);
            List<Coords> shuffledCoords = generateShuffledCoords(width-1, height-1);

            for (Coords coords : busysCoords) {
                shuffledCoords.remove(coords);
            }


            Iterator<Coords> iterator = shuffledCoords.iterator();

            for (int i = foods.size(); i < maxFoods; i++) {
                if (iterator.hasNext())
                {
                    Coords randomCoords = iterator.next();
                    foods.add(randomCoords);
                }
                else {
                    break;
                }
            }
        }
    }

    public void removeFood(Coords coords)
    {
        foods.remove(coords);
    }
    public ArrayList<Coords> getFoods()
    {
        return (ArrayList<Coords>)foods.clone();
    }

    public int getWidth()
    {
        return width;
    }
    public int getHeight()
    {
        return height;
    }

    private List<Coords> generateShuffledCoords(int maxX, int maxY) {
        List<Coords> allCoords = new ArrayList<>();

        // Заполнение списка всеми возможными координатами
        for (int x = 0; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                allCoords.add(new Coords(x, y));
            }
        }

        // Перемешивание списка
        Collections.shuffle(allCoords);

        return allCoords;
    }

    public int getMaxFoods() {
        return maxFoods;
    }


    public void setFood(ArrayList<Coords> coords)
    {
        foods.clear();
        foods.addAll(coords);
    }

    public void spawnRandomFoods(ArrayList<Coords> coords)
    {
        for (Coords coord:coords)
        {
            if (random.nextBoolean())
            {
                foods.add(coord);
            }
        }
    }
    private int width;
    private int height;
    private int maxFoods=10;
    private ArrayList<Coords> foods=new ArrayList<Coords>();
    private static final Random random = new Random();

}
