package kashyap.anurag.frenzyadmin.Models;

public class ModelNewQC {

    String productId, shopId;

    public ModelNewQC() {
    }

    public ModelNewQC(String productId, String shopId) {
        this.productId = productId;
        this.shopId = shopId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
