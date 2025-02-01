public class BahanMentah extends Product {
    public BahanMentah(String name, double price, int stock) {
        super(name, price, stock, "BahanMentah");
    }

    @Override
    public String getDescription() {
        return "Bahan Mentah: " + getName() + " (Stok: " + getStock() + ")";
    }

    @Override
    public boolean reduceStock(int amount) {
        // Contoh implementasi khusus untuk Bahan Mentah
        System.out.println("Mengurangi stok bahan mentah");
        return super.reduceStock(amount);
    }
}