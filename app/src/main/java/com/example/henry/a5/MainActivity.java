package com.example.henry.a5;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    RecyclerView lista;
    List<Item> datos=new ArrayList<Item>();
    Button respaldar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lista=findViewById(R.id.lista);
        respaldar=findViewById(R.id.respaldar);
        //Obtener el historial de llamadas
        Cursor mCursor=managedQuery(CallLog.Calls.CONTENT_URI,null,null,null,null);
        int number=mCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date=mCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration=mCursor.getColumnIndex(CallLog.Calls.DURATION);
        int type=mCursor.getColumnIndex(CallLog.Calls.TYPE);
        while(mCursor.moveToNext()){
            String numbertxt=mCursor.getString(number);
            String datetxt=mCursor.getString(date);
            String durationtxt=mCursor.getString(duration);
            String typetxt=mCursor.getString(type);
            switch (Integer.parseInt(typetxt)){
                case CallLog.Calls.OUTGOING_TYPE:
                    typetxt="Llamada";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    typetxt="Recibida";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    typetxt="Perdida";
                    break;
            }

            datos.add(new Item(numbertxt,datetxt,durationtxt,typetxt));
        }

        Adapter adapter=new Adapter(datos);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(MainActivity.this);
        lista.setLayoutManager(layoutManager);
        lista.setAdapter(adapter);

        respaldar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db= FirebaseDatabase.getInstance().getReference();
                for(int i=0;i<datos.size();i++){
                    db.child(i+"").child("number").setValue(datos.get(i).getNumber());
                    db.child(i+"").child("date").setValue(datos.get(i).getDate().toString());
                    db.child(i+"").child("duration").setValue(datos.get(i).getDuration());
                    db.child(i+"").child("type").setValue(datos.get(i).getType());
                }
                Toast.makeText(getApplicationContext(),"Respaldo completo",Toast.LENGTH_SHORT).show();
            }
        });


    }


}
