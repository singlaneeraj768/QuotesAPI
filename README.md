Project Details
Project: Cucumber, BDD, gherkins, page object model
Language: Java
Library: Rest-assured
Features files: src/test/resources/feature/ListQuotes.feature, src/test/resources/feature/FavQuotes.feature, 
Runner class: src/test/java/runner/CucumberRunnerTests.java
Pom.xml: Available at project root. We have below dependencies:
-	io.rest-assured:rest-assured
-	 org.json:json
-	io.cucumber:cucumber-junit
-	io.cucumber:cucumber-java
-	io.cucumber:cucumber-testng
-	io.cucumber:gherkins
Implementation
Methods 
-	Created method for creating sessions and tokens
-	Created method for List quotes with multiple filters
-	Added code to handle all the pages of the result
-	Verification has been covered with id and related field (based on filter)
-	For Fav quotes hitting the ListQuotes api and picking the first quote object id from the response and storing the count of favorite_count of the object and then making it fav/unfav and then comparing the count:
o	 if fav case, then validating the before count (before making fav)< after count (after making fav), returned as true
o	If unfav case, If before count and after count is equal then returned as true
Cases Covered
List Quotes:
-	Search quotes while passing name as “funny” and verified the same
-	Search quotes with value “funny” with respect to tag
-	Search author with value “Neeraj Singla” and expecting the value as true
-	Search author with value “Mohnish” and expecting value as false since there is no author with this name
-	Search tag as private and expecting the value as true since don’t have the privilege to add the quotes so expecting the value as true but since the result return nothing hence test case is passing
Covered all the pages of the searched output
Fav Quotes:
-	Picked the random quote id and store the count of quote and then make it fav and verified the result
-	Picked the random quote id and store the count of quote and then make it unfav and verified the result
-	Verified favorite_count Boolean type after hitting the put api of fav/unfav
Test cases did not cover:
-	Search hidden filter since don’t have the privilege to add and hide quotes so could not cover the case
-	Search quotes with user filter since not sure how we can validate the case as in response cant see the key named as user hence could not verify
-	Search for private quotes of the current user session that contain the words
-	Since don’t have the privilege to add quotes hence randomly picked the quote id for fav/unfav
-	Negative/edge cases
Set up for running locally!
-	Please clone the project from https://github.com/singlaneeraj768/QuotesAPI repo
-	Import it any IDE. For instance: IntelliJ. It provides us option to import new project from Version control as well through File -> New -> “Project from Version Control…”
-	Provide the user details like login(login name), password, apiKey in the file src/test/java/pages/APITesting.java (Ideally should be externalized to properties file or similar)
-	Execute/Run the feature files available at src/test/resources/feature (right click and select run)
-	 
 
