public class Biji extends Product {
    public Biji(String name, double price, int stock) {
        super(name, price, stock, "Biji");
    }

    @Override
    public String getDescription() {
        return getName() + " (Harga per biji: Rp" + getPrice() + ")";
    }

    @Override
    public boolean reduceStock(int amount) {
        // Contoh validasi khusus untuk Biji
        if (getStock() - amount < 10) {
            System.out.println("Stok biji tidak boleh kurang dari 10");
            return false;
        }
        return super.reduceStock(amount);
    }
}