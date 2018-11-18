package org.shaong.javajsonplaceholder.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class Posts {
    private List<Post> posts;
}
