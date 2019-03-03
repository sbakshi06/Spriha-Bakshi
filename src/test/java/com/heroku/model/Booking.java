package com.heroku.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private String firstname;
    private String lastname;
    private Number totalprice;
    private Boolean depositpaid;
    private String additionalneeds;
    private BookingDates bookingdates;
}
