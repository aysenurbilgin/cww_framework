package fatSecret.recipeSearch;

public class RecipeSearchResult {
        int recipeID;
        String recipeName;

    public RecipeSearchResult(int recipeID, String recipeName) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    @Override
    public String toString() {
        return "RecipeSearchResult{" +
                "recipeID=" + recipeID +
                ", recipeName='" + recipeName + '\'' +
                '}';
    }
}
