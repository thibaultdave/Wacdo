package com.gdu.wacdo.dto;

import lombok.Data;

@Data
public class RestaurantRequestDTO {
    private String name;
    private String address;
    private String postalCode;
    private String city;
}