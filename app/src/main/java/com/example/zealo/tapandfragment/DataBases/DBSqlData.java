package com.example.zealo.tapandfragment.DataBases;

/**
 * Created by USER on 2017-12-04.
 */

public class DBSqlData {

    public static final String SQL_DB_CREATE_TABLE =
            "CREATE TABLE chat_text " +
            "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_ad_id TEXT NOT NULL, " +
            "chat_room_num INTEGER NOT NULL, " +
            "chat_posted_date TEXT NOT NULL, " +
            "chat_posted_time TEXT NOT NULL, " +
            "chat_message_text TEXT NOT NULL)";

    public static final String SQL_DB_CREATE_TEMP_TABLE =
            "CREATE TABLE chat_temp_user " +
                    "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_ad_id TEXT NOT NULL, " +
                    "user_desc TEXT, " +
                    "user_nickname TEXT NOT NULL, " +
                    "user_profile_img TEXT)";

    public static final String SQL_DB_CREATE_ROOM_TABLE =
            "CREATE TABLE chat_room " +
                    "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_ad_id TEXT NOT NULL, " +
                    "chat_room_num INTEGER NOT NULL)";

    public static final String SQL_DB_INSERT_TEMP_USER =
            "INSERT INTO chat_temp_user " +
                    "(user_ad_id, user_desc, user_nickname, user_profile_img) " +           // 유저 ID, 채팅방번호, 날짜, 시간, 메시지
                    "VALUES (?, ?, ?, ?)";

    public static final String SQL_DB_INSERT_DATA =
            "INSERT INTO chat_text " +
                    "(user_ad_id, chat_room_num, chat_posted_date, chat_posted_time, chat_message_text) " +           // 유저 ID, 채팅방번호, 날짜, 시간, 메시지
                    "VALUES (?, ?, ?, ?, ?)";

    public static final String SQL_DB_INSERT_ROOM_DATA =
            "INSERT INTO chat_room " +
                    "(user_ad_id, chat_room_num) " +           // 채팅 상대방 유저 ID, 채팅방번호
                    "VALUES (?, ?)";

    public static final String SQL_DB_SELECT_ALL_TEMP_USER =
            "SELECT * FROM chat_temp_user";

    public static final String SQL_DB_SELECT_ALL_DATA =
            "SELECT * FROM chat_text ORDER BY reg_id ASC";

    public static final String SQL_DB_SELECT_ALL_ROOM =
            "SELECT * FROM chat_room WHERE chat_room_num!=999 ORDER BY chat_room_num ASC";

    public static final String SQL_DB_DELETE_DATA =
            "DELETE FROM chat_text " + "WHERE chat_room_num=?";

    public static final String SQL_DB_DELETE_ROOM_DATA =
            "DELETE FROM chat_room " + "WHERE chat_room_num=?";

    public static final String SQL_DB_SELECT_DATA =
            "SELECT * FROM chat_text " + "WHERE chat_room_num=? " + "ORDER BY reg_id ASC";

    public static final String SQL_DB_SELECT_DATA_BY_ID =
            "SELECT * FROM chat_text " + "WHERE user_ad_id=? " + "ORDER BY reg_id ASC";

    public static final String SQL_DB_SELECT_NEW_CHAT_USERS =
            "SELECT DISTINCT user_ad_id FROM chat_text WHERE chat_room_num=999";

    public static final String SQL_DB_SELECT_RECENT_MESSAGE =
            "SELECT * FROM chat_text " + "WHERE chat_room_num=? " + "ORDER BY reg_id DESC LIMIT 1";

    public static final String SQL_DB_SELECT_NEW_MESSAGE =
            "SELECT * FROM chat_text " + "WHERE user_ad_id=? AND chat_room_num=999 " + "ORDER BY reg_id DESC LIMIT 1";

    public static final String SQL_DB_SELECT_LAST_MESSAGE =
            "SELECT * FROM chat_text " + "WHERE chat_room_num IN (SELECT chat_room_num FROM chat_room WHERE user_ad_id=?)";

    public static final String SQL_DB_DELETE_TEMP_USER =
            "DELETE FROM chat_temp_user " + "WHERE user_ad_id=?";
}
