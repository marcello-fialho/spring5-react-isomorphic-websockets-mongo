import React from 'react'
import { connect } from 'react-redux'
import PropTypes from 'prop-types'
import TodoTextInput from '../components/TodoTextInput'
import { beforeAddTodo } from '../actions'

export const Header = ({ beforeAddTodo }) => (
  <header className="header">
    <h1>todos</h1>
    <TodoTextInput
      newTodo
      onSave={(text) => {
        if (text.length !== 0) {
          beforeAddTodo(text)
        }
      }}
      placeholder="What needs to be done?"
    />
  </header>
)

Header.propTypes = {
  beforeAddTodo: PropTypes.func.isRequired
}

export default connect(null, { beforeAddTodo })(Header)