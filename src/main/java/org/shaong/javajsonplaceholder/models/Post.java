package org.shaong.javajsonplaceholder.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Post {
    private Integer userId;
    private Integer id;
    private String title;
    private String body;
}
