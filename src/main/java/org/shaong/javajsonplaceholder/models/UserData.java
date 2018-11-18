package org.shaong.javajsonplaceholder.models;

import lombok.Data;

import java.util.List;

@Data
public class UserData {
    private User user;
    private List<Todo> todos;
    private List<Post> posts;
}
