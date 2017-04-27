package fatSecret;
import java.io.UnsupportedEncodingException;
import java.sql.*;

public class FnDatabase {
    Connection conn = null;
    public void openConnection() throws SQLException
    {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
            System.out.println("error! mysql driver not found");
        }

        conn = DriverManager.getConnection("jdbc:mysql://localhost/nightingale?user=root&password=cnt5517&useUnicode=yes&characterEncoding=UTF-8");
    }

    public void closeConnection() throws SQLException
    {
        if (conn == null)
            return;
        conn.close();
    }

    private static String ToSqlString(String aStr) {
        return aStr.replace("'", "''").replace("\\", "\\\\");
    }

    private static String Quote(String aStr) {
        if (aStr == null || aStr.equals(""))
            return "null";
        else
            return "_utf8'" + ToSqlString(aStr)  + "'";
    }

    private int executeInt(String sql, int def) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        int cnt = def;
        if(rs.next())
            cnt = rs.getInt(1);
        rs.close();
        stmt.close();
        return cnt;
    }

    private String executeStr(String sql, String def) throws SQLException, UnsupportedEncodingException
    {
        Statement stmt = null;
        ResultSet rs = null;
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        String res = def;
        if(rs.next())
            res = rs.getString(1);
        rs.close();
        stmt.close();
        return res;
    }

    private int executeUpdate(String sql) throws SQLException
    {
        Statement stmt = null;
        stmt = conn.createStatement();

        int res = stmt.executeUpdate(sql);
        stmt.close();
        return res;
    }

//    public void insertRecipe(Recipe r) throws SQLException
//    {
//        Statement stmt = null;
//        stmt = conn.createStatement();
//
//        stmt.executeUpdate("INSERT INTO tblRecipe (" +
//                "recipeId, name, calories, validUntil, xml, rating) " +
//                "VALUES(" + r.getRecipeId() +
//                ", " + Quote(r.getName()) +
//                ", " + r.getCalories() +
//                ", ADDDATE(NOW(), INTERVAL 7 DAY)" +
//                ", " + Quote(r.getXml()) +
//                ", " + r.getRating() +
//                ") ON DUPLICATE KEY UPDATE name = "  + Quote(r.getName()) +
//                ", " + "calories = " + r.getCalories() +
//                ", validUntil = ADDDATE(NOW(), INTERVAL 7 DAY)" +
//                ", " + "xml = " + Quote(r.getXml()) +
//                ", " + "rating = " + r.getRating() +
//                ", " + "imageFilename = NULL");
//
//        stmt.close();
//
//        if (r.getImageFilename() != "")
//        {
//            String sql = "UPDATE tblRecipe SET imageFilename = " +
//                    Quote(r.getImageFilename()) + " WHERE recipeId = " +
//                    r.getRecipeId();
//            this.executeUpdate(sql);
//        }
//    }

//    public void insertFood(Ingredient ing) throws SQLException {
//        Statement stmt = null;
//        stmt = conn.createStatement();
//
//        stmt.executeUpdate("INSERT INTO tblFood (" +
//                "foodId, name, validUntil, xml) " +
//                "VALUES(" + ing.getFoodId() +
//                ", " + Quote(ing.getFoodName()) +
//                ", ADDDATE(NOW(), INTERVAL 31 DAY)" +
//                ", " + Quote(ing.getXml()) +
//                ") ON DUPLICATE KEY UPDATE name = "  + Quote(ing.getFoodName()) +
//                ", validUntil = ADDDATE(NOW(), INTERVAL 31 DAY)" +
//                ", " + "xml = " + Quote(ing.getXml()));
//
//        stmt.close();
//    }

    public int getCategoryId(String categoryName) throws SQLException {
        String sql = "SELECT categoryId FROM tblCategory WHERE name = " + Quote(categoryName);

        return executeInt(sql, -1);
    }

    public int insertCategory(String categoryName) throws SQLException
    {
        Statement stmt = null;
        ResultSet rs = null;

        stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO tblCategory(name) VALUES (" + Quote(categoryName) + ")", Statement.RETURN_GENERATED_KEYS);

        rs = stmt.getGeneratedKeys();

        rs.next();
        int res = rs.getInt(1);
        rs.close();
        stmt.close();
        return res;
    }

    public void insertRecipeCategory(int recipeId, int categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM tblRecipeCategory WHERE recipeId = " + recipeId + " AND categoryId = " + categoryId;
        int cnt = executeInt(sql, 0);
        if (cnt > 0)
            return;
        sql = "INSERT INTO tblRecipeCategory(recipeId, categoryId) VALUES(" + recipeId + "," + categoryId + ")";
        executeUpdate(sql);
    }

    public boolean needToInvalidateFood(int foodId) throws SQLException
    {
        String sql = "SELECT COUNT(*) AS cnt FROM tblFood WHERE foodId = " + foodId + " AND NOW() < validUntil";
        int cnt = executeInt(sql, 0);
        //if cnt is 1, there is valid food
        return (cnt == 0);
    }

    public boolean needToInvalidateRecipe(int recipeId) throws SQLException
    {
        String sql = "SELECT COUNT(*) AS cnt FROM tblRecipe WHERE recipeId = " + recipeId + " AND NOW() < validUntil";
        int cnt = executeInt(sql, 0);
        //if cnt is 1, there is valid recipe
        return (cnt == 0);
    }

    //This method is made for a test.
    public String getRecipeXml(int recipeId) throws SQLException, UnsupportedEncodingException
    {
        String sql = "SELECT xml FROM tblRecipe WHERE recipeId = " + recipeId;
        return executeStr(sql, "");
    }

    public void insertIngredient(int recipeId, int foodId) throws SQLException {
        String sql = "INSERT INTO tblIngredient (recipeId, foodId) VALUES(" +
                recipeId + ", " + foodId + ")";
        executeUpdate(sql);
    }

    public void deleteIngredients(int recipeId) throws SQLException {
        String sql = "DELETE FROM tblIngredient WHERE recipeId = " + recipeId;
        executeUpdate(sql);
    }

    public void deleteCategories(int recipeId) throws SQLException {
        String sql = "DELETE FROM tblRecipeCategory WHERE recipeId = " + recipeId;
        executeUpdate(sql);
    }

    public void deleteRecipe(int recipeId) throws SQLException {
        executeUpdate("DELETE FROM tblRecipe WHERE recipeId = " + recipeId);
    }
}
