package com.wonderprints.isomorphic.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wonderprints.isomorphic.example.actions.*;
import com.wonderprints.isomorphic.example.model.Todo;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.util.regex.Pattern;

@Service("clientMessageDecoder")
public class ClientMessageDecoder {

    private final TodosService todosService;
    private Pattern pattern = Pattern.compile(".*type[^:]*:[^A-Z]*([A-Z_][A-Z_]*).*");

    @Autowired
    public ClientMessageDecoder(TodosService todosService) {
        this.todosService = todosService;
    }

    public Mono<String> handleMessage(String message) {
        val objectMapper = new ObjectMapper();
        val matcher = pattern.matcher(message);
        if (matcher.matches()) {
            val actionType = matcher.group(1);
            try {
                switch (actionType) {
                    case "ADD_TODO" :
                        val addTodoAction = objectMapper.readValue(message, AddTodo.class);
                        return todosService.addTodo(new Todo(addTodoAction.getId(), addTodoAction.getText(), false)).then(Mono.just(message));
                    case "DELETE_TODO" :
                        val deleteTodoAction = objectMapper.readValue(message, DeleteTodo.class);
                        return todosService.deleteTodo(deleteTodoAction.getId()).then(Mono.just(message));
                    case "UPDATE_TODO" :
                        val updateTodoAction = objectMapper.readValue(message, UpdateTodo.class);
                        return todosService.updateTodo(updateTodoAction.getTodo().getId(), updateTodoAction.getTodo()).then(Mono.just(message));
                    case "COMPLETE_ALL_TODOS":
                        return todosService.completeAllTodos().then(Mono.just(message));
                    case "CLEAR_COMPLETED":
                        return todosService.clearCompleted().then(Mono.just(message));
                    case "SET_VISIBILITY_FILTER":
                        val setVisibilityFilterAction = objectMapper.readValue(message, SetVisibilityFilter.class);
                        return todosService.setVisibilityFilter(setVisibilityFilterAction.getFilter()).then(Mono.just(message));
                }
            } catch (IOException e) {
                System.out.println("ClientMessageDecoder: not an action message");
                return Mono.empty();
            }
        } else {
            System.out.println("ClientMessageDecoder: not an action message");
            return Mono.empty();
        }
        return Mono.empty();
    }
}
