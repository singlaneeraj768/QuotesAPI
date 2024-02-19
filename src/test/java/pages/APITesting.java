package pages;

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.core.IsEqual.equalTo;

public class APITesting {
    // To do
    //Please provide login, password, apiKey
    static String login = "";
    static String password = "";

    static String base_URI = "";
    static String apiKey = "";

    /**
     * @return String value
     * @description method for creating session
     */
    private String createSession() {

        JSONObject user = new JSONObject();
        JSONObject data = new JSONObject();
        Header auth = new Header("Authorization", "Token token=" + apiKey);
        user.put("user", data);
        data.put("login", login);
        data.put("password", password);
        Response rs = given()
                .header(auth)
                .contentType("application/json")
                .body(user.toString())
                .when()
                .post(base_URI + "/session")
                .then()
                .statusCode(200)
                .extract().response();

        return rs.path("User-Token");
    }

    /**
     * @return Header
     * @description method for getting Authorized token
     */
    private Header getAuthorization() {
        Header auth = new Header("Authorization", "Token token=" + apiKey);
        return auth;
    }

    /**
     * @return Header
     * @description method for creating and getting user token while calling createSession method
     */
    private Header getUserToken() {
        Header userToken = new Header("User-Token", createSession());
        return userToken;
    }

    /***
     *
     * @param filter
     * @param value
     * @param valueExpected
     * @description filter used in List Quotes
     */
    @Test
    public void filter(String filter, String value, String valueExpected) {
        // Filter : we have to pass which type of filter we want to apply
        // value : value is for string on which bases we want to filter
        // valueExpected : is used here to verify whether we are expecting the value to be there or not
        boolean expected = Boolean.parseBoolean(valueExpected);
        //Converting String to boolean type
        String filterType;
        //filter type is to search different types of filer
        String fieldName;
        // fieldName is used to verify the output
        switch (filter) {
            case "text":
                filterType = "filter=" + value;
                fieldName = "quotes";
                listQuotes(filterType, fieldName, value, expected);
                break;

            case "tags":
                filterType = "filter=" + value + "&type=tag";
                fieldName = "tags";
                listQuotes(filterType, fieldName, value, expected);
                break;

            case "author":
                filterType = "filter=" + value + "&type=author";
                fieldName = "author";
                listQuotes(filterType, fieldName, value, expected);
                break;

            case "user":
                filterType = "filter=" + value + "&type=user";
                fieldName = "author";
                listQuotes(filterType, fieldName, value, expected);
                break;

            case "private":
                fieldName = "private";
                listQuotes("private=1", fieldName, value, expected);
                break;
            case "hidden":
                filterType = "hidden=1";
                fieldName = "hidden";
                listQuotes(filterType, fieldName, value, expected);
                break;

            default:
                filterType = "";
                fieldName = "id";
                listQuotes(filterType, fieldName, value, expected);
                break;
        }
    }

    /***
     *
     * @param filterType
     * @param fieldName
     * @param value
     * @param valueExpected
     * @description Method for getting the list of quotes and verification of same
     * for all the pages
     */
    @Test
    public void listQuotes(String filterType, String fieldName, String value, Boolean valueExpected) {
        Map<Integer, JSONObject> map = new HashMap<>();
        boolean last_page = false;

        int count = 1;
        // Declaring count variable for checking the values in different pages
        Response response;
        // Storing response
        while (last_page != true) {
            // looping till it reaches the last page
            // Since we required User token for private filter adding the condition
            // loop while end when last_page becomes true
            if (fieldName.equals("private")) {
                response = given()
                        .header(getUserToken())
                        .header(getAuthorization())
                        .contentType("application/json")
                        .when()
                        .get(base_URI + "/quotes/?page=" + count + "&" + filterType)
                        // count is used for checking different pages
                        // filterType is used for passing different type of filters
                        .then()
                        .statusCode(200)
                        .extract().response();
            } else {
                response = given()
                        .header(getAuthorization())
                        .contentType("application/json")
                        .when()
                        .get(base_URI + "/quotes/?page=" + count + "&" + filterType)
                        .then()
                        .statusCode(200)
                        .extract().response();
            }
            //Since we get object in Json storing it in JsonObject and storing it as String
            JSONObject jsnobject = new JSONObject(response.asString());
            // Since we have number of quotes object storing the quotes data in JsonArray object
            JSONArray jsonArray = jsnobject.getJSONArray("quotes");
            // if search does not give any result we are actually coming out of this method
            if (jsonArray.getJSONObject(0).getString("body").equals("No quotes found")) {
                break;
            } else {
                // Looping for checking for all objects based on search
                for (int i = 0; i < jsonArray.length(); i++) {
                    // Storing the quotes id as Key and related details in value
                    map.put(jsonArray.getJSONObject(i).getInt("id"), jsonArray.getJSONObject(i));
                    // declaring boolean variable for checking whether actual result
                    boolean match = false;
                    // have seggregated verification fields based on different filters
                    if (fieldName.equals("quotes")) {
                        match = jsnobject.getJSONArray(fieldName).toString().contains(value);
                    } else if (fieldName.equals("tags")) {
                        match = jsonArray.getJSONObject(i).getJSONArray(fieldName).toString().contains(value);
                    } else if (fieldName.equals("private")) {
                        match = jsonArray.getJSONObject(i).getBoolean(fieldName);

                    } else if (fieldName.equals("hidden")) {
                        match = jsonArray.getJSONObject(i).getJSONObject("user_details").getBoolean(fieldName);
                    } else if (fieldName.equals("favorites_count") && jsonArray.getJSONObject(i).getInt("id") == Integer.parseInt(value)) {
                        int size = jsonArray.getJSONObject(i).getInt("favorites_count");
                        if (size > 0) {
                            match = true;
                        }
                    } else {
                        match = jsonArray.getJSONObject(i).getString(fieldName).contains(value);

                    }
                    // returning true if actaul and expected both are matching
                    Assert.assertEquals(match, valueExpected);
                    // appending to 1 for checking all pages
                    count++;
                    last_page = response.path("last_page");
                }
            }
        }
        System.out.println(map.entrySet());
    }

