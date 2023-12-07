package org.example.BusinessLogic.GameData;

public class Coords {
    Coords()
    {
        x=0;
        y=0;
    }
    public Coords(int x, int y)
    {
        this.x=x;
        this.y=y;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Coords other = (Coords) obj;
        return this.x == other.x && this.y == other.y;
    }

    public int x;
    public int y;
}
