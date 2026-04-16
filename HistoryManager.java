import java.util.ArrayList;

public class HistoryManager {

    private static ArrayList<String> history = new ArrayList<>();

    public static void addBill(String bill) {
        history.add(bill);
    }

    public static String getHistory() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < history.size(); i++) {
            sb.append("Bill ").append(i + 1).append(":\n");
            sb.append(history.get(i)).append("\n\n");
        }

        return sb.toString();
    }
}