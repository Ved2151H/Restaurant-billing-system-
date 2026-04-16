public class BillingService {

    public int calculateTotal(Order o) {
        return o.pizza * 200 +
               o.burger * 100 +
               o.sandwich * 120 +
               o.drink * 80;
    }

    public double calculateGST(int total) {
        return total * 0.05;
    }

    public double applyDiscount(double amount) {
        return amount - (amount * 0.10);
    }
}