import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();
        List<Ingredient> allIngredients = dataRetriever.findIngredients(1, 20);
        for (Ingredient ingredient : allIngredients){
            System.out.println(ingredient.getName() + " : " + ingredient.getStockValueAt(LocalDateTime
                    .of(2024, 1, 6, 12, 0)
                    .atZone(ZoneId.systemDefault()).toInstant()));
        }
        System.out.println("=========");

        for (Ingredient ingredient : allIngredients) {
            System.out.println(ingredient.getName() + " : " + ingredient.getQuantity());
        }

    }
}
