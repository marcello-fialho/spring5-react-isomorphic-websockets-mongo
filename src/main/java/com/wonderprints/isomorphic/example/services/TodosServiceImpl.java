package com.wonderprints.isomorphic.example.services;

import java.util.List;
import com.wonderprints.isomorphic.example.model.Todo;
import com.wonderprints.isomorphic.example.model.VisibilityFilter;
import com.wonderprints.isomorphic.example.repositories.TodoRepository;
import com.wonderprints.isomorphic.example.repositories.VisibilityFilterRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("todosService")
public class TodosServiceImpl implements TodosService {
    private final VisibilityFilterRepository visibilityFilterRepository;
    private final TodoRepository todoRepository;

    @Autowired
    public TodosServiceImpl(VisibilityFilterRepository visibilityFilterRepository, TodoRepository todoRepository) {
        this.visibilityFilterRepository = visibilityFilterRepository;
        this.todoRepository = todoRepository;
    }

    @Override
    public synchronized Mono<VisibilityFilter> setVisibilityFilter(String visibilityFilter) {
        return visibilityFilterRepository.deleteAll()
            .then(visibilityFilterRepository.save(new VisibilityFilter(visibilityFilter)));
    }

    private BiFunction<ArrayList<Todo>, Todo, ArrayList<Todo>> reducer = (ArrayList<Todo> acc, Todo curr) -> {
        acc.add(curr);
        return acc;
    };

    private Mono<Void> findAndProcessAll(TodoRepository repository, Function<List<Todo>, Mono<Void>> func)  {
        return repository.findAll().reduce(new ArrayList<>(), reducer).flatMap(func).then();
    }

    @Override
    public synchronized Mono<Void> completeAllTodos() {
        Function<List<Todo>, Mono<Void>> completeAll = (List<Todo> todosList) -> {
            val areAllMarked = todosList.stream().allMatch(Todo::isCompleted);
            val newTodosList = todosList.stream().map(todo -> new Todo(todo.getId(), todo.getText(), !areAllMarked)).map(Todo::cp).collect(Collectors.toList());
            return todoRepository.deleteAll().thenMany(Flux.fromStream(newTodosList.stream()).flatMap(todoRepository::save)).then();
        };
        try {
            return findAndProcessAll(todoRepository, completeAll);
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    @Override
    public synchronized Mono<Void> clearCompleted() {
        Function<List<Todo>, Mono<Void>> clearCompleted = (List<Todo> todosList) -> {
            val newTodosList = todosList.stream().filter(todo -> !todo.isCompleted()).map(Todo::cp).collect(Collectors.toList());
            return todoRepository.deleteAll().thenMany(Flux.fromStream(newTodosList.stream()).flatMap(todoRepository::save)).then();
        };
        try {
            return findAndProcessAll(todoRepository, clearCompleted);
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    @Override
    public synchronized Mono<Todo> addTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    @Override
    public synchronized Mono<Void> deleteTodo(String id) {
        return todoRepository.findById(id).flatMap(todoRepository::delete);
    }

    @Override
    public synchronized Mono<Todo> updateTodo(String id, Todo todo) {
        return todoRepository.save(todo);
    }
}
