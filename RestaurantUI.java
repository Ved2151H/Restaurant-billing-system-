import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class RestaurantUI implements ActionListener {

    JFrame frame;
    JTextField nameField, pizzaField, burgerField, sandwichField, drinkField;
    JTextArea billArea;
    JButton calculateBtn, discountBtn, clearBtn, printBtn, historyBtn;

    BillingService service = new BillingService();
    double finalAmount = 0;
    String currentBill = "";

    // ─── Color Palette ───────────────────────────────────────────
    static final Color BG_DARK       = new Color(0x1C1410);
    static final Color BG_CARD       = new Color(0x2A1F18);
    static final Color BG_INPUT      = new Color(0x352820);
    static final Color ACCENT_AMBER  = new Color(0xF5A623);
    static final Color ACCENT_ORANGE = new Color(0xE07040);
    static final Color TEXT_PRIMARY  = new Color(0xF5ECD7);
    static final Color TEXT_MUTED    = new Color(0xA89070);
    static final Color TEXT_LABEL    = new Color(0xD4AA70);
    static final Color BORDER_SUBTLE = new Color(0x4A3828);
    static final Color BTN_DANGER    = new Color(0xC0392B);
    static final Color BTN_SUCCESS   = new Color(0x27AE60);
    static final Color BTN_INFO      = new Color(0x2980B9);

    public RestaurantUI() {
        frame = new JFrame("Restaurant Billing System");
        frame.setSize(680, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        // ── HEADER ─────────────────────────────────────────────
        JPanel header = buildHeader();
        root.add(header, BorderLayout.NORTH);

        // ── CENTER: Form + Bill ─────────────────────────────────
        JPanel center = new JPanel(new GridLayout(1, 2, 16, 0));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(16, 20, 8, 20));

        center.add(buildFormPanel());
        center.add(buildBillPanel());
        root.add(center, BorderLayout.CENTER);

        // ── BUTTON BAR ──────────────────────────────────────────
        root.add(buildButtonBar(), BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────
    //  HEADER
    // ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x3D1F0A), getWidth(), 0, new Color(0x1C1410));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // decorative bottom border
                g2.setColor(ACCENT_AMBER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 24, 16, 24));

        // Logo / icon
        JLabel icon = new JLabel("\uD83C\uDF7D");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        header.add(icon, BorderLayout.WEST);

        // Title block
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBorder(new EmptyBorder(0, 14, 0, 0));

        JLabel title = new JLabel("RESTAURANT BILLING");
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Point of Sale  •  Dine In / Takeaway");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);

        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);
        header.add(titleBlock, BorderLayout.CENTER);

        // Date/Time badge
        JLabel badge = new JLabel(java.time.LocalDate.now().toString());
        badge.setFont(new Font("Courier New", Font.PLAIN, 12));
        badge.setForeground(ACCENT_AMBER);
        badge.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(badge, BorderLayout.EAST);

        return header;
    }

    // ─────────────────────────────────────────────────────────────
    //  FORM PANEL
    // ─────────────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel card = createCard("ORDER DETAILS");

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[][] items = {
            {"Customer Name", "", ""},
            {"Pizza", "₹200", "🍕"},
            {"Burger", "₹100", "🍔"},
            {"Sandwich", "₹120", "🥪"},
            {"Cold Drink", "₹80", "🥤"},
        };

        JTextField[] flds = {nameField = styledField(), pizzaField = styledField(),
                              burgerField = styledField(), sandwichField = styledField(),
                              drinkField = styledField()};

        for (int i = 0; i < items.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.55;
            JPanel lbl = buildLabel(items[i][2] + " " + items[i][0], items[i][1]);
            fields.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.45;
            fields.add(flds[i], gbc);
        }

        card.add(fields, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLabel(String name, String price) {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        JLabel nm = new JLabel(name);
        nm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nm.setForeground(TEXT_LABEL);
        p.add(nm, BorderLayout.CENTER);
        if (!price.isEmpty()) {
            JLabel pr = new JLabel(price);
            pr.setFont(new Font("Courier New", Font.BOLD, 11));
            pr.setForeground(ACCENT_AMBER);
            p.add(pr, BorderLayout.EAST);
        }
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  BILL PANEL
    // ─────────────────────────────────────────────────────────────
    private JPanel buildBillPanel() {
        JPanel card = createCard("BILL PREVIEW");

        billArea = new JTextArea();
        billArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        billArea.setBackground(new Color(0x1A1208));
        billArea.setForeground(TEXT_PRIMARY);
        billArea.setCaretColor(ACCENT_AMBER);
        billArea.setEditable(false);
        billArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        billArea.setLineWrap(true);
        billArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(billArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE, 1));
        scroll.getViewport().setBackground(new Color(0x1A1208));

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ─────────────────────────────────────────────────────────────
    //  BUTTON BAR
    // ─────────────────────────────────────────────────────────────
    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new GridLayout(1, 5, 10, 0));
        bar.setBackground(BG_DARK);
        bar.setBorder(new EmptyBorder(8, 20, 20, 20));

        calculateBtn = styledBtn("Calculate",    "\uD83D\uDCB0", ACCENT_AMBER,  new Color(0x3D2800));
        discountBtn  = styledBtn("10% Off",      "\uD83C\uDFF7",  ACCENT_ORANGE, new Color(0x3D1800));
        printBtn     = styledBtn("Save Bill",    "\uD83D\uDCBE", BTN_SUCCESS,   new Color(0x0D2B1A));
        historyBtn   = styledBtn("History",      "\uD83D\uDDC3",  BTN_INFO,      new Color(0x0A1E2E));
        clearBtn     = styledBtn("Clear",        "\uD83D\uDDD1",  BTN_DANGER,    new Color(0x2E0A0A));

        calculateBtn.addActionListener(this);
        discountBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        printBtn.addActionListener(this);
        historyBtn.addActionListener(this);

        bar.add(calculateBtn);
        bar.add(discountBtn);
        bar.add(printBtn);
        bar.add(historyBtn);
        bar.add(clearBtn);

        return bar;
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────
    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_SUBTLE, 1),
            new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Georgia", Font.BOLD, 11));
        lbl.setForeground(ACCENT_AMBER);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_SUBTLE);
        sep.setBackground(BORDER_SUBTLE);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lbl, BorderLayout.CENTER);
        top.add(sep, BorderLayout.SOUTH);

        card.add(top, BorderLayout.NORTH);
        return card;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT_AMBER);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_SUBTLE, 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_AMBER, 1),
                    new EmptyBorder(5, 8, 5, 8)));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_SUBTLE, 1),
                    new EmptyBorder(5, 8, 5, 8)));
            }
        });
        return f;
    }

    private JButton styledBtn(String text, String icon, Color accent, Color bg) {
        JButton btn = new JButton("<html><center><span style='font-size:16px'>"
                + icon + "</span><br><span style='font-size:10px'>" + text + "</span></center></html>") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? accent.darker() :
                            getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setForeground(accent);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(0, 62));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────
    //  ACTION HANDLER
    // ─────────────────────────────────────────────────────────────
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == calculateBtn) {
            try {
                Order order = new Order(
                    nameField.getText(),
                    getValue(pizzaField),
                    getValue(burgerField),
                    getValue(sandwichField),
                    getValue(drinkField)
                );

                int total = service.calculateTotal(order);
                double gst = service.calculateGST(total);
                finalAmount = total + gst;

                StringBuilder bill = new StringBuilder();
                bill.append("══════════════════════════\n");
                bill.append("  RESTAURANT RECEIPT\n");
                bill.append("══════════════════════════\n");
                bill.append(String.format("  Customer : %s\n", order.name));
                bill.append(String.format("  Date     : %s\n", java.time.LocalDate.now()));
                bill.append("──────────────────────────\n");

                if (order.pizza    > 0) bill.append(String.format("  Pizza     x%-2d  ₹%d\n",  order.pizza,    order.pizza * 200));
                if (order.burger   > 0) bill.append(String.format("  Burger    x%-2d  ₹%d\n",  order.burger,   order.burger * 100));
                if (order.sandwich > 0) bill.append(String.format("  Sandwich  x%-2d  ₹%d\n",  order.sandwich, order.sandwich * 120));
                if (order.drink    > 0) bill.append(String.format("  Cold Drink x%-2d ₹%d\n",  order.drink,    order.drink * 80));

                bill.append("──────────────────────────\n");
                bill.append(String.format("  Subtotal :        ₹%d\n", total));
                bill.append(String.format("  GST (5%%) :        ₹%.2f\n", gst));
                bill.append("══════════════════════════\n");
                bill.append(String.format("  TOTAL    :        ₹%.2f\n", finalAmount));
                bill.append("══════════════════════════\n");
                bill.append("  Thank you for dining!\n");

                currentBill = bill.toString();
                billArea.setText(currentBill);

            } catch (Exception ex) {
                billArea.setText("⚠ Invalid input! Please enter numbers only.");
            }
        }

        if (e.getSource() == discountBtn) {
            if (finalAmount == 0) { billArea.setText("⚠ Calculate bill first!"); return; }
            double discount = finalAmount * 0.10;
            double payable  = finalAmount - discount;
            billArea.append(String.format("\n  Discount  :       -₹%.2f\n", discount));
            billArea.append("──────────────────────────\n");
            billArea.append(String.format("  PAYABLE   :        ₹%.2f\n", payable));
        }

        if (e.getSource() == printBtn) {
            if (currentBill.isEmpty()) { billArea.setText("⚠ Nothing to save!"); return; }
            HistoryManager.addBill(currentBill);
            JOptionPane.showMessageDialog(frame, "✔ Bill saved to history!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == historyBtn) {
            String hist = HistoryManager.getHistory();
            billArea.setText(hist.isEmpty() ? "No history yet." : hist);
        }

        if (e.getSource() == clearBtn) {
            nameField.setText("");    pizzaField.setText("");
            burgerField.setText("");  sandwichField.setText("");
            drinkField.setText("");   billArea.setText("");
            finalAmount = 0;          currentBill = "";
        }
    }

    private int getValue(JTextField field) {
        return field.getText().isEmpty() ? 0 : Integer.parseInt(field.getText().trim());
    }
}