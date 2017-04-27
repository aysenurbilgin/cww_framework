package fatSecret;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name="INGREDIENT")
public class Ingredient implements Serializable
{

    private int foodId;
    private String foodName;
//    private String xml = "";
    public Ingredient(int foodId, String foodName)
    {
        this.setFoodId(foodId);
        this.setFoodName(foodName);
    }

    public Ingredient() {
    }

    protected void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    @Id
    @Column(name="FOOD_ID")
    public int getFoodId() {
        return foodId;
    }
    protected void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodName() {
        return foodName;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "foodId=" + foodId +
                ", foodName='" + foodName + '\'' +
                '}';
    }
}
