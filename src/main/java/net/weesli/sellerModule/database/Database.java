package net.weesli.sellerModule.database;

import net.weesli.rClaim.utils.Claim;

public interface Database {

    void insert(String id, int amount);
    void update(String id, int amount);
    void delete(String id);
    boolean isValid(String id);
    int getPrice(String id);

}
