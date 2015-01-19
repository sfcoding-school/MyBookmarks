package marcomessini.mybookmarks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by marcomessini on 19/01/15.
 */
public class DataBaseManager {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = DataBaseManager.class.getSimpleName();

    private static Context context;
    private static SQLiteDatabase db;
    private static SQLiteHelperManager dbHelper;

    //nome tabelle

    //tab gruppi
    public static final String TABLE_GROUPS = "groups";
    //tab website
    public static final String TABLE_WEBSITE = "website";

    //nomi colonne

    //tab groups
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    //tab website
    public static final String KEY_IDWS ="id_ws";
    public static final String KEY_IDG = "id_group";
    public static final String KEY_NAMEWS = "nameWS";
    public static final String KEY_URL = "url";
    public static final String KEY_HASH = "hash";

    private static final String[] COLUMNS_GROUP = {KEY_ID,KEY_NAME};

    private static final String[] COLUMNS_WEBSITE = {KEY_IDWS,KEY_IDG,KEY_NAMEWS,KEY_URL,KEY_HASH};

    public DataBaseManager(Context context1) {
        context = context1;
    }

    //aprire connesione DB
    private static void open() throws SQLException {
        dbHelper = new SQLiteHelperManager(context);
        db = dbHelper.getWritableDatabase();
    }
    //chiudere connesione DB
    private static void close() {
        dbHelper.close();
    }

    //per tab gruppi
    private static ContentValues createContentValuesG(String name ) {
        ContentValues valuesG = new ContentValues();
        valuesG.put( KEY_NAME, name );

        return valuesG;
    }

    //per tab website
    private static ContentValues createContentValuesWS(int idG, String url, String nameWS, String hash) {
        ContentValues valuesWS = new ContentValues();
        valuesWS.put( KEY_IDG, idG );
        valuesWS.put( KEY_NAMEWS, nameWS );
        valuesWS.put( KEY_URL, url );
        valuesWS.put( KEY_HASH, hash );

        return valuesWS;
    }

    //funzioni per la tabella gruppi

    //add gruppo
    public static long addGroup(String name) {
        ContentValues initialValues = createContentValuesG(name);
        open();
        long res=db.insertOrThrow(TABLE_GROUPS, null, initialValues);
        close();
        return res;
    }

    //delete group
    public boolean delGroup(String name) {
        open();
        boolean res = db.delete(TABLE_GROUPS, KEY_NAME + "=" + "'"+ name +"'", null) > 0;
        close();
        return res;
    }

    //see table
    public static ArrayList<Group> getGroup() {
        open();
        Cursor cursor = db.query(TABLE_GROUPS, new String[]{KEY_ID, KEY_NAME}, null, null, null, null, null);
        ArrayList<Group> ris= new ArrayList<Group>();
        while (cursor.moveToNext()) {
            int ID = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String name= cursor.getString(cursor.getColumnIndex(KEY_NAME));
            //query numero siti
            ris.add(new Group(ID,name,0));
        }
        close();
        return ris;
    }


    //funzioni tabella WebSite

    public long addWebSite(int idG, String url, String nameWS, String hash){
        ContentValues initialValuesWS = createContentValuesWS(idG, url, nameWS, hash);
        open();
        long resW=db.insertOrThrow(TABLE_WEBSITE, null, initialValuesWS);
        close();
        return resW;
    }

    //show all WS
    public Cursor fetchAllWS() {
        return db.query(TABLE_WEBSITE, new String[] { KEY_IDWS, KEY_IDG, KEY_NAMEWS, KEY_URL, KEY_HASH }, null, null, null, null, null);
    }
}