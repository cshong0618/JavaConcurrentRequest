package org.shaong.javajsonplaceholder.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class User {
    Integer id;
    String name;
    String username;
    String email;
    Address address;
    String phone;
    String website;
    Company company;
}
