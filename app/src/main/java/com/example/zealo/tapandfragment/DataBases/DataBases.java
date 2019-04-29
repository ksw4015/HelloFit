package com.example.zealo.tapandfragment.DataBases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.zealo.tapandfragment.Models.ChatItems;
import com.example.zealo.tapandfragment.Models.RoomData;
import com.example.zealo.tapandfragment.Models.UserProfile;

import java.util.ArrayList;

/**
 * Created by USER on 2017-12-01.
 */

public class DataBases {              // SQLite

    private final String DB_NAME = "chat_db.db";
    private final int DB_VER = 1;

    private Context mContext = null;
    private OpenHelper mOpener = null;
    private SQLiteDatabase mDB = null;

    private class OpenHelper extends SQLiteOpenHelper {       // .db 파일 생성 (DB이름, 버전 등등...)

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {     // DB 파일이 만들어 진 후 Table Create
            sqLiteDatabase.execSQL(DBSqlData.SQL_DB_CREATE_TABLE);            // 실제 대화가 정보가 들어가는 데이터베이스
            sqLiteDatabase.execSQL(DBSqlData.SQL_DB_CREATE_ROOM_TABLE);      // 채팅 상대방 아이디와 방번호만 들어가는 데이터베이스
            sqLiteDatabase.execSQL(DBSqlData.SQL_DB_CREATE_TEMP_TABLE);       // 메시지 보낸 상대가 친구가 아닌경우 Userprofile을 저장하기 위한 데이터베이스
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public DataBases(Context context) {
        this.mContext = context;
        this.mOpener = new OpenHelper(mContext, DB_NAME, null, DB_VER);   // 실제 DB생성은 DataBases 클래스의 생성자에서
    }

    public void dbOpen() {
        this.mDB = mOpener.getWritableDatabase();
    }

    public void dbClose() {
        this.mDB.close();
    }

    public void insertData(String sql, ChatItems mData) {
        String[] query = mData.chatArgs();
        this.mDB.execSQL(sql, query);       // String객체로 쿼리문을 실행하는것이 아닌 특정 타입으로 전달하면 injection공격을 어느정도 막아줄 수 있다
    }

    public void insertData(String sql, String[] room_data) {
        this.mDB.execSQL(sql, room_data);
    }

    public void insertData(String sql, UserProfile temp) {
        String[] values = {temp.getUser_ad_id(), temp.getUser_desc(), temp.getUser_nickname(), temp.getUser_profile_img()};
        this.mDB.execSQL(sql, values);
    }

    public void deleteData(String sql, ChatItems mData) {
        String[] query = {mData.getChat_room_num()};
        this.mDB.execSQL(sql, query);
    }

    public void deleteData(String sql, String room_num) {
        String[] query = {room_num};
        this.mDB.execSQL(sql, query);
    }

    public ArrayList<ChatItems> selectAll(String sql, String chat_room_num) {
        String[] query = {chat_room_num};
        ArrayList<ChatItems> items = new ArrayList<>();

        Cursor cursor = this.mDB.rawQuery(sql, query);  // SELECT * FROM chat_text " + "WHERE chat_room_num=? " + "ORDER BY reg_id ASC
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {    // 커서위치가 마지막이 아닐 때 까지 반복

            ChatItems item = new ChatItems(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
            );

            items.add(item);
            cursor.moveToNext();  // 안써주면 무한루프
        }

        cursor.close();

        return items;
    }

    public ChatItems selectRecentMessage(String sql, String user_ad_id) {
        String[] query = {user_ad_id};

        Cursor cursor = this.mDB.rawQuery(sql, query);
        cursor.moveToFirst();

        ChatItems item = null;

        while (!cursor.isAfterLast()) {    // 커서위치가 마지막이 아닐 때 까지 반복

            item = new ChatItems(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
            );

            cursor.moveToNext();

        }

        cursor.close();

        return item;
    }

    public ChatItems selectNewMessage(String sql, String user_ad_id) {
        String[] query = {user_ad_id};

        Cursor cursor = this.mDB.rawQuery(sql, query);
        cursor.moveToFirst();

        ChatItems item = new ChatItems(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
        );

        cursor.close();

        return item;
    }

    public ArrayList<UserProfile> getTempUsers(String sql) {

        ArrayList<UserProfile> temp = new ArrayList<>();

        Cursor cursor = this.mDB.rawQuery(sql, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {        // 무한 반복 걸림
            temp.add(new UserProfile(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            cursor.moveToNext();         // moveToNext 메소드 빼먹었다가 무한루프 도는바람에 엄청 고생함
        }
        cursor.close();

        return temp;
    }

    public ArrayList<String> selectAllNewChat(String sql) {
        ArrayList<String> items = new ArrayList<>();

        Cursor cursor = this.mDB.rawQuery(sql, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {    // 커서위치가 마지막이 아닐 때 까지 반복
            items.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor.close();

        return items;
    }

    public ArrayList<RoomData> selectAllRoomNumber(String sql) {

        ArrayList<RoomData> roomNums = new ArrayList<>();

        Cursor cursor = this.mDB.rawQuery(sql, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {    // 커서위치가 마지막이 아닐 때 까지 반복
            roomNums.add(new RoomData(cursor.getInt(2), cursor.getString(1)));
            cursor.moveToNext();
        }

        cursor.close();
/*
        for (int i = 0 ; i < roomNums.size() ; i++) {
            Log.d("171221", "DataBase id : " + roomNums.get(i).getUser_ad_id() + " num : " + roomNums.get(i).getRoomNum());
        }
*/
        return roomNums;
    }

    public void upDateRoomNumber(String user_id, String room_num) {     // 업데이트 문 실행 rawQuery 메소드가 아닌 update메소드 사용 (방법이 좀 다름)
        String[] query = {user_id};

        ContentValues values = new ContentValues();
        values.put("chat_room_num", room_num);   // 컬럼, 벨류

        this.mDB.update("chat_text", values, "user_ad_id=? AND chat_room_num=999", query);
    }
}
