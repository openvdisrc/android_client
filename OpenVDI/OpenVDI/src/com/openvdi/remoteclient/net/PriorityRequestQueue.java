package com.openvdi.remoteclient.net;

import java.util.ArrayList;

public class PriorityRequestQueue 
{
    public final String TAG = getClass().getSimpleName();

    public static int MAX_CONNECTION_TIMEOUT_CURRENT_REQUEST = 5 * 60 * 1000;	

    private ArrayList<Request> queue = new ArrayList<Request>();

    public PriorityRequestQueue() {}

    public void addRequest(Request request) 
    {
        try 
        {					
            synchronized (queue) 
            {			
                queue.add(request);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRequestFirst(Request request) 
    {
        try 
        {			
            synchronized (queue) 
            {			
                if(queue.size() > 0)
                {                    
                        queue.add(0, request);
                }
                else
                    queue.add(0, request);
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }


    public boolean isEmpty() 
    {
        return queue.isEmpty();
    }

    public Request getFirst() 
    {
        if (queue.size() > 0) 
            return queue.remove(0);
        else 
            return null;
    }

    public void clear() 
    {
        for(int i = 0; i< queue.size();i++)
        {
            Request rs = queue.get(i);
            rs.cancelRequest();
        }		
        queue.clear();
    }
}
