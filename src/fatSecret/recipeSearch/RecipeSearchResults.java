package fatSecret.recipeSearch;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

public class RecipeSearchResults {

    ArrayList<RecipeSearchResult> recipeSearchResults;
    String xmlResult;

    public RecipeSearchResults(String xmlResult) {
        this.recipeSearchResults = new ArrayList<RecipeSearchResult>();
        this.xmlResult = xmlResult;
        parseXML(xmlResult);
    }

    private void parseXML(String xmlResult) {
        try {

            SAXReader reader = new SAXReader();
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); // never forget this!
            org.xml.sax.InputSource inStream = new org.xml.sax.InputSource();

            inStream.setCharacterStream(new java.io.StringReader(xmlResult));
            Document doc = reader.read(inStream);
            Element root = doc.getRootElement();

            List nodes = root.elements();
            for (int i = 0; i < nodes.size(); i++) {
                Element element = (Element) nodes.get(i);
                int recipeID;
                String recipeName;
                if (element.getName().equals("recipe")) {
                    recipeID = Integer.parseInt(element.element("recipe_id").getText());
                    recipeName = element.element("recipe_name").getText();
                    this.addRecipeResult(new RecipeSearchResult(recipeID,recipeName));
                }
            }


        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public ArrayList<RecipeSearchResult> getRecipeSearchResults() {
        return recipeSearchResults;
    }

    public void setRecipeSearchResults(ArrayList<RecipeSearchResult> recipeSearchResults) {
        this.recipeSearchResults = recipeSearchResults;
    }

    public void addRecipeResult(RecipeSearchResult recipeSearchResult)
    {
        this.recipeSearchResults.add(recipeSearchResult);
    }
}
