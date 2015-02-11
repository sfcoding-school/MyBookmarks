package marcomessini.mybookmarks;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import java.util.ArrayList;


public class MainP extends ActionBarActivity{

    ListView listView ;
    DataBaseManager db=new DataBaseManager(this);
    GroupsAdapter adapter;
    ArrayList<Group> values1 = null;
    public String url;
    long timer;
    int pos;
    AlarmManager alarmManager;
    PendingIntent pending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_p);

        //alarm & Service
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this,OnAlarmReceiver.class);
        pending = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        //set timer
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        timer=pref.getLong("TIMER",-1);
        Log.e("SET TIMER", " ON CREATE");
        if (timer!=-1) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, timer, timer, pending);
        }
        Log.e("SET TIMER", " ON CREATE");

        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        //        SystemClock.elapsedRealtime() +
        //                10 * 1000, pending);
                                //-------------//
        setTitle("MyBookmarks - GROUP LIST -");



        //Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        //values1= db.getGroup();
                //new Group(1,"social",3)
        /*if (values1.isEmpty()){
            Log.e("ArrayList Gruppo","vuoto");
            new AlertDialog.Builder(MainP.this)
                    .setTitle("There aren't any Groups")
                    .setPositiveButton("Add Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent ActivityAddGroup = new Intent(MainP.this, AddGroup.class);
                            startActivityForResult(ActivityAddGroup, 1);
                        }
                    })
                    .show();
        }*/

        //adapter = new GroupsAdapter(this, values1);
        //adapter.notifyDataSetChanged();

        // Assign adapter to ListView
        //listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int idG=values1.get(position).id_group;
                String nomeGroup=values1.get(position).name;
                Intent newActivity = new Intent(MainP.this, ListWebSIte.class);
                newActivity.putExtra("id_gruppo",idG);
                newActivity.putExtra("nome_gruppo",nomeGroup);
                //parte l'actuvity
                startActivity(newActivity);
            }

        });

        //per eliminare il gruppo pressione prolungata
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainP.this)
                        .setTitle("Delete Group")
                        .setMessage("Are you sure you want to delete this group?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //funzione per eliminare il gruppo
                                int idGroup=values1.get(position).id_group;
                                boolean valG=db.delGroup(idGroup);
                                Log.i("valore query delG",Boolean.toString(valG));
                                adapter.remove(adapter.getItem(position));
                                //adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        values1= db.getGroup();
        if (values1.isEmpty()){
            Log.e("ArrayList Gruppo","vuoto");
            new AlertDialog.Builder(MainP.this)
                    .setTitle("There aren't any Groups")
                    .setPositiveButton("Add Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent ActivityAddGroup = new Intent(MainP.this, AddGroup.class);
                            startActivityForResult(ActivityAddGroup, 1);
                        }
                    })
                    .show();
        }
        adapter = new GroupsAdapter(this, values1);
        //adapter.notifyDataSetChanged();

        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                //recupero i dati
                String name=data.getStringExtra("newNameGroup");
                //eseguo la query di inserimento
                long idWS=DataBaseManager.addGroup(name);
                //inserisci dentro il content value
                int id_G=(int)idWS;
                adapter.add(new Group(id_G,name,0));
            }
            if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    /*@Override
    public void done(int hash, WebSite webSite){
        int newHash=hash;
        int oldHash=webSite.hash;
        if(newHash!=oldHash){
            Log.e("HASH MODIFICATO "+webSite.id_WebSite," new H "+newHash+" old H"+oldHash);
            db.setCheckWS(webSite.id_WebSite,1);
            db.updateHash(webSite.id_WebSite,newHash);
            webSite.check=1;
            adapter.notifyDataSetChanged();
        }
        else{
            Log.e("HASH INVARIATO "+webSite.id_WebSite," old H"+oldHash);
            db.setCheckWS(webSite.id_WebSite,0);
            webSite.check=0;
            adapter.notifyDataSetChanged();
        }
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            //Put the code for an action menu from the top here
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //set alarm
    public void setAlarm(long timerS, AlarmManager alarmM,PendingIntent pend){
        if(timerS!=-1){
            alarmM.cancel(pend);
            alarmM.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,timerS,timerS, pend);
            Log.e("ALARM SET","-DISABLE-"+timerS);
        }
        else{
            alarmM.cancel(pend);
            Log.e("ALARM SET","-DISABLE-");
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addGroup) {
            Intent ActivityAddGroup = new Intent(MainP.this, AddGroup.class);
            startActivityForResult(ActivityAddGroup, 1);
        }
        if (id == R.id.action_setTimer) {
            final CharSequence[] items = {"2 Min","15 Min", "30 Min", "1 Hour","2 Hours", "Disable Service"};
            final long[] itemPos = {60*2*1000 , 60*15*1000 , 60*30*1000 , 60*60*1000 , 60*120*1000 ,-1};
            final SharedPreferences pref = getPreferences(MODE_PRIVATE);
            final SharedPreferences.Editor edit= pref.edit();
            long timerPos=pref.getLong("TIMER",-1);
            for(int i=0;i<=items.length-1;i++){
                if(itemPos[i]==timerPos){
                    pos=i;
                }
            }
            //sistema cancella FOR
            new AlertDialog.Builder(this)
            .setTitle("Set Timer for UpDate")
            .setSingleChoiceItems(items, pos, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case (0):
                            edit.putLong("TIMER", 60 * 2 * 1000);
                            edit.commit();
                            setAlarm(60*2*1000,alarmManager,pending);
                            break;
                        case (1):
                            edit.putLong("TIMER", 60 * 15 * 1000);
                            edit.commit();
                            setAlarm(60*15*1000,alarmManager,pending);
                            break;
                        case (2):
                            edit.putLong("TIMER", 60 * 30 * 1000);
                            edit.commit();
                            setAlarm(60*30*1000,alarmManager,pending);
                            break;
                        case(3):
                            edit.putLong("TIMER", 60 * 60 * 1000);
                            edit.commit();
                            setAlarm(60*60*1000,alarmManager,pending);
                            break;
                        case(4):
                            edit.putLong("TIMER", 60 * 120 * 1000);
                            edit.commit();
                            setAlarm(60*120*1000,alarmManager,pending);
                            break;
                        case(5):
                            edit.putLong("TIMER", -1);
                            edit.commit();
                            setAlarm(-1, alarmManager, pending);
                            break;
                    }
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
