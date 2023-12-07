package org.example.BusinessLogic.GameData;

import java.util.*;

public class Field
{
    Field (int width, int height,int maxFoods)
    {
        this.width = width;
        this.height = height;
        this.maxFoods=maxFoods;
    }

    public ArrayList<Coords> getFoods() {return new ArrayList<>(foods);}
    public int getWidth()
    {
        return width;
    }
    public int getHeight()
    {
        return height;
    }
    public int getMaxFoods() {
        return maxFoods;
    }

    public void setFoods(ArrayList<Coords> coords)
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

    public void spawnFoods(ArrayList<Coords> busysCoords)
    {
        if(foods.size()<maxFoods)
        {
            busysCoords.addAll(foods);
            List<Coords> shuffledCoords = generateShuffledCoords(width-1, height-1);

//            Set<Coords> busyCoordsSet = new HashSet<>(busysCoords);
//
//            for (Coords coord : shuffledCoords) {
//                if (!busyCoordsSet.contains(coord)) {
//                    foods.add(coord);
//                    //busyCoordsSet.add(coord);
//
//                    if (foods.size() >= maxFoods) {
//                        break;
//                    }
//                }
//            }
//
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


    private List<Coords> generateShuffledCoords(int maxX, int maxY)
    {
        List<Coords> allCoords = new ArrayList<>();

        for (int x = 0; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                allCoords.add(new Coords(x, y));
            }
        }

        Collections.shuffle(allCoords);

        return allCoords;
    }

    private final int width;
    private final int height;
    private final int maxFoods;
    private final ArrayList<Coords> foods=new ArrayList<Coords>();
    private final Random random = new Random();
}
