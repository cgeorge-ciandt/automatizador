package com.clog.automatizador.util;

import com.clog.automatizador.records.Parcela;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class ParcelasCsvParaObjetoUtil {

    public static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    public List<Parcela> csvParaParcela(String arquivo) {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(arquivo);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.lines().skip(1).map((line) -> {
            List<String> itens = Arrays.stream(line.split(";")).toList();

            return new Parcela(
                    Integer.valueOf(itens.get(0)),
                    LocalDate.parse(itens.get(1), DATA_FORMATTER),
                    Integer.valueOf(itens.get(2)),
                    new BigDecimal(itens.get(3)),
                    new BigDecimal(itens.get(4)),
                    new BigDecimal(itens.get(5)),
                    new BigDecimal(itens.get(6)),
                    new BigDecimal(itens.get(7)),
                    Integer.valueOf(itens.get(8)),
                    new BigDecimal(itens.get(9)),
                    new BigDecimal(itens.get(10)),
                    new BigDecimal(itens.get(11))
            );
        }).toList();

    }
}
