import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Dish {

    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients;
    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(dishIngredients, dish.dishIngredients) && Objects.equals(price, dish.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, dishIngredients, price);
    }

    @Override
    public String toString() {
        String ingredientsStr = dishIngredients == null ? "[]":
                dishIngredients
                        .stream()
                        .map(dishIngredient -> {
                            Ingredient ingredient = dishIngredient.getIngredient();
                                return "[id=" + ingredient.getId()
                                        + ", name=" + ingredient.getName()
                                        + ", price=" + ingredient.getPrice()
                                        + ", category=" + ingredient.getCategory()
                                        + "]";
                        })
                        .collect(Collectors.joining(", "));

        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ingredients={" + ingredientsStr + "}" +
                ", price=" + price +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public List<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public void setDishIngredients(List<DishIngredient> dishIngredients) {
        this.dishIngredients = dishIngredients;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, List<DishIngredient> dishIngredients, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.dishIngredients = dishIngredients;
        this.price = price;
    }

    public Dish() {

    }

    public BigDecimal getDishCost() {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (DishIngredient dishIngredient : dishIngredients) {
            BigDecimal ingredientPrice = dishIngredient.getIngredient().getPrice();
            BigDecimal quantity = dishIngredient.getQuantityRequired();

            BigDecimal cost = ingredientPrice.multiply(quantity);
            totalPrice = totalPrice.add(cost);
        }

        return totalPrice;
    }

    public BigDecimal getGrossMargin() {
        if (price == null) {
            throw new RuntimeException("Price is null");
        }
        return price.subtract(getDishCost());
    }
}
