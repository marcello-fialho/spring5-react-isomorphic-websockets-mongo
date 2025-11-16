package com.reactive.isomorphic.example.services;

import java.util.List;
import com.reactive.isomorphic.example.model.Todo;
import com.reactive.isomorphic.example.model.VisibilityFilter;
import com.reactive.isomorphic.example.repositories.TodoRepository;
import com.reactive.isomorphic.example.repositories.VisibilityFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;

@Service("todosService")
public class TodosServiceImpl implements TodosService {
    private final VisibilityFilterRepository visibilityFilterRepository;
    private final TodoRepository todoRepository;
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Autowired
    public TodosServiceImpl(VisibilityFilterRepository visibilityFilterRepository, TodoRepository todoRepository) {
        this.visibilityFilterRepository = visibilityFilterRepository;
        this.todoRepository = todoRepository;
    }

    @Override
    public synchronized VisibilityFilter setVisibilityFilter(String visibilityFilter) {
        return CompletableFuture.supplyAsync(() -> {
            visibilityFilterRepository.deleteAll();
            return visibilityFilterRepository.save(new VisibilityFilter(visibilityFilter));
        }, virtualThreadExecutor).join();
    }

    private void findAndProcessAll(TodoRepository repository, Function<List<Todo>, Void> func) {
        CompletableFuture.runAsync(() -> {
            var todosList = repository.findAll();
            func.apply(todosList);
        }, virtualThreadExecutor).join();
    }

    @Override
    public synchronized void completeAllTodos() {
        Function<List<Todo>, Void> completeAll = (List<Todo> todosList) -> {
            var areAllMarked = todosList.stream().allMatch(Todo::completed);
            var newTodosList = todosList.stream()
                .map(todo -> new Todo(todo.id(), todo.text(), !areAllMarked))
                .map(Todo::cp)
                .collect(Collectors.toList());
            todoRepository.deleteAll();
            todoRepository.saveAll(newTodosList);
            return null;
        };
        try {
            findAndProcessAll(todoRepository, completeAll);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void clearCompleted() {
        Function<List<Todo>, Void> clearCompleted = (List<Todo> todosList) -> {
            var newTodosList = todosList.stream()
                .filter(todo -> !todo.completed())
                .map(Todo::cp)
                .collect(Collectors.toList());
            todoRepository.deleteAll();
            todoRepository.saveAll(newTodosList);
            return null;
        };
        try {
            findAndProcessAll(todoRepository, clearCompleted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Todo addTodo(Todo todo) {
        return CompletableFuture.supplyAsync(() -> todoRepository.save(todo), virtualThreadExecutor).join();
    }

    @Override
    public synchronized void deleteTodo(String id) {
        CompletableFuture.runAsync(() -> {
            todoRepository.findById(id).ifPresent(todoRepository::delete);
        }, virtualThreadExecutor).join();
    }

    @Override
    public synchronized Todo updateTodo(String id, Todo todo) {
        return CompletableFuture.supplyAsync(() -> todoRepository.save(todo), virtualThreadExecutor).join();
    }
}
