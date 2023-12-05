package org.example.BusinessLogic.Network.Data;

public class DataGameConfig
{
    public DataGameConfig(int width, int height, int countFood, int stateDelayMs)
    {
        this.width = width;
        this.height = height;
        this.countFood = countFood;
        this.stateDelayMs = stateDelayMs;
    }

    public DataGameConfig cloneSelf() {
        return new DataGameConfig(this.width,this.height,this.countFood,this.stateDelayMs);
    }

     public int width;
     public int height;
     public int countFood;
     public int stateDelayMs;
}
