package org.example.BusinessLogic.Network.Data;

public class DataTimeSend
{
    public long getTimeLastSend()
    {
        return timeLastSend;
    }

    public void updateTimeSend()
    {
        timeLastSend=System.currentTimeMillis();
    }

    private long timeLastSend=0;

}
