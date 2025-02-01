public abstract class Product {
    private String name;
    private double price;
    private int stock;
    private String category;

    public Product(String name, double price, int stock, String category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // Abstract method untuk polymorphism
    public abstract String getDescription();

    // Method untuk override
    public boolean reduceStock(int amount) {
        if (amount <= stock) {
            stock -= amount;
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategory() { 
        return category; 
    }
}