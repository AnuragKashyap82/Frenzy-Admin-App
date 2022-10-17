package kashyap.anurag.frenzyadmin.Models;

public class ModelVerifiedSeller {

    String shopId, shopName, sellerName;

    public ModelVerifiedSeller() {
    }

    public ModelVerifiedSeller(String shopId, String shopName, String sellerName) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.sellerName = sellerName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
}
