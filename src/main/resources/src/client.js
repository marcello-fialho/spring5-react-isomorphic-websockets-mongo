import React from 'react'
import ReactDom from 'react-dom'
import {createStore} from 'redux'
import { Provider } from 'react-redux'
import App from './components/App'
import reducer from './reducers'
import 'todomvc-app-css/index.css'
import * as TodoActions from './actions'
import {SimpleWebSocket} from './utils/simpleWebSocket'
import { bindActionCreators } from 'redux'
export const store = createStore(reducer)
window.store = store
const {setTodosState, setVisibilityFilter} = bindActionCreators(TodoActions, store.dispatch)
const todos = window.__PRELOADED_STATE__.todos
const visibilityFilter = window.__PRELOADED_STATE__.visibilityFilter
setTodosState(todos)
setVisibilityFilter(visibilityFilter)
const webSocket = SimpleWebSocket()
window.webSocket = webSocket
const handler = {
    onopen: () => {},
    onclose: () => {},
    onmessage: (message) => store.dispatch(JSON.parse(message))
}
webSocket.initialize(handler, '/react');
document.getElementById("loader").style.display = 'none';
ReactDom.render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('app')
)
