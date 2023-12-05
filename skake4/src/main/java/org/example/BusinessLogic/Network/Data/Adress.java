package org.example.BusinessLogic.Network.Data;

import org.example.BusinessLogic.Coords;

public class Adress
{
    public Adress(String ip, int port)
    {
        this.ip=ip;
        this.port=port;
    }

    public String getIp()
    {
        return  ip;
    }

    public int getPort()
    {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Adress other = (Adress) obj;
        return this.ip.equals(other.getIp()) && this.port == other.getPort();
    }


    String ip;
    int port;
}
