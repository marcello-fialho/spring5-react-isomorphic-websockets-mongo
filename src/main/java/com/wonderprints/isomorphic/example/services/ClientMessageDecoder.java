package com.wonderprints.isomorphic.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wonderprints.isomorphic.example.actions.AddTodo;
import com.wonderprints.isomorphic.example.actions.DeleteTodo;
import com.wonderprints.isomorphic.example.actions.UpdateTodo;
import com.wonderprints.isomorphic.example.actions.SetVisibilityFilter;
import com.wonderprints.isomorphic.example.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;

@Service("clientMessageDecoder")
public class ClientMessageDecoder {

    private final TodosService todosService;
    private Pattern pattern = Pattern.compile(".*type[^:]*:[^A-Z]*([A-Z_][A-Z_]*).*");
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Autowired
    public ClientMessageDecoder(TodosService todosService) {
        this.todosService = todosService;
    }

    public String handleMessage(String message) {
        var objectMapper = new ObjectMapper();
        var matcher = pattern.matcher(message);
        if (matcher.matches()) {
            var actionType = matcher.group(1);
            try {
                return CompletableFuture.supplyAsync(() -> {
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
                    }
                }, virtualThreadExecutor).join();
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
