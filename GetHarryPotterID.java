package RestAssuredTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
class GetHarryPotterID {


		@BeforeAll
		public static void setup () {

			RestAssured.baseURI = "http://www.omdbapi.com/?apikey=edf63880";
		}

		@Test
		public Object getHarryPotterID () {
			ValidatableResponse response = given()
					.contentType(ContentType.JSON)
		 			.param("s", "Harry Potter")
					.when()
					.get(baseURI)
					.then()
					.assertThat().statusCode(200).body("Search.Year",notNullValue());

			JsonPath jsonPathEvaluator = response.extract().jsonPath();
			//if (jsonPathEvaluator.get("Search.Title") == "Harry Potter and the Sorcerer's Stone") {
			ArrayList imdbID = jsonPathEvaluator.get("Search.imdbID");
			//String imdbID = imdbID.get(1).toString();

			System.out.println("imdb Id is: " + imdbID.get(1));


			// SECOND PART : ID İLE ARAMA YAPMA ----------------------------------

			ValidatableResponse response2 = given()
					.contentType(ContentType.JSON).with()
					.queryParam("t","Harry Potter and the Sorcerer's Stone")
					.queryParam("i",imdbID.get(1))
					.when()
					.get(baseURI)
					.then()
					.assertThat().statusCode(200).body("Year",notNullValue())
					.body("Released",notNullValue())
					.log().body();
			return response2;


		}

	}