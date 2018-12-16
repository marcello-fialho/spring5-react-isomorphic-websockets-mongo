import * as types from '../constants/ActionTypes'
import shortid from 'shortid'

export const updateTodo = todo => ({ type: types.UPDATE_TODO, todo })
export const addTodo = (text, id) => ({ type: types.ADD_TODO, text, id })
export const beforeAddTodo = text =>  {
    const id = shortid.generate()
    if (window && window.webSocket) window.webSocket.sendMessage(JSON.stringify(addTodo(text, id)))
    return { type: types.BEFORE_ADD_TODO, text, id }
}
export const deleteTodo = id => ({ type: types.DELETE_TODO, id })
export const beforeDeleteTodo = id =>  {
    if (window && window.webSocket) window.webSocket.sendMessage(JSON.stringify(deleteTodo(id)))
    return { type: types.BEFORE_DELETE_TODO, id }
}
export const beforeEditTodo = (id, text) =>  {
    const todo = {...window.store.getState().todos.filter(todo => todo.id === id)[0], text}
    if (window && window.webSocket) window.webSocket.sendMessage(JSON.stringify(updateTodo(todo)))
    return { type: types.BEFORE_EDIT_TODO, id, text }
}
export const beforeCompleteTodo = id =>  {
    let todo = window.store.getState().todos.filter(todo => todo.id === id)[0]
    todo = {...todo, completed: !todo.completed}
    if (window && window.webSocket) window.webSocket.sendMessage(JSON.stringify(updateTodo(todo)))
    return { type: types.BEFORE_COMPLETE_TODO, id }
}
export const completeAllTodos = () => ({ type: types.COMPLETE_ALL_TODOS })
export const beforeCompleteAllTodos = () => {
    if (window && window.webSocket) window.webSocket.sendMessage(JSON.stringify(completeAllTodos()))
    return {type: types.BEFORE_COMPLETE_ALL_TODOS }
}
export const clearCompleted = () => ({ type: types.CLEAR_COMPLETED })
export const beforeClearCompleted = () =>  {
    if (window && window.webSocket) window.webSocket.sendMessage(JSON.stringify(clearCompleted()))
    return { type: types.BEFORE_CLEAR_COMPLETED }
}
export const setVisibilityFilter = filter => ({ type: types.SET_VISIBILITY_FILTER, filter})
export const beforeSetVisibilityFilter = filter => {
    if (window && window.webSocket) window.webSocket.sendMessage(JSON.stringify(setVisibilityFilter(filter)))
    return { type: types.BEFORE_SET_VISIBILITY_FILTER, filter }
}
export const editTodo = (id, text) => ({ type: types.EDIT_TODO, id, text })
export const completeTodo = id => ({ type: types.COMPLETE_TODO, id })
export const setTodosState = (todos) => ({type: types.SET_TODOS_STATE, state: todos })
