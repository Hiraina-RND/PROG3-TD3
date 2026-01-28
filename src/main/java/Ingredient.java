import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Ingredient {

    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
    private List<StockMovement> stockMovementList;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && category == that.category && Objects.equals(price, that.price) && Objects.equals(stockMovementList, that.stockMovementList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price, stockMovementList);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", stockMovementList=" + stockMovementList +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    public Ingredient(Integer id, String name, CategoryEnum category, Double price, List<StockMovement> stockMovementList) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockMovementList = stockMovementList;
    }


    public Ingredient() {
    }

    public StockValue getStockValueAt(Instant instant) {
        if (instant == null || stockMovementList == null || stockMovementList.isEmpty()) {
            return null;
        }

        for (StockMovement stockMovement : stockMovementList) {
            if (instant.equals(stockMovement.getCreationDateTime())) {
                return stockMovement.getValue();
            }
        }
        return null;
    }

    public double getQuantity(){
        double inNumber = 0;
        double outNumber = 0;

        for (StockMovement stockMovement : stockMovementList){
            if (stockMovement.getType().toString().equals("IN")){
                inNumber = inNumber + stockMovement.getValue().getQuantity();
            } else {
                outNumber = outNumber + stockMovement.getValue().getQuantity();
            }
        }
        return inNumber - outNumber;
    }
}
