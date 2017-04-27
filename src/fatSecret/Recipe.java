package fatSecret;

import cbr.Value;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import gui.Utility;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.persistence.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Recipe")
public class Recipe implements Serializable{

    private String name;
    private int calories;
    private int rating;
    private int recipeId;
    private double preparationTime;
    private double cookingTime;
    private Set<Ingredient> ingredients = new HashSet<Ingredient>();
    private List<String> directions = new ArrayList<String>();
    private List<String> personalNotes = new ArrayList<String>();
    private String imageUrl = "";
    private String imageFilename = "";
    private String recipeUrl ="";

    public Recipe() {
    }
    
    public Recipe(Recipe rec) {
        this.name = rec.getName();
        this.calories = rec.getCalories();
        this.rating = rec.getRating();
        this.recipeId = rec.getRecipeId();
        this.preparationTime = rec.getPreparationTime();
        this.cookingTime = rec.getCookingTime();
        this.ingredients = rec.getIngredients();
        this.directions = rec.getDirections();
        this.personalNotes = rec.getPersonalNotes();
        this.imageUrl = rec.getImageUrl();
        this.imageFilename = rec.getImageFilename();
        this.recipeUrl = rec.getRecipeUrl();
    }

    public  Recipe(String xml) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException
    {
       this.preparationTime = -1;
       this.cookingTime = -1;
        readRecipe(xml);

    }

    protected void readRecipe(String xml) {
        try {

            SAXReader reader = new SAXReader();
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); // never forget this!
            org.xml.sax.InputSource inStream = new org.xml.sax.InputSource();

            inStream.setCharacterStream(new java.io.StringReader(xml));
            Document doc = reader.read(inStream);
            Element root = doc.getRootElement();

            List nodes = root.elements();

            for (int i = 0; i < nodes.size(); i++) {
                Element element = (Element) nodes.get(i);

                if (element.getName().equals("recipe_id")) {
                    this.setRecipeId(Integer.parseInt(element.getText()));
                }
                else if (element.getName().equals("recipe_name")) {
                    this.setName(element.getText());
                }
                else if (element.getName().equals("recipe_url")) {
                    this.setRecipeUrl(element.getText());
                }
                else if (element.getName().equals("rating")) {
                    if(!element.getText().contains("NaN")) {
                        this.setRating(Integer.parseInt(element.getText()));
                    }
                    else {
                        this.setRating(0);
                    }
                }
                else if (element.getName().equals("serving_sizes")) {
                    this.calories = Integer.parseInt(element.element("serving").element("calories").getText());
                }
                else if (element.getName().equals("recipe_images")) {
                    this.setImageUrl(element.element("recipe_image").getText());
                }
                else if (element.getName().equals("preparation_time_min")) {
                    this.setPreparationTime(Double.parseDouble(element.getText()));
                }
                else if (element.getName().equals("cooking_time_min")) {
                    this.setCookingTime(Double.parseDouble(element.getText()));
                }
                else if (element.getName().equals("ingredients")) {
                    List ings = element.elements();
                    int foodId = 0;
                    String foodName = "";
                    for(int j=0;j<ings.size();j++)
                    {
                        Element ingredient = (Element) ings.get(j);
                        foodId = Integer.parseInt(ingredient.element("food_id").getText());
                        foodName = ingredient.element("food_name").getText();
                        Ingredient ing = new Ingredient(foodId,foodName);
                        ingredients.add(ing);
                    }
                }
                else if (element.getName().equals("directions")) {
                    List<Element> dirs = element.elements();
                    for (int j = 0; j<dirs.size(); j++) {
                        Element direct = (Element) dirs.get(j).element("direction_description");
                        directions.add(direct.getText());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }

    public double getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(double preparationTime) {
        this.preparationTime = preparationTime;
    }

    public double getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(double cookingTime) {
        this.cookingTime = cookingTime;
    }

    public double getOverallTime() {
        return this.cookingTime+this.preparationTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "RECIPE_NAME", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getCalories() {
        return calories;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    @Id
    @Column(name = "RECIPE_ID")
    public int getRecipeId() {
        return recipeId;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "RECIPE_INGREDIENT", joinColumns = { @JoinColumn(name = "RECIPE_ID") }, inverseJoinColumns = { @JoinColumn(name = "FOOD_ID") })
    public Set<Ingredient> getIngredients() {
        return ingredients;
    }
    
    public List<String> getDirections() {
        return directions;
    }
    
    public List<String> getPersonalNotes() {
        return personalNotes;
    }
    
    public void addPersonalNotes(String note) {
        personalNotes.add(note);
    }


    private void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        if (imageUrl.equals(""))
            return;
        this.imageFilename = getRecipeId() + getImageUrl().substring(getImageUrl().lastIndexOf("."));
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRecipeUrl() {
        return recipeUrl;
    }

    public void setRecipeUrl(String recipeUrl) {
        this.recipeUrl = recipeUrl;
    }
    
    public String getUserFriendlyDifficulty() {

        //september 2013 addition overall time
        AgreementMF_zMFs[] compsets = Utility.fcconceptDifficulty.getCompositeSets2(getOverallTime());
        //make an arraylist for getting difficulty
        //order is cooking time and prep time
        ArrayList<Double> inputs = new ArrayList<Double>();
        inputs.add(cookingTime);
        inputs.add(preparationTime);
        //september 2013 addition of overalltime
        inputs.add(getOverallTime());
        Value difficulty = new Value(Utility.fcconceptDifficulty.evaluateConceptfor(inputs));

        return difficulty.getConsensusSliceName(compsets);
        
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", calories=" + calories +
                ", rating=" + rating +
                ", recipeId=" + recipeId +
                ", preparationTime=" + preparationTime +
                ", cookingTime=" + cookingTime +
                ", ingredients=" + ingredients +
                ", directions=" + directions +
                ", personal notes='" + personalNotes +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageFilename='" + imageFilename + '\'' +
                '}';
    }
}