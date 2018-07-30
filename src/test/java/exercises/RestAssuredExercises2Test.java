package exercises;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;


public class RestAssuredExercises2Test {

	private static RequestSpecification requestSpec;

	@BeforeAll
	public static void createRequestSpecification() {

		requestSpec = new RequestSpecBuilder().
			setBaseUri("http://localhost").
			setPort(9876).
			setBasePath("/api/f1").
			build();
	}
	
	/*******************************************************
	 * Use junit-jupiter-params for @ParameterizedTest that
	 * specifies in which country
	 * a specific circuit can be found (specify that Monza 
	 * is in Italy, for example) 
	 ******************************************************/

	//todo
	@ParameterizedTest(name = "{index} => circuit={0}")
	@CsvSource({"monza"})
	public void findCircuitInWhichContry(String circuit) {
		given()
				.spec(requestSpec)
				.pathParam("circuit", circuit)
				.when()
				.get("/circuits/{circuit}.json")
				.then()
				.body("MRData.CircuitTable.Circuits.Location.country", hasItem("Italy"));
	}

	/*******************************************************
	 * Use junit-jupiter-params for @ParameterizedTest that specifies for all races
	 * (adding the first four suffices) in 2015 how many  
	 * pit stops Max Verstappen made
	 * (race 1 = 1 pitstop, 2 = 3, 3 = 2, 4 = 2)
	 ******************************************************/

	//todo
	@ParameterizedTest(name = "{index} => race={0}, pit={1}")
	@CsvSource({
			"1, 1",
			"2, 3",
			"3, 2",
			"4, 2"
	})
	public void findPitstopsNum(int race, int pit) {

		given().
				spec(requestSpec).
				pathParam("race", race).
				when().
				get("/2015/{race}/drivers/max_verstappen/pitstops.json").
				then().
				assertThat().
				body("MRData.RaceTable.Races[0].PitStops.size()", equalTo(pit));
	}

	/*******************************************************
	 * Request data for a specific circuit (for Monza this 
	 * is /circuits/monza.json)
	 * and check the country this circuit can be found in
	 ******************************************************/
	
	@Test
	public void checkCountryForCircuit() {
		
		given().
			spec(requestSpec).
				pathParam("circuit", "monza").
		when().
				get("/circuits/{circuit}.json").
		then().
				body("MRData.CircuitTable.Circuits.Location.country", hasItem("Italy"));
	}
	
	/*******************************************************
	 * Request the pitstop data for the first four races in
	 * 2015 for Max Verstappen (for race 1 this is
	 * /2015/1/drivers/max_verstappen/pitstops.json)
	 * and verify the number of pit stops made
	 ******************************************************/
	
	@ParameterizedTest(name = "{index} => race={0}, pit={1}")
	@CsvSource({
			"1, 1",
			"2, 3",
			"3, 2",
			"4, 2"
	})
	public void checkNumberOfPitstopsForMaxVerstappenIn2015(int race, int pit) {
		
		given().
			spec(requestSpec).
				pathParam("race", race).
		when().
				get("/2015/{race}/drivers/max_verstappen/pitstops.json").
		then().
				body("MRData.RaceTable.Races[0].PitStops.size()", is(pit));
	}
}