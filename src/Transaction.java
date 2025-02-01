import java.util.ArrayList;

public class Transaction {
    private ArrayList<Product> products;
    private double total;

    public Transaction() {
        products = new ArrayList<>();
        total = 0.0;
    }

    public void addProduct(Product product, int quantity) {
        if (product.reduceStock(quantity)) { // Gunakan method polymorphic
            products.add(product);
            total += product.getPrice() * quantity;
        } else {
            throw new IllegalArgumentException("Gagal mengurangi stok untuk " + product.getName());
        }
    }

    public double getTotal() {
        return total;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}
