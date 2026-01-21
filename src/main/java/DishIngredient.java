public class DishIngredient {

    private Integer id;
    private Dish dish;
    private Ingredient ingredient;
    private double quantityRequired;
    private UnitType unit;

    public int getId() {
        return id;
    }

    public Dish getDish() {
        return dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getQuantityRequired() {
        return quantityRequired;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setQuantityRequired(double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    public DishIngredient(int id, Dish dish, Ingredient ingredient, double quantityRequired, UnitType unit) {
        this.id = id;
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }
}