    @Test
    /* Creating this method to get the random quotes and pick the id
     *  and count of favorite counts, running favQuotes method to make
     * the quotes fav/unfav once it is done we are checking the count
     * if we are making it fav then count is increasing and if we are
     * making it unfav then the same count we are getting verifying that
     */
    public void getQuotesfav(String isfav) {
        boolean fav = Boolean.parseBoolean(isfav);
        Map<Integer, JSONObject> map = new HashMap<>();
        Response rs = given()
                .header(getAuthorization())
                .contentType("application/json")
                .when()
                .get(base_URI + "/quotes/")
                .then()
                .statusCode(200)
                .extract().response();
        JSONObject jsnobject = new JSONObject(rs.asString());
        JSONArray jsonArray = jsnobject.getJSONArray("quotes");
        map.put(jsonArray.getJSONObject(0).getInt("id"), jsonArray.getJSONObject(0));

        int favouriteCount = jsonArray.getJSONObject(0).getInt("favorites_count");
        int output = favQuotes(jsonArray.getJSONObject(0).getInt("id"), fav);

        if ((favouriteCount < output && fav) || (favouriteCount == output && !fav)) {
            Assert.assertTrue(true);
        } else {
            Assert.fail();
        }
    }

    /***
     *
     * @param id
     * @param isFav
     * @return favoritecount
     * favQuotes method is used for making the quotes favorite/unfavorite based on boolean isFav
     */
    @Test
    public int favQuotes(int id, boolean isFav) {
        String quoteType;
        if (isFav) {
            quoteType = "/fav";
        } else {
            quoteType = "/unfav";
        }
        Response rs = given()
                .header(getAuthorization())
                .header(getUserToken())
                .contentType("application/json")
                .when()
                .put(base_URI + "/quotes/" + id + quoteType)
                .then()
                .statusCode(200)
                .extract().response();
        String favorites = rs.path("favorites_count").toString();
        if ((isFav && Boolean.parseBoolean(favorites)) || (!isFav && Boolean.parseBoolean(favorites))) {
            System.out.println("Operation Successful");
        }
        return rs.path("favorites_count");
    }

    public Map<String, String> addQuotes(String author, String body) {

        JSONObject quote = new JSONObject();
        JSONObject data = new JSONObject();
        quote.put("quote", data);
        data.put("author", author);
        data.put("body", body);
        Response rs = given()
                .header(getAuthorization())
                .header(getUserToken())
                .contentType("application/json")
                .body(quote.toString())
                .when()
                .post(base_URI + "/quotes")
                .then()

                .statusCode(200)
                .body("author", equalTo(author))
                .body("body", equalTo(body))
                .extract().response();
        Map<String, String> hm = new HashMap<>();
        hm.put("id", rs.then().extract().path("id"));
        hm.put("author", rs.then().extract().path("author"));
        hm.put("body", rs.then().extract().path("body"));
        return hm;
    }

    public Map<String, Object> getQuotes(Object id) {
        Response rs = given()
                .header(getAuthorization())
                .contentType("application/json")
                .when()
                .get(base_URI + "/quotes/" + id)
                .then()
                .statusCode(200)
                .extract().response();
        Map<String, Object> quotes = new HashMap<>();
        quotes.put("id", rs.then().extract().path("id"));
        quotes.put("author", rs.then().extract().path("author"));
        quotes.put("body", rs.then().extract().path("body"));
        System.out.println(quotes.keySet());
        System.out.println(quotes.entrySet());
        return quotes;
    }

}