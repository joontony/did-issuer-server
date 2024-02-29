package org.snubi.did.issuerserver.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilterMemberServiceImplementTest {

    @Test
    public void dateFormatChangeTest() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDate date = LocalDate.now();
        Integer period = 180;

        String start = date + " 00:00:00";
        LocalDateTime dateTime = LocalDateTime.parse(start, formatter);
        LocalDateTime dateTimeBegin = dateTime.minusDays(period);

        System.out.println(dateTimeBegin);

        String end = date + " 23:59:59";
        LocalDateTime dateTimeEnd = LocalDateTime.parse(end, formatter);

        System.out.println(dateTimeEnd);

    }
}