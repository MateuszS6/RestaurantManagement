package JDBC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Kitchen implements IKitchen {
    private Connection connection;

    public Kitchen(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Order getDeliveryOrder() {
        Order deliveryOrder = null;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM DeliveryOrders LIMIT 1");

            if (resultSet.next()) {
                HashMap<IngredientOrder, Integer> ingredientAmounts = new HashMap<>();

                do {
                    int ingredientId = resultSet.getInt("IngredientID");
                    String ingredientName = resultSet.getString("IngredientName");
                    int quantity = resultSet.getInt("Quantity");

                    IngredientOrder ingredientOrder = new IngredientOrder(ingredientId, ingredientName, quantity);
                    ingredientAmounts.put(ingredientOrder, quantity);
                } while (resultSet.next());

                String deliveryDetails = "Delivery!";
                deliveryOrder = new Order(ingredientAmounts, deliveryDetails);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deliveryOrder;
    }

    @Override
    public List<IngredientOrder> getIngredientsAvailable() {
        List<IngredientOrder> availableIngredients = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM IngredientsAvailable");

            while (resultSet.next()) {
                int ingredientId = resultSet.getInt("IngredientID");
                String ingredientName = resultSet.getString("IngredientName");
                int quantity = resultSet.getInt("Quantity");

                IngredientOrder ingredientOrder = new IngredientOrder(ingredientId, ingredientName, quantity);
                availableIngredients.add(ingredientOrder);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableIngredients;
    }

    @Override
    public boolean getMenuApproval(Menu menu, boolean forceApproval) {
        boolean isMenuApproved = false;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM IngredientsAvailable WHERE IngredientName IN (SELECT IngredientName FROM MenuItems)");

            if (resultSet.next() && resultSet.getInt(1) == menu.getDishes().size()) {
                if (forceApproval || resultSet.getInt(1) == menu.getDishes().size()) {
                    isMenuApproved = true;
                }
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isMenuApproved;
    }

}
