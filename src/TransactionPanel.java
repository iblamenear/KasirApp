import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TransactionPanel extends JPanel {
    private JTextArea cartArea;
    private JTextField productIdField;
    private JTextField quantityField;
    private JButton checkoutBtn; // Deklarasi button sebagai variabel instance
    private HistoryPanel historyPanel;
    private ProductPanel productPanel; // Declare productPanel

    public TransactionPanel(HistoryPanel historyPanel, ProductPanel productPanel) {
        this.historyPanel = historyPanel;
        this.productPanel = productPanel;// Initialize productPanel
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Transaksi"));
        
        initializeCartArea();
        initializeInputPanel();
    }

    private void initializeCartArea() {
        cartArea = new JTextArea();
        cartArea.setEditable(false);
        add(new JScrollPane(cartArea), BorderLayout.CENTER);
    }

    private void initializeInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        
        // Panel Input Field
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productIdField = new JTextField(10);
        quantityField = new JTextField(10);
        fieldsPanel.add(new JLabel("ID Produk:"));
        fieldsPanel.add(productIdField);
        fieldsPanel.add(new JLabel("Jumlah:"));
        fieldsPanel.add(quantityField);
        
        // Panel Button
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Deklarasi button secara eksplisit
        JButton addBtn = new JButton("Tambah ke Keranjang");
        checkoutBtn = new JButton("Checkout"); // Inisialisasi button
        JButton printBtn = new JButton("Cetak Struk");
        
        // Styling button
        UIHelper.styleButton(addBtn);
        UIHelper.styleButton(checkoutBtn);
        UIHelper.styleButton(printBtn);
        
        // Event Handlers
        addBtn.addActionListener(this::handleAddToCart);
        checkoutBtn.addActionListener(this::handleCheckout);
        printBtn.addActionListener(this::handlePrintReceipt);
        
        buttonsPanel.add(addBtn);
        buttonsPanel.add(checkoutBtn);
        buttonsPanel.add(printBtn);
        
        inputPanel.add(fieldsPanel);
        inputPanel.add(buttonsPanel);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void handleAddToCart(ActionEvent e) {
        TransactionController.addToCart(
            productIdField.getText(),
            quantityField.getText(),
            cartArea
        );
    }

    private void handleCheckout(ActionEvent e) {
            TransactionController.checkout(
            cartArea,
            productPanel.getModel(),  // Gunakan method accessor
            historyPanel,
            productPanel
        );
    }

    private void handlePrintReceipt(ActionEvent e) {
        TransactionController.printReceipt(cartArea);
    }
}