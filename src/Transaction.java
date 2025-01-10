import java.util.ArrayList;

public class Transaction {
    private ArrayList<Product> products;
    private double total;

    public Transaction() {
        products = new ArrayList<>();
        total = 0.0;
    }

    public void addProduct(Product product, int quantity) {
        if (product.getStock() >= quantity) {
            product.reduceStock(quantity);
            products.add(product);
            total += product.getPrice() * quantity;
        }
    }

    public double getTotal() {
        return total;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}
