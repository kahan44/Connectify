import java.sql.*;

public class Database {
    Connection con;
    public static void main(String[] args) throws Exception {
        Database db = new Database();
        db.connect();
        System.out.println((db.con != null) ? "Done" :"Not done");
    }

    public void connect() throws Exception{
        String dburl = "jdbc:mysql://localhost:3306/connectify";
        String dbuser = "root";
        String dbpass = "";
        con = DriverManager.getConnection(dburl, dbuser, dbpass);
    }

    public void addNewAppUser(String phone, String name, String password) throws Exception {
        String insert = "{call addUser(?,?,?)}";
        CallableStatement cst = con.prepareCall(insert);
        cst.setString(1, phone);
        cst.setString(2, name);
        cst.setString(3, password);
        cst.executeUpdate();
    }

    public void updatePassword (String phone, String newPassword) throws Exception {
        String update = "{call updatePassword(?,?)}";
        CallableStatement cst = con.prepareCall(update);
        cst.setString(1, phone);
        cst.setString(2, newPassword);
        cst.executeUpdate();
    }

    public void addToList(List list) throws Exception {
        String getData = "SELECT * FROM AppUsers";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(getData);
        boolean hasNext = rs.next();
        while(hasNext) {
            list.add(new AppUsers(rs.getString(1), rs.getString(2), rs.getString(3)));
            hasNext = rs.next();
        }
    }
}