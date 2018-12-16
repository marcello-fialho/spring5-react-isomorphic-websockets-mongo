import { connect } from 'react-redux'
import { beforeSetVisibilityFilter } from '../actions'
import Link from '../components/Link'

const mapStateToProps = (state, ownProps) => ({
  active: ownProps.filter === state.visibilityFilter
})

const mapDispatchToProps = (dispatch, ownProps) => ({
  setFilter: () => {
    dispatch(beforeSetVisibilityFilter(ownProps.filter))
  }
})

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Link)
