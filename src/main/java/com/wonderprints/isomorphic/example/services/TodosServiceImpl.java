package com.wonderprints.isomorphic.example.services;

import java.util.List;
import com.wonderprints.isomorphic.example.model.Todo;
import com.wonderprints.isomorphic.example.model.VisibilityFilter;
import com.wonderprints.isomorphic.example.repositories.TodoRepository;
import com.wonderprints.isomorphic.example.repositories.VisibilityFilterRepository;
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
            List<Todo> todosList = repository.findAll();
            func.apply(todosList);
        }, virtualThreadExecutor).join();
    }

    @Override
    public synchronized void completeAllTodos() {
        Function<List<Todo>, Void> completeAll = (List<Todo> todosList) -> {
            boolean areAllMarked = todosList.stream().allMatch(Todo::completed);
            List<Todo> newTodosList = todosList.stream()
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
            List<Todo> newTodosList = todosList.stream()
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
