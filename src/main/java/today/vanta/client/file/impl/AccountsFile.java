package today.vanta.client.file.impl;

import com.google.gson.JsonObject;
import today.vanta.Vanta;
import today.vanta.client.file.File;
import today.vanta.util.client.network.account.Account;
import today.vanta.util.system.EncryptUtil;

public class AccountsFile extends File {
    public AccountsFile() {
        super("accounts");
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject accountsObject = new JsonObject();

        for (Account acc : Vanta.instance.accountStorage.list) {
            JsonObject accObject = new JsonObject();

            accObject.addProperty("UUID", acc.uuid);
            accObject.addProperty("Access token", EncryptUtil.encrypt(acc.token));
            accObject.addProperty("Refresh token", EncryptUtil.encrypt(acc.refreshToken));

            accountsObject.add(acc.username, accObject);
        }

        return accountsObject;
    }

    @Override
    protected void readJson(JsonObject json) {
        if (json == null) {
            return;
        }

        Vanta.instance.accountStorage.list.clear();

        json.entrySet().forEach(entry -> {
            String username = entry.getKey();
            JsonObject accObject = entry.getValue().getAsJsonObject();

            String uuid = accObject.has("UUID") ? accObject.get("UUID").getAsString() : "";
            String token = accObject.has("Access token") ? EncryptUtil.decrypt(accObject.get("Access token").getAsString()) : "";
            String refreshToken = accObject.has("Refresh token") ? EncryptUtil.decrypt(accObject.get("Refresh token").getAsString()) : "";

            Account account = new Account(username, uuid, token, refreshToken);
            Vanta.instance.accountStorage.list.add(account);
        });
    }
}
