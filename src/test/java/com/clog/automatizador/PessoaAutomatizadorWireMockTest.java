package com.clog.automatizador;

import com.clog.automatizador.base.AutomatizadorMockBaseConfig;
import com.clog.automatizador.records.Parcela;
import com.clog.automatizador.util.ParcelasCsvParaObjetoUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.TypedArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

class PessoaAutomatizadorWireMockTest extends AutomatizadorMockBaseConfig {

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
			body("data.first_name", is("Janet111"));

	}

	@Test
	void givenUserInMap_whenPostUsers_thenStatus201() {

		Map<String, String> body = new HashMap<String, String>();
		body.put("name", "morpheus");
		body.put("job","leader");

		given().
			accept(ContentType.JSON).
			contentType(ContentType.JSON).
			body(body).
		when().
			post("users").
		then().
			statusCode(201).
			body("name", is("morpheus")).
			body("job", is("leader"));
	}

	@DisplayName("Test ParameterizedTest from CSV to Parcelas")
	@ParameterizedTest
	@ValueSource(strings = {"planos/OPER_1250_SIMULACAO_EMPRESTIMO_PESSOAL_PRICE.csv"})
	void givenUserInMap_whenPostUsers_thenStatus201_ParameterizedTest(String fileName) {

		List<Parcela> parcelas = new ParcelasCsvParaObjetoUtil().csvParaParcela(fileName);

		Assertions.assertFalse(parcelas.isEmpty());
		Assertions.assertEquals(20, parcelas.size());
	}

}
