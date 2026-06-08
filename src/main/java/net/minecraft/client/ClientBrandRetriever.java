package net.minecraft.client;

import today.vanta.Vanta;
import today.vanta.client.module.impl.misc.ClientBrand;

public class ClientBrandRetriever {
    public static String getClientModName() {
        String brand = "Benjamin Netanyahu";
        if (Vanta.instance.moduleStorage.getT(ClientBrand.class).isEnabled()) {
            brand = Vanta.instance.moduleStorage.getT(ClientBrand.class).brand.getValue();
        } else {
            brand = "Benjamin Netanyahu";
        }
        return brand;
    }
}
