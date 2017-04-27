package fatSecret;

import gui.Utility;
import fatSecret.recipeSearch.RecipeSearchResults;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SignatureException;
import java.sql.SQLException;
import java.util.Hashtable;

public class FatSecret {
    
    /**
    * this gets rid of exception for not using native acceleration
    */
    
    private int user_count = 0;
    public static boolean changeKey = false;
    
    static
    {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    }
    
    public FatSecret()
    {}

    private String callRest(Hashtable<String, String> params) throws SignatureException, IOException
    {
        String urlString = "http://platform.fatsecret.com/rest/server.api";

        if (FatSecret.changeKey) {
            FatSecret.changeKey = false;
            user_count = (user_count+1) % 4;
        }
        FatParameter fp = new FatParameter(true, params, user_count);
        urlString += "?" + fp.GetAllParamString();

        return readUrl(urlString);
    }

    private String callRest(String accessSharedSecret, Hashtable<String, String> params) throws SignatureException, IOException
    {
        String urlString = "http://platform.fatsecret.com/rest/server.api";
        
        if (FatSecret.changeKey) {
            FatSecret.changeKey = false;
            user_count = (user_count+1) % 4;
        }

        FatParameter fp = new FatParameter(true, params, accessSharedSecret, user_count);
        urlString += "?" + fp.GetAllParamString();

        return readUrl(urlString);
    }

    private String callRestByDefaultUser(Hashtable<String, String> params) throws SignatureException, IOException
    {
//        params.put("oauth_token", "c0d80ee3e5fb47f49f122439ee036189");
//        return callRest("0f7d0dabc817415495a6d972df9389a7", params);
        
        params.put("oauth_token", "b4352e571ada43c1beb2d8787b987cee");
        return callRest("911f9f4276eb4a57865d07f95033c157", params);
        
    }

    private String readUrl(String urlString) throws IOException
    {
        String result = "";
        // Create a URL for the desired page
        URL url = new URL(urlString);

        // Read all the text returned by the server
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
        String str;
        while ((str = in.readLine()) != null) {
            // str is one line of text; readLine() strips the newline character(s)
            result += str + "\n";
        }
        in.close();
        return result;
    }

    public String getRecipe(int recipeId) throws SignatureException, IOException, SQLException
    {
        Hashtable<String, String> params = new Hashtable<String, String>();
        //method        String  MUST be "recipe.get"
        params.put("method", "recipe.get");
        //recipe_id     Long     The ID of the recipe to retrieve.
        params.put("recipe_id", Integer.toString(recipeId));

        return callRest(params);
    }

    public String searchFood(String food) throws SignatureException, IOException, SQLException
    {
        Hashtable<String, String> params = new Hashtable<String, String>();
        //method        String  MUST be "recipe.get"
        params.put("method", "foods.search");
        //recipe_id     Long     The ID of the recipe to retrieve.
        params.put("search_expression", food);

        return callRest(params);
    }

    public String searchRecipes(String recipe,String page, String maxResult) throws SignatureException, IOException, SQLException
    {
        Hashtable<String, String> params = new Hashtable<String, String>();
        //method        String  MUST be "recipe.get"
        params.put("method", "recipes.search");
        //recipe_id     Long     The ID of the recipe to retrieve.
        params.put("search_expression", recipe);
        params.put("page_number", page);
//        params.put("recipe_type", "Breakfast");
        params.put("max_results", maxResult);

        return callRest(params);
    }

    private String getFood(int foodId) throws SignatureException, IOException
    {
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("method", "food.get");
        //food_id       Long     The ID of the food to retrieve.
        params.put("food_id", Integer.toString(foodId));

        return callRest(params);
    }

    // user_id : nightingale
    //  <auth_token>c0d80ee3e5fb47f49f122439ee036189</auth_token>
    //  <auth_secret>0f7d0dabc817415495a6d972df9389a7</auth_secret>
    private String createProfile(String userId) throws SignatureException, IOException
    {
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("method", "profile.create");
        params.put("user_id", userId);

        return callRest(params);
    }

    public static int crawlOneRecipe(int recipeId, FatSecret fs) throws Exception
    {
        System.out.println("Getting recipe #" + recipeId);

        String xml = fs.getRecipe(recipeId);
//        System.out.println(xml);
        if (xml.equals(""))
        {
            System.out.println(xml);
            return 0;
        }

        if (xml.indexOf("</error>") != -1)
        {
            System.out.println(xml);
            return -1;
        }

        Recipe r = new Recipe(xml);

        if (!r.getImageUrl().equals(""))
        {
            UrlDownload.fileUrl(r.getImageUrl(), r.getImageFilename(), "img/");
            if (r.getImageFilename().toLowerCase().endsWith(".jpg") ||
                    r.getImageFilename().toLowerCase().endsWith(".png") ||
                    r.getImageFilename().toLowerCase().endsWith(".gif"))
            {
                String filepath = "img/\\" + r.getImageFilename();
                File f = new File(filepath);
                byte[] imageData = Utils.getBytesFromFile(f);
                ImageResize ir = new ImageResize();
                byte[] largeImageData = ir.resizeImageAsJPG(imageData, 300);
                byte[] mediumImageData = ir.resizeImageAsJPG(imageData, 200);
                byte[] smallImageData = ir.resizeImageAsJPG(imageData, 100);
                String prefixPath = "img/" + r.getRecipeId() + "_";
                Utils.writeBytesToFile(prefixPath + "large.jpg", largeImageData);
                Utils.writeBytesToFile(prefixPath + "medium.jpg", mediumImageData);
                Utils.writeBytesToFile(prefixPath + "small.jpg", smallImageData);
            }
            else
                throw new Exception("Unsupport Image File type: " + r.getImageFilename());
        }

        if(r.getCookingTime()>-1)
        {
            if (r.getPreparationTime() < 0) {
                r.setPreparationTime(0);
            }
            if (!Utility.PUBLIC_BASE.doesSolutionExist(Utility.activity, r)) {
                Utility.NEWRECIPES.add(r);
            }

        }
        return 1;
    }


    public static int searchFood(String food, FatSecret fs) throws Exception
    {
        System.out.println("Searching for: " + food);

        String xml = fs.searchFood(food);

        System.out.println(xml);
        if (xml.equals(""))
        {
            System.out.println(xml);
            return 0;
        }

        if (xml.indexOf("</error>") != -1)
        {
            System.out.println(xml);
            return -1;
        }

        return 1;
    }

    public static RecipeSearchResults searchRecipe(String recipe, FatSecret fs,String page, String maxResult) throws Exception
    {
        System.out.println("Searching for: " + recipe);

        String xml = fs.searchRecipes(recipe, page, maxResult);

        RecipeSearchResults recipeSearchResults = new RecipeSearchResults(xml);
        

        return recipeSearchResults;
    }

//    public static void main(String[] args) throws NamingException {
//        FatSecret fs = new FatSecret();
//        Utility.fatSecret = fs;
//        Context ctx = new InitialContext();
//
//        FatSecretGUI fatSecretGUI = new FatSecretGUI("FatSecret");
//
//    }
}