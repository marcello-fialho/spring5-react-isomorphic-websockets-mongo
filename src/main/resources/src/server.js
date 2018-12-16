import React from 'react';
import ReactDOMServer from 'react-dom/server';
import {createStore} from 'redux';
import { Provider } from 'react-redux';
import reducer from './reducers';
import App from './components/App';
import 'todomvc-app-css/index.css'
import {setTodosState, setVisibilityFilter} from "./actions";

global.render = (initialStateAsString, _) => {
  const initialState = JSON.parse(initialStateAsString);
  const { todos, visibilityFilter } = initialState;
  const store = createStore(reducer);
  store.dispatch(setTodosState(todos))
  store.dispatch(setVisibilityFilter(visibilityFilter))
  return ReactDOMServer.renderToString(
    <Provider store={store}>
      <App />
    </Provider>
  );
};
