package org.shaong.javajsonplaceholder.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class Todos {
    private List<Todo> todos;
}
