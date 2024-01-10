package com.clog.automatizador;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PessoaAutomatizadorTest {

	@Value("${reqres.uri}")
	private String uri;

	@BeforeEach
    void startUp() {
		RestAssured.baseURI = uri;
		RestAssured.basePath = "api";
	}

	@Test
	void whenGetUsers_thenStatus200() {

		when().
			get("users/2").
		then().
			statusCode(200).
			body("data.email", is("janet.weaver@reqres.in")).
			body("data.first_name", is("Janet"));

	}

	@Test
	void whenGetUsers_thenStatus404() {

		when().
				get("users/240").
		then().
			statusCode(404).
			assertThat().
			body(equalTo("{}"));
	}

	@Test
	void givenUserInMap_whenPostUsers_thenStatus201() {

		Map<String, String> body = new HashMap<String, String>();
		body.put("name", "morpheus");
		body.put("job","leader");

		given().
			contentType(ContentType.JSON).
			body(body).
		when().
			post("users").
		then().
			statusCode(201).
			body("name", is("morpheus")).
			body("job", is("leader"));
	}

	@Test
	void givenUser_whenPostUsers_thenStatus201() {

		Pessoa pessoaBody = new Pessoa("morpheus", "leader");

		given().
			contentType(ContentType.JSON).
			body(pessoaBody).
		when().
			post("users").
		then().
			statusCode(201).
			body("name", is("morpheus")).
			body("job", is("leader"));
	}

	record Pessoa(String name, String job){}

}
