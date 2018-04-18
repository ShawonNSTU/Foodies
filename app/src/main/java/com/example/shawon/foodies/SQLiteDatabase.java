package com.example.shawon.foodies;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHAWON on 2/13/2018.
 */

public class SQLiteDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "FoodiesDB1.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    public List<Order> getCarts(String userPhone){

        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] dbColumnSelect = {"UserPhone","ProductID","ProductName","Quantity","Price","Discount","ProductImage"};

        String TABLE_NAME = "OrderDetails";

        queryBuilder.setTables(TABLE_NAME);

        // The Cursor provides the read-write access to the result set that are returned by the database query...

        Cursor cursor = queryBuilder.query(db,dbColumnSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        // here dbColumnSelect is the projectionIN that means selecting the columns on which the query will perform...
        // SelectionIN means selecting the rows...

        final List<Order> result = new ArrayList<>();

        if(cursor.moveToFirst()){           // move to first row...it returns false if the cursor has 0 row...

            do {
                result.add(new Order(
                        cursor.getString(cursor.getColumnIndex("UserPhone")),
                        cursor.getString(cursor.getColumnIndex("ProductID")),
                        cursor.getString(cursor.getColumnIndex("ProductName")),
                        cursor.getString(cursor.getColumnIndex("Quantity")),
                        cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Discount")),
                        cursor.getString(cursor.getColumnIndex("ProductImage"))
                ));
            }while (cursor.moveToNext()); // loop until this method will return false...when it goes to the last row of the TABLE_NAME
        }
        return result;
    }

    public boolean checkFoodExist(String foodID, String userPhone){
        boolean flag;
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String query = String.format("SELECT * FROM OrderDetails WHERE UserPhone = '%s' AND ProductID = '%s'",userPhone,foodID);
        cursor = db.rawQuery(query,null);
        if (cursor.getCount() > 0){
            flag = true;
        }
        else{
            flag = false;
        }
        cursor.close();
        return flag;
    }

    public String getFoodQuantity(String userPhone, String foodID){
        String quantity =  null;
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String query = String.format("SELECT * FROM OrderDetails WHERE UserPhone = '%s' AND ProductID = '%s'",userPhone,foodID);
        cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()){
            do{
                quantity = cursor.getString(cursor.getColumnIndex("Quantity"));
            }while (cursor.moveToNext());
        }
        return quantity;
    }

    public void increaseCartItem(String userPhone, String foodID, String countItem, String number){
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetails SET Quantity = '%s' + '%s' WHERE UserPhone = '%s' AND ProductID = '%s'",countItem,number,userPhone,foodID);
        db.execSQL(query);
    }

    public void addCart(Order order){

        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        String query = String.format("INSERT INTO OrderDetails(UserPhone,ProductID,ProductName,Quantity,Price,Discount,ProductImage) VALUES('%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductID(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getProductImage());

        db.execSQL(query);

    }

    public void deleteCart(String userPhone){

        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();

        String query = String.format("DELETE FROM OrderDetails WHERE UserPhone='%s'",userPhone);
        db.execSQL(query);
    }

    public int getCountCartItem(String userPhone) {

        int countCartItem = 0;

        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        String query = String.format("SELECT COUNT(*) FROM OrderDetails WHERE UserPhone='%s'",userPhone); // the number of rows in the table...

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){

            do {
                countCartItem = cursor.getInt(0);
            }while (cursor.moveToNext());

        }

        return countCartItem;

    }

    public void updateCart(Order order) {

        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        String query = String.format("UPDATE OrderDetails SET Quantity = '%s' WHERE UserPhone = '%s' AND ProductID = '%s'",order.getQuantity(),order.getUserPhone(),order.getProductID());

        db.execSQL(query);

    }

    public void addToFavourites(FavouriteFood favouriteFood){

        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        String query = String.format("INSERT INTO Favourites(FoodID,UserPhone,FoodName,FoodImage,FoodPrice,FoodDiscount,FoodDescription,FoodMenuID) VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                favouriteFood.getFoodID(),
                favouriteFood.getUserPhone(),
                favouriteFood.getFoodName(),
                favouriteFood.getFoodImage(),
                favouriteFood.getFoodPrice(),
                favouriteFood.getFoodDiscount(),
                favouriteFood.getFoodDescription(),
                favouriteFood.getFoodMenuID());

        db.execSQL(query);

    }

    public void removeFromFavourites(String foodID, String userPhone){

        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        String query = String.format("DELETE FROM Favourites WHERE FoodID='%s' and UserPhone='%s';",foodID,userPhone);

        db.execSQL(query);

    }

    public boolean isFavourite(String foodID,String userPhone){

        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        String query = String.format("SELECT * FROM Favourites WHERE FoodID='%s' and UserPhone='%s';",foodID,userPhone);

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }

        cursor.close();
        return true;

    }

    public void deleteFavourites(String userPhone){

        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();

        String query = String.format("DELETE FROM Favourites WHERE UserPhone='%s';",userPhone);
        db.execSQL(query);

    }

    public List<FavouriteFood> getFavourites(String userPhone){

        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] dbColumnSelect = {"FoodID","UserPhone","FoodName","FoodImage","FoodPrice","FoodDiscount","FoodDescription","FoodMenuID"};

        String TABLE_NAME = "Favourites";

        queryBuilder.setTables(TABLE_NAME);

        // The Cursor provides the read-write access to the result set that are returned by the database query...

        Cursor cursor = queryBuilder.query(db,dbColumnSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        // here dbColumnSelect is the projectionIN that means selecting the columns on which the query will perform...
        // SelectionIN means selecting the rows...

        final List<FavouriteFood> result = new ArrayList<>();

        if(cursor.moveToFirst()){           // move to first row...it returns false if the cursor has 0 row...

            do {
                result.add(new FavouriteFood(
                        cursor.getString(cursor.getColumnIndex("FoodID")),
                        cursor.getString(cursor.getColumnIndex("UserPhone")),
                        cursor.getString(cursor.getColumnIndex("FoodName")),
                        cursor.getString(cursor.getColumnIndex("FoodImage")),
                        cursor.getString(cursor.getColumnIndex("FoodPrice")),
                        cursor.getString(cursor.getColumnIndex("FoodDiscount")),
                        cursor.getString(cursor.getColumnIndex("FoodDescription")),
                        cursor.getString(cursor.getColumnIndex("FoodMenuID"))
                ));
            }while (cursor.moveToNext()); // loop until this method will return false...when it goes to the last row of the TABLE_NAME
        }
        return result;
    }

    public void removeFromCart(String productID, String phone) {

        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();

        String query = String.format("DELETE FROM OrderDetails WHERE UserPhone='%s' and ProductID = '%s'",phone,productID);
        db.execSQL(query);

    }
}