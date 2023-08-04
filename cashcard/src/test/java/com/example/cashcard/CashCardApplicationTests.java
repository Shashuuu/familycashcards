package com.example.cashcard;

import com.example.cashcard.model.CashCard;
import com.example.cashcard.model.CashCardUser;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//	Start spring boot application and make it available for test to perform requests
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//	Handles inter-test interaction. For example, cleaning up after creating a new CashCard
class CashCardApplicationTests {
	@Autowired
//	create dependency instance when necessary
	TestRestTemplate restTemplate;

	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
//		ResponseEntity<String> is the type of response returned when requested to the /cashcards/{id} endpoint using GET
		ResponseEntity<String> response = restTemplate					// restTemplate makes http requests to the endpoint
										.withBasicAuth("sarah1", "abc123")		//	user credentials as configured in SecurityConfig
										.getForEntity("/home/cashcards/99", String.class);		//	makes GET request on url and converts response

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);		//	200 OK

		DocumentContext documentContext = JsonPath.parse(response.getBody());		//	JsonPath.parse() converts response string to json object

		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(99);

		Double amount = documentContext.read("$.amount");
		assertThat(amount).isEqualTo(123.45);
	}

	@Test
	void shouldNotReturnACashCardWithAnUnknownId() {
		ResponseEntity<String> response = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.getForEntity("/home/cashcards/1000", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);		//	404 NOT_FOUND
		assertThat(response.getBody()).isBlank();
	}

	@Test
	@DirtiesContext
	//	Handles inter-test interaction. For example, cleaning up after creating a new CashCard
	void shouldCreateANewCashCard() {
		CashCard newCashCard = new CashCard(null, 250.00, null);
		ResponseEntity<Void> createResponse = restTemplate
											.withBasicAuth("sarah1", "abc123")
											.postForEntity("/home/cashcards", newCashCard, Void.class);		//	makes POST request on the url to create a resourse that is passed as request and dont expect any CashCard to return and hence the Void.Class

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);		//	201 CREATED

		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();		//	to retrieve location of created resource from header of create response which is a URI

		ResponseEntity<String> getResponse = restTemplate
											.withBasicAuth("sarah1", "abc123")
											.getForEntity(locationOfNewCashCard, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");

		assertThat(id).isNotNull();
		assertThat(amount).isEqualTo(250.00);
	}

	@Test
	void shouldReturnAllCashCardsWhenListIsRequested() {
		ResponseEntity<String> response = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.getForEntity("/home/cashcards", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		int cashCardCount = documentContext.read("$.length()");			//	$.length() calculates length of the array in response body
		assertThat(cashCardCount).isEqualTo(3);

		JSONArray ids = documentContext.read("$..id");						//	$..id retrieves list of all id values
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00);
	}

	@Test
	void shouldReturnAPageOfCashCards() {
		ResponseEntity<String> response = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.getForEntity("/home/cashcards?page=0&size=1", String.class);		//	url represents the condition to retrieve first page with atmost 1 entry

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");

		assertThat(page.size()).isEqualTo(1);
	}

	@Test
	void shouldReturnASortedPageOfCashCards() {
		ResponseEntity<String> response = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.getForEntity("/home/cashcards?page=0&size=1&sort=amount,desc", String.class);		//	retrieve first page of size 1 and sorted descending on amount

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		JSONArray read = documentContext.read("$[*]");
		assertThat(read.size()).isEqualTo(1);

		double amount = documentContext.read("$[0].amount");
		assertThat(amount).isEqualTo(150.00);
	}

	@Test
	void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
		ResponseEntity<String> response = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.getForEntity("/home/cashcards", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(3);

		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
	}

	@Test
	void shouldNotReturnACashCardWhenUsingBadCredentials() {
		ResponseEntity<String> response = restTemplate
										.withBasicAuth("BAD-USER", "abc123")
										.getForEntity("/home/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		response = restTemplate
				.withBasicAuth("sarah1", "BAD-PASSWORD")
				.getForEntity("/home/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldRejectUsersWhoAreNotCardOwners() {
		ResponseEntity<String> response = restTemplate
										.withBasicAuth("hank-owns-no-cards", "qrs456")
										.getForEntity("/home/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
		ResponseEntity<String> response = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.getForEntity("/home/cashcards/102", String.class); // kumar2's data

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingCashCard() {
		CashCard cashCardUpdate = new CashCard(null, 19.99, null);
		HttpEntity<CashCard> request = new HttpEntity<>(cashCardUpdate);

		ResponseEntity<Void> response = restTemplate
									.withBasicAuth("sarah1", "abc123")
									.exchange("/home/cashcards/99", HttpMethod.PUT, request, Void.class);		//	.exchange() is generalized method for GET, POST, PUT and DELETE methods

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);			//	204 NO_CONTENT

		ResponseEntity<String> getResponse = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.getForEntity("/home/cashcards/99", String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");

		assertThat(id).isEqualTo(99);
		assertThat(amount).isEqualTo(19.99);
	}

	@Test
	void shouldNotUpdateACashCardThatDoesNotExist() {
		CashCard unknownCard = new CashCard(null, 19.99, null);
		HttpEntity<CashCard> request = new HttpEntity<>(unknownCard);

		ResponseEntity<Void> response = restTemplate
									.withBasicAuth("sarah1", "abc123")
									.exchange("/home/cashcards/99999", HttpMethod.PUT, request, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingCashCard() {
		ResponseEntity<Void> response = restTemplate
									.withBasicAuth("sarah1", "abc123")
									.exchange("/home/cashcards/99", HttpMethod.DELETE, null, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
									.withBasicAuth("sarah1", "abc123")
									.getForEntity("/home/cashcards/99", String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteACashCardThatDoesNotExist() {
		ResponseEntity<Void> deleteResponse = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.exchange("/home/cashcards/99999", HttpMethod.DELETE, null, Void.class);

		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotAllowDeletionOfCashCardsTheyDoNotOwn() {
		ResponseEntity<Void> deleteResponse = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.exchange("/home/cashcards/102", HttpMethod.DELETE, null, Void.class);

		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		ResponseEntity<String> getResponse = restTemplate
										.withBasicAuth("kumar2", "xyz789")
										.getForEntity("/home/cashcards/102", String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@DirtiesContext
	@Test
	void shouldCreateANewUser() {
		CashCardUser newCashCardUser = new CashCardUser("shashu3", "saisau1996");
		ResponseEntity<Void> createUserResponse = restTemplate
										.postForEntity("/index/create", newCashCardUser, Void.class);

		assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI newCashCardUserHome = createUserResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate
										.withBasicAuth("shashu3", "saisau1996")
										.getForEntity(newCashCardUserHome, String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	@DirtiesContext
	void shouldNotCreateADuplicateCashCardUser() {
		CashCardUser newCashCardUser = new CashCardUser("sarah1", "abc");
		ResponseEntity<String> duplicationResponse = restTemplate
				.postForEntity("/index/create", newCashCardUser, String.class);

		assertThat(duplicationResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@DirtiesContext
	void shouldChangePasswordOfLoggedInUser() {
		HttpEntity<String> newPassword = new HttpEntity<>("new123");

		ResponseEntity<Void> changeResponse = restTemplate
										.withBasicAuth("sarah1", "abc123")
										.exchange("/home/changepwd", HttpMethod.PUT, newPassword, Void.class);

		assertThat(changeResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
										.withBasicAuth("sarah1", "new123")
										.getForEntity("/home", String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

	@Test
	void shouldReturnPortalWelcomeMessage() {
		String welcomeText = "Welcome to Family CashCards Portal";

		ResponseEntity<String> getResponse = restTemplate
										.getForEntity("/index", String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody()).isEqualTo(welcomeText);
	}

	@Test
	void shouldWelcomeCashCardUser() {
		String welcomeMessage = "Welcome to your CashCards Dashboard sarah1";

		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/home", String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody()).isEqualTo(welcomeMessage);
	}
}