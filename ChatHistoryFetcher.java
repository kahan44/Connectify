import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

class MessageStack {
    static int TOP = -1;
    static int n = 1000;
    static Message[] messages = new Message[n];

    void push(Message message) {
        if(TOP >= n-1) {
            System.out.println("Stack Overflow");
            return;
        }
        else {
            TOP = TOP + 1;
            messages[TOP] = message;
        }
    }

    List<Message> toList() {
        List<Message> messageList = new ArrayList<>();
        for (int i = 0; i <= TOP; i++) {
            messageList.add(messages[i]);
        }
        return messageList;
    }
}

public class ChatHistoryFetcher {

    public static MessageStack fetchChatHistory(String user1, String user2, String tableName) {
        MessageStack messages = new MessageStack();

        String query = "SELECT * FROM "+tableName+" WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) ORDER BY timestamp";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/connectify", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender");
                String receiver = rs.getString("receiver");
                String message = rs.getString("message");
                String timestamp = rs.getString("timestamp");
            
                messages.push(new Message(sender,receiver, message,timestamp));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages;
    }
}