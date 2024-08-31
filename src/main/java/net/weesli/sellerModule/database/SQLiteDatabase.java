package net.weesli.sellerModule.database;

import net.weesli.rClaim.RClaim;
import net.weesli.rozsLib.database.mysql.Column;
import net.weesli.rozsLib.database.mysql.Delete;
import net.weesli.rozsLib.database.mysql.Insert;
import net.weesli.rozsLib.database.mysql.Update;
import net.weesli.rozsLib.database.sqlite.SQLiteBuilder;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLiteDatabase implements Database {

    private SQLiteBuilder builder;
    private Connection connection;

    public SQLiteDatabase() {
        builder = new SQLiteBuilder("RClaim").setPath(new File(RClaim.getInstance().getDataFolder(), "data").getPath());
        try {
            connection = builder.build();
            createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTable() {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("id", "VARCHAR(255)", 255).setPrimary(true));
        columns.add(new Column("price", "INT", 9999));
        try {
            builder.createTable("rclaims_prices", connection, columns);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void insert(String id, int amount) {
        Insert insert = new Insert("rclaims_prices", List.of("id", "price"), List.of(id,amount));
        try {
            builder.insert(connection,insert);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(String id, int amount) {
        Update update = new Update("rclaims_prices", List.of("price"), List.of(amount), Map.of("id", id));
        try {
            builder.update(connection, update);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        Delete delete = new Delete(connection, "rclaims_prices", Map.of("id", id));
        try {
            builder.delete(delete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValid(String id) {
        try {
            String sql = "SELECT * FROM rclaims_prices WHERE id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, id);
                ResultSet rs = statement.executeQuery();
                return rs.next();
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPrice(String id) {
        try {
            String sql = "SELECT price FROM rclaims_prices WHERE id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, id);
                ResultSet rs = statement.executeQuery();
                if(rs.next()){
                    return rs.getInt("price");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
