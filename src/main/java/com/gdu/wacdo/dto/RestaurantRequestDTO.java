package com.gdu.wacdo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestaurantRequestDTO {
    @NotBlank(message = "{validation.restaurant.name.not-blank}")
    private String name;

    @NotBlank(message = "{validation.restaurant.address.not-blank}")
    private String address;

    @NotBlank(message = "{validation.restaurant.postal-code.not-blank}")
    @Size(
            min = 5,
            max = 5,
            message = "{validation.restaurant.postal_code.size}"
    )
    private String postalCode;

    @NotBlank(message = "{validation.restaurant.city.not-blank}")
    private String city;
}