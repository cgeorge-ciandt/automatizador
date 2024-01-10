package com.clog.automatizador;

import com.clog.automatizador.base.AutomatizadorMockBaseConfig;
import com.clog.automatizador.records.Parcela;
import com.clog.automatizador.util.ParcelasCsvParaObjetoUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class Oper1250SimulacaoEmprestimoPessoalPriceTest extends AutomatizadorMockBaseConfig {

    @Value("${topaz.uri}")
    private String uri;

    @BeforeEach
    void startUp() {
        RestAssured.baseURI = uri;
        RestAssured.basePath = "topazinterpretedws";
    }

    @DisplayName("OPER-1250: Simulacao Emrpestimo Pessoal Price Status 200")
    @ParameterizedTest
    @ValueSource(strings = {"planos/OPER_1250_SIMULACAO_EMPRESTIMO_PESSOAL_PRICE.csv"})
    void givenOperacaoMap_whenPostSimulacao_thenStatus200(String arquivo) {

        List<Parcela> parcelas = new ParcelasCsvParaObjetoUtil().csvParaParcela(arquivo);

        Map<String, Object> body = Map.of(
                "insuranceAmount", 600,
                "productCode", "200",
                "hiringDate", "2023-12-28",
                "organization", 730,
                "requestedAmount", 15500.0,
                "paymentPeriod", "MONTHLY",
                "firstDueDate", "2024-01-12",
                "monthlyInterestRate", 4.15,
                "installmentsQuantity", 20,
                "customer", 2435594
        );

        Map<String, String> headers = Map.of(
                "Topaz-Transaction-ID", "Topaz-Transaction-ID",
                "Topaz-User", "WEB",
                "Topaz-Branch", "10",
                "Topaz-Company", "730",
                "Topaz-Channel", "WEB"
        );

        JsonPath jsonPath = given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                headers(headers).
                body(body).
                log().all().
            when().
                post("credit/v1/simulation").
            then().
                log().all().
                statusCode(200).
                body("customer", is(2435594)).
                body("requestedAmount", is(15500.0F)).
                body("organization", is(730)).
            extract().jsonPath();

        Assertions.assertTrue(Objects.nonNull(jsonPath));
        validarParcelas(jsonPath, parcelas);
    }

    private void validarParcelas(JsonPath jsonPath, List<Parcela> parcelas) {
        parcelas.forEach(parcela -> {

            Integer installment = jsonPath.get("installments.find { it.installment == " + parcela.parc() + " }.installment");
            String dueDateString = jsonPath.get("installments.find { it.installment == " + parcela.parc() + " }.dueDate").toString();
            LocalDate dueDate = OffsetDateTime.parse(dueDateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate();
            ;
            BigDecimal interestAmount =
                    new BigDecimal(
                            jsonPath.get("installments.find { it.installment == " + parcela.parc() + " }.interestAmount").toString()
                    ).setScale(2);

            BigDecimal principalAmount =
                    new BigDecimal(
                            jsonPath.get("installments.find { it.installment == " + parcela.parc() + " }.principalAmount").toString()
                    ).setScale(2);

            BigDecimal installmentValue =
                    new BigDecimal(
                            jsonPath.get("installments.find { it.installment == " + parcela.parc() + " }.installmentValue").toString()
                    ).setScale(2);

            Assertions.assertEquals(parcela.parc(), installment);
            Assertions.assertEquals(parcela.vencimento(), dueDate);
            Assertions.assertEquals(parcela.juros(), interestAmount);
            Assertions.assertEquals(parcela.valorPrincipal(), principalAmount);
            Assertions.assertEquals(parcela.prestacao(), installmentValue);
        });
    }

}
