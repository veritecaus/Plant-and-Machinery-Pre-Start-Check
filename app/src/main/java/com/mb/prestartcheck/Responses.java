package com.mb.prestartcheck;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.ListIterator;

public class Responses {
    ArrayList<Response> responses = new ArrayList<Response>();

    private ComparerResponse comparerResponse = new ComparerResponse();

    public  Responses()
    {

    }

    public void clear()
    {
        this.responses.clear();
    }

    public  void add(Response e)
    {
        this.responses.add(e);
        Collections.sort(this.responses, this.comparerResponse);
    }

    public  Response getAt(int i)
    {
        return i>=0 && i < this.responses.size()  ?  this.responses.get(i) : null;
    }

    public  Response find(Question q)
    {
        for(int i = 0; i < this.responses.size(); i++)
        {
            if (this.responses.get(i).getQuestion().equals(q))
            {
                return this.responses.get(i);
            }
        }

        return null;
    }

    public  boolean toFile(Context cx)
    {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(cx.openFileOutput(
                    FileName.get(Session.getInstance().getUser()), Context.MODE_PRIVATE));
            User user = Session.getInstance().getUser();
            String newline =  System.getProperty("line.separator");

            for(Response response : this.responses)
            {
                outputStreamWriter.write(SerializerCSV.build(response, user));
                outputStreamWriter.write(newline);
            }
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
        catch(FileNotFoundException e)
        {
            Log.e(App.TAG, e.getMessage());
        }
        catch(IOException e)
        {
            Log.e(App.TAG, e.getMessage());
        }
        return true;

    }

   Iterable<Response> getIterable() { return this.responses;}



}
