import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        String SQL = """
                            select dish.id as dish_id, dish.name as dish_name, dish_type, dish.selling_price as dish_selling_price
                            from dish
                            where dish.id = ?
                            """;

        try (
                Connection connection = dbConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL)
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setPrice(resultSet.getObject("dish_selling_price") == null
                        ? null : resultSet.getDouble("dish_selling_price"));
                dish.setDishIngredients(findDishIngredientsByDishId(id, dish));
                return dish;
            }
            dbConnection.closeConnection(connection);
            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<Dish> findDishsByIngredientName(String ingredientName) {
        DBConnection dbConnection = new DBConnection();

        String sql = """
                SELECT d.*
                FROM dish d
                JOIN dish_ingredient di ON d.id = di.id_dish
                JOIN ingredient i ON di.id_ingredient = i.id
                WHERE i.name ILIKE ?;
                """;
        List<Dish> findedDishs = new ArrayList<>();

        try(
                Connection connection = dbConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, "%" + ingredientName + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    int dishId = resultSet.getInt("id");
                    Dish dish = new Dish(
                            dishId,
                            resultSet.getString("name"),
                            DishTypeEnum.valueOf(resultSet.getString("dish_type")),
                            findDishIngredientsByDishId(dishId, findDishById(dishId)),
                            resultSet.getDouble("selling_price")
                    );
                    findedDishs.add(dish);
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error executing query", e);
        }
        return findedDishs;
    }

    private Ingredient findIngredientById(int id_ingredient){
        DBConnection dbConnection = new DBConnection();

        String SQL = """
                SELECT i.id, i.name, i.price, i.category
                FROM ingredient i
                WHERE i.id = ?
                """;
        Ingredient findedIngredient = new Ingredient();

        try (
                Connection connection = dbConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL)
        ){
            preparedStatement.setInt(1, id_ingredient);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    findedIngredient = new Ingredient(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            CategoryEnum.valueOf(resultSet.getString("category")),
                            resultSet.getDouble("price"),
                            findStockMovementsByIdIngredient(resultSet.getInt("id"))
                    );
                }
            }
            return findedIngredient;
        } catch (SQLException e){
            throw new RuntimeException("Error executing query", e);
        }
    }

    private List<DishIngredient> findDishIngredientsByDishId(int id_dish, Dish dish) {
        DBConnection dbConnection = new DBConnection();

        List<DishIngredient> findedDishIngredients = new ArrayList<>();
        String SQL = """
                SELECT di.id, di.id_dish, di.id_ingredient, di.quantity_required, di.unit
                FROM dish_ingredient di
                WHERE di.id_dish = ?;
                """;

        try (
                Connection connection = dbConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL)
        ) {
            preparedStatement.setInt(1, id_dish);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    findedDishIngredients.add(new DishIngredient(
                            resultSet.getInt("id"),
                            dish,
                            findIngredientById(resultSet.getInt("id_ingredient")),
                            resultSet.getDouble("quantity_required"),
                            UnitType.valueOf(resultSet.getString("unit"))
                    ));
                }
            }
            return findedDishIngredients;
        } catch (SQLException e){
            throw new RuntimeException("Error executing query", e);
        }
    }

    List<Ingredient> findIngredientsByDishId(int dishId){
        Dish dish = findDishById(dishId);
        List<Ingredient> findedIngredients = new ArrayList<>();

        for (DishIngredient dishIngredient : dish.getDishIngredients()){
            findedIngredients.add(dishIngredient.getIngredient());
        }
        return findedIngredients;
    }

    List<Ingredient> findIngredients(int page, int size) {
        DBConnection dbConnection = new DBConnection();

        String sql = """
                SELECT i.id, i.name, i.price, i.category
                FROM ingredient i
                LIMIT ? OFFSET ?""";
        List<Ingredient> findedIngredientsList = new ArrayList<>();

        try(
                Connection connection = dbConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, size);
            preparedStatement.setInt(2, (page - 1) * size);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()){
                    findedIngredientsList.add(new Ingredient(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            CategoryEnum.valueOf(resultSet.getString("category")),
                            resultSet.getDouble("price"),
                            findStockMovementsByIdIngredient(resultSet.getInt("id"))
                    ));
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error executing query", e);
        }
        return findedIngredientsList;
    }

    private List<StockMovement> findStockMovementsByIdIngredient(Integer idIngredient){
        DBConnection dbConnection = new DBConnection();
        String sql = """
                SELECT sm.id, sm.quantity, sm.type, sm.unit, sm.creation_date
                FROM stock_movement sm
                WHERE sm.id_ingredient = ?
                """;
        List<StockMovement> findedStockMovements = new ArrayList<>();

        try (
                Connection connection = dbConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ){
            preparedStatement.setInt(1, idIngredient);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    findedStockMovements.add(new StockMovement(
                            resultSet.getInt("id"),
                            new StockValue(
                                    resultSet.getDouble("quantity"),
                                    UnitType.valueOf(resultSet.getString("unit"))
                            ),
                            MovementTypeEnum.valueOf(resultSet.getString("type")),
                            resultSet.getTimestamp("creation_date").toInstant()
                    ));
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error executing query", e);
        }
        return findedStockMovements;
    }

    Dish saveDish(Dish toSave) {
        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId;
            if (toSave.getId() != null){
                dishId = upSetDishById(conn, toSave);
                updateSequenceNextValue(conn, "dish", "id", getSerialSequenceName(conn, "dish", "id"));
            } else {
                dishId = upSetDishByName(conn, toSave);
            }

            if (toSave.getDishIngredients() == null || toSave.getDishIngredients().isEmpty()){
                deleteDishIngredientByIdDish(conn, dishId);
            } else {
                for (DishIngredient dishIngredient : toSave.getDishIngredients()){
                    saveIngredient(dishIngredient.getIngredient());
                    saveDishIngredient(conn, dishId, dishIngredient);
                }
            }

            conn.commit();
            return findDishById(dishId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer upSetDishById(Connection connection, Dish dishToSave) {
        String upsertDishByIdSql = """
                    INSERT INTO dish (id, selling_price, name, dish_type)
                    VALUES (?, ?, ?, ?::dish_type)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type,
                        selling_price = EXCLUDED.selling_price
                    RETURNING id
                """;

        try (PreparedStatement ps = connection.prepareStatement(upsertDishByIdSql)) {
            ps.setInt(1, dishToSave.getId());
            if (dishToSave.getPrice() != null) {
                ps.setDouble(2, dishToSave.getPrice());
            } else {
                ps.setNull(2, Types.DOUBLE);
            }
            ps.setString(3, dishToSave.getName());
            ps.setString(4, dishToSave.getDishType().name());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    private Integer upSetDishByName(Connection connection, Dish dishToSave) {
        String upsertDishByIdSql = """
                    INSERT INTO dish (name, selling_price, dish_type)
                    VALUES (?, ?, ?::dish_type)
                    ON CONFLICT (name) DO UPDATE
                    SET dish_type = EXCLUDED.dish_type,
                        selling_price = EXCLUDED.selling_price
                    RETURNING id
                """;

        try (PreparedStatement ps = connection.prepareStatement(upsertDishByIdSql)) {
            ps.setString(1, dishToSave.getName());
            if (dishToSave.getPrice() != null) {
                ps.setDouble(2, dishToSave.getPrice());
            } else {
                ps.setNull(2, Types.DOUBLE);
            }
            ps.setString(3, dishToSave.getDishType().name());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    private void deleteDishIngredientByIdDish(Connection conn, Integer idDish) {
        String sqlToDeleteDishIngredientsByIdDish = """
                DELETE FROM dish_ingredient
                WHERE id_dish = ?
                """;

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlToDeleteDishIngredientsByIdDish)){
            preparedStatement.setInt(1, idDish);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    private void saveDishIngredient(Connection connection, Integer dishId, DishIngredient dishIngredient) {
        String sqlToSaveIngredient = """
                INSERT INTO dish_ingredient (id, id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?, ?::unit_type)
                ON CONFLICT (id_dish, id_ingredient)
                DO UPDATE SET
                    quantity_required = EXCLUDED.quantity_required,
                    unit = EXCLUDED.unit;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlToSaveIngredient)){
                  if (dishIngredient.getId() == null){
                      preparedStatement.setInt(1, getNextSerialValue( connection,"dish_ingredient", "id"));
                  } else {
                      preparedStatement.setInt(1, dishIngredient.getId());
                      updateSequenceNextValue(connection, "dish_ingredient", "id", getSerialSequenceName(connection, "dish_ingredient", "id"));
                  }

                  preparedStatement.setInt(2, dishId);
                  preparedStatement.setInt(3, dishIngredient.getIngredient().getId());
                  preparedStatement.setDouble(4, dishIngredient.getQuantityRequired());
                  preparedStatement.setString(5, dishIngredient.getUnit().name());

                  preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Error executing query", e);
        }
    }

    Ingredient saveIngredient(Ingredient ingredient) throws SQLException {
        DBConnection dbConnection = new DBConnection();
        int ingredientId;

        try (Connection connection = dbConnection.getConnection()){
            if (ingredient.getId() != null) {
                ingredientId = upsetIngredientById(connection, ingredient);
                updateSequenceNextValue( connection,"ingredient", "id", getSerialSequenceName( connection,"ingredient", "id"));
            } else {
                updateSequenceNextValue( connection,"ingredient", "id", getSerialSequenceName( connection,"ingredient", "id"));
                ingredientId = upsetIngredientByName(connection, ingredient);
            }
        } catch (SQLException e){
            throw new RuntimeException("Error executing query", e);
        }
        return findIngredientById(ingredientId);
    }

    private int upsetIngredientById(Connection conn, Ingredient ingredient) {
        String sql = """
        INSERT INTO ingredient (id, name, price, category)
        VALUES (?, ?, ?, ?::ingredient_category)
        ON CONFLICT (id) DO UPDATE
        SET name = EXCLUDED.name,
            price = EXCLUDED.price,
            category = EXCLUDED.category
        RETURNING id
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredient.getId());
            ps.setString(2, ingredient.getName());
            ps.setDouble(3, ingredient.getPrice());
            ps.setString(4, ingredient.getCategory().name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ingredient.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredient.getId();
    }

    private int upsetIngredientByName(Connection conn, Ingredient ingredient) {
        String sql = """
        INSERT INTO ingredient (name, price, category)
        VALUES (?, ?, ?::ingredient_category)
        ON CONFLICT (name) DO UPDATE
        SET price = EXCLUDED.price,
            category = EXCLUDED.category
        RETURNING id
        """;
        int id;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getPrice());
            ps.setString(3, ingredient.getCategory().name());

            try (ResultSet resultSet = ps.executeQuery()){
                if (resultSet.next()) {
                    ingredient.setId(resultSet.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredient.getId();
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sql = "SELECT pg_get_serial_sequence(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException(
                    "Any sequence found for " + tableName + "." + columnName
            );
        }
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);

        String nextValSql = "SELECT nextval(?)";

        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );

        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients){
        DBConnection dbConnection = new DBConnection();
        List<Ingredient> savedIngredients = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection()){
            connection.setAutoCommit(false);

            try {
                for (Ingredient ingredient : newIngredients){
                    saveIngredient(ingredient);
                    savedIngredients.add(ingredient);
                }
            } catch (SQLException e){
                connection.rollback();
                throw new RuntimeException("Error executing queries", e);
            }
            connection.commit();
        } catch (SQLException e){
            throw new RuntimeException("Error executing query", e);
        }
        return savedIngredients;
    }

    List<Ingredient> findIngredientsByCriteria(
            String ingredientName, CategoryEnum category, String dishName, int page, int size
    ) {
        DBConnection dbConnection = new DBConnection();
        String sql = """
                SELECT i.id, i.name, i.price, i.category
                FROM ingredient i
                JOIN dish_ingredient di ON i.id = di.id_ingredient
                JOIN dish d ON di.id_dish = d.id
                WHERE 1=1
                """;
        List<Ingredient> findedIngredients = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection()){
            if (ingredientName != null) {
                sql += " AND i.name ILIKE ?";
            }
            if (category != null) {
                sql += " AND i.category = ?::ingredient_category";
            }
            if (dishName != null) {
                sql += " AND d.name ILIKE ?";
            }
            sql += " LIMIT ? OFFSET ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                int paramIndex = 1;
                if (ingredientName != null && !ingredientName.isEmpty()) {
                    preparedStatement.setString(paramIndex++, "%" + ingredientName + "%");
                }
                if (category != null) {
                    preparedStatement.setString(paramIndex++, category.name());
                }
                if (dishName != null && !dishName.isEmpty()) {
                    preparedStatement.setString(paramIndex++, "%" + dishName + "%");
                }
                preparedStatement.setInt(paramIndex++, size);
                preparedStatement.setInt(paramIndex++, (page - 1) * size);

                try (ResultSet resultSet = preparedStatement.executeQuery()){
                    while (resultSet.next()){
                        findedIngredients.add(new Ingredient(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                CategoryEnum.valueOf(resultSet.getString("category")),
                                resultSet.getDouble("price"),
                                findStockMovementsByIdIngredient(resultSet.getInt("id"))
                        ));
                    }
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error executing query", e);
        }
        return findedIngredients;
    }
}
