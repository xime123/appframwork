package com.yap.webapp.was.webruntime;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class ParcelableMap implements Parcelable
{
    private Map<String,String> args;
    
    public ParcelableMap()
    {
    }
    
    public ParcelableMap(Map<String,String> args)
    {
        this.args = args;
    }
    
    public Map<String,String> getArgs()
    {
        return this.args;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        int count = args == null ? 0 : args.keySet().size();
        dest.writeInt(count);
        
        if(count==0) return;
        
        for(String key:args.keySet())
        {
            String value = args.get(key);
            dest.writeString(key);
            dest.writeString(value);
        }
    }
    
    public void readFromParcel(Parcel source)
    {
        HashMap<String,String> args = new HashMap<String,String>();
        int count = source.readInt();
        for(int i=0;i<count;i++)
        {
            String key = source.readString();
            String value = source.readString();
            args.put(key, value);
        }
        this.args = args;
    }

    public static final Creator<ParcelableMap> CREATOR = new Creator<ParcelableMap>() {

        @Override
        public ParcelableMap createFromParcel(Parcel source)
        {
            ParcelableMap startArgs =  new ParcelableMap();
            startArgs.readFromParcel(source);
            return startArgs;
        }

        @Override
        public ParcelableMap[] newArray(int size)
        {
            return new ParcelableMap[size];
        }

    };
    
}
