package com.reactive.isomorphic.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactive.isomorphic.example.actions.AddTodo;
import com.reactive.isomorphic.example.actions.DeleteTodo;
import com.reactive.isomorphic.example.actions.UpdateTodo;
import com.reactive.isomorphic.example.actions.SetVisibilityFilter;
import com.reactive.isomorphic.example.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.regex.Pattern;

@Service("clientMessageDecoder")
public class ClientMessageDecoder {

    private final TodosService todosService;
    private final ObjectMapper objectMapper;
    private Pattern pattern = Pattern.compile(".*type[^:]*:[^A-Z]*([A-Z_][A-Z_]*).*");

    @Autowired
    public ClientMessageDecoder(TodosService todosService, ObjectMapper objectMapper) {
        this.todosService = todosService;
        this.objectMapper = objectMapper;
    }

    public String handleMessage(String message) {
        System.out.println("ClientMessageDecoder.handleMessage called with: " + message);
        var matcher = pattern.matcher(message);
        if (matcher.matches()) {
            var actionType = matcher.group(1);
            System.out.println("Matched action type: " + actionType);
            try {
                return switch (actionType) {
                    case "ADD_TODO" -> {
                        AddTodo addTodoAction = objectMapper.readValue(message, AddTodo.class);
                        todosService.addTodo(new Todo(addTodoAction.id(), addTodoAction.text(), false));
                        yield message;
                    }
                    case "DELETE_TODO" -> {
                        DeleteTodo deleteTodoAction = objectMapper.readValue(message, DeleteTodo.class);
                        todosService.deleteTodo(deleteTodoAction.id());
                        yield message;
                    }
                    case "UPDATE_TODO" -> {
                        UpdateTodo updateTodoAction = objectMapper.readValue(message, UpdateTodo.class);
                        todosService.updateTodo(updateTodoAction.todo().id(), updateTodoAction.todo());
                        yield message;
                    }
                    case "COMPLETE_ALL_TODOS" -> {
                        todosService.completeAllTodos();
                        yield message;
                    }
                    case "CLEAR_COMPLETED" -> {
                        todosService.clearCompleted();
                        yield message;
                    }
                    case "SET_VISIBILITY_FILTER" -> {
                        SetVisibilityFilter setVisibilityFilterAction = objectMapper.readValue(message, SetVisibilityFilter.class);
                        todosService.setVisibilityFilter(setVisibilityFilterAction.filter());
                        yield message;
                    }
                    default -> null;
                };
            } catch (IOException e) {
                System.out.println("ClientMessageDecoder: error parsing action: " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.out.println("ClientMessageDecoder: error processing message: " + e.getMessage());
                return null;
            }
        } else {
            System.out.println("ClientMessageDecoder: not an action message");
            return null;
        }
    }
}
